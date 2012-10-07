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
package turtle.player.playlist;

// Import - Java
import java.io.FileFilter;
import java.util.*;
import java.io.File;
import java.io.FilenameFilter;

// Import - Android Log
import android.util.Log;

// Import - Android Media
import android.media.MediaMetadataRetriever;

// Import - Android Context
import android.content.Context;
import turtle.player.Database;
import turtle.player.Stats;
import turtle.player.common.filefilter.FileFilters;
import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Track;
import turtle.player.playlist.filter.Filters;
import turtle.player.playlist.playorder.PlayOrderStrategy;
import turtle.player.playlist.filter.PlaylistFilter;
import turtle.player.playlist.playorder.PlayOrderRandom;
import turtle.player.playlist.playorder.PlayOrderSorted;
import turtle.player.preferences.Key;
import turtle.player.preferences.Keys;
import turtle.player.preferences.Preferences;
import turtle.player.preferences.PreferencesObserver;
import turtle.player.util.GenericInstanceComperator;
import turtle.player.util.Shorty;
import turtle.player.util.dev.PerformanceMeasure;

public class Playlist {

    //Log Constants
    private enum durtions{
        NEXT,
        PREV,
        PULL
    }

    // Not in ClassDiagram
    public Preferences preferences;
    public Stats stats = new Stats();

    public static final int MAX_DIR_SCAN_DEPTH = 50;
    private Filters filters = new Filters();

    private PlayOrderStrategy playOrderStrategy;

	private Set<Track> trackList = new HashSet<Track>();
	private Set<Track> filteredTrackList = new HashSet<Track>();

	private Database syncDB;

    private Track currTrack = null;

    public Playlist(Context mainContext)
	{

        // Location, Repeat, Shuffle (Remember Trailing / on Location)
		preferences = new Preferences(mainContext);
        syncDB = new Database(mainContext);
        init();
	}

    private void init(){
        preferences.addObserver(new PreferencesObserver()
        {
            @Override
            public void changed(Key key)
            {
                if(key.equals(Keys.SHUFFLE))
                {
                    setPlayOrderStrategyAccordingPreferences();
                }
            }
        });

        filters.addObserver(new Filters.FilterObserver()
        {
            @Override
            public void filterChanged()
            {
                filteredTrackList = new HashSet<Track>();
                for(PlaylistObserver observer : observers){
                    observer.playlistCleaned();
                }

                filteredTrackList = filters.getValidTracks(trackList);

                for(PlaylistObserver observer : observers){
                    observer.playlistSetted(filteredTrackList);
                }
            }
        });
        setPlayOrderStrategyAccordingPreferences();
    }

    private void setPlayOrderStrategyAccordingPreferences(){
        if(playOrderStrategy != null)
        {
            playOrderStrategy.disconnect();
        }
        playOrderStrategy = preferences.GetShuffle() ?
                PlayOrderStrategy.RANDOM.connect(preferences, this) :
                PlayOrderStrategy.SORTED.connect(preferences, this);
    }
	
	private void AddTrack(Track nTrack)
	{
		trackList.add(nTrack);
        for(PlaylistObserver observer : observers){
            observer.trackAdded(nTrack);
        }
        if(filters.isValidAccordingFilters(nTrack)){
            filteredTrackList.add(nTrack);

            for(PlaylistObserver observer : observers){
                observer.trackAddedToPlaylist(nTrack);
            }
        }
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

    public Track getNext()
    {
        PerformanceMeasure.start(durtions.NEXT.name());

        Track track = playOrderStrategy.getNext(getCurrTrack());

        PerformanceMeasure.stop(durtions.NEXT.name());

        return track;
    }

    public Track getPrevious()
    {
        PerformanceMeasure.start(durtions.PREV.name());

        Track track = playOrderStrategy.getPrevious(getCurrTrack());

        PerformanceMeasure.stop(durtions.PREV.name());

        return track;
    }

    public void UpdateList() {
        new Thread(new Runnable() {
            public void run() {
                for (PlaylistObserver observer : observers) {
                    observer.startUpdatePlaylist();
                }

                ClearList();

                try {
                    if (syncDB.IsEmpty()) {
                        try {
                            final File mediaPath = preferences.GetMediaPath();

                            for (PlaylistObserver observer : observers) {
                                observer.startRescan(mediaPath);
                            }

                            CheckDir(mediaPath);

                        } catch (NullPointerException e) {
                            Log.v(preferences.GetTag(), e.getMessage());
                        } finally {
                            for (PlaylistObserver observer : observers) {
                                observer.endRescan();
                            }
                        }

                        syncDB = new Database(preferences.getContext());
                        DatabasePush();
                    } else {
                        DatabasePull();
                    }
                } finally {
                    for (PlaylistObserver observer : observers) {
                        observer.endUpdatePlaylist();
                    }
                }
            }
        }).start();
    }

    private void CheckDir(File rootNode){
        MediaMetadataRetriever metaDataReader = new MediaMetadataRetriever();
        CheckDir(metaDataReader, rootNode, 0, null);
        metaDataReader.release();
    }

    /**
     * @param rootNode
     * @param depth number of parent allready visited
     */
	private void CheckDir(MediaMetadataRetriever metaDataReader, File rootNode, int depth, String parentAlbumArt)
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
                Log.v(preferences.GetTag(), "register " + rootNode + "/" + mp3);
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
				
				this.AddTrack(t);
			}
			
