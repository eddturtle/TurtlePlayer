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
import turtle.player.model.*;
import turtle.player.persistance.framework.creator.ResultCreator;
import turtle.player.persistance.framework.db.Database;
import turtle.player.persistance.framework.db.ObservableDatabase;
import turtle.player.persistance.framework.executor.OperationExecutor;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.sort.FieldOrder;
import turtle.player.persistance.framework.sort.SortOrder;
import turtle.player.persistance.source.relational.FieldPersistable;
import turtle.player.persistance.source.relational.View;
import turtle.player.persistance.source.sql.MappingDistinct;
import turtle.player.persistance.source.sql.MappingTable;
import turtle.player.persistance.source.sql.query.Select;
import turtle.player.persistance.source.sqlite.*;
import turtle.player.persistance.turtle.FileBase;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.db.structure.Views;
import turtle.player.persistance.turtle.mapping.*;

import java.util.Arrays;
import java.util.List;

// Import - Android Content
// Import - Android Database


public class TurtleDatabase extends ObservableDatabase<Select, Cursor, SQLiteDatabase> implements FileBase
{

	final SQLiteDatabase db;

	public TurtleDatabase(Context context)
	{
		SQLiteOpenHelper turtleDatabaseImpl = new TurtleDatabaseImpl(context)
		{
			@Override
			public void dbResetted()
			{
				notifyCleared();
			}
		};
		db = turtleDatabaseImpl.getWritableDatabase();
	}

	//Write------------------------------------

	/**
	 * @return true when successful inserted
	 */
	public boolean push(final Track track)
	{
		int insertedCount = OperationExecutor.execute(this, new InsertOperationSqlLite<Track>(new TrackToDbMapper()), track);
		if(insertedCount > 0)
		{
			notifyUpdate(track);
			return true;
		}
		return false;
	}

	public void push(final AlbumArtLocation albumArtLocation)
	{
		OperationExecutor.execute(this, new InsertOperationSqlLite<AlbumArtLocation>(new AlbumArtLoactionToDbMapper()), albumArtLocation);
	}

	public void push(final FSobject dir)
	{
		OperationExecutor.execute(this, new InsertOperationSqlLite<FSobject>(new FsObjectToDbMapper()), dir);
	}

	public void clear()
	{
		OperationExecutor.execute(this, new DeleteTableContentSqlLite(), Tables.TRACKS);
		notifyCleared();
	}

	//Read------------------------------------

	public boolean isEmpty(Filter<Tables.Tracks> filter)
	{
		return OperationExecutor.execute(
				  this,
				  new QuerySqlite<Tables.Tracks, Tables.Tracks, Integer>(filter, new CounterSqlite(Tables.TRACKS))).equals(0);
	}

	public int countAvailableTracks(Filter<Tables.Tracks> filter)
	{
		return OperationExecutor.execute(
				  this,
				  new QuerySqlite<Tables.Tracks, Tables.Tracks, Integer>(filter, new CounterSqlite(Tables.TRACKS)));
	}

	public List<? extends Track> getTracks(Filter<? super Tables.Tracks> filter)
	{
		return OperationExecutor.execute(
				  this,
				  new QuerySqlite<Tables.Tracks, Tables.Tracks, List<Track>>(
							 filter,
							 new MappingTable<Tables.Tracks, Track>(
										Tables.TRACKS,
										new CreatorForListSqlite<Tables.Tracks, Track>(new TrackCreator())
							 )
				  )
		);
	}

	public List<? extends Song> getSongs(Filter<? super Tables.Tracks> filter)
	{
		return getList(filter, new SongCreator(), Tables.TRACKS, Views.SONGS ,Tables.SongsReadable.TITLE);
	}

	public List<? extends Artist> getArtists(Filter<? super Tables.Tracks> filter)
	{
		return getList(filter, new ArtistCreator(), Tables.TRACKS, Views.ARTISTS,  Tables.ArtistsReadable.ARTIST);
	}

	public List<? extends Genre> getGenres(Filter<? super Tables.Tracks> filter)
	{
		return getList(filter, new GenreCreator(), Tables.TRACKS, Views.GENRES, Tables.GenresReadable.GENRE);
	}

	public List<? extends Album> getAlbums(Filter<? super Tables.Tracks> filter)
	{
		return getList(filter, new AlbumCreator(), Tables.TRACKS, Views.ALBUMS, Tables.AlbumsReadable.ALBUM);
	}

	public List<? extends FSobject> getDirList(Filter<? super Tables.Dirs> filter)
	{
		return getList(filter, new DirCreator(), Tables.DIRS, Tables.DIRS, Tables.Dirs.NAME);
	}

	private <RESULT, TARGET extends View, PROJECTION extends View, Z> List<RESULT> getList(
			  Filter<? super PROJECTION> filter,
			  ResultCreator<TARGET, RESULT, Cursor> creator,
			  PROJECTION view,
			  TARGET target,
			  FieldPersistable<? super RESULT, Z>... sortFields)
	{
		return OperationExecutor.execute(
				  this,
				  new QuerySqlite<PROJECTION, TARGET, List<RESULT>>(
							 filter,
							 FieldOrder.<PROJECTION, RESULT, Z>getMultiFieldOrder(SortOrder.ASC, sortFields),
							 new MappingDistinct<TARGET, PROJECTION,  RESULT>(view, new CreatorForListSqlite<TARGET, RESULT>(creator), target)
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

	public <I> int write(DbWriteOp<SQLiteDatabase, I> writeOp,
								 I instance)
	{
		return writeOp.write(db, instance);
	}

}