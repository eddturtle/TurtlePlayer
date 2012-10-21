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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Track;
import turtle.player.persistance.framework.executor.OperationExecutor;
import turtle.player.persistance.source.relational.Table;
import turtle.player.persistance.source.sqlite.InsertOperationSqlLite;
import turtle.player.persistance.turtle.FileBase;
import turtle.player.persistance.framework.db.Database;
import turtle.player.persistance.framework.db.ObservableDatabase;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.source.sql.Sql;
import turtle.player.persistance.source.sqlite.Counter;
import turtle.player.persistance.source.sqlite.QuerySqlite;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.selector.*;
import turtle.player.persistance.turtle.selector.AlbumMapping;
import turtle.player.persistance.turtle.selector.ArtistMapping;

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
		OperationExecutor.execute(this, new InsertOperationSqlLite<Track>(), new TrackInsertOperation(), track);
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
		return OperationExecutor.execute(this, new QuerySqlite<Integer>(filter), new Counter(Tables.TRACKS.getName())).equals(0);
	}

	@Override
	public Set<Track> getTracks(Filter<Sql> filter)
	{
		return OperationExecutor.execute(this, new QuerySqlite<Set<Track>>(filter), new TrackMapping());
	}

	@Override
	public Set<Album> getAlbums(Filter<Sql> filter)
	{
		return OperationExecutor.execute(this, new QuerySqlite<Set<Album>>(filter), new AlbumMapping());
	}

	@Override
	public Set<Artist> getArtist(Filter<Sql> filter)
	{
		return OperationExecutor.execute(this, new QuerySqlite<Set<Artist>>(filter), new ArtistMapping());
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