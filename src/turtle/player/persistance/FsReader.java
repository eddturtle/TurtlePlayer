package turtle.player.persistance;/*
 * 
 * TURTLE PLAYER
 * 
 * Licensed under MIT & GPL
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * Created by Edd Turtle (www.eddturtle.co.uk)
 * More Information @ www.turtle-player.co.uk
 * 
 * author: honegs
 */

import android.media.MediaMetadataRetriever;
import android.util.Log;
import turtle.player.common.filefilter.FileFilters;
import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Track;
import turtle.player.preferences.Preferences;
import turtle.player.util.Shorty;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public class FsReader
{
    public static final int MAX_DIR_SCAN_DEPTH = 50;


    public static void scanDir(Database db, File rootNode){
        MediaMetadataRetriever metaDataReader = new MediaMetadataRetriever();
        scanDir(db, metaDataReader, rootNode, 0, null);
        metaDataReader.release();
    }

    /**
     * @param rootNode
     * @param depth number of parent allready visited
     */
    private static void scanDir(Database db, MediaMetadataRetriever metaDataReader, File rootNode, int depth, String parentAlbumArt)
    {
        // http://www.exampledepot.com/egs/java.io/GetFiles.html

        try
        {
            String albumArt = getAlbumArt(rootNode);

            if(albumArt != null)
            {
                albumArt = rootNode + "/" + albumArt;
            }
            else
            {
                albumArt = parentAlbumArt;
            }

            for (String mp3 : rootNode.list(FileFilters.PLAYABLE_FILES_FILTER))
            {
                Log.v(Preferences.TAG, "register " + rootNode + "/" + mp3);
                metaDataReader.setDataSource(rootNode + "/" + mp3);

                String title = extractMetadata(metaDataReader, MediaMetadataRetriever.METADATA_KEY_TITLE);
                int number = parseTrackNumber(extractMetadata(metaDataReader, MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));
                double length = parseDuration(extractMetadata(metaDataReader, MediaMetadataRetriever.METADATA_KEY_DURATION));
                String artist = extractMetadata(metaDataReader, MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String album = extractMetadata(metaDataReader, MediaMetadataRetriever.METADATA_KEY_ALBUM);

                if (Shorty.isVoid(title))
                {
                    title = "Unknown";
                }

                if (Shorty.isVoid(artist))
                {
                    artist = "Unknown";
                }

                if (Shorty.isVoid(album))
                {
                    album = "Unknown";
                }

                Track t = new Track(
                        title,
                        number,
                        new Artist(artist),
                        new Album(album),
                        length,
                        rootNode + "/" + mp3,
                        rootNode + "/",
                        albumArt
                );

                db.push(t);
            }

            for (File dir : rootNode.listFiles(isDIR))
            {
                if(depth < MAX_DIR_SCAN_DEPTH) // avoid Stack overflow - symbolic link points to a parent dir
                {
                    scanDir(db, metaDataReader, dir, depth + 1, albumArt);
                }
            }
        }
        catch (NullPointerException e)
        {
            // Probably No SD-Card
            Log.v(Preferences.TAG, e.getMessage());
        }
    }

    /**
     * calls {@link MediaMetadataRetriever#extractMetadata(int)} and removes
     * chunk after 0 terminated string
     *
     * @param keyCode see {@link MediaMetadataRetriever#extractMetadata(int)}
     * @return
     */
    static String extractMetadata(MediaMetadataRetriever metaDataReader, int keyCode)
    {
        String metaData = Shorty.avoidNull(metaDataReader.extractMetadata(keyCode));

        //replace all chars exept letters and digits, space and dash
        //return metaData.replaceAll("^\\w\\s-,:;?$[]\"]","");

        int indexOfZeroTermination = metaData.indexOf(0);
        return indexOfZeroTermination < 0 ? metaData : metaData.substring(0, indexOfZeroTermination);
    }

    static private String getAlbumArt(File dir)
    {
        for(FilenameFilter filenameFilter : FileFilters.folderArtFilters)
        {
            String[] paths = dir.list(filenameFilter);
            if(paths.length > 0){
                return paths[0];
            }
        }
        return null;
    }

    static int parseTrackNumber(String trackNumber)
    {
        //strips all chars beginning at first non digit
        String strippedTrackNumber = trackNumber.replaceAll("\\D.*", "");

        if(strippedTrackNumber.length() > 0)
        {
            return Integer.parseInt(strippedTrackNumber);
        }
        return 0;
    }

    static double parseDuration(String duration)
    {
        try
        {
            return Double.parseDouble(duration);
        }
        catch (NumberFormatException e)
        {
            Log.v(Preferences.TAG, "Not able to parse duration '" + duration + "': " + e.getMessage());
        }
        return 0;
    }

    final static FileFilter isDIR = new FileFilter()
    {
        final String[] IGNORED_DIRS = new String[]{
                "/proc/",
                "/sys/",
                "/system/",
                "/proc/",
                "/root/",
        };

        @Override
        public boolean accept(File file) {
            for(String ignoredDir : IGNORED_DIRS)
            {
                file.toString().startsWith(ignoredDir);
            }

            return file.canRead() && file.isDirectory();
        }
    };
}
