package turtle.player.playlist.playorder;

import turtle.player.model.Track;

import java.util.List;

public abstract class AbstractPlayOrderStrategy implements  PlayOrderStrategy
{
	public Track getNext(Track currTrack)
	{
		List<Track> candidates = getNext(currTrack, 1);
		return candidates.size() > 0 ? candidates.get(0) : null;
	}

	public Track getPrevious(Track currTrack)
	{
		List<Track> candidates = getPrevious(currTrack, 1);
		return candidates.size() > 0 ? candidates.get(0) : null;
	}
}