			for (File dir : rootNode.listFiles(isDIR))
			{
                if(depth < MAX_DIR_SCAN_DEPTH) // avoid Stack overflow - symbolic link points to a parent dir
                {
                    CheckDir(metaDataReader, dir, depth + 1, albumArt);
                }
			}
		}
		catch (NullPointerException e)
		{
			// Probably No SD-Card
			Log.v(preferences.GetTag(), e.getMessage());
		}
	}

    /**
     * calls {@link MediaMetadataRetriever#extractMetadata(int)} and removes
     * chunk after 0 terminated string
     *
     * @param keyCode see {@link MediaMetadataRetriever#extractMetadata(int)}
     * @return
     */
    String extractMetadata(MediaMetadataRetriever metaDataReader, int keyCode)
    {
        String metaData = Shorty.avoidNull(metaDataReader.extractMetadata(keyCode));

        //replace all chars exept letters and digits, space and dash
        //return metaData.replaceAll("^\\w\\s-,:;?$[]\"]","");

        int indexOfZeroTermination = metaData.indexOf(0);
        return indexOfZeroTermination < 0 ? metaData : metaData.substring(0, indexOfZeroTermination);
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

    private String getAlbumArt(File dir)
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

    public void ClearList()
	{
		trackList.clear();
        for(PlaylistObserver observer : observers){
            observer.cleaned();
        }
	}

	public Set<Track> getCurrTracks()
	{
        return filteredTrackList;
	}

    public Set<Track> getTracks(PlaylistFilter filter)
    {
        Set<PlaylistFilter> filters = new HashSet<PlaylistFilter>();
        filters.add(filter == null ? PlaylistFilter.ALL : filter);
        return getTracks(filters);
    }

    public Set<Track> getTracks(Set<PlaylistFilter> filters)
    {
        Set<Track> tracks = new HashSet<Track>();

        for(Track track : trackList){
            if(Filters.isValidAccordingFilters(track, filters)){
                tracks.add(track);
            }
        }

        return tracks;
    }

    public Set<Album> getAlbums(PlaylistFilter filter)
    {
        Set<PlaylistFilter> filters = new HashSet<PlaylistFilter>();
        filters.add(filter == null ? PlaylistFilter.ALL : filter);
        return getAlbums(filters);
    }

    public Set<Album> getAlbums(Set<PlaylistFilter> filters)
    {
        Set<Album> albums = new HashSet<Album>();

        for(Track track : trackList){
            if(Filters.isValidAccordingFilters(track, filters)){
                albums.add(track.GetAlbum());
            }
        }

        return albums;
    }

    public Set<Album> getArtists(PlaylistFilter filter)
    {
        Set<PlaylistFilter> filters = new HashSet<PlaylistFilter>();
        filters.add(filter == null ? PlaylistFilter.ALL : filter);
        return getAlbums(filters);
    }

    public Set<Artist> getArtists(Set<PlaylistFilter> filters)
    {
        Set<Artist> artists = new HashSet<Artist>();

        for(Track track : trackList)
        {
            if(Filters.isValidAccordingFilters(track, filters)){
                artists.add(track.GetArtist());
            }
        }

        return artists;
    }

    public Track getCurrTrack()
    {
        return currTrack;
    }

    public void setCurrTrack(Track currTrack)
    {
        this.currTrack = currTrack;
    }

    public int Length()
	{
		return getCurrTracks().size();
	}

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
	// 	DB Sync
	// ========================================= //
	
	public void DatabasePush()
	{
        syncDB.Clear();
        syncDB.Push(trackList);
	}
	
	public void DatabasePull()
	{
        ClearList();

        PerformanceMeasure.start(durtions.PULL.name());

        for(Track track : syncDB.Pull())
        {
            AddTrack(track);
        }

        PerformanceMeasure.stop(durtions.PULL.name());
	}
	
	public void DatabaseClear()
	{
		syncDB.Clear();
	}

    // ========================================= //
    // 	Observable
    // ========================================= //

    List<PlaylistObserver> observers = new ArrayList<PlaylistObserver>();

    public interface PlaylistObserver{
        void trackAdded(Track track);
        void cleaned();
        void startRescan(File mediaPath);
        void endRescan();
        void startUpdatePlaylist();
        void endUpdatePlaylist();
        void trackAddedToPlaylist(Track track);
        void playlistSetted(Set<Track> tracks);
        void playlistCleaned();
    }

    public static class PlaylistObserverAdapter implements  PlaylistObserver{
        @Override
        public void trackAdded(Track track) {
            //do nothing
        }

        @Override
        public void cleaned() {
            //do nothing
        }

        @Override
        public void startRescan(File mediaPath) {
            //do nothing
        }

        @Override
        public void endRescan() {
            //do nothing
        }

        @Override
        public void startUpdatePlaylist() {
            //do nothing
        }

        @Override
        public void endUpdatePlaylist() {
            //do nothing
        }

        @Override
        public void trackAddedToPlaylist(Track track)
        {
            //do nothing
        }

        @Override
        public void playlistSetted(Set<Track> tracks)
        {
            //do nothing
        }

        @Override
        public void playlistCleaned()
        {
            //do nothing
        }
    }

    public void addObserver(PlaylistObserver observer){
        observers.add(observer);
    }

    public void removeObserver(PlaylistObserver observer){
        observers.remove(observer);
    }

}