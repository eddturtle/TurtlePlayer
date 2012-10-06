package turtle.player.playlist.playorder;

import turtle.player.model.Track;
import turtle.player.playlist.Playlist;
import turtle.player.preferences.Preferences;

import java.util.*;

public class PlayOrderSorted extends Playlist.PlaylistObserverAdapter implements PlayOrderStrategy
{

    final private Comparator<? super Track> order;

    private List<Track> sortedTracks = new ArrayList<Track>();
    Preferences preferences;
    Playlist playlist;

    PlayOrderSorted(Comparator<? super Track> order)
    {
        this.order = order;
    }

    public PlayOrderStrategy connect(Preferences preferences, Playlist playlist){

        this.playlist = playlist;
        playlist.addObserver(this);

        playlistSetted(playlist.getCurrTracks());

        this.preferences = preferences;

        return this;
    }

    public void disconnect(){
        playlist.removeObserver(this);
        sortedTracks = new ArrayList<Track>();
    }

    @Override
    public Track getNext(Track currTrack)
    {
        if(sortedTracks.size() == 0)
        {
            return null;
        }

        Track nextTrack;

        int indexOfCurrent = sortedTracks.indexOf(currTrack);

        //current not in list, so return first one
        if(indexOfCurrent < 0){
            return sortedTracks.get(0);
        }

        int indexOfNext = indexOfCurrent + 1;

        if(indexOfNext >= sortedTracks.size())
        {
            if(preferences.GetRepeat())
            {
                nextTrack = sortedTracks.get(indexOfNext % sortedTracks.size());
            }
            else
            {
                return null;
            }
        }
        else
        {
            nextTrack = sortedTracks.get(indexOfNext);
        }
        return nextTrack;
    }

    @Override
    public Track getPrevious(Track currTrack)
    {
        if(sortedTracks.size() == 0)
        {
            return null;
        }

        int indexOfCurrent = sortedTracks.indexOf(currTrack);
        if(indexOfCurrent <= 0){
            return null;
        }
        return sortedTracks.get(indexOfCurrent - 1);
    }

    @Override
    public void trackAddedToPlaylist(Track track)
    {
        //copy of: http://www.exampledepot.com/egs/java.util/coll_InsertInList.html

        // Search for the non-existent item
        int index = Collections.binarySearch(sortedTracks, track, order);

        // Add the non-existent item to the list
        if (index < 0) {
            sortedTracks.add(-index-1, track);
        }
    }

    @Override
    public void playlistSetted(Set<Track> tracks)
    {
        sortedTracks = new ArrayList<Track>(tracks);
        Collections.sort(sortedTracks, order);
    }

    @Override
    public void playlistCleaned()
    {
        playlistSetted(new HashSet<Track>());
    }


}
