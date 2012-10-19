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

package turtle.player.persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Track;
import turtle.player.persistance.Database;
import turtle.player.persistance.FileBase;
import turtle.player.persistance.filter.Filter;
import turtle.player.persistance.sqlite.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// Import - Android Content
// Import - Android Database


public class TurtleDatabase extends SQLiteOpenHelper implements FileBase<String>, Database<String, Cursor, SQLiteDatabase>
{

	//TODO: this should not be public, instead package visible
	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "TurtlePlayer";
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


	public TurtleDatabase(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
				  + KEY_ID + " INTEGER PRIMARY KEY, "
				  + KEY_TITLE + " TEXT, "
				  + KEY_NUMBER + " INTEGER, "
				  + KEY_ARTIST + " TEXT, "
				  + KEY_ALBUM + " TEXT, "
				  + KEY_LENGTH + " REAL, "
				  + KEY_SRC + " TEXT, "
				  + KEY_ROOTSRC + " TEXT, "
				  + KEY_ALBUMART + " TEXT);";
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db,
								 int oldVersion,
								 int newVersion)
	{db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
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

	public boolean isEmpty(Filter<String> filter)
	{
		return new QuerySqlite<Integer>(new Counter()).execute(this, filter).equals(0);
	}

	@Override
	public Set<Track> getTracks(Filter<String> filter)
	{
		return new QuerySqlite<Set<Track>>(new TrackSelector()).execute(this, filter);
	}

	@Override
	public Set<Album> getAlbums(Filter<String> filter)
	{
		return new QuerySqlite<Set<Album>>(new AlbumSelector()).execute(this, filter);
	}

	@Override
	public Set<Artist> getArtist(Filter<String> filter)
	{
		return new QuerySqlite<Set<Artist>>(new ArtistSelector()).execute(this, filter);
	}

	@Override
	public void read(String query,
						  Database.DbReadOp<Cursor> readOp)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		try
		{
			readOp.read(db.rawQuery(query, null));
		}
		finally
		{
			db.close();
		}
	}

	@Override
	public void write(DbWriteOp<SQLiteDatabase> writeOp)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		try
		{
			writeOp.write(db);
		}
		finally
		{
			db.close();
		}
	}

	//--------------------------------------------- Observable

	private List<DbObserver> observers = new ArrayList<DbObserver>();

	public void notifyUpdate(){
		for(DbObserver observer : observers){
			observer.updated();
		}
	}

	public interface DbObserver
	{
		void updated();
	}

	public void addObserver(DbObserver observer)
	{
		observers.add(observer);
	}

	public void removeObserver(DbObserver observer)
	{
		observers.remove(observer);
	}
}