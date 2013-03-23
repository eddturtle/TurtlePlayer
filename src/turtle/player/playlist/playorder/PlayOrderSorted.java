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

import android.util.Log;
import turtle.player.model.Track;
import turtle.player.model.TrackBundle;
import turtle.player.persistance.framework.executor.OperationExecutor;
import turtle.player.persistance.framework.paging.Paging;
import turtle.player.persistance.framework.sort.OrderSet;
import turtle.player.persistance.framework.sort.SortOrder;
import turtle.player.persistance.source.sql.First;
import turtle.player.persistance.source.sqlite.QuerySqlite;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.mapping.TrackCreator;
import turtle.player.playlist.Playlist;

import java.util.ArrayList;
import java.util.List;

public class PlayOrderSorted extends AbstractPlayOrderStrategy
{

	private final Playlist playlist;
	private final TurtleDatabase db;

	public PlayOrderSorted(final TurtleDatabase db,
								  final Playlist playlist)
	{
		this.playlist = playlist;
		this.db = db;
	}

	public List<Track> getNext(Track currTrack, int n)
	{
		return getTracks(currTrack, new DefaultOrder(SortOrder.ASC), n);
	}

	public List<Track> getPrevious(Track currTrack, int n)
	{
		return getTracks(currTrack, new DefaultOrder(SortOrder.DESC), n);
	}

	private List<Track> getTracks(Track currTrack, OrderSet order, int n)
	{
		Track previous = currTrack;
		List<Track> trackList = new ArrayList<Track>();
		for(int i = 0; i < n ; i++){
			previous = get(previous, order);
			trackList.add(previous);
		}
		return trackList;
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
