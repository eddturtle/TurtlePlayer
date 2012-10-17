package turtle.player.persistance.sqlite;

import android.database.Cursor;
import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Track;
import turtle.player.persistance.TurtleDatabase;

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

public class TrackSelector extends SelectorForSetSqlite<String, Track>
{
	@Override
	public String get()
	{
		return "SELECT * FROM " + TurtleDatabase.TABLE_NAME;
	}

	@Override
	public Track createPart(Cursor cursor)
	{
		return new Track(
				  cursor.getString(1),
				  Integer.parseInt(cursor.getString(2)),
				  new Artist(cursor.getString(3)),
				  new Album(cursor.getString(4)),
				  cursor.getDouble(5),
				  cursor.getString(6),
				  cursor.getString(7),
				  cursor.getString(8)
		);
	}
}
