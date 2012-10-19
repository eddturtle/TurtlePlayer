package turtle.player.persistance.sqlite;

import android.database.Cursor;
import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.persistance.TurtleDatabase;
import turtle.player.persistance.sql.Sql;

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

public class ArtistSelector extends SelectorForSetSqlite<Sql, Artist>
{
	@Override
	public Sql get()
	{
		return new Sql("SELECT DISTINCT " + TurtleDatabase.KEY_ARTIST + " FROM " + TurtleDatabase.TABLE_NAME);
	}

	@Override
	public Artist createPart(Cursor cursor)
	{
		return new Artist(cursor.getString(0));
	}
}
