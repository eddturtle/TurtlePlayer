package turtle.player.persistance.framework.paging;

import turtle.player.persistance.framework.filter.FieldFilter;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.filter.FilterSet;
import turtle.player.persistance.framework.filter.Operator;
import turtle.player.persistance.framework.sort.*;
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

public class PagingFilterBuilder<TARGET, RESULT> extends OrderVisitorGenerified<TARGET, RESULT, Object, Filter<TARGET>>
{
	final RESULT instance;

	public PagingFilterBuilder(RESULT instance)
	{
		this.instance = instance;
	}

	@Override
	public Filter<TARGET> visit(FieldOrder<TARGET, RESULT, Object> fieldOrder,
										 FieldPersistable<RESULT, Object> field)
	{
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
		return new FieldFilter<TARGET, RESULT, Object>(field, op, field.get(instance));
	}

//	public <T, Z> Filter visit(FieldOrder<TARGET, Z, T> fieldOrder)
//	{
//		FieldPersistable<RESULT, T> field = fieldOrder.getField();
//
//		final Operator op;
//
//		switch(fieldOrder.getOrder()){
//			case ASC:
//				op = Operator.GT;
//				break;
//			case DESC:
//				op = Operator.LT;
//				break;
//		   default:
//				throw new IllegalArgumentException();
//		}
//		return new FieldFilter<TARGET, RESULT, T>(field, op, field.get(instance));
//	}

	public Filter visit(RandomOrder orderFilter)
	{
		return null;
	}

	public Filter visit(OrderSet<TARGET> orderFilter)
	{
		if(!orderFilter.isEmpty()){
			Filter<TARGET> filterSet = new FilterSet<TARGET>();
			for( int i = 0; i < orderFilter.getOrders().size() -1; i++)
			{
				final Filter<TARGET> finalFilterSet = filterSet;
				filterSet = orderFilter.getOrders().get(i).accept(new OrderVisitorGenerified<TARGET, RESULT, Object, Filter<TARGET>>()
				{
					public Filter<TARGET> visit(RandomOrder orderFilter)
					{
						// :-)
						return null;
					}

					@Override
					public Filter<TARGET> visit(FieldOrder<TARGET, RESULT, Object> fieldOrder,
														 FieldPersistable<RESULT, Object> field)
					{
						return new FilterSet<TARGET>(
								  finalFilterSet,
								  new FieldFilter<TARGET, RESULT, Object>(fieldOrder.getField(), Operator.EQ, field.get(instance)));
					}

					public Filter<TARGET> visit(OrderSet orderFilter)
					{
						return this.visit(orderFilter);
					}
				});
			}

			return new FilterSet(filterSet, orderFilter.getOrders().get(orderFilter.getOrders().size()-1).accept(this));
		}
		else
		{
			return null;
		}
	}

}
