/**
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
 * More Information @ www.turtle-player.co.uk
 *
 * @author Simon Honegger (Hoene84)
 */


package turtle.player.persistance.turtle;

import android.media.MediaMetadataRetriever;
import android.util.Log;
import turtle.player.common.filefilter.FileFilters;
import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Genre;
import turtle.player.model.Track;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.preferences.Preferences;
import turtle.player.util.Shorty;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FsReader
{
	public static final int MAX_DIR_SCAN_DEPTH = 50;


	public static void scanFiles(Collection<String> mediaFilePaths, TurtleDatabase db, File rootPath)
	{
		MediaMetadataRetriever metaDataReader = new MediaMetadataRetriever();

		try{
			for(String mediaFilePath : mediaFilePaths)
			{
				try
				{
					scanFile(mediaFilePath, rootPath, db, metaDataReader);
				}
				catch (IOException e)
				{
					//log and go on with next File
					Log.v(Preferences.TAG, "failed to process " + mediaFilePath);
				}
			}
		}
		finally {
			metaDataReader.release();
		}
	}

	private static void scanFile(String filePath,
										  File rootPath,
										  TurtleDatabase db,
										  MediaMetadataRetriever metaDataReader) throws IOException
	{
		// http://www.exampledepot.com/egs/java.io/GetFiles.html
		final File file = new File(filePath);
		if(file.exists() && file.canRead() && file.isFile()){
			Log.v(Preferences.TAG, "register " + filePath);
			long start = System.currentTimeMillis();
			metaDataReader.setDataSource(filePath);
			Log.v(Preferences.TAG, "init   " + (System.currentTimeMillis() - start) + "ms");
			String title = extractMetadata(metaDataReader, MediaMetadataRetriever.METADATA_KEY_TITLE);
			int number = parseTrackNumber(extractMetadata(metaDataReader, MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));
			double length = parseDuration(extractMetadata(metaDataReader, MediaMetadataRetriever.METADATA_KEY_DURATION));
			String artist = extractMetadata(metaDataReader, MediaMetadataRetriever.METADATA_KEY_ARTIST);
			String album = extractMetadata(metaDataReader, MediaMetadataRetriever.METADATA_KEY_ALBUM);
			String genre = extractMetadata(metaDataReader, MediaMetadataRetriever.METADATA_KEY_GENRE);
			Log.v(Preferences.TAG, "md     " + (System.currentTimeMillis() - start) + "ms");
			String albumArt = getAlbumArt(file.getParentFile(), rootPath);
			Log.v(Preferences.TAG, "albumAr" + (System.currentTimeMillis() - start) + "ms");
			if (Shorty.isVoid(title))
			{
				title = "Unknown";
			}
			if (Shorty.isVoid(album))
			{
				number = 0; //tracknumbers with no album results in strange sorting
			}

			Track t = new Track(
					  title,
					  number,
					  new Artist(artist),
					  new Album(album),
					  new Genre(genre),
					  length,
					  file.getAbsolutePath(),
					  file.getPath(),
					  albumArt
			);
			Log.v(Preferences.TAG, "created " + (System.currentTimeMillis() - start) + "ms");
			db.push(t);
			Log.v(Preferences.TAG, "pushed  " + (System.currentTimeMillis() - start) + "ms");
		}
	}

	/**
	 * calls {@link MediaMetadataRetriever#extractMetadata(int)} and removes
	 * chunk after 0 terminated string
	 *
	 * @param keyCode see {@link MediaMetadataRetriever#extractMetadata(int)}
	 * @return
	 */
	static String extractMetadata(MediaMetadataRetriever metaDataReader,
											int keyCode)
	{
		String metaData = Shorty.avoidNull(metaDataReader.extractMetadata(keyCode));

		//replace all chars exept letters and digits, space and dash
		//return metaData.replaceAll("^\\w\\s-,:;?$[]\"]","");

		int indexOfZeroTermination = metaData.indexOf(0);
		return indexOfZeroTermination < 0 ? metaData : metaData.substring(0, indexOfZeroTermination);
	}

	static public Set<String> getMediaFilesPaths(File mediaPath){

		Set<String> paths = new HashSet<String>();

		try
		{
			String mediaPathString = mediaPath.toString();
			Process p = Runtime.getRuntime().exec(new String[]{"ls", "-R", mediaPathString});
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String currPath = mediaPathString;
			String line = "";
			while (line != null)
			{
				line = br.readLine();
				if (line != null)
				{
					if(line.startsWith("/")){
						currPath = line;
						currPath = currPath.lastIndexOf(":") == currPath.length()-1 ? currPath.substring(0, currPath.length()-1) : currPath;
						currPath = currPath.indexOf(".") == 0 ? currPath.replaceFirst(".", mediaPathString) : currPath;
						currPath = currPath.lastIndexOf("/") != currPath.length()-1 ? currPath + "/" : currPath;
					}
					else
					{
						if(FileFilters.PLAYABLE_FILES_FILTER.accept(null, line))
						{
							paths.add(currPath + line);
							Log.v(Preferences.TAG, currPath + line);
						}
					}
				}
			}
		} catch (IOException e)
		{
			//Empty
		}
		return paths;
	}

	static private String getAlbumArt(File mediaFileDir, File rootDir)
	{
		String albumArtPath = null;
		if(mediaFileDir.isDirectory() && mediaFileDir.canRead() && mediaFileDir.getPath().contains(rootDir.getPath())){

			for (FilenameFilter filenameFilter : FileFilters.folderArtFilters)
			{
				File[] paths = mediaFileDir.listFiles(filenameFilter);
				if (paths.length > 0)
				{
					return paths[0].getAbsolutePath();
				}
			}
			return getAlbumArt(mediaFileDir.getParentFile(), rootDir);
		}
		return albumArtPath;
	}

	static int parseTrackNumber(String trackNumber)
	{
		//strips all chars beginning at first non digit (e.g. 5/10)
		String strippedTrackNumber = trackNumber.replaceAll("\\D.*", "");

		if (strippedTrackNumber.length() > 0)
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
		} catch (NumberFormatException e)
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

		public boolean accept(File file)
		{
			for (String ignoredDir : IGNORED_DIRS)
			{
				if(file.toString().startsWith(ignoredDir)){
					return false;
				}
			}

			return file.canRead() && file.isDirectory();
		}
	};
}
