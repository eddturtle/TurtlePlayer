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

package turtle.player.common.filefilter;

import java.io.File;

public class NameFilter extends AccessableFileFilter
{
	final String[] patterns;

	public NameFilter(String... patterns)
	{
		this.patterns = patterns;
	}

	@Override
	public boolean accept(File dir,
								 String name)
	{
		if (super.accept(dir, name))
		{
			for (String pattern : patterns)
			{
				if (name.toLowerCase().matches(pattern))
				{
					return true;
				}
			}
		}
		return false;
	}
}
