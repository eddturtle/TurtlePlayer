package com.turtleplayer.persistance.source.sql.query;

import com.turtleplayer.persistance.source.relational.Field;
import com.turtleplayer.persistance.source.relational.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class Select implements Sql
{
	private final String sql;
	private WhereClause whereClause;
	private OrderClause orderClause;
	private Limit limit;

	public enum SelectMethod{
		NORMAL,
		COUNT,
		DISTINCT
	}

	public Select(View view)
	{
		this.sql = "SELECT * FROM " + getTableList(view);
	}

	public Select(View view, Field... fields)
	{
		this(view, SelectMethod.NORMAL, fields);
	}

	public Select(View view, SelectMethod selectMethod, Field... fields)
	{
		this.sql = "SELECT " +
				  getFieldsList(selectMethod, fields) +
				  " FROM " + getTableList(view);
	}

	private String getFieldsList(SelectMethod selectMethod, Field... fields)
	{
		switch (selectMethod)
		{
			case NORMAL:
				return new FieldsPart(Arrays.asList(fields)).toSql();
			case COUNT:
				return " COUNT(" + new FieldsPart(Arrays.asList(fields)).toSql() + ")";
			case DISTINCT:
				return " DISTINCT " + new FieldsPart(Arrays.asList(fields)).toSql() + " ";
			default:
				throw new RuntimeException("Implement SelectMethod " + selectMethod.name());
		}
	}

	private String getTableList(View view)
	{
		return new TablesPart(view.getTables()).toSql();
	}

	public String toSql()
	{
		return sql
				  + (whereClause != null ? " WHERE " + whereClause.toSql() : "")
				  + (orderClause != null ? " ORDER BY "  + orderClause.toSql() : "")
				  + (limit != null ? limit.toSql() : "");
	}

	public List<Object> getParams()
	{
		List<Object> params = new ArrayList<Object>();
		params.addAll(whereClause != null ? whereClause.getParams() : new ArrayList<Object>());
		return params;
	}

	public void setWhereClause(WhereClause whereClause)
	{
		this.whereClause = whereClause;
	}

	public void setOrderClause(OrderClause orderClause)
	{
		this.orderClause = orderClause;
	}

	public void setLimit(Limit limit){
		this.limit = limit;
	}
}
