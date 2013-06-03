package com.turtleplayer.persistance.framework.filter;

import java.util.Arrays;
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

public class FilterSet<PROJECTION> implements Filter<PROJECTION>
{
	private final Set<Filter<? super PROJECTION>> filters;

	public FilterSet(Filter<? super PROJECTION>... filter)
	{
		this.filters = new HashSet<Filter<? super PROJECTION>>(Arrays.asList(filter));
	}

	public FilterSet(Set<? extends Filter<? super PROJECTION>> filters)
	{
		this.filters = new HashSet<Filter<? super PROJECTION>>(filters);
	}

	public <R> R accept(FilterVisitor<? extends PROJECTION, R> visitor)
	{
		return visitor.visit(this);
	}

	/**
	 * @return never null, Set can be empty
	 */
	public Set<Filter<? super PROJECTION>> getFilters()
	{
		return filters;
	}

	public boolean makesObsolete(Filter<?> filter)
	{
		return false;  //Not implemented yet
	}

	@Override
	public String toString()
	{
		return Arrays.deepToString(filters.toArray());
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof FilterSet)) return false;

		FilterSet filterSet = (FilterSet) o;

		if (filters != null ? !filters.equals(filterSet.filters) : filterSet.filters != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		return filters != null ? filters.hashCode() : 0;
	}
}
