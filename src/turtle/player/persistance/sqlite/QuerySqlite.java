package turtle.player.persistance.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import turtle.player.model.Instance;
import turtle.player.persistance.Database;
import turtle.player.persistance.filter.FieldFilter;
import turtle.player.persistance.filter.Filter;
import turtle.player.persistance.filter.FilterSet;
import turtle.player.persistance.query.Query;
import turtle.player.persistance.selector.Selector;

import java.io.FileReader;
import java.lang.ref.Reference;
import java.util.Set;

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

public class QuerySqlite<I> implements Query<String, I, Cursor>
{

	private final static String FILTER_CONNECTOR = " and ";

	Selector<String, I, Cursor> selector;

	public QuerySqlite(Selector<String, I, Cursor> selector)
	{
		this.selector = selector;
	}

	public Selector<String, I, Cursor> getSelector()
	{
		return selector;
	}

	@Override
	public String get(Filter<String> filter)
	{
		String sql = getSelector().get();

		if(filter != null){
			sql += " where ";
			sql = filter.accept(sql, this);
		}
		return sql;
	}

	@Override
	public I execute(Database<String, Cursor, ?> db,
						  Filter<String> filter)
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
	public String visit(String query, FieldFilter fieldFilter)
	{
		return fieldFilter.getFieldName() + " = " + fieldFilter.getFieldValue();
	}

	@Override
	public String visit(String query, FilterSet<String> filterSet)
	{
		for(Filter<String> filter : filterSet.getFilters()){
			query += filter.accept(query, this);
			query += FILTER_CONNECTOR;
		}

		return query.endsWith(FILTER_CONNECTOR) ?
				  query.substring(0, query.length() - FILTER_CONNECTOR.length()) :
				  query; //cut off last FILTER_CONNECTOR
	}
}
