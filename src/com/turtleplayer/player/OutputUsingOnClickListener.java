package com.turtleplayer.player;

import android.view.View;

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

public abstract class OutputUsingOnClickListener implements View.OnClickListener
{
	final OutputAccess outputAccess;

	public OutputUsingOnClickListener(OutputAccess outputAccess)
	{
		this.outputAccess = outputAccess;
	}

	public void onClick(final View v)
	{
		outputAccess.connectPlayer(new OutputCommand()
		{
			public void connected(Output output)
			{
				onClick(v, output);
			}
		});
	}

	public abstract void onClick(View v, Output output);
}
