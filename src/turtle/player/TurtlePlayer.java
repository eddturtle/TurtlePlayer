/*
 * TURTLE PLAYER
 */

// Package
package turtle.player;

// Import - Android Libs
import android.app.Application;
import android.media.MediaPlayer;


public class TurtlePlayer extends Application 
{
	public MediaPlayer mp = new MediaPlayer();
	public Playlist playlist = new Playlist();

	public boolean isPaused = true;
	public boolean isInitialised = false;
	
	public Track currentlyPlaying = new Track();
}
