package turtle.player.persistance.source.sqlite;

import android.database.Cursor;
import turtle.player.persistance.framework.filter.*;
import turtle.player.persistance.framework.query.Query;
import turtle.player.persistance.framework.mapping.Mapping;
import turtle.player.persistance.framework.sort.FieldOrder;
import turtle.player.persistance.framework.sort.Order;
import turtle.player.persistance.framework.sort.OrderSet;
import turtle.player.persistance.framework.sort.RandomOrder;
import turtle.player.persistance.source.relational.FieldPersistable;
import turtle.player.persistance.source.relational.fieldtype.FieldPersistableAsDouble;
import turtle.player.persistance.source.relational.fieldtype.FieldPersistableAsInteger;
import turtle.player.persistance.source.relational.fieldtype.FieldPersistableAsString;
import turtle.player.persistance.source.sql.query.*;
import turtle.player.persistance.source.sql.query.Operator;

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

public class QuerySqlite<PROJECTION, TARGET, RESULT> extends Query<Select, WhereClause, OrderClause, RESULT, Cursor, TARGET, PROJECTION>
{
	 private final Mapping<Select, RESULT, Cursor> mapping;

	 public QuerySqlite(Mapping<Select, RESULT, Cursor> mapping)
	 {
		  this.mapping = mapping;
	 }

	 public QuerySqlite(Filter<? super PROJECTION> filter, Mapping<Select, RESULT, Cursor> mapping)
	 {
		  super(filter);
		  this.mapping = mapping;
	 }

	 public QuerySqlite(Order<? super PROJECTION> order, Mapping<Select, RESULT, Cursor> mapping)
	 {
		  super(order);
		  this.mapping = mapping;
	 }

	 public QuerySqlite(Filter<? super PROJECTION> filter, Order<? super PROJECTION> order, Mapping<Select, RESULT, Cursor> mapping)
	{
		super(filter, order);

		  this.mapping = mapping;
	}

	public Select get()
	{
		Select sql = mapping.get();

		if(getFilter() != null){
			sql.setWhereClause(getFilter().accept(this));
		}

	  if(getOrder() != null){
			sql.setOrderClause(getOrder().accept(this));
	  }

		return sql;
	}

	public RESULT map(final Cursor cursor)
	{
		return mapping.create(cursor);
	}

	public <T, Z> WhereClause visit(FieldFilter<? super PROJECTION, Z, T> fieldFilter)
	{
		final Operator operator;
		switch (fieldFilter.getOperator()){
			case EQ:
				operator = Operator.EQ;
				break;
			case NEQ:
				operator = Operator.NEQ;
				break;
			case GE:
				operator = Operator.GE;
				break;
			case GT:
				operator = Operator.GT;
				break;
			case LE:
				operator = Operator.LE;
				break;
			case LIKE:
				operator = Operator.LIKE;
				break;
			case NOT_LIKE:
				operator = Operator.NOT_LIKE;
				break;
			case LT:
				operator = Operator.LT;
				break;
			default:
				throw new IllegalArgumentException();
		}

		return new WhereClause(new WhereClauseField(fieldFilter.getField(), fieldFilter.getValue(), operator));
	}

	public OrderClause visit(RandomOrder<? super PROJECTION> orderFilter)
	{
		return new OrderClauseRandom();
	}

	public WhereClause visit(FilterSet<? super PROJECTION> filterSet)
	{
		WhereClause whereClause = null;
		for(Filter<? super PROJECTION> filter : filterSet.getFilters()){
			if(whereClause == null)
			{
				whereClause = filter.accept(this);
			}
			else
			{
				whereClause = whereClause.apply(BoolOperator.AND, filter.accept(this));
			}
		}
		return whereClause;
	}


	public WhereClause visit(NotFilter<? super PROJECTION> notFilter)
	{
		Filter<? super PROJECTION> inversedFilter = notFilter.getFilter().accept(new FilterVisitorGenerified<PROJECTION, RESULT,Object,Filter<? super PROJECTION>>()
		{

			@Override
			public Filter<? super PROJECTION> visit(FieldFilter<PROJECTION, RESULT, Object> fieldFilter,
													  FieldPersistable<RESULT, Object> field)
			{
				final turtle.player.persistance.framework.filter.Operator inversedOp;
				switch (fieldFilter.getOperator())
				{
					case NEQ:
						inversedOp = turtle.player.persistance.framework.filter.Operator.EQ;
						break;
					case EQ:
						inversedOp = turtle.player.persistance.framework.filter.Operator.NEQ;
						break;
					case GE:
						inversedOp = turtle.player.persistance.framework.filter.Operator.LT;
						break;
					case GT:
						inversedOp = turtle.player.persistance.framework.filter.Operator.LE;
						break;
					case LE:
						inversedOp = turtle.player.persistance.framework.filter.Operator.GT;
						break;
					case LIKE:
						inversedOp = turtle.player.persistance.framework.filter.Operator.NOT_LIKE;
						break;
					case NOT_LIKE:
						inversedOp = turtle.player.persistance.framework.filter.Operator.LIKE;
						break;
					case LT:
						inversedOp = turtle.player.persistance.framework.filter.Operator.GE;
						break;
					default:
						throw new RuntimeException("Not supported Operator");
				}
				return new FieldFilter<PROJECTION, RESULT, Object>(fieldFilter.getField(), inversedOp, fieldFilter.getValue());
			}

			public Filter<? super PROJECTION> visit(FilterSet<? super PROJECTION> filterSet)
			{
				for(Filter<? super PROJECTION> f : filterSet.getFilters())
				{
					f.accept(this);
				}
				return null;
			}

			public Filter<? super PROJECTION> visit(NotFilter<? super PROJECTION> notFilter)
			{
				return notFilter.getFilter();
			}
		});
		return inversedFilter.accept(this);
	}

	public <T, Z> OrderClause visit(FieldOrder<? super PROJECTION, Z, T> fieldOrder)
	 {
		  return new OrderClauseFields(new OrderClausePartField(fieldOrder.getField(), fieldOrder.getOrder()));
	 }

	 public OrderClause visit(OrderSet<? super PROJECTION> clauseSet)
	 {
		  OrderClause orderClause = null;
		  for(Order<? super PROJECTION> order : clauseSet.getOrders()){
				if(orderClause == null)
				{
					orderClause = order.accept(this);
				}
				else
				{
					 orderClause = orderClause.apply(order.accept(this));
				}
		  }
		  return orderClause;
	 }
}
