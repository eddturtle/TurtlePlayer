package com.turtleplayer.persistance.turtle.mapping;

import com.turtleplayer.model.Album;
import com.turtleplayer.model.Artist;
import com.turtleplayer.model.Genre;
import com.turtleplayer.model.Track;
import com.turtleplayer.persistance.framework.creator.Creator;
import com.turtleplayer.persistance.turtle.db.structure.Tables;

import android.database.Cursor;

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

public class TrackCreator implements Creator<Track, Cursor>
{
	public Track create(Cursor cursor)
	{
		return new Track(
				  cursor.getString(cursor.getColumnIndex(Tables.TRACKS.TITLE.getName())),
				  cursor.getInt(cursor.getColumnIndex(Tables.TRACKS.NUMBER.getName())),
				  new Artist(cursor.getString(cursor.getColumnIndex(Tables.TRACKS.ARTIST.getName()))),
				  new Album(cursor.getString(cursor.getColumnIndex(Tables.TRACKS.ALBUM.getName()))),
				  new Genre(cursor.getString(cursor.getColumnIndex(Tables.TRACKS.GENRE.getName()))),
				  cursor.getString(cursor.getColumnIndex(Tables.TRACKS.SRC.getName())),
				  cursor.getString(cursor.getColumnIndex(Tables.TRACKS.ROOTSRC.getName()))
		);
	}
}
