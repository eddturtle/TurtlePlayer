/*
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
 * Created by Edd Turtle (www.eddturtle.co.uk)
 * More Information @ www.turtle-player.co.uk
 * 
 */

// Package
package turtle.player;

// Import - Java
import java.util.ArrayList;
import java.util.List;
 
// Import - Android Content
import android.content.ContentValues;
import android.content.Context;

// Import - Android Database
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;
import android.database.DatabaseUtils;


public class Database extends SQLiteOpenHelper
{
	
	// ========================================= //
	// 	Attributes
	// ========================================= //
	
	private static final int DATABASE_VERSION = 1;
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
	private static final String KEY_HASALBUMART = "hasAlbumArt";
	
	
	// ========================================= //
	// 	Constructor, onCreate & onDestroy
	// ========================================= //
	
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
				+ KEY_HASALBUMART + " INTEGER);";
		db.execSQL(CREATE_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	
	// ========================================= //
	// 	Push to Database
	// ========================================= //
	
	public void Push(List<Track> tList)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values;
		
		for (Track t : tList)
		{
			values = new ContentValues();
			
			values.put(KEY_TITLE, t.GetTitle());
			values.put(KEY_NUMBER, t.GetNumber());
			values.put(KEY_ARTIST, t.GetArtist());
			values.put(KEY_ALBUM, t.GetAlbum());
			values.put(KEY_LENGTH, t.GetLength());
			values.put(KEY_SRC, t.GetSrc());
			values.put(KEY_ROOTSRC, t.GetRootSrc());
			values.put(KEY_HASALBUMART, this.BooleanToInt(t.HasAlbumArt()));
			
			db.insert(TABLE_NAME, null, values);
			values = null;
		}
		
		db.close();
	}
	
	
	// ========================================= //
	// 	Pull from Database
	// ========================================= //
	
	public List<Track> Pull()
	{
		List<Track> tList = new ArrayList<Track>();
		
		String query = "SELECT * FROM " + TABLE_NAME;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		int count = 0;
		Track t;
		
		if (cursor.moveToFirst())
		{
			do
			{
				t = new Track();
				
				t.SetId(count);
				t.SetTitle(cursor.getString(1));
				t.SetNumber(Integer.parseInt(cursor.getString(2)));
				t.SetArtist(cursor.getString(3));
				t.SetAlbum(cursor.getString(4));
				t.SetLength(cursor.getDouble(5));
				t.SetSrc(cursor.getString(6));
				t.SetRootSrc(cursor.getString(7));
				t.SetAlbumArt(this.IntToBoolean(cursor.getInt(8)));
				
				tList.add(t);
				
				count++;
				t = null;
				
			} while (cursor.moveToNext());
		}
		
		return tList;
	}
	
	
	public void Clear()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		
		onCreate(db);
	}
	
	public boolean IsEmpty()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		long rows = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
		
		if (rows == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	
	public boolean Exists()
	{
		SQLiteDatabase check = null;
		
		try
		{
			check = SQLiteDatabase.openDatabase("/data/data/hnd.turtle.player/databases/TurtlePlayer", null, SQLiteDatabase.OPEN_READONLY);
			check.close();
		}
		catch (SQLiteException e)
		{
			// No Database
		}
		
		if (check != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public int BooleanToInt(boolean val)
	{
		if (val)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
	
	public boolean IntToBoolean(int item)
	{
		if (item == 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}