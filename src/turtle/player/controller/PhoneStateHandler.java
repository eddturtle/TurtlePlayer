package turtle.player.controller;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import turtle.player.player.Output;
import turtle.player.player.OutputAccess;
import turtle.player.player.OutputCommand;

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
	private final OutputAccess outputAccess;

	private boolean wasPlaying = false;

	public PhoneStateHandler(OutputAccess outputAccess)
	{
		this.outputAccess = outputAccess;
	}

	@Override
	public void onCallStateChanged(int state,
											 String incomingNumber)
	{
		OutputCommand outputCommand = null;
		if (state == TelephonyManager.CALL_STATE_RINGING)
		{
			outputCommand = new OutputCommand()
			{
				public void connected(Output output)
				{
					wasPlaying = output.pause();
				}
			};
		}
		else if (state == TelephonyManager.CALL_STATE_IDLE)
		{
			if(wasPlaying)
			{
				outputCommand = new OutputCommand()
				{
					public void connected(Output output)
					{
						output.play();
					}
				};
			}
		}
		if(outputCommand != null)
		{
			outputAccess.connectPlayer(outputCommand);
		}
		super.onCallStateChanged(state, incomingNumber);
	}
}
