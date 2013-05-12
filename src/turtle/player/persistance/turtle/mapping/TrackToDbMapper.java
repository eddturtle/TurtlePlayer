package turtle.player.persistance.turtle.mapping;

import android.content.ContentValues;
import turtle.player.model.Track;
import turtle.player.persistance.source.sql.QueryGeneratorTable;
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

public class TrackToDbMapper extends QueryGeneratorTable<Track>
{
	public TrackToDbMapper()
	{
		super(Tables.TRACKS);
	}

	public Tables.Tracks get()
	{
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public ContentValues create(Track track)
	{
		final ContentValues values = new ContentValues();

		values.put(Tables.Tracks.TITLE.getName(), track.getSongName());
		values.put(Tables.Tracks.NUMBER.getName(), track.GetNumber());
		values.put(Tables.Tracks.ARTIST.getName(), track.getArtistId());
		values.put(Tables.Tracks.ALBUM.getName(), track.getAlbumId());
		values.put(Tables.Tracks.GENRE.getName(), track.getGenreId());
		values.put(Tables.FsObjects.PATH.getName(), track.getPath());
		values.put(Tables.FsObjects.NAME.getName(), track.getPath());

		return  values;
	}
}
