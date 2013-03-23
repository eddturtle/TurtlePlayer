package turtle.player.playlist.playorder;

import turtle.player.model.Track;
import turtle.player.model.TrackBundle;

import java.util.List;

public interface PlayOrderStrategy {

	/**
	 * @return empty list if strategy has no next song for this config and tracklist
	 */
	 List<Track> getNext(Track currTrack, int n);

    /**
     * @return empty list if strategy has no previous song for this config and tracklist
     */
	 List<Track> getPrevious(Track currTrack, int n);

	/*
     * @return null if strategy has no next song for this config and tracklist
     */
	Track getNext(Track currTrack);

	/**
	 * @return null if strategy has no previous song for this config and tracklist
	 */
	Track getPrevious(Track currTrack);

}
