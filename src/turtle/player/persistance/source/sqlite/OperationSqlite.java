package turtle.player.persistance.source.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import turtle.player.persistance.framework.db.Database;
import turtle.player.persistance.framework.filter.FieldFilter;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.filter.FilterSet;
import turtle.player.persistance.framework.query.Operation;
import turtle.player.persistance.framework.selector.QuerySelector;
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

public class OperationSqlite implements Operation<Sql, SQLiteDatabase>
{

	private final static String FILTER_CONNECTOR = " and ";

	Selector<Sql> querySelector;

	public OperationSqlite(Selector<Sql> selector)
	{
		this.querySelector = querySelector;
	}

	public Selector<Sql> getSelector()
	{
		return querySelector;
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
	public void execute(Database<Sql, ?, SQLiteDatabase> db, final Filter<Sql> filter)
	{
		db.write(new Database.DbWriteOp<SQLiteDatabase>()
		{
			@Override
			public void write(SQLiteDatabase db)
			{
				db.execSQL(get(filter).getSql());
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
