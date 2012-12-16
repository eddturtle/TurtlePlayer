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
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Track;
import turtle.player.persistance.framework.db.Database;
import turtle.player.persistance.framework.db.ObservableDatabase;
import turtle.player.persistance.framework.executor.OperationExecutor;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.sort.FieldOrder;
import turtle.player.persistance.framework.sort.SortOrder;
import turtle.player.persistance.source.relational.FieldPersistable;
import turtle.player.persistance.source.sql.MappingDistinct;
import turtle.player.persistance.source.sql.MappingTable;
import turtle.player.persistance.source.sql.query.OrderClause;
import turtle.player.persistance.source.sql.query.Select;
import turtle.player.persistance.source.sql.query.WhereClause;
import turtle.player.persistance.source.sqlite.*;
import turtle.player.persistance.turtle.FileBase;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.mapping.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

// Import - Android Content
// Import - Android Database


public class TurtleDatabase extends ObservableDatabase<Select, Cursor, SQLiteDatabase> implements FileBase<WhereClause>
{

	final SQLiteDatabase db;

	public TurtleDatabase(Context context)
	{
		SQLiteOpenHelper turtleDatabaseImpl = new TurtleDatabaseImpl(context, Tables.TRACKS);
		db = turtleDatabaseImpl.getWritableDatabase();
	}

	//Write------------------------------------

	public void push(final Track track)
	{
		OperationExecutor.execute(this, new InsertOperationSqlLite<Track>(new TrackToDbMapper()), track);
		notifyUpdate(track);
	}

	public void clear()
	{
		OperationExecutor.execute(this, new DeleteTableContentSqlLite(), Tables.TRACKS);
		notifyCleared();
	}

	//Read------------------------------------

	public boolean isEmpty(Filter<WhereClause> filter)
	{
		return OperationExecutor.execute(
				  this,
				  new QuerySqlite<Integer>(filter, new CounterSqlite(Tables.TRACKS))).equals(0);
	}

	public int countAvailableTracks(Filter<WhereClause> filter)
	{
		return OperationExecutor.execute(
				  this,
				  new QuerySqlite<Integer>(filter, new CounterSqlite(Tables.TRACKS)));
	}

	public Collection<Track> getTracks(Filter<WhereClause> filter)
	{
		return OperationExecutor.execute(
				  this,
				  new QuerySqlite<List<Track>>(filter,
							 new MappingTable<Track>(Tables.TRACKS, new CreatorForListSqlite<Track>(new TrackCreator())))
		);
	}

	public Collection<Album> getAlbums(Filter<WhereClause> filter)
	{
		return OperationExecutor.execute(
				  this,
				  new QuerySqlite<List<Album>>(filter,
							 new MappingTable<Album>(Tables.TRACKS, new CreatorForListSqlite<Album>(new AlbumCreator())))
		);
	}

	public Collection<Artist> getArtist(Filter<WhereClause> filter)
	{
		return OperationExecutor.execute(
				  this,
				  new QuerySqlite<List<Artist>>(filter,
							 new MappingTable<Artist>(Tables.TRACKS, new CreatorForListSqlite<Artist>(new ArtistCreator()))
				  )
		);
	}

	public List<String> getTrackList(Filter<WhereClause> filter)
	{
		return getList(filter, Tables.TRACKS.TITLE);
	}

	public List<String> getArtistList(Filter<WhereClause> filter)
	{
		return getList(filter, Tables.TRACKS.ARTIST);
	}

	public List<String> getAlbumList(Filter<WhereClause> filter)
	{
		return getList(filter, Tables.TRACKS.ALBUM);
	}

	private <I,T> List<String> getList(Filter<WhereClause> filter,
										  FieldPersistable<I, T> field)
	{
		return OperationExecutor.execute(
				  this,
				  new QuerySqlite<List<String>>(
							 filter,
							 new FieldOrder<I,T, OrderClause>(field, SortOrder.ASC),
							 new MappingDistinct<String>(Tables.TRACKS, field, new CreatorForListSqlite<String>(new StringCreator("")))
				  )
		);
	}


	public <I> I read(Select query,
							Database.DbReadOp<I, Cursor> readOp)
	{
		Cursor cursor = null;
		try
		{
			String[] params = new String[query.getParams().size()];
			int i = 0;

			for (Object param : query.getParams())
			{
				params[i++] = param.toString();
			}
			Log.v(TurtleDatabase.class.getName(),
					  "Running Query: " + query.toSql() + " with params " + Arrays.deepToString(params));

			cursor = db.rawQuery(query.toSql(), params);

			Log.v(TurtleDatabase.class.getName(),
					  "Resulting in " + cursor.getCount() + " Resulting Rows");

			return readOp.read(cursor);
		} finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
		}
	}

	public <I> void write(DbWriteOp<SQLiteDatabase, I> writeOp,
								 I instance)
	{
		writeOp.write(db, instance);
	}

}