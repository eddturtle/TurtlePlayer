/**
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
 * More Information @ www.turtle-player.co.uk
 *
 * @author Simon Honegger (Hoene84)
 */

package turtle.player.playlist.filter;

import turtle.player.playlist.Playlist;
import turtle.player.model.*;

public class ChildsFilter implements PlaylistFilter
{

	final Instance instance;
	final Playlist playlist;

	public ChildsFilter(Instance instance,
							  Playlist playlist)
	{
		this.instance = instance;
		this.playlist = playlist;
	}

	@Override
	public boolean accept(final Track candidate)
	{
		if (instance == null)
		{
			return true;
		}

		return instance.getChilds(playlist.getTracks(PlaylistFilter.ALL)).contains(candidate);
	}

	@Override
	public int getForce()
	{
		return 2;
	}
}
