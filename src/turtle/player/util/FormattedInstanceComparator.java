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

import turtle.player.model.*;
import turtle.player.presentation.InstanceFormatter;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class FormattedInstanceComparator implements Comparator<Instance>
{
	private final InstanceFormatter listInstanceFormatter;



	public FormattedInstanceComparator(InstanceFormatter listInstanceFormatter)
	{
		this.listInstanceFormatter = listInstanceFormatter;
	}

	public int compare(Instance lhs,
							 Instance rhs)
	{
		int lhsWeight = getOrderWeight(lhs);
		int rhsWeight = getOrderWeight(rhs);
		int weightDiff = new Integer(lhsWeight).compareTo(rhsWeight);
		return weightDiff != 0 ? weightDiff :
				  Collator.getInstance().compare(lhs.accept(listInstanceFormatter), rhs.accept(listInstanceFormatter));
	}

	private int getOrderWeight(Instance instance)
	{
		return instance.accept(new InstanceVisitor<Integer>()
		{
			public Integer visit(Track track)
			{
				return 5;
			}

			public Integer visit(SongDigest track)
			{
				return 5;
			}

			public Integer visit(Album album)
			{
				return 4;
			}

			public Integer visit(GenreDigest genre)
			{
				return 2;
			}

			public Integer visit(ArtistDigest artist)
			{
				return 3;
			}

			public Integer visit(FSobject FSobject)
			{
				return 1;
			}
		});
	}
}
