package turtle.player.persistance.source.sqlite;

import android.database.Cursor;
import turtle.player.persistance.framework.filter.FieldFilter;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.filter.FilterSet;
import turtle.player.persistance.framework.query.Query;
import turtle.player.persistance.framework.mapping.Mapping;
import turtle.player.persistance.source.sql.query.*;

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

public class QuerySqlite<I> extends Query<Select, WhereClause,  I, Cursor>
{
	private final static String FILTER_CONNECTOR = " and ";

	public QuerySqlite(Filter<WhereClause> filter)
	{
		super(filter);
	}

	public Select get(Mapping<Select, I, Cursor> mapping)
	{
		Select sql = mapping.get();

		if(getFilter() != null){
			sql.setWhereClause(getFilter().accept(this));
		}
		return sql;
	}

	public I map(final Cursor cursor,
					 final Mapping<Select, I, Cursor> mapping)
	{
		return mapping.create(cursor);
	}

	public WhereClause visit(FieldFilter fieldFilter)
	{
		return new WhereClause(
				  new WhereClauseField(fieldFilter.getField(), fieldFilter.getFieldValue(), Operator.EQUALS));
	}

	public WhereClause visit(FilterSet<WhereClause> filterSet)
	{
		WhereClause whereClause = null;
		for(Filter<WhereClause> filter : filterSet.getFilters()){
			if(whereClause == null)
			{
				whereClause = filter.accept(this);
			}
			else
			{
				whereClause.apply(BoolOperator.AND, filter.accept(this));
			}
		}
		return whereClause;
	}
}
