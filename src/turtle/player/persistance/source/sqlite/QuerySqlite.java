package turtle.player.persistance.source.sqlite;

import android.database.Cursor;
import turtle.player.persistance.framework.db.Database;
import turtle.player.persistance.framework.filter.FieldFilter;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.filter.FilterSet;
import turtle.player.persistance.framework.query.Query;
import turtle.player.persistance.framework.selector.QuerySelector;
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

	@Override
	public Sql get(QuerySelector<Sql, I, Cursor> querySelector, Filter<Sql> filter)
	{
		Sql sql = querySelector.get();

		if(filter != null){
			sql.append(" where ");
			sql = filter.accept(sql, this);
		}
		return sql;
	}

	@Override
	public I execute(final Database<Sql, Cursor, ?> db, final QuerySelector<Sql, I, Cursor> querySelector, Filter<Sql> filter)
	{
		return db.read(get(querySelector, filter), new Database.DbReadOp<I, Cursor>()
		{
			@Override
			public I read(Cursor cursor)
			{
				return querySelector.create(cursor);
			}
		});
	}

	@Override
	public Sql visit(Sql query, FieldFilter fieldFilter)
	{
		return query.append(fieldFilter.getField().getName() + " = ?1 ", fieldFilter.getFieldValue());
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
