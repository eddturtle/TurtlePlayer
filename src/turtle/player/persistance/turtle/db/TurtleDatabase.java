/**
 *
 * TURTLE PLAYER
 *
 * Licensed under MIT & GPL
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * More Information @ www.turtle-player.co.uk
 *
 * @author Simon Honegger (Hoene84)
 */

package turtle.player.persistance.turtle.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Track;
import turtle.player.persistance.source.relational.Table;
import turtle.player.persistance.source.sqlite.OperationSqlite;
import turtle.player.persistance.turtle.FileBase;
import turtle.player.persistance.framework.db.Database;
import turtle.player.persistance.framework.db.ObservableDatabase;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.source.sql.Sql;
import turtle.player.persistance.source.sqlite.Counter;
import turtle.player.persistance.source.sqlite.QuerySqlite;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.selector.AlbumQuerySelector;
import turtle.player.persistance.turtle.selector.ArtistQuerySelector;
import turtle.player.persistance.turtle.selector.TrackInsertOperation;
import turtle.player.persistance.turtle.selector.TrackQuerySelector;

import java.util.Set;

// Import - Android Content
// Import - Android Database


public class TurtleDatabase extends ObservableDatabase<Sql, Cursor, SQLiteDatabase> implements FileBase<Sql>
{

	final TurtleDatabaseImpl turtleDatabaseImpl;


	public TurtleDatabase(Context context)
	{
		turtleDatabaseImpl = new TurtleDatabaseImpl(context, this, Tables.TRACKS);
	}

	public void push(final Track track)
	{
		new OperationSqlite<Track>().execute(this, new TrackInsertOperation(), track);
	}

	public void clear()
	{
		write(new DbWriteOp<SQLiteDatabase, Table>()
		{
			@Override
			public void write(SQLiteDatabase target,
									Table table)
			{
				target.execSQL("DELETE FROM " + table.getName());
				notifyUpdate();
			}
		}, Tables.TRACKS);
	}

	public boolean isEmpty(Filter<Sql> filter)
	{
		return new QuerySqlite<Integer>().execute(this, new Counter(Tables.TRACKS.getName()), filter).equals(0);
	}

	@Override
	public Set<Track> getTracks(Filter<Sql> filter)
	{
		return new QuerySqlite<Set<Track>>().execute(this, new TrackQuerySelector(), filter);
	}

	@Override
	public Set<Album> getAlbums(Filter<Sql> filter)
	{
		return new QuerySqlite<Set<Album>>().execute(this, new AlbumQuerySelector(), filter);
	}

	@Override
	public Set<Artist> getArtist(Filter<Sql> filter)
	{
		return new QuerySqlite<Set<Artist>>().execute(this, new ArtistQuerySelector(), filter);
	}

	@Override
	public <I> I read(Sql query,
						  Database.DbReadOp<I, Cursor> readOp)
	{
		SQLiteDatabase db = turtleDatabaseImpl.getReadableDatabase();
		try
		{
			return readOp.read(db.rawQuery(query.getSql(), query.getParams().toArray(new String[query.getParams().size()])));
		}
		finally
		{
			db.close();
		}
	}

	@Override
	public <I> void write(DbWriteOp<SQLiteDatabase, I> writeOp, I instance)
	{
		SQLiteDatabase db = turtleDatabaseImpl.getWritableDatabase();
		try
		{
			writeOp.write(db, instance);
		}
		finally
		{
			db.close();
		}
	}

}