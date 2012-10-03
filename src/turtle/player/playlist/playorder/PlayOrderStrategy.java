package turtle.player.playlist.playorder;

import turtle.player.model.Track;

import java.util.List;
import java.util.Set;

public interface PlayOrderStrategy {

    /**
     *
     * @param tracks
     * @return null if strategy has no next song for this config and tracklist
     */
    Track getNext(Set<Track> tracks, Track currTrack);

    /**
     *
     * @param tracks
     * @return null if strategy has no previous song for this config and tracklist
     */
    Track getPrevious(Set<Track> tracks, Track currTrack);
}
