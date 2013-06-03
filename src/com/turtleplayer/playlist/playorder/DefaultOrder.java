package com.turtleplayer.playlist.playorder;

import com.turtleplayer.model.Track;
import com.turtleplayer.persistance.framework.sort.FieldOrder;
import com.turtleplayer.persistance.framework.sort.OrderSet;
import com.turtleplayer.persistance.framework.sort.SortOrder;
import com.turtleplayer.persistance.turtle.db.structure.Tables;

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

public class DefaultOrder extends OrderSet<Tables.Tracks>
{
	public DefaultOrder(SortOrder sortOrder)
	{
		super(new FieldOrder<Tables.Tracks, Track, String>(Tables.ArtistsReadable.ARTIST, sortOrder),
				  new FieldOrder<Tables.Tracks, Track, String>(Tables.AlbumsReadable.ALBUM, sortOrder),
				  new FieldOrder<Tables.Tracks, Track, Integer>(Tables.Tracks.NUMBER, sortOrder),
				  new FieldOrder<Tables.Tracks, Track, String>(Tables.SongsReadable.TITLE, sortOrder)
		);
	}
}
