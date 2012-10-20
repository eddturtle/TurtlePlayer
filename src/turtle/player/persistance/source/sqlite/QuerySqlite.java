package turtle.player.persistance.source.sqlite;

import android.database.Cursor;
import turtle.player.persistance.framework.db.Database;
import turtle.player.persistance.framework.filter.FieldFilter;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.filter.FilterSet;
import turtle.player.persistance.framework.query.Query;
import turtle.player.persistance.framework.selector.Selector;
import turtle.player.persistance.source.sql.Sql;

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

public class QuerySqlite<I> implements Query<Sql, I, Cursor>
{

	private final static String FILTER_CONNECTOR = " and ";

	Selector<Sql, I, Cursor> selector;

	public QuerySqlite(Selector<Sql, I, Cursor> selector)
	{
		this.selector = selector;
	}

	public Selector<Sql, I, Cursor> getSelector()
	{
		return selector;
	}

	@Override
	public Sql get(Filter<Sql> filter)
	{
		Sql sql = getSelector().get();

		if(filter != null){
			sql.append(" where ");
			sql = filter.accept(sql, this);
		}
		return sql;
	}

	@Override
	public I execute(Database<Sql, Cursor, ?> db, Filter<Sql> filter)
	{
		final Object[] returnValue = new Object[1];

		db.read(get(filter), new Database.DbReadOp<Cursor>()
		{
			@Override
			public void read(Cursor db)
			{
				returnValue[0] = getSelector().create(db);
			}
		});

		return (I) returnValue[0];
	}

	@Override
	public Sql visit(Sql query, FieldFilter fieldFilter)
	{
		return query.append(fieldFilter.getFieldName() + " = ?1 ", fieldFilter.getFieldValue());
	}

	@Override
	public Sql visit(Sql query, FilterSet<Sql> filterSet)
	{
		for(Filter<Sql> filter : filterSet.getFilters()){
			query.append(filter.accept(query, this));
			query.append(FILTER_CONNECTOR);
		}

		query.removeLast(FILTER_CONNECTOR);
		return query;
	}
}
