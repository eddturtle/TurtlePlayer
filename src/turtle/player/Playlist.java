/*
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
 */

// Package
package turtle.player;

// Import - Java
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;
import java.io.File;
import java.io.FilenameFilter;

// Import - Android Log
import android.util.Log;

// Import - Android Media
import android.media.MediaMetadataRetriever;

// Import - Android Context
import android.content.Context;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;
import android.content.res.Resources;

public class Playlist {

	
	// ========================================= //
	// 	Attributes
	// ========================================= //
	
	private List<Track> trackList;
	private List<String> artistList;
	private List<String> albumList;
	private LinkedList<Integer> historyList;
	
	private Database syncDB;
	
	private int listPosition; 
	private int historyPosition;
	
	// Not in ClassDiagram
	public Preferences preferences;
	public Stats stats;
	
	private String lastUsedArtist;
	private String lastUsedAlbum;
	private int returnType;
	
	private MediaMetadataRetriever metaDataReader;
	
	private String title;
	private int number;
	private String artist;
	private String album;
	private double length;
	private int trackCount;
	
	private Context mainContext;
	
	
	// ========================================= //
	// 	Constructor
	// ========================================= //
	
	public Playlist()
	{
		trackList = new ArrayList<Track>();
		artistList = new ArrayList<String>();
		albumList = new ArrayList<String>();
		historyList = new LinkedList<Integer>();

		listPosition = 0;
		historyPosition = 0;
		
		// Location, Repeat, Shuffle (Remember Trailing / on Location)
		preferences = new Preferences("/sdcard/Music/", true, true);
		stats = new Stats();
		
		lastUsedArtist = "";
		lastUsedAlbum = "";
		returnType = 0;
	}
	
	
	// ========================================= //
	// 	Set Context
	// ========================================= //
	
	public void SetContext(Context con)
	{		
		mainContext = con;
		syncDB = new Database(mainContext);
	}
	
	
	// ========================================= //
	// 	Destroy
	// ========================================= //
	
	public void Destroy()
	{
		this.ClearList();
		this.DatabaseClose();
		
		if (metaDataReader != null)
		{
			metaDataReader.release();
		}
	}
	
	
	// ========================================= //
	// 	Add Track
	// ========================================= //
	
	public void AddTrack(Track nTrack)
	{
		trackList.add(nTrack);
	}
	
	
	// ========================================= //
	// 	Remove Track
	// ========================================= //
	
	public boolean RemoveTrack(String title)
	{
		// http://www.java2s.com/Code/JavaAPI/java.util/foreachloopforArrayList.htm
		
		int count = 0;
		boolean success = false;
		
		if (!trackList.isEmpty())
		{
			for (Track t : trackList)
			{
				count++;
				if (t.GetTitle() == title)
				{
					trackList.remove(count);
					success = true;
				}
			}
		}
		
		return success;
	}
	
	
	// ========================================= //
	// 	Filename Filters
	// ========================================= //
	
	FilenameFilter hasAlbumArt = new FilenameFilter()
	{
		public boolean accept(File dir, String name)
		{
			return name.contains("Folder.jpg");
		}
	};
	
	FilenameFilter isMP3 = new FilenameFilter()
	{
		public boolean accept(File dir, String name)
		{
			boolean result = false;
			
			if (name.endsWith(".mp3"))
			{
				result = true;
			}
			
			if (name.endsWith(".ogg"))
			{
				result = true;
			}
			
			return result;
		}
	};
	
	FilenameFilter isDIR = new FilenameFilter()
	{
		public boolean accept(File dir, String name)
		{
			return !name.contains(".");
		}
	};
	
	
	// ========================================= //
	// 	Update List
	// ========================================= //
	
	public void UpdateList()
	{
		this.ClearList();
		//syncDB.Clear();
		
		// TODO EXISTS BUT MAYBE BLANK!?!
		
		if (!syncDB.Exists() || syncDB.IsEmpty())
		{
			try
			{
				metaDataReader = new MediaMetadataRetriever();
				CheckDir(preferences.GetMediaPath());
				metaDataReader.release();
			}
			catch (NullPointerException e)
			{
				Log.v(preferences.GetTag(), e.getMessage());
			}
			
			syncDB = new Database(mainContext);
			this.DatabasePush();
		}
		else
		{
			this.DatabasePull();
		}
	}
	
	
	// ========================================= //
	// 	Check Directory for MP3s
	// ========================================= //
	
	private void CheckDir(String src)
	{
		// http://www.exampledepot.com/egs/java.io/GetFiles.html
		
		File rootNode = new File(src);
		boolean folderHasAlbumArt = false;
		
		try
		{
			if (rootNode.list(hasAlbumArt).length > 0)
			{
				folderHasAlbumArt = true;
			}
			
			for (String mp3 : rootNode.list(isMP3))
			{
				metaDataReader.setDataSource(src + mp3);
				
				title = metaDataReader.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
				
				String preFormat = metaDataReader.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
				String postFormat = "0";
				String item;
				boolean passed = false;
				
				if (preFormat != null && preFormat != "")
				{
					for (int i = 0; i < preFormat.length(); i++)
					{
						item = preFormat.substring(i,i+1);
						
						if (item != "/" && passed != true)
						{
							postFormat = postFormat + item;
						}
						else
						{
							passed = true;
						}
					}
					
					try
					{
						number = Integer.parseInt(postFormat);
					}
					catch (NumberFormatException e)
					{
						// TODO fill
					}
				}
				else
				{
					number = 0;
				}
				
				artist = metaDataReader.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
				album = metaDataReader.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
				length = Double.parseDouble(metaDataReader.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
				
				if (title == null)
				{
					title = "Unknown";
				}
				
				if (artist == null)
				{
					artist = "Unknown";
				}
				
				if (album == null)
				{
					album = "Unknown";
				}
				
				Track t = new Track(trackCount, title, number, artist, album, length, src + mp3);
				
				t.SetAlbumArt(folderHasAlbumArt);
				t.SetRootSrc(src);
				
				this.AddTrack(t);
				
				trackCount++;
			}
			
			for (String dir : rootNode.list(isDIR))
			{
				CheckDir(src + dir + "/");
			}
		}
		catch (NullPointerException e)
		{
			// Probably No SD-Card
			Log.v(preferences.GetTag(), e.getMessage());
		}
	}
	
	
	// ========================================= //
	// 	Clear List
	// ========================================= //
	
	public void ClearList()
	{
		trackList.clear();
		historyList.clear();
	}
	
	
	// ========================================= //
	// 	Add to History
	// ========================================= //
	
	public void AddToHistory()
	{
		if (historyPosition == 0)
		{
			historyList.add(listPosition);
		}
	}
	
	// ========================================= //
	// 	Sort by Title
	// ========================================= //

	public void SortByTitle()
	{
		returnType = 0;
		DatabasePull();
		Collections.sort(trackList, new TitleComparator());
	}
	
	public void SortByArtist()
	{
		returnType = 1;
		DatabasePull();
		Collections.sort(trackList, new ArtistComparator());
		Collections.sort(artistList);
	}
	
	public void SortByAlbum()
	{
		returnType = 2;
		DatabasePull();
		
		try
		{
			Collections.sort(trackList, new AlbumComparator());
			Collections.sort(albumList);
		}
		catch (NullPointerException e)
		{
			// TODO Log Error
		}
	}
	
	public void FilterListByArtist()
	{
		
	}
	
	
	// ========================================= //
	// 	Find Previous Track
	// ========================================= //
	
	public Track PreviousTrack()
	{
		if (historyPosition < (historyList.size() - 1))
		{
			historyPosition++;
			listPosition = historyList.get((historyList.size() - 1) - historyPosition);
			return trackList.get(listPosition);
		}
		else
		{
			return null;
		}
	}
	
	
	// ========================================= //
	// 	Find Next Track
	// ========================================= //
	
	public Track NextTrack()
	{
		if (historyPosition != 0)
		{
			// In History List
			historyPosition--;
			listPosition = historyList.get((historyList.size() - 1) - historyPosition);
		}
		else
		{
			if (trackList.size() > 1)
			{
				if (preferences.GetShuffle())
				{
					int ranNum = (int)((Math.random() * trackList.size()));
					
					while (ranNum == listPosition)
					{
						ranNum = (int)((Math.random() * trackList.size()));
					}
					
					listPosition = ranNum;
				}
				else
				{
					if ((this.GetPosition() + 1) >= this.Length())
					{
						if (preferences.GetRepeat())
						{
							listPosition = 0;					
						}
						else
						{
							return null;
						}
					}
					else
					{
						++listPosition;
					}
				}
			}
		}
		
		return trackList.get(listPosition);
	}
	
	
	// ========================================= //
	// 	Return Single Track
	// ========================================= //
	
	public Track GetTrack(int position)
	{
		// Return Type:
		// 0 = Track
		// 1 = Artist
		// 2 = Album
		// 3 = Inside Artist
		// 4 = Inside Album
		
		if (returnType != 3 && returnType != 4)
		{
			Track t = trackList.get(position);
			
			listPosition = position;
			
			if (t != null)
			{
				return t;
			}
			else
			{
				return null;			
			}
		}
		else if (returnType == 3)
		{
			listPosition = position;
			
			ArrayList<Track> byAr = new ArrayList<Track>();
			
			for (Track t : trackList)
			{
				if (lastUsedArtist.contains(t.GetArtist()))
				{
					byAr.add(t);
				}
			}
			
			trackList = byAr;
			
			return byAr.get(position);
		}
		else
		{
			listPosition = position;
			
			ArrayList<Track> byAlbum = new ArrayList<Track>();
			
			for (Track t : trackList)
			{
				if (lastUsedAlbum.contains(t.GetAlbum()))
				{
					byAlbum.add(t);
				}
			}
			
			trackList = byAlbum;
			
			return trackList.get(position);
		}
	}

	
	
	// ========================================= //
	// 	Return Track List
	// ========================================= //
	
	public String[] GetList()
	{
		// Return Type:
		// 0 = Track
		// 1 = Artist
		// 2 = Album
		// 3 = Inside Artist
		// 4 = Inside Album
		
		int count = 0;
		String[] list;
		
		if (returnType == 0)
		{
			list = new String[trackList.size()];
			for (Track t : trackList)
			{
				list[count] = t.GetTitle() + " - " + t.GetArtist();
				count++;
			}
		}
		else if (returnType == 1)
		{
			ArrayList<String> tempArray = new ArrayList<String>();
			
			for (Track t : trackList)
			{
				tempArray.add(t.GetArtist());
				count++;
			}
			
			HashSet<String> h = new HashSet<String>(tempArray);
			tempArray.clear();
			tempArray.addAll(h);
			
			Collections.sort(tempArray);
			
			int x = 0;
			for (String s : tempArray)
			{
				if (s != null && !s.equals(""))
				{
					x++;
				}
			}
			
			list = new String[x];
			artistList.clear();
			
			for (int i = 0; i < x; i++)
			{
				list[i] = tempArray.get(i);
				artistList.add(list[i]);
			}
		}
		else
		{
			ArrayList<String> tempArray = new ArrayList<String>();
			
			for (Track t : trackList)
			{
				tempArray.add(t.GetAlbum());
				count++;
			}
			
			HashSet<String> h = new HashSet<String>(tempArray);
			tempArray.clear();
			tempArray.addAll(h);
			
			Collections.sort(tempArray);
			
			int x = 0;
			for (String s : tempArray)
			{
				if (s != null && !s.equals(""))
				{
					x++;
				}
			}
			
			list = new String[x];
			albumList.clear();
			
			for (int i = 0; i < x; i++)
			{
				list[i] = tempArray.get(i);
				albumList.add(list[i]);
			}
		}
		
		return list;
	}
	
	public String[] GetTracksByArtist(int position)
	{
		// Return Type:
		// 0 = Track
		// 1 = Artist
		// 2 = Album
		// 3 = Inside Artist
		// 4 = Inside Album
		
		if (returnType == 1)
		{
			Collections.sort(artistList);
			
			String[] list = new String[trackList.size()];
			String artistName = artistList.get(position);
			int i = 0;
			
			for (Track t : trackList)
			{
				if (artistName.contains(t.GetArtist()))
				{
					list[i] = t.GetTitle();
					i++;
				}
			}
			
			String[] compact = new String[i];
			
			for (int k = 0; k < i; k++)
			{
				if (list[k] != "" && list[k] != null)
				{
					compact[k] = list[k];
				}
			}
			
			lastUsedArtist = artistName;
			returnType = 3;
			
			return compact;
		}
		else
		{
			return null;
		}
	}
	
	public String[] GetTracksByAlbum(int position)
	{
		// Return Type:
		// 0 = Track
		// 1 = Artist
		// 2 = Album
		// 3 = Inside Artist
		// 4 = Inside Album
		
		Collections.sort(albumList);

		ArrayList<Track> newList = new ArrayList<Track>();
		String albumName = albumList.get(position);
		int numOfTracks = 0;
		
		for (Track t : trackList)
		{
			if (albumName.contains(t.GetAlbum()))
			{
				newList.add(t);
				numOfTracks++;
			}
		}
		
		Collections.sort(newList, new NumberComparator());
		
		String[] compact = new String[numOfTracks];

		albumList.clear();
		
		for (int k = 0; k < numOfTracks; k++)
		{
			String v = String.valueOf(newList.get(k).GetNumber()) + ". " + newList.get(k).GetTitle();
			
			compact[k] = v;
			albumList.add(k, v);
		}
		
		lastUsedAlbum = albumName;
		returnType = 4;
		
		Collections.sort(trackList, new NumberComparator());
		
		return compact;
	}
	
	
	// ========================================= //
	// 	Return TrackList Length
	// ========================================= //
	
	public int Length()
	{
		return trackList.size();
	}
	
	
	// ========================================= //
	// 	Return TrackList Length
	// ========================================= //
	
	public boolean IsEmpty()
	{
		if (trackList.size() < 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	// ========================================= //
	// 	List Position
	// ========================================= //
	
	public int GetPosition()
	{
		return listPosition;
	}
	
	public void SetPosition(int nlistPosition)
	{
		listPosition = nlistPosition;
	}
	
	public void IncrementPosition()
	{
		listPosition++;
	}
	
	public void DecrementPosition()
	{
		listPosition--;
	}
	
	
	// ========================================= //
	// 	DB Sync
	// ========================================= //
	
	public void DatabasePush()
	{
		if (syncDB.Exists())
		{
			syncDB.Clear();
			syncDB.Push(trackList);
		}
	}
	
	public void DatabasePull()
	{
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainContext);
	    boolean previouslyStarted = prefs.getBoolean("firstTime", false);
	    if(!previouslyStarted)
	    {
	    	SharedPreferences.Editor edit = prefs.edit();
	    	edit.putBoolean("firstTime", Boolean.TRUE);
	        edit.commit();
			trackList = syncDB.Pull();
	    }
	}
	
	public void DatabaseClear()
	{
		syncDB.Clear();
	}
	
	public void DatabaseDelete()
	{
		mainContext.deleteDatabase("TurtlePlayer");
	}
	
	public void DatabaseClose()
	{
		syncDB.close();
	}
	
	public int GetReturnType()
	{
		return returnType;
	}
}