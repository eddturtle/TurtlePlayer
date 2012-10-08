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

import java.util.ArrayList;
import java.util.List;

// Import - Android Content
import android.content.ContentValues;
import android.content.Context;

// Import - Android Database
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.DatabaseUtils;
import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Track;


public class Database extends SQLiteOpenHelper
{

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "TurtlePlayer";
	private static final String TABLE_NAME = "Tracks";

	private static final String KEY_ID = "id";
	private static final String KEY_TITLE = "title";
	private static final String KEY_NUMBER = "number";
	private static final String KEY_ARTIST = "artist";
	private static final String KEY_ALBUM = "album";
	private static final String KEY_LENGTH = "length";
	private static final String KEY_SRC = "src";
	private static final String KEY_ROOTSRC = "rootSrc";
	private static final String KEY_ALBUMART = "hasAlbumArt";


	public Database(Context context)
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
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public void push(final Track track)
	{

		write(new DbWriteOp()
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

				for (DbObserver observer : observers)
				{
					observer.trackAdded(track);
				}
			}
		});
	}

	public List<Track> pull()
	{
		return read(new DbReadOp<List<Track>>()
		{
			@Override
			public List<Track> read(SQLiteDatabase db)
			{
				List<Track> tList = new ArrayList<Track>();

				String query = "SELECT * FROM " + TABLE_NAME;

				Cursor cursor = db.rawQuery(query, null);

				Track t;

				if (cursor.moveToFirst())
				{
					do
					{
						t = new Track(
								  cursor.getString(1),
								  Integer.parseInt(cursor.getString(2)),
								  new Artist(cursor.getString(3)),
								  new Album(cursor.getString(4)),
								  cursor.getDouble(5),
								  cursor.getString(6),
								  cursor.getString(7),
								  cursor.getString(8)
						);
						tList.add(t);

					} while (cursor.moveToNext());
				}

				return tList;
			}
		});
	}


	public void clear()
	{
		write(new DbWriteOp()
		{
			@Override
			public void write(SQLiteDatabase db)
			{
				db.execSQL("DELETE FROM " + TABLE_NAME);
				for (DbObserver observer : observers)
				{
					observer.cleaned();
				}
			}
		});
	}

	public boolean isEmpty()
	{
		return read(new DbReadOp<Boolean>()
		{
			@Override
			public Boolean read(SQLiteDatabase db)
			{
				return DatabaseUtils.queryNumEntries(db, TABLE_NAME) == 0;
			}
		});
	}

	//--------------------------------------------- DB Access helper

	private interface DbReadOp<R>
	{
		public R read(SQLiteDatabase db);
	}

	private interface DbWriteOp
	{
		public void write(SQLiteDatabase db);
	}

	private <R> R read(DbReadOp<R> dbOperation)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		try
		{
			return dbOperation.read(db);
		} finally
		{
			db.close();
		}
	}

	private void write(DbWriteOp dbOperation)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		try
		{
			dbOperation.write(db);
		} finally
		{
			db.close();
		}
	}

	//--------------------------------------------- Observable

	List<DbObserver> observers = new ArrayList<DbObserver>();

	public interface DbObserver
	{
		void trackAdded(Track track);

		void cleaned();
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