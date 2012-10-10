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

package turtle.player.dirchooser;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;

/**
 * Compares Files and sort it like:
 * 1.) Dir (alphabetical order)
 * 2.) Files (alphabetical order)
 */
public class FileSorter implements Comparator<File>
{
	@Override
	public int compare(File lhs,
							 File rhs)
	{
		if (lhs == null)
		{
			return -1;
		}
		if (rhs == null)
		{
			return 1;
		}

		if (lhs.isDirectory())
		{
			if (rhs.isDirectory())
			{
				return nameCompare(lhs, rhs);
			} else
			{
				return -1;
			}
		} else
		{
			if (rhs.isDirectory())
			{
				return 1;
			} else
			{
				return nameCompare(lhs, rhs);
			}
		}
	}

	private int nameCompare(File lhs,
									File rhs)
	{
		return Collator.getInstance().compare(lhs.getAbsolutePath(), rhs.getAbsolutePath());
	}

}
