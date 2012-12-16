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

package turtle.player.playlist.playorder;

import android.database.Cursor;
import android.util.Log;
import turtle.player.model.Track;
import turtle.player.persistance.framework.executor.OperationExecutor;
import turtle.player.persistance.framework.mapping.Mapping;
import turtle.player.persistance.framework.paging.Paging;
import turtle.player.persistance.framework.sort.Order;
import turtle.player.persistance.framework.sort.OrderSet;
import turtle.player.persistance.framework.sort.SortOrder;
import turtle.player.persistance.source.sql.First;
import turtle.player.persistance.source.sql.MappingTable;
import turtle.player.persistance.source.sql.query.OrderClause;
import turtle.player.persistance.source.sql.query.Select;
import turtle.player.persistance.source.sql.query.WhereClause;
import turtle.player.persistance.source.sqlite.QuerySqlite;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.mapping.TrackCreator;
import turtle.player.playlist.Playlist;

import java.util.Arrays;

public class PlayOrderSorted implements PlayOrderStrategy
{

	private Playlist playlist;
	private TurtleDatabase db;

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

	private Track get(Track currTrack, OrderSet order)
	{
		OrderSet currOrder = order;
		while(!currOrder.isEmpty())
		{
			Log.v(PlayOrderSorted.class.getName(),
					  "Generate Paging Filters from: " + order);
			Log.v(PlayOrderSorted.class.getName(),
					  "resulting in Paging Filters : " + Paging.getFilter(playlist.getFilter(), currTrack, currOrder));
			Track nextTrack = OperationExecutor.execute(
				  db,
				  new QuerySqlite<Track>(
							 Paging.getFilter(playlist.getFilter(), currTrack, currOrder),
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
