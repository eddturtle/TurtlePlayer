package turtle.player.playlist.playorder;

import turtle.player.model.Track;
import turtle.player.playlist.Playlist;
import turtle.player.preferences.Preferences;
import turtle.player.util.GenericInstanceComperator;

import java.util.List;
import java.util.Set;

public interface PlayOrderStrategy {

    PlayOrderRandom RANDOM = new PlayOrderRandom();
    PlayOrderSorted SORTED = new PlayOrderSorted(new GenericInstanceComperator());

    /*
     * @return null if strategy has no next song for this config and tracklist
     */
    Track getNext(Track currTrack);

    /**
     * @return null if strategy has no previous song for this config and tracklist
     */
    Track getPrevious(Track currTrack);

    /**
     * Has to be called before adding this strategy. Allows the implementation to initialize.
     */
    public PlayOrderStrategy connect(Preferences preferences, Playlist playlist);

    /**
     * Has to be called after removing this strategy. Allows the implementation to cleanup.
     */
    void disconnect();
}
