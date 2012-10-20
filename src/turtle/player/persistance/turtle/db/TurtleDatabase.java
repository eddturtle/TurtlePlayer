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
import turtle.player.persistance.turtle.FileBase;
import turtle.player.persistance.framework.db.Database;
import turtle.player.persistance.framework.db.ObservableDatabase;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.source.sql.Sql;
import turtle.player.persistance.source.sqlite.Counter;
import turtle.player.persistance.source.sqlite.QuerySqlite;
import turtle.player.persistance.turtle.selector.AlbumSelector;
import turtle.player.persistance.turtle.selector.ArtistSelector;
import turtle.player.persistance.turtle.selector.TrackSelector;

import java.util.Set;

// Import - Android Content
// Import - Android Database


public class TurtleDatabase extends ObservableDatabase<Sql, Cursor, SQLiteDatabase> implements FileBase<Sql>
{

	//TODO: this should not be public, instead package visible
	public static final String TABLE_NAME = "Tracks";

	public static final String KEY_ID = "id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_NUMBER = "number";
	public static final String KEY_ARTIST = "artist";
	public static final String KEY_ALBUM = "album";
	public static final String KEY_LENGTH = "length";
	public static final String KEY_SRC = "src";
	public static final String KEY_ROOTSRC = "rootSrc";
	public static final String KEY_ALBUMART = "hasAlbumArt";

	final TurtleDatabaseImpl turtleDatabaseImpl;


	public TurtleDatabase(Context context)
	{
		turtleDatabaseImpl = new TurtleDatabaseImpl(context);
	}

	public void push(final Track track)
	{

		write(new DbWriteOp<SQLiteDatabase>()
		{
			@Override
			public void write(SQLiteDatabase db)
			{
				ContentValues values = new ContentValues();

				values.put(KEY_TITLE, track.GetTitle());
				values.put(KEY_NUMBER, track.GetNumber());
				values.put(KEY_ARTIST, track.GetArtist().getName());
				values.put(KEY_ALBUM, track.GetAlbum().getName());
				values.put(KEY_LENGTH, track.GetLength());
				values.put(KEY_SRC, track.GetSrc());
				values.put(KEY_ROOTSRC, track.GetRootSrc());
				values.put(KEY_ALBUMART, track.albumArt());

				db.insert(TABLE_NAME, null, values);

				notifyUpdate();
			}
		});
	}

	public void clear()
	{
		write(new DbWriteOp<SQLiteDatabase>()
		{
			@Override
			public void write(SQLiteDatabase db)
			{
				db.execSQL("DELETE FROM " + TABLE_NAME);
				notifyUpdate();
			}
		});
	}

	public boolean isEmpty(Filter<Sql> filter)
	{
		return new QuerySqlite<Integer>(new Counter(TABLE_NAME)).execute(this, filter).equals(0);
	}

	@Override
	public Set<Track> getTracks(Filter<Sql> filter)
	{
		return new QuerySqlite<Set<Track>>(new TrackSelector()).execute(this, filter);
	}

	@Override
	public Set<Album> getAlbums(Filter<Sql> filter)
	{
		return new QuerySqlite<Set<Album>>(new AlbumSelector()).execute(this, filter);
	}

	@Override
	public Set<Artist> getArtist(Filter<Sql> filter)
	{
		return new QuerySqlite<Set<Artist>>(new ArtistSelector()).execute(this, filter);
	}

	@Override
	public void read(Sql query,
						  Database.DbReadOp<Cursor> readOp)
	{
		SQLiteDatabase db = turtleDatabaseImpl.getReadableDatabase();
		try
		{
			readOp.read(db.rawQuery(query.getSql(), query.getParams().toArray(new String[query.getParams().size()])));
		}
		finally
		{
			db.close();
		}
	}

	@Override
	public void write(DbWriteOp<SQLiteDatabase> writeOp)
	{
		SQLiteDatabase db = turtleDatabaseImpl.getReadableDatabase();
		try
		{
			writeOp.write(db);
		}
		finally
		{
			db.close();
		}
	}

}