package com.turtleplayer.persistance.framework.filter;

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

public class NotFilter<PROJECTION> implements Filter<PROJECTION>
{
	private final Filter<? super PROJECTION> filter;

	public NotFilter(Filter<? super PROJECTION> filter)
	{
		this.filter = filter;
	}

	public <R> R accept(FilterVisitor<? extends PROJECTION, R> visitor)
	{
		return visitor.visit(this);
	}

	public Filter<? super PROJECTION> getFilter()
	{
		return filter;
	}

	@Override
	public String toString()
	{
		return " NOT (" + filter.toString() + ") ";
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof NotFilter)) return false;

		NotFilter notFilter = (NotFilter) o;

		if (filter != null ? !filter.equals(notFilter.filter) : notFilter.filter != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		return filter != null ? filter.hashCode() : 0;
	}

}
