/*
 * TURTLE PLAYER
 */

// Package
package turtle.player;
 

public class Preferences {

	
	// ========================================= //
	// 	Attributes
	// ========================================= //
	
	private String mediaPath;
	private boolean repeat;
	private boolean shuffle;
	
	// Not in ClassDiagram
	
	private static final String TAG = "TurtlePlayer";
	
	
	// ========================================= //
	// 	Constructor & Destructor
	// ========================================= //
	
	public Preferences(String nMediaPath, boolean nRepeat, boolean nShuffle)
	{
		mediaPath = nMediaPath;
		repeat = nRepeat;
		shuffle = nShuffle;
	}
	
	
	// ========================================= //
	// 	MediaPath
	// ========================================= //
	
	public String GetMediaPath()
	{
		return mediaPath;
	}
	
	public void SetMediaPath(String nMediaPath)
	{
		mediaPath = nMediaPath;
	}

	
	// ========================================= //
	// 	Repeat
	// ========================================= //
	
	public boolean GetRepeat()
	{
		return repeat;
	}
	
	public void SetRepeat(boolean nRepeat)
	{
		repeat = nRepeat;
	}

	
	// ========================================= //
	// 	Shuffle
	// ========================================= //
	
	public boolean GetShuffle()
	{
		return shuffle;
	}
	
	public void SetShuffle(boolean nShuffle)
	{
		shuffle = nShuffle;
	}
	
	
	// ========================================= //
	// 	Tag
	// ========================================= //
	
	public String GetTag()
	{
		return TAG;
	}
	
}
