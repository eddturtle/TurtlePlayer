package turtle.player.playlist.playorder;

import turtle.player.model.Track;
import turtle.player.playlist.Playlist;
import turtle.player.preferences.Preferences;

import java.util.*;

public class PlayOrderSorted extends Playlist.PlaylistObserverAdapter implements PlayOrderStrategy
{

    final Preferences preferences;

    Comparator<? super Track> order;

    public PlayOrderSorted(Preferences preferences, Comparator<? super Track> order)
    {
        this.preferences = preferences;
        this.order = order;
    }

    @Override
    public Track getNext(Set<Track> tracks, Track currTrack)
    {
        if(tracks.size() == 0)
        {
            return null;
        }

        List<Track> orderedList = new ArrayList<Track>(tracks);
        Collections.sort(orderedList, order);

        Track nextTrack;

        int indexOfCurrent = orderedList.indexOf(currTrack);

        //current not in list, so return first one
        if(indexOfCurrent < 0){
            return orderedList.get(0);
        }

        int indexOfNext = indexOfCurrent + 1;

        if(indexOfNext >= orderedList.size())
        {
            if(preferences.GetRepeat())
            {
                nextTrack = orderedList.get(indexOfNext % orderedList.size());
            }
            else
            {
                return null;
            }
        }
        else
        {
            nextTrack = orderedList.get(indexOfNext);
        }
        return nextTrack;
    }

    @Override
    public Track getPrevious(Set<Track> tracks, Track currTrack)
    {
        if(tracks.size() == 0)
        {
            return null;
        }

        List<Track> orderedList = new ArrayList<Track>(tracks);
        Collections.sort(orderedList, order);

        int indexOfCurrent = orderedList.indexOf(currTrack);
        if(indexOfCurrent <= 0){
            return null;
        }
        return orderedList.get(indexOfCurrent - 1);
    }
}
