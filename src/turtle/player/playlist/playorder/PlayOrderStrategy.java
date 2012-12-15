package turtle.player.playlist.playorder;

import turtle.player.model.Track;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.playlist.Playlist;
import turtle.player.preferences.Preferences;

public interface PlayOrderStrategy {

    /*
     * @return null if strategy has no next song for this config and tracklist
     */
    Track getNext(Track currTrack);

    /**
     * @return null if strategy has no previous song for this config and tracklist
     */
    Track getPrevious(Track currTrack);

}
