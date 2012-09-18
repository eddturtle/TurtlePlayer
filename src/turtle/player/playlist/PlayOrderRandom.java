package turtle.player.playlist;

import turtle.player.model.Track;
import turtle.player.preferences.Preferences;

import java.util.ArrayList;
import java.util.List;

public class PlayOrderRandom implements PlayOrderStrategy {

    final LimitedStack<Track> history = new LimitedStack<Track>(1000);

    final Preferences preferences;

    public PlayOrderRandom(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public Track getNext(List<Track> tracks, Track currTrack) {
        List<Track> candidates = new ArrayList<Track>(tracks);
        candidates.removeAll(history);

        final Track nextTrack;

        if(!candidates.isEmpty())
        {
            nextTrack = candidates.get((int)((Math.random() * candidates.size())));
        }
        else
        {
            if(preferences.GetRepeat())
            {
                nextTrack = tracks.get((int) ((Math.random() * tracks.size())));
            }
            else
            {
                return null;
            }
        }

        history.add(currTrack);
        return nextTrack;
    }

    @Override
    public Track getPrevious(List<Track> tracks, Track currTrack)
    {
        while(history.size() > 0){
            Track candidate = history.pop();
            if(tracks.contains(candidate))
            {
                return candidate;
            }
        }
        return null;
    }
}
