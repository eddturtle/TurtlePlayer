package com.turtleplayer.persistance.turtle.mapping;

import com.turtleplayer.model.AlbumArtLocation;
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

public class AlbumArtLoactionToDbMapper extends QueryGeneratorTable<AlbumArtLocation>
{
	public AlbumArtLoactionToDbMapper()
	{
		super(Tables.ALBUM_ART_LOCATIONS);
	}

	public ContentValues create(AlbumArtLocation albumArtLocation)
	{
		final ContentValues values = new ContentValues();

		values.put(Tables.ALBUM_ART_LOCATIONS.PATH.getName(), albumArtLocation.getPath());
		values.put(Tables.ALBUM_ART_LOCATIONS.ALBUM_ART_PATH.getName(), albumArtLocation.getAlbumArtpath());

		return  values;
	}
}
