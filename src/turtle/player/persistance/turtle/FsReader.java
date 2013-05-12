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

import android.util.Log;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import turtle.player.common.filefilter.FileFilters;
import turtle.player.model.*;
import turtle.player.persistance.framework.executor.OperationExecutor;
import turtle.player.persistance.framework.filter.FieldFilter;
import turtle.player.persistance.framework.filter.Operator;
import turtle.player.persistance.source.sql.First;
import turtle.player.persistance.source.sqlite.QuerySqlite;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.db.structure.Views;
import turtle.player.persistance.turtle.mapping.AlbumArtLocationCreator;
import turtle.player.preferences.Preferences;
import turtle.player.util.Shorty;

import java.io.*;
import java.util.*;

public class FsReader
{

	public static boolean scanFile(String filePath,
										  TurtleDatabase db,
	                             Set<String> encounteredRootSrcs) throws IOException
	{
		// http://www.exampledepot.com/egs/java.io/GetFiles.html

		Log.i(Preferences.TAG, "register " + filePath);

		long start = System.currentTimeMillis();

		String rootSrc = filePath.substring(0, filePath.lastIndexOf("/"));

		String title = null;
		String artist = null;
		String album = null;
		int genre = -1;
		int number = 0;
		try
		{
			synchronized (Mp3File.class)
			{
				Mp3File mp3file = new Mp3File(filePath, false);
				final ID3v1 id3tag;

				if(mp3file.hasId3v1Tag()){
					id3tag = mp3file.getId3v1Tag();
				}
				else if(mp3file.hasId3v2Tag())
				{
					id3tag = mp3file.getId3v2Tag();
				}
				else
				{
					id3tag = null;
				}

				if(id3tag != null){
					title = id3tag.getTitle();
					artist = id3tag.getArtist();
					album = id3tag.getAlbum();
					genre = id3tag.getGenre();
					number = parseTrackNumber(id3tag.getTrack());
				}
			}
		}
		catch (UnsupportedTagException e)
		{
			throw new IOException("Unable to read ID3 tag", e);
		}
		catch (InvalidDataException e)
		{
			throw new IOException("Unable to read ID3 tag", e);
		}

		if (Shorty.isVoid(title))
		{
			title = "Unknown";
		}
		if (Shorty.isVoid(album))
		{
			number = 0; //tracknumbers with no album results in strange sorting
		}

		Track t = new Track(
				  new SongDigest(title),
				  new ArtistDigest(artist),
				  new AlbumDigest(album),
				  new GenreDigest(genre < 0 ? "" : String.valueOf(genre)),
				  number,
				  filePath,
				  rootSrc
		);
		Log.v(Preferences.TAG, "created " + (System.currentTimeMillis() - start) + "ms");
		boolean added = db.push(t);
		Log.v(Preferences.TAG, "pushed  " + (System.currentTimeMillis() - start) + "ms");

		if(added)
		{
			if(encounteredRootSrcs.add(rootSrc))
			{
				db.push(new FSobject(rootSrc));
			}
		}

		return added;
	}

	static public List<String> getMediaFilesPaths(String mediaPath, List<? extends FilenameFilter> filters, boolean recursive, boolean getFirstMatch){

		List<String> candidates = new ArrayList<String>();

		try
		{
			String[] arguments = recursive ? new String[]{"ls", "-R", mediaPath} : new String[]{"ls", mediaPath};
			Process p = Runtime.getRuntime().exec(arguments);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String currPath = mediaPath + "/";
			String line = "";
			while (line != null)
			{
				line = br.readLine();
				if (line != null)
				{
					if(line.startsWith("/")){
						currPath = line;
						currPath = currPath.lastIndexOf(":") == currPath.length()-1 ? currPath.substring(0, currPath.length()-1) : currPath;
						currPath = currPath.indexOf(".") == 0 ? currPath.replaceFirst(".", mediaPath) : currPath;
						currPath = currPath.lastIndexOf("/") != currPath.length()-1 ? currPath + "/" : currPath;
					}
					else
					{
						if(!Shorty.isVoid(line))
						{
							candidates.add(currPath + line);
						}
					}
				}
			}
		} catch (IOException e)
		{
			//Empty
		}

		//Log.v(Preferences.TAG, "found " + candidates.size() + " "+ (System.currentTimeMillis() - start) + "ms");

		List<String> acceptedPaths = new ArrayList<String>();

		for (FilenameFilter filenameFilter : filters)
		{
			for(String path : candidates)
			{
				//Log.v(Preferences.TAG, "filter " + path + (System.currentTimeMillis() - start) + "ms " + path);
				if(filenameFilter.accept(null, path)){

					acceptedPaths.add(path);
					if(getFirstMatch){
						//Log.v(Preferences.TAG, "completed " + (System.currentTimeMillis() - start) + "ms " + path);
						return acceptedPaths;
					}
				}
			}
		}
		return acceptedPaths;
	}

	public static String getAlbumArt(String mediaFileDir, TurtleDatabase db)
	{
		final String result;

		AlbumArtLocation albumArtLocation = OperationExecutor.execute(
				  db,
				  new QuerySqlite<Tables.AlbumArtLocations, Tables.AlbumArtLocations, AlbumArtLocation>(
							 new FieldFilter<Tables.AlbumArtLocations, AlbumArtLocation, String>(Tables.AlbumArtLocations.PATH, Operator.EQ, mediaFileDir),
				  			 new First<AlbumArtLocation>(Tables.ALBUM_ART_LOCATIONS, new AlbumArtLocationCreator())
				  )
		);

		if(albumArtLocation != null)
		{
			return albumArtLocation.getAlbumArtpath();
		}

		if (!Shorty.isVoid(mediaFileDir.replaceAll("/", "").replaceAll("\\.", ""))){
			List<String> albumArtStrings = FsReader.getMediaFilesPaths(mediaFileDir, FileFilters.folderArtFilters, false, true);
			if(!albumArtStrings.isEmpty())
			{
				result = albumArtStrings.iterator().next();
			}
			else
			{
				result = getAlbumArt(mediaFileDir.substring(0, mediaFileDir.lastIndexOf("/")), db);
			}
		}
		else{
			result = null;
		}

		db.push(new AlbumArtLocation(mediaFileDir, result));
		return result;
	}

	static int parseTrackNumber(String trackNumber)
	{
		//strips all chars beginning at first non digit (e.g. 5/10)
		String strippedTrackNumber = trackNumber == null ? "" : trackNumber.replaceAll("\\D.*", "");

		if (strippedTrackNumber.length() > 0)
		{
			try
			{
				return Integer.parseInt(strippedTrackNumber);
			}
			catch (NumberFormatException e)
			{
				Log.w(Preferences.TAG, "Unable to parse track number :" + strippedTrackNumber);
			}
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
			Log.v(Preferences.TAG, "Unable to parse duration '" + duration + "': " + e.getMessage());
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
