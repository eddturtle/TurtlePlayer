package turtle.player.common;

import turtle.player.model.Instance;
import turtle.player.persistance.framework.filter.FieldFilter;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.filter.FilterSet;
import turtle.player.persistance.framework.filter.FilterVisitor;
import turtle.player.persistance.framework.sort.FieldOrder;

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

public class MatchFilterVisitor<I> implements FilterVisitor<I, Boolean>
{
	private final I instance;

	public MatchFilterVisitor(I instance)
	{
		this.instance = instance;
	}

	public <T> Boolean visit(FieldFilter<I, T> fieldFilter)
	{
		String instanceValue = fieldFilter.getField().getAsString(instance);

		switch (fieldFilter.getOperator())
		{
			case EQ:
				return instanceValue.equals(fieldFilter.getFieldValue());
			case GT:
				return instanceValue.compareTo(fieldFilter.getFieldValue()) > 0;
			case LE:
				return instanceValue.compareTo(fieldFilter.getFieldValue()) <= 0;
			case GE:
				return instanceValue.compareTo(fieldFilter.getFieldValue()) >= 0;
			case LIKE:
				return instanceValue.contains(fieldFilter.getFieldValue());
			case LT:
				return instanceValue.compareTo(fieldFilter.getFieldValue()) < 0;
		}
		throw new RuntimeException("Operator " + fieldFilter.getOperator() + " is not supported");
	}

	public Boolean visit(FilterSet filterSet)
	{
		for(Filter filter : filterSet.getFilters())
		{
			if(!filter.accept(this))
			{
				return false;
			}
		}
		return true;
	}
}
