package turtle.player.persistance.turtle.selector;

import android.database.Cursor;
import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Track;
import turtle.player.persistance.source.sql.QuerySelectorTable;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.persistance.turtle.db.structure.Tables;

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

public class TrackQuerySelector extends QuerySelectorTable<Track>
{
	public TrackQuerySelector()
	{
		super(Tables.TRACKS);
	}

	@Override
	public Track createPart(Cursor cursor)
	{
		return new Track(
				  cursor.getString(cursor.getColumnIndex(Tables.TRACKS.TITLE.getName())),
				  cursor.getInt(cursor.getColumnIndex(Tables.TRACKS.NUMBER.getName())),
				  new Artist(cursor.getString(cursor.getColumnIndex(Tables.TRACKS.ARTIST.getName()))),
				  new Album(cursor.getString(cursor.getColumnIndex(Tables.TRACKS.ALBUM.getName()))),
				  cursor.getDouble(cursor.getColumnIndex(Tables.TRACKS.LENGTH.getName())),
				  cursor.getString(cursor.getColumnIndex(Tables.TRACKS.SRC.getName())),
				  cursor.getString(cursor.getColumnIndex(Tables.TRACKS.ROOTSRC.getName())),
				  cursor.getString(cursor.getColumnIndex(Tables.TRACKS.ALBUMART.getName()))
		);
	}
}
