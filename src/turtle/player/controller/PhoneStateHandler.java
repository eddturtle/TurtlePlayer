package turtle.player.controller;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
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

public class PhoneStateHandler extends PhoneStateListener
{
	private final Player player;

	private boolean wasPlaying = false;

	public PhoneStateHandler(Player player)
	{
		this.player = player;
	}

	@Override
	public void onCallStateChanged(int state,
											 String incomingNumber)
	{
		if (state == TelephonyManager.CALL_STATE_RINGING)
		{
			wasPlaying = player.pause();
		}
		else if (state == TelephonyManager.CALL_STATE_IDLE)
		{
			if(wasPlaying)
			{
				player.play();
			}
		}
		super.onCallStateChanged(state, incomingNumber);
	}
}
