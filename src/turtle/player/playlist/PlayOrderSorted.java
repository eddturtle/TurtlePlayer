package turtle.player.playlist;

import turtle.player.Playlist;
import turtle.player.model.Track;
import turtle.player.preferences.Preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    public Track getNext(List<Track> tracks, Track currTrack)
    {
        if(tracks.size() == 0)
        {
            return null;
        }

        List<Track> orderedList = new ArrayList<Track>(tracks);
        Collections.sort(orderedList, order);

        Track nextTrack;

        int indexOfCurrent = orderedList.indexOf(currTrack);

        //current not in list, so retrun first one
        if(indexOfCurrent < 0){
            return tracks.get(0);
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
    public Track getPrevious(List<Track> tracks, Track currTrack)
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
