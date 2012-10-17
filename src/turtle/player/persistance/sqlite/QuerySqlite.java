package turtle.player.persistance.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import turtle.player.model.Instance;
import turtle.player.persistance.Database;
import turtle.player.persistance.filter.FieldFilter;
import turtle.player.persistance.filter.Filter;
import turtle.player.persistance.query.Query;
import turtle.player.persistance.selector.Selector;

import java.io.FileReader;
import java.lang.ref.Reference;

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
	public String get(Filter<String>... filters)
	{
		String sql = getSelector().get();

		if(filters.length > 0){
			sql += " where ";
		}

		for(Filter<String> f : filters){
			sql = f.accept(sql, this);
		}

		return sql;
	}

	@Override
	public I execute(Database<String, Cursor, ?> db,
						  Filter<String>... filters)
	{
		final Object[] returnValue = new Object[1];

		db.read(get(filters), new Database.DbReadOp<Cursor>()
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
		return query + " and " + fieldFilter.getFieldName() + " = " + fieldFilter.getFieldValue() + " ";
	}
}
