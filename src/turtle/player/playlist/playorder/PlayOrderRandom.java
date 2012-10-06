package turtle.player.playlist.playorder;

import turtle.player.model.Track;
import turtle.player.playlist.Playlist;
import turtle.player.preferences.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlayOrderRandom implements PlayOrderStrategy
{

    final LimitedStack<Track> history = new LimitedStack<Track>(1000);

    private Preferences preferences;
    private Playlist playlist;

    public PlayOrderStrategy connect(Preferences preferences, Playlist playlist){
        this.preferences = preferences;
        this.playlist = playlist;
        return this;
    }

    @Override
    public void disconnect()
    {
        //empty
    }

    @Override
    public Track getNext(Track currTrack) {

        List<Track> candidates = new ArrayList<Track>(playlist.getCurrTracks());
        List<Track> notPlayedCandidates = new ArrayList<Track>(playlist.getCurrTracks());

        notPlayedCandidates.removeAll(history);

        final Track nextTrack;

        if(!notPlayedCandidates.isEmpty())
        {
            nextTrack = notPlayedCandidates.get((int)((Math.random() * notPlayedCandidates.size())));
        }
        else
        {
            if(preferences.GetRepeat())
            {
                nextTrack = new ArrayList<Track>(candidates).get((int) ((Math.random() * candidates.size())));
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
    public Track getPrevious(Track currTrack)
    {
        while(history.size() > 0){
            Track candidate = history.pop();
            if(playlist.getCurrTracks().contains(candidate))
            {
                return candidate;
            }
        }
        return null;
    }
}
