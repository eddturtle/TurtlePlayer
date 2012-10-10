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
import java.util.*;
import java.io.File;

// Import - Android Log
import android.util.Log;

// Import - Android Media

// Import - Android Context
import android.content.Context;
import turtle.player.persistance.Database;
import turtle.player.Stats;
import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Track;
import turtle.player.persistance.FsReader;
import turtle.player.playlist.filter.Filters;
import turtle.player.playlist.playorder.PlayOrderStrategy;
import turtle.player.playlist.filter.PlaylistFilter;
import turtle.player.preferences.Key;
import turtle.player.preferences.Keys;
import turtle.player.preferences.Preferences;
import turtle.player.preferences.PreferencesObserver;
import turtle.player.util.dev.PerformanceMeasure;

public class Playlist {

    //Log Constants
    private enum durations
    {
        NEXT,
        PREV,
        PULL
    }

    // Not in ClassDiagram
    public Preferences preferences;
    public Stats stats = new Stats();

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

        syncDB.addObserver(new Database.DbObserver(){
            @Override
            public void trackAdded(Track track)
            {
                AddTrack(track);
            }

            @Override
            public void cleaned()
            {
                ClearList();
            }
        });

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

    public Track getNext()
    {
        PerformanceMeasure.start(durations.NEXT.name());

        Track track = playOrderStrategy.getNext(getCurrTrack());

        PerformanceMeasure.stop(durations.NEXT.name());

        return track;
    }

    public Track getPrevious()
    {
        PerformanceMeasure.start(durations.PREV.name());

        Track track = playOrderStrategy.getPrevious(getCurrTrack());

        PerformanceMeasure.stop(durations.PREV.name());

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
                    if (syncDB.isEmpty()) {
                        try {
                            final File mediaPath = preferences.GetMediaPath();

                            for (PlaylistObserver observer : observers) {
                                observer.startRescan(mediaPath);
                            }

                            FsReader.scanDir(syncDB, mediaPath);

                        } catch (NullPointerException e) {
                            Log.v(preferences.GetTag(), e.getMessage());
                        } finally {
                            for (PlaylistObserver observer : observers) {
                                observer.endRescan();
                            }
                        }
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
	
	public void DatabasePull()
	{
        ClearList();

        PerformanceMeasure.start(durations.PULL.name());

        for(Track track : syncDB.pull())
        {
            AddTrack(track);
        }

        PerformanceMeasure.stop(durations.PULL.name());
	}
	
	public void DatabaseClear()
	{
		syncDB.clear();
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