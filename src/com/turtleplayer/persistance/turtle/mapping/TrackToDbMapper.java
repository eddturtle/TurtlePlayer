package com.turtleplayer.persistance.turtle.mapping;

import com.turtleplayer.model.Track;
import com.turtleplayer.persistance.source.sql.QueryGeneratorTable;
import com.turtleplayer.persistance.turtle.db.structure.Tables;

import android.content.ContentValues;

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

	public ContentValues create(Track track)
	{
		final ContentValues values = new ContentValues();

		values.put(Tables.TRACKS.TITLE.getName(), track.GetTitle());
		values.put(Tables.TRACKS.NUMBER.getName(), track.GetNumber());
		values.put(Tables.TRACKS.ARTIST.getName(), track.GetArtist().getId());
		values.put(Tables.TRACKS.ALBUM.getName(), track.GetAlbum().getId());
		values.put(Tables.TRACKS.GENRE.getName(), track.GetGenre().getId());
		values.put(Tables.TRACKS.SRC.getName(), track.GetSrc());
		values.put(Tables.TRACKS.ROOTSRC.getName(), track.GetRootSrc());

		return  values;
	}
}
