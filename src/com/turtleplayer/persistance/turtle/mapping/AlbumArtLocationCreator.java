package com.turtleplayer.persistance.turtle.mapping;

import com.turtleplayer.model.*;
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

public class AlbumArtLocationCreator implements Creator<AlbumArtLocation, Cursor>
{
	public AlbumArtLocation create(Cursor cursor)
	{
		return new AlbumArtLocation(
				  cursor.getString(cursor.getColumnIndex(Tables.ALBUM_ART_LOCATIONS.PATH.getName())),
				  cursor.getString(cursor.getColumnIndex(Tables.ALBUM_ART_LOCATIONS.ALBUM_ART_PATH.getName()))
		);
	}
}
