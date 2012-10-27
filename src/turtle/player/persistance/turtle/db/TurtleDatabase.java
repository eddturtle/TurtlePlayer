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
import turtle.player.persistance.source.sql.MappingTable;
import turtle.player.persistance.source.sql.query.Select;
import turtle.player.persistance.source.sql.query.WhereClause;
import turtle.player.persistance.source.sqlite.*;
import turtle.player.persistance.turtle.FileBase;
import turtle.player.persistance.framework.db.Database;
import turtle.player.persistance.framework.db.ObservableDatabase;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.mapping.*;
import turtle.player.persistance.turtle.mapping.AlbumCreator;
import turtle.player.persistance.turtle.mapping.ArtistCreator;

import java.util.Set;

// Import - Android Content
// Import - Android Database


public class TurtleDatabase extends ObservableDatabase<Select, Cursor, SQLiteDatabase> implements FileBase<WhereClause>
{

	final TurtleDatabaseImpl turtleDatabaseImpl;


	public TurtleDatabase(Context context)
	{
		turtleDatabaseImpl = new TurtleDatabaseImpl(context, this, Tables.TRACKS);
	}

	public void push(final Track track)
	{
		OperationExecutor.execute(this, new InsertOperationSqlLite<Track>(new TrackToDbMapper()), track);
	}

	public void clear()
	{
		OperationExecutor.execute(this, new DeleteTableContentSqlLite(), Tables.TRACKS);
	}

	public boolean isEmpty(Filter<WhereClause> filter)
	{
		return OperationExecutor.execute(this, new QuerySqlite<Integer>(filter, new CounterSqlite(Tables.TRACKS))).equals(0);
	}

	public int countAvailableTracks(Filter<WhereClause> filter)
	{
		return OperationExecutor.execute(this, new QuerySqlite<Integer>(filter, new CounterSqlite(Tables.TRACKS)));
	}

	public Set<Track> getTracks(Filter<WhereClause> filter)
	{
		return OperationExecutor.execute(
                this,
                new QuerySqlite<Set<Track>>(filter,
                        new MappingTable<Track>(Tables.TRACKS, new CreatorForSetSqlite<Track>(new TrackCreator())))
        );
	}

	public Set<Album> getAlbums(Filter<WhereClause> filter)
	{
        return OperationExecutor.execute(
                this,
                new QuerySqlite<Set<Album>>(filter,
                        new MappingTable<Album>(Tables.TRACKS, new CreatorForSetSqlite<Album>(new AlbumCreator())))
        );
	}

	public Set<Artist> getArtist(Filter<WhereClause> filter)
	{
        return OperationExecutor.execute(
                this,
                new QuerySqlite<Set<Artist>>(filter, new MappingTable<Artist>(Tables.TRACKS, new CreatorForSetSqlite<Artist>(new ArtistCreator()))
                )
        );
	}

	public <I> I read(Select query,
						  Database.DbReadOp<I, Cursor> readOp)
	{
		SQLiteDatabase db = turtleDatabaseImpl.getReadableDatabase();
		try
		{
			String[] params = new String[query.getParams().size()];
			int i = 0;

			for(Object param : query.getParams())
			{
				params[i++] = param.toString();
			}
            Cursor cursor = db.rawQuery(query.toSql(), params);
            cursor.moveToFirst();
			return readOp.read(cursor);
		}
		finally
		{
			db.close();
		}
	}

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