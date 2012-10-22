package turtle.player.persistance.framework.filter;

import java.util.HashSet;
import java.util.Set;

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

public class FilterSet<Q> implements Filter<Q>
{
	private final Set<Filter<Q>> filters = new HashSet<Filter<Q>>();

	public FilterSet(Filter<Q>... filter)
	{
		for(Filter<Q> currFilter : filter)
		{
			filters.add(currFilter);
		}
	}

	public FilterSet(Set<Filter<Q>> filters)
	{
		filters.addAll(filters);
	}

	public Q accept(FilterVisitor<Q> visitor)
	{
		return visitor.visit(this);
	}

	/**
	 * @return never null, Set can be empty
	 */
	public Set<Filter<Q>> getFilters()
	{
		return filters;
	}
}
