package turtle.player.playlist;

import turtle.player.model.Track;

import java.util.List;

public interface PlayOrderStrategy {

    /**
     *
     * @param tracks
     * @return null if strategy has no next song for this config and tracklist
     */
    Track getNext(List<Track> tracks, Track currTrack);

    /**
     *
     * @param tracks
     * @return null if strategy has no previous song for this config and tracklist
     */
    Track getPrevious(List<Track> tracks, Track currTrack);
}
