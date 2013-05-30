/*
 *
 * TURTLE PLAYER
 *
 * Licensed under MIT & GPL
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Created by Edd Turtle (www.eddturtle.co.uk)
 * More Information @ www.turtle-player.co.uk
 *
 */

package com.turtleplayer.playlist.playorder;

import android.util.Log;

import com.turtleplayer.model.Track;
import com.turtleplayer.persistance.framework.executor.OperationExecutor;
import com.turtleplayer.persistance.framework.paging.Paging;
import com.turtleplayer.persistance.framework.sort.OrderSet;
import com.turtleplayer.persistance.framework.sort.SortOrder;
import com.turtleplayer.persistance.source.sql.First;
import com.turtleplayer.persistance.source.sqlite.QuerySqlite;
import com.turtleplayer.persistance.turtle.db.TurtleDatabase;
import com.turtleplayer.persistance.turtle.db.structure.Tables;
import com.turtleplayer.persistance.turtle.mapping.TrackCreator;
import com.turtleplayer.playlist.Playlist;

public class PlayOrderSorted implements PlayOrderStrategy
{

	private final Playlist playlist;
	private final TurtleDatabase db;

	public PlayOrderSorted(final TurtleDatabase db,
								  final Playlist playlist)
	{
		this.playlist = playlist;
		this.db = db;
	}

	public Track getNext(Track currTrack)
	{
		return get(currTrack, new DefaultOrder(SortOrder.ASC));
	}

	public Track getPrevious(Track currTrack)
	{
		return get(currTrack, new DefaultOrder(SortOrder.DESC));
	}

	private Track get(Track ofTrack, OrderSet order)
	{
		OrderSet currOrder = order;
		while(!currOrder.isEmpty())
		{
			Log.v(PlayOrderSorted.class.getName(),
					  "Generate Paging Filters from: " + order);
			Log.v(PlayOrderSorted.class.getName(),
					  "resulting in Paging Filters : " + Paging.getFilter(playlist.getCompressedFilter(), ofTrack, currOrder));
			Track nextTrack = OperationExecutor.execute(
				  db,
				  new QuerySqlite<Track>(
							 Paging.getFilter(playlist.getCompressedFilter(), ofTrack, currOrder),
							 order,
							 new First<Track>(Tables.TRACKS, new TrackCreator())
				  )
			);
			if(nextTrack != null){
				return nextTrack;
			}
			currOrder = currOrder.removeLast();
		}
		return null;
	}
}
