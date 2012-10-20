package turtle.player.persistance.turtle.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * TURTLE PLAYER
 * <p/>
 * Licensed under MIT & GPL
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 * <p/>
 * More Information @ www.turtle-player.co.uk
 *
 * @author Simon Honegger (Hoene84)
 */

public class TurtleDatabaseImpl extends SQLiteOpenHelper
{

	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "TurtlePlayer";

	public TurtleDatabaseImpl(Context context)
	{

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String CREATE_TABLE = "CREATE TABLE " + TurtleDatabase.TABLE_NAME + " ("
				  + TurtleDatabase.KEY_ID + " INTEGER PRIMARY KEY, "
				  + TurtleDatabase.KEY_TITLE + " TEXT, "
				  + TurtleDatabase.KEY_NUMBER + " INTEGER, "
				  + TurtleDatabase.KEY_ARTIST + " TEXT, "
				  + TurtleDatabase.KEY_ALBUM + " TEXT, "
				  + TurtleDatabase.KEY_LENGTH + " REAL, "
				  + TurtleDatabase.KEY_SRC + " TEXT, "
				  + TurtleDatabase.KEY_ROOTSRC + " TEXT, "
				  + TurtleDatabase.KEY_ALBUMART + " TEXT);";
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db,
								 int oldVersion,
								 int newVersion)
	{db.execSQL("DROP TABLE IF EXISTS " + TurtleDatabase.TABLE_NAME);
		onCreate(db);
	}
}
