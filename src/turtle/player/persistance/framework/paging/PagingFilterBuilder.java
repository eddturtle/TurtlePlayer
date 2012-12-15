package turtle.player.persistance.framework.paging;

import turtle.player.persistance.framework.filter.FieldFilter;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.filter.FilterSet;
import turtle.player.persistance.framework.filter.Operator;
import turtle.player.persistance.framework.sort.FieldOrder;
import turtle.player.persistance.framework.sort.OrderSet;
import turtle.player.persistance.framework.sort.OrderVisitor;
import turtle.player.persistance.framework.sort.RandomOrder;
import turtle.player.persistance.source.relational.FieldPersistable;

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

public class PagingFilterBuilder<I, W, Q> implements OrderVisitor<I, Filter<W>, Q>
{
	final I instance;

	public PagingFilterBuilder(I instance)
	{
		this.instance = instance;
	}

	public <Q, T> Filter<W> visit(FieldOrder<I, T, Q> fieldOrder)
	{
		FieldPersistable<I, ?> field = fieldOrder.getField();

		final Operator op;

		switch(fieldOrder.getOrder()){
			case ASC:
				op = Operator.GT;
				break;
			case DESC:
				op = Operator.LT;
				break;
		   default:
				throw new IllegalArgumentException();
		}
		return new FieldFilter<W>(field, op, field.get(instance).toString());
	}

	public Filter<W> visit(RandomOrder<Q> orderFilter)
	{
		return null;
	}

	public Filter<W> visit(OrderSet<Q> orderFilter)
	{
		if(!orderFilter.isEmpty()){
			Filter<W> filterSet = new FilterSet<W>();
			for( int i = 0; i < orderFilter.getOrders().size() -1; i++)
			{
				final Filter<W> finalFilterSet = filterSet;
				filterSet = orderFilter.getOrders().get(i).accept(new OrderVisitor<I, Filter<W>, Q>()
				{
					public Filter<W> visit(RandomOrder<Q> orderFilter)
					{
						// :-)
						return null;
					}

					public <Q, T> Filter<W> visit(FieldOrder<I, T, Q> fieldOrder)
					{
						FieldPersistable<I, T> field = fieldOrder.getField();
						return new FilterSet<W>(
								  finalFilterSet,
								  new FieldFilter<W>(fieldOrder.getField(), Operator.EQ, field.get(instance).toString()));
					}

					public Filter<W> visit(OrderSet<Q> orderFilter)
					{
						return this.visit(orderFilter);
					}
				});
			}

			return new FilterSet<W>(filterSet, orderFilter.getOrders().get(orderFilter.getOrders().size()-1).accept(this));
		}
		else
		{
			return null;
		}
	}

}
