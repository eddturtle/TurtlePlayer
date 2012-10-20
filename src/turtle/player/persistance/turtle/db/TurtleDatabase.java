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
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.selector.AlbumQuerySelector;
import turtle.player.persistance.turtle.selector.ArtistQuerySelector;
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

		write(new DbWriteOp<SQLiteDatabase>()
		{
			@Override
			public void write(SQLiteDatabase db)
			{
				ContentValues values = new ContentValues();

				values.put(Tables.TRACKS.TITLE.getName(), track.GetTitle());
				values.put(Tables.TRACKS.NUMBER.getName(), track.GetNumber());
				values.put(Tables.TRACKS.ARTIST.getName(), track.GetArtist().getName());
				values.put(Tables.TRACKS.ALBUM.getName(), track.GetAlbum().getName());
				values.put(Tables.TRACKS.LENGTH.getName(), track.GetLength());
				values.put(Tables.TRACKS.SRC.getName(), track.GetSrc());
				values.put(Tables.TRACKS.ROOTSRC.getName(), track.GetRootSrc());
				values.put(Tables.TRACKS.ALBUMART.getName(), track.albumArt());

				db.insert(Tables.TRACKS.getName(), null, values);

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
				db.execSQL("DELETE FROM " + Tables.TRACKS.getName());
				notifyUpdate();
			}
		});
	}

	public boolean isEmpty(Filter<Sql> filter)
	{
		return new QuerySqlite<Integer>(new Counter(Tables.TRACKS.getName())).execute(this, filter).equals(0);
	}

	@Override
	public Set<Track> getTracks(Filter<Sql> filter)
	{
		return new QuerySqlite<Set<Track>>(new TrackQuerySelector()).execute(this, filter);
	}

	@Override
	public Set<Album> getAlbums(Filter<Sql> filter)
	{
		return new QuerySqlite<Set<Album>>(new AlbumQuerySelector()).execute(this, filter);
	}

	@Override
	public Set<Artist> getArtist(Filter<Sql> filter)
	{
		return new QuerySqlite<Set<Artist>>(new ArtistQuerySelector()).execute(this, filter);
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