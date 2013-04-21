package turtle.player.persistance.source.sqlite;

import android.database.Cursor;
import turtle.player.persistance.framework.filter.*;
import turtle.player.persistance.framework.query.Query;
import turtle.player.persistance.framework.mapping.Mapping;
import turtle.player.persistance.framework.sort.FieldOrder;
import turtle.player.persistance.framework.sort.Order;
import turtle.player.persistance.framework.sort.OrderSet;
import turtle.player.persistance.framework.sort.RandomOrder;
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

public class QuerySqlite<TARGET, RESULT> extends Query<Select, WhereClause, OrderClause, RESULT, Cursor, TARGET>
{
	 private final Mapping<Select, RESULT, Cursor> mapping;

	 public QuerySqlite(Mapping<Select, RESULT, Cursor> mapping)
	 {
		  this.mapping = mapping;
	 }

	 public QuerySqlite(Filter<TARGET> filter, Mapping<Select, RESULT, Cursor> mapping)
	 {
		  super(filter);
		  this.mapping = mapping;
	 }

	 public QuerySqlite(Order order, Mapping<Select, RESULT, Cursor> mapping)
	 {
		  super(order);
		  this.mapping = mapping;
	 }

	 public QuerySqlite(Filter<TARGET> filter, Order order, Mapping<Select, RESULT, Cursor> mapping)
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

	public <T, Z> WhereClause visit(FieldFilter<TARGET, Z, T> fieldFilter)
	{
		final Operator operator;
		Object filterValue = fieldFilter.getField().accept(fieldFilter.new FieldVisitorField<String>()
		{
			public String visit(FieldPersistableAsString<Z> field,String filterValue)
			{
				return filterValue;
			}

			public String visit(FieldPersistableAsDouble<Z> field, Double filterValue)
			{
				return String.valueOf(filterValue);
			}

			public String visit(FieldPersistableAsInteger<Z> field, Integer filterValue)
			{
				return String.valueOf(filterValue);
			}
		});

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

		return new WhereClause(new WhereClauseField(fieldFilter.getField(), filterValue, operator));
	}

	public OrderClause visit(RandomOrder orderFilter)
	{
		return new OrderClauseRandom();
	}

	public WhereClause visit(FilterSet<TARGET> filterSet)
	{
		WhereClause whereClause = null;
		for(Filter<TARGET> filter : filterSet.getFilters()){
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

	public WhereClause visit(NotFilter<TARGET> notFilter)
	{
		Filter<TARGET> inversedFilter = notFilter.getFilter().accept(new FilterVisitor<TARGET, Filter<TARGET>>()
		{
			public <T, Z> Filter<TARGET> visit(FieldFilter<TARGET, Z, T> fieldFilter)
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
				return new FieldFilter<TARGET, Z, T>(fieldFilter.getField(), inversedOp, fieldFilter.getValue());
			}

			public Filter<TARGET> visit(FilterSet<TARGET> filterSet)
			{
				for(Filter<TARGET> f : filterSet.getFilters())
				{
					f.accept(this);
				}
				return null;
			}

			public Filter<TARGET> visit(NotFilter<TARGET> notFilter)
			{
				return notFilter.getFilter();
			}
		});
		return inversedFilter.accept(this);
	}


	public OrderClause visit(FieldOrder fieldOrder)
	 {
		  return new OrderClauseFields(new OrderClausePartField(fieldOrder.getField(), fieldOrder.getOrder()));
	 }

	 public OrderClause visit(OrderSet<TARGET> clauseSet)
	 {
		  OrderClause orderClause = null;
		  for(Order<TARGET> order : clauseSet.getOrders()){
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
