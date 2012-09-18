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
import android.database.DatabaseUtils;
import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Track;


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

        try{
            ContentValues values;

            for (Track t : tList)
            {
                values = new ContentValues();

                values.put(KEY_TITLE, t.GetTitle());
                values.put(KEY_NUMBER, t.GetNumber());
                values.put(KEY_ARTIST, t.GetArtist().getName());
                values.put(KEY_ALBUM, t.GetAlbum().getName());
                values.put(KEY_LENGTH, t.GetLength());
                values.put(KEY_SRC, t.GetSrc());
                values.put(KEY_ROOTSRC, t.GetRootSrc());
                values.put(KEY_HASALBUMART, this.BooleanToInt(t.HasAlbumArt()));

                db.insert(TABLE_NAME, null, values);
                values = null;
            }
        }
        finally {
            if(db != null)
            {
                db.close();
            }
        }
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
                        this.IntToBoolean(cursor.getInt(8))
                );
				tList.add(t);
				
			} while (cursor.moveToNext());
		}
		
		return tList;
	}
	
	
	public void Clear()
	{
		SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
        finally {
            if(db != null)
            {
                db.close();
            }
        }
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