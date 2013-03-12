package turtle.player.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import turtle.player.player.Player;

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
public class BroadcastsHandler extends BroadcastReceiver
{
	private boolean headsetConnected = false;
	private Player player;

	public BroadcastsHandler(Player player)
	{
		this.player = player;
	}

	public void onReceive(Context context, Intent intent)
	{
		if (intent.hasExtra("state"))
		{
			if (headsetConnected && intent.getIntExtra("state", 0) == 0)
			{
				headsetConnected = false;
				Toast.makeText(context, "Headphones UnPlugged", Toast.LENGTH_LONG).show();
				player.pause();
			}
			else if (!headsetConnected && intent.getIntExtra("state", 0) == 1)
			{
				headsetConnected = true;
				player.play();
			}
		}
	}
}
