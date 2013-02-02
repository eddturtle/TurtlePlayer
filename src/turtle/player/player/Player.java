package turtle.player.player;

import android.media.MediaPlayer;
import android.util.Log;
import turtle.player.model.Track;
import turtle.player.preferences.Preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TURTLE PLAYER
 * <p/>
 * Licensed under MIT & GPL
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 * <p/>
 * More Information @ www.turtle-player.co.uk
 *
 * @author Simon Honegger (Hoene84)
 */

public class Player
{

	private final MediaPlayer mp = new MediaPlayer();
	private final List<PlayerObserver> observers = new ArrayList<PlayerObserver>();

	private boolean isPlaying = false;
	private Track currTrack = null;

	public Player()
	{
		addObserver(new PlayerObserver()
		{
			public void trackChanged(Track track)
			{
				currTrack = track;
			}

			public void started()
			{
				isPlaying = true;
			}

			public void stopped()
			{
				isPlaying = false;
			}
		});
	}

	public void play(Track t)
	{
		if (t != null)
		{
			try
			{
				mp.reset();
				mp.setDataSource(t.GetSrc());

				notifyTrackChanged(t);

				mp.prepare();

				mp.start();

				notifyStarted();
			}
			catch (IOException e)
			{
				Log.v(Preferences.TAG, e.getMessage());
			}
		}
	}

	public void toggle()
	{
		if(isPlaying)
		{
			pause();
		}
		else
		{
			play();
		}
	}

	/**
	 * @return true if this call had an effect
	 */
	public boolean pause()
	{
		if(isPlaying)
		{
			mp.pause();
			notifyStopped();
			return true;
		}
		return false;
	}

	/**
	 * @return true if this call had an effect
	 */
	public boolean play(){
		if(!isPlaying)
		{
			mp.start();
			notifyStarted();
			return true;
		}
		return false;
	}

	public void goToMillis(int millis)
	{
		mp.seekTo(millis);
	}

	public int getCurrentMillis()
	{
		return mp.getCurrentPosition();
	}

	public Track getCurrTrack()
	{
		return currTrack;
	}

	public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener)
	{
		mp.setOnCompletionListener(listener);
	}

	//---------------------------------- Observable

	private void notifyTrackChanged(Track track){
		for(PlayerObserver observer : observers){
			observer.trackChanged(track);
		}
	}

	private void notifyStarted(){
		for(PlayerObserver observer : observers){
			observer.started();
		}
	}

	private void notifyStopped(){
		for(PlayerObserver observer : observers){
			observer.stopped();
		}
	}

	public void addObserver(PlayerObserver observer)
	{
		observers.add(observer);
	}

	public void removeObserver(PlayerObserver observer)
	{
		observers.remove(observer);
	}

	public interface PlayerObserver
	{
		void trackChanged(Track track);
		void started();
		void stopped();
	}
}
