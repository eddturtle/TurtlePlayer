/*
 * 
 * TURTLE PLAYER
 * 
 * Licensed under MIT & GPL
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * Created by Edd Turtle (www.eddturtle.co.uk)
 * More Information @ www.turtle-player.co.uk
 * 
 */

// Package
package turtle.player;

import android.app.Application;
import android.content.Context;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.playlist.Playlist;

public class TurtlePlayer extends Application
{
	public final turtle.player.player.Player player = new turtle.player.player.Player();
	public Playlist playlist;
	public TurtleDatabase db;

	private static TurtlePlayer instance = null;

	public TurtlePlayer()
	{
		/*mp = new MediaPlayer();
		playlist = new Playlist();
		isPaused = true;
		isInitialised = false;
		currentlyPlaying = new Track();*/
	}

	@Override
	public void onCreate()
	{
		instance = this;
	}

	public static TurtlePlayer getStaticInstance()
	{
		if(instance == null){
			throw new IllegalStateException("Not initialized yet");
		}
		return instance;
	}
}
