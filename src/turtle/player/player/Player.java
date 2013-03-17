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

/**
 * Access for native Android Media player.<br />
 *
 * take a look at: http://developer.android.com/reference/android/media/MediaPlayer.html#StateDiagram
 * <br /><br />
 * {@link #getMp()} garantees media player is at least in idle state<br />
 * {@link #initialized} garantees media player is at least in initialized state<br />
 */
public class Player
{

	private final List<PlayerObserver> observers = new ArrayList<PlayerObserver>();

	private MediaPlayer mp = null; //use getMp to access plz
	private boolean isPlaying = false;
	private Track currTrack = null;

	private boolean initialized = false; //indicates the player is at least in Initialized mode

	public Player()
	{
		addObserver(new PlayerObserver()
		{
			public void trackChanged(Track track, int lengthInMillis)
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

	private void prepare(Track t)
	{
		if (t != null)
		{
			try
			{
				final MediaPlayer mediaPlayer = getMp();
				mp.reset();
				mediaPlayer.setDataSource(t.GetSrc());

				mediaPlayer.prepare();
				mediaPlayer.getDuration();
				initialized = true;
				notifyStopped();
				notifyTrackChanged(t, mediaPlayer.getDuration());
			}
			catch (IOException e)
			{
				Log.v(Preferences.TAG, e.getMessage());
			}
		}
	}

	public void change(Track t){
		boolean wasPlaying = isPlaying;
		prepare(t);

		if(wasPlaying){
			play();
		}
	}

	public void play(Track t)
	{
		prepare(t);
		play();
	}

	public void toggle()
	{
		if(initialized)
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
	}

	/**
	 * @return true if this call had an effect
	 */
	public boolean pause()
	{
		if(initialized && isPlaying)
		{
			getMp().pause();
			notifyStopped();
			return true;
		}
		return false;
	}

	/**
	 * @return true if this call had an effect
	 */
	public boolean play(){
		if(!isPlaying && initialized)
		{
			getMp().start();
			notifyStarted();
			return true;
		}
		return false;
	}

	public void goToMillis(int millis)
	{
		if(initialized)
		{
			getMp().seekTo(Math.max(Math.min(millis, getMp().getDuration()), 0));
		}
	}

	public int getCurrentMillis()
	{
		return initialized ? getMp().getCurrentPosition() : 0;
	}

	public Track getCurrTrack()
	{
		return currTrack;
	}

	public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener)
	{
		getMp().setOnCompletionListener(listener);
	}

	/**
	 * @return MediaPlayer at least in idle state
	 */
   private MediaPlayer getMp(){
		if(mp == null)
		{
			mp = new MediaPlayer();
			mp.reset();
		}
		return mp;
	}

	/**
	 * releases the mp and kill the reference because the old instance is not usable anymore
	 */
	public void release()
	{
		initialized = false;
		pause();
		setOnCompletionListener(null);
		getMp().release();
		mp = null;
	}

	//---------------------------------- Observable

	private void notifyTrackChanged(Track track, int lengthInMillis){
		for(PlayerObserver observer : observers){
			observer.trackChanged(track, lengthInMillis);
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
		void trackChanged(Track track, int lengthInMillis);
		void started();
		void stopped();
	}
}
