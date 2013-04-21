package turtle.player.persistance.turtle.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import turtle.player.persistance.framework.db.Database;
import turtle.player.persistance.framework.sort.Order;
import turtle.player.persistance.framework.sort.SortOrder;
import turtle.player.persistance.source.relational.FieldPersistable;
import turtle.player.persistance.source.relational.Table;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.playlist.playorder.DefaultOrder;

import java.util.Arrays;

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

public abstract class TurtleDatabaseImpl extends SQLiteOpenHelper
{

	public static final int DATABASE_VERSION = 9;
	public static final String DATABASE_NAME = "TurtlePlayer";

	public TurtleDatabaseImpl(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String createTracksSql = "CREATE TABLE " + Tables.TRACKS.getName() + " ("
				  + Tables.TRACKS.TITLE.getName() + " TEXT COLLATE LOCALIZED, "
				  + Tables.TRACKS.NUMBER.getName() + " INTEGER, "
				  + Tables.TRACKS.ARTIST.getName() + " TEXT COLLATE LOCALIZED, "
				  + Tables.TRACKS.ALBUM.getName() + " TEXT COLLATE LOCALIZED, "
				  + Tables.TRACKS.GENRE.getName() + " TEXT, "
				  + Tables.TRACKS.PATH.getName() + " TEXT PRIMARY KEY, "
				  + Tables.TRACKS.NAME.getName() + " TEXT);";
		db.execSQL(createTracksSql);

		for(FieldPersistable<?,?> field : Arrays.asList(
				  Tables.TRACKS.ARTIST,
				  Tables.TRACKS.ALBUM,
				  Tables.TRACKS.NUMBER,
				  Tables.TRACKS.TITLE
				  ))
		{
			String createTracksIndeces = "CREATE INDEX " + Tables.TRACKS.getName() + "_" + field.getName() + "_idx " +
					  " ON " + Tables.TRACKS.getName() + "(" + field.getName() + ");";
			db.execSQL(createTracksIndeces);
		}

		String createAlbumArtSql = "CREATE TABLE " + Tables.ALBUM_ART_LOCATIONS.getName() + " ("
				  + Tables.ALBUM_ART_LOCATIONS.PATH.getName() + " TEXT PRIMARY KEY, "
				  + Tables.ALBUM_ART_LOCATIONS.ALBUM_ART_PATH.getName() + " TEXT);";
		db.execSQL(createAlbumArtSql);

		for(FieldPersistable<?,?> field : Arrays.asList(
				  Tables.ALBUM_ART_LOCATIONS.PATH
		))
		{
			String createAlbumArtIndeces = "CREATE INDEX " + Tables.ALBUM_ART_LOCATIONS.getName() + "_" + field.getName() + "_idx " +
					  " ON " + Tables.ALBUM_ART_LOCATIONS.getName() + "(" + field.getName() + ");";
			db.execSQL(createAlbumArtIndeces);
		}

		String createDirsSql = "CREATE TABLE " + Tables.DIRS.getName() + " ("
				  + Tables.DIRS.NAME.getName() + " TEXT COLLATE LOCALIZED, "
				  + Tables.DIRS.PATH.getName() + " TEXT COLLATE LOCALIZED,"
				  + " PRIMARY KEY (" + Tables.DIRS.NAME.getName() + ", " + Tables.DIRS.PATH.getName() + "));";
		db.execSQL(createDirsSql);

		for(FieldPersistable<?,?> field : Arrays.asList(
				  Tables.DIRS.PATH,
				  Tables.DIRS.NAME
		))
		{
			String createAlbumArtIndeces = "CREATE INDEX " + Tables.DIRS.getName() + "_" + field.getName() + "_idx " +
					  " ON " + Tables.DIRS.getName() + "(" + field.getName() + ");";
			db.execSQL(createAlbumArtIndeces);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db,
								 int oldVersion,
								 int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + Tables.TRACKS.getName());
		db.execSQL("DROP TABLE IF EXISTS " + Tables.ALBUM_ART_LOCATIONS.getName());
		db.execSQL("DROP TABLE IF EXISTS " + Tables.DIRS.getName());

		onCreate(db);
		dbResetted();
	}

	public abstract void dbResetted();
}
