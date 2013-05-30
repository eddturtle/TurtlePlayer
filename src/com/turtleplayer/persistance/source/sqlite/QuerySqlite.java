package com.turtleplayer.persistance.source.sqlite;

import com.turtleplayer.persistance.framework.filter.FieldFilter;
import com.turtleplayer.persistance.framework.filter.Filter;
import com.turtleplayer.persistance.framework.filter.FilterSet;
import com.turtleplayer.persistance.framework.mapping.Mapping;
import com.turtleplayer.persistance.framework.query.Query;
import com.turtleplayer.persistance.framework.sort.FieldOrder;
import com.turtleplayer.persistance.framework.sort.Order;
import com.turtleplayer.persistance.framework.sort.OrderSet;
import com.turtleplayer.persistance.framework.sort.RandomOrder;
import com.turtleplayer.persistance.source.relational.fieldtype.FieldPersistableAsDouble;
import com.turtleplayer.persistance.source.relational.fieldtype.FieldPersistableAsInteger;
import com.turtleplayer.persistance.source.relational.fieldtype.FieldPersistableAsString;
import com.turtleplayer.persistance.source.sql.query.*;

import android.database.Cursor;

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

public class QuerySqlite<I> extends Query<Select, WhereClause, OrderClause, I, Cursor>
{
	 private final Mapping<Select, I, Cursor> mapping;

	 public QuerySqlite(Mapping<Select, I, Cursor> mapping)
	 {
		  this.mapping = mapping;
	 }

	 public QuerySqlite(Filter filter, Mapping<Select, I, Cursor> mapping)
	 {
		  super(filter);
		  this.mapping = mapping;
	 }

	 public QuerySqlite(Order order, Mapping<Select, I, Cursor> mapping)
	 {
		  super(order);
		  this.mapping = mapping;
	 }

	 public QuerySqlite(Filter filter, Order order, Mapping<Select, I, Cursor> mapping)
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

	public I map(final Cursor cursor)
	{
		return mapping.create(cursor);
	}

	public <T> WhereClause visit(final FieldFilter<I, T> fieldFilter)
	{
		final Operator operator;
		Object filterValue = fieldFilter.getValue();

		switch (fieldFilter.getOperator()){
			case EQ:
				operator = Operator.EQ;
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
				filterValue = fieldFilter.getField().accept(fieldFilter.new FieldVisitorField<Object>()
				{
					public Object visit(FieldPersistableAsString<I> field, String filterValue)
					{
						return "%" + filterValue + "%";
					}

					public Object visit(FieldPersistableAsDouble<I> field, Double filterValue)
					{
						return "%" + String.valueOf(filterValue) + "%";
					}

					public Object visit(FieldPersistableAsInteger<I> field, Integer filterValue)
					{
						return "%" + String.valueOf(filterValue) + "%";
					}
				});
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

	public WhereClause visit(FilterSet filterSet)
	{
		WhereClause whereClause = null;
		for(Filter filter : filterSet.getFilters()){
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


	 public OrderClause visit(FieldOrder fieldOrder)
	 {
		  return new OrderClauseFields(new OrderClausePartField(fieldOrder.getField(), fieldOrder.getOrder()));
	 }

	 public OrderClause visit(OrderSet clauseSet)
	 {
		  OrderClause orderClause = null;
		  for(Order order : clauseSet.getOrders()){
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
