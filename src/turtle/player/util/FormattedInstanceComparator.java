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

package turtle.player.util;

import turtle.player.model.Instance;
import turtle.player.presentation.InstanceFormatter;

import java.text.Collator;
import java.util.Comparator;

public class FormattedInstanceComparator implements Comparator<Instance>
{
	private final InstanceFormatter listInstanceFormatter;

	public FormattedInstanceComparator(InstanceFormatter listInstanceFormatter)
	{
		this.listInstanceFormatter = listInstanceFormatter;
	}

	@Override
	public int compare(Instance lhs,
							 Instance rhs)
	{
		return Collator.getInstance().compare(lhs.accept(listInstanceFormatter), rhs.accept(listInstanceFormatter));
	}


}
