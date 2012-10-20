package turtle.player.persistance.turtle.selector;

import android.database.Cursor;
import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Track;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.persistance.source.sql.SelectorTable;

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

public class TrackSelector extends SelectorTable<Track>
{
	public TrackSelector()
	{
		super(TurtleDatabase.TABLE_NAME);
	}

	@Override
	public Track createPart(Cursor cursor)
	{
		return new Track(
				  cursor.getString(cursor.getColumnIndex(TurtleDatabase.KEY_TITLE)),
				  Integer.parseInt(cursor.getString(cursor.getColumnIndex(TurtleDatabase.KEY_NUMBER))),
				  new Artist(cursor.getString(cursor.getColumnIndex(TurtleDatabase.KEY_ARTIST))),
				  new Album(cursor.getString(cursor.getColumnIndex(TurtleDatabase.KEY_ALBUM))),
				  cursor.getDouble(cursor.getColumnIndex(TurtleDatabase.KEY_LENGTH)),
				  cursor.getString(cursor.getColumnIndex(TurtleDatabase.KEY_SRC)),
				  cursor.getString(cursor.getColumnIndex(TurtleDatabase.KEY_ROOTSRC)),
				  cursor.getString(cursor.getColumnIndex(TurtleDatabase.KEY_ALBUMART))
		);
	}
}
