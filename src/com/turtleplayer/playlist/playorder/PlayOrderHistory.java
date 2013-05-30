package com.turtleplayer.playlist.playorder;
///*
// *
// * TURTLE PLAYER
// *
// * Licensed under MIT & GPL
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
// * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
// * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
// * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
// * OR OTHER DEALINGS IN THE SOFTWARE.
// *
// * Created by Edd Turtle (www.eddturtle.co.uk)
// * More Information @ www.turtle-player.co.uk
// *
// */
//
//package com.turtleplayer.playlist.playorder;
//
//import android.util.Log;
//import com.turtleplayer.model.Track;
//import com.turtleplayer.persistance.framework.executor.OperationExecutor;
//import com.turtleplayer.persistance.framework.paging.Paging;
//import com.turtleplayer.persistance.framework.sort.OrderSet;
//import com.turtleplayer.persistance.framework.sort.SortOrder;
//import com.turtleplayer.persistance.source.sql.First;
//import com.turtleplayer.persistance.source.sqlite.QuerySqlite;
//import com.turtleplayer.persistance.turtle.db.TurtleDatabase;
//import com.turtleplayer.persistance.turtle.db.structure.Tables;
//import com.turtleplayer.persistance.turtle.mapping.TrackCreator;
//import com.turtleplayer.playlist.Playlist;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class PlayOrderHistory extends AbstractPlayOrderStrategy
//{
//
//	private final Playlist playlist;
//
//	public PlayOrderHistory(final Playlist playlist)
//	{
//		this.playlist = playlist;
//	}
//
//	public List<Track> getNext(Track currTrack, int n)
//	{
//		return getTracks(currTrack, new DefaultOrder(SortOrder.ASC), n);
//	}
//
//	public List<Track> getPrevious(Track currTrack, int n)
//	{
//		return getTracks(currTrack, new DefaultOrder(SortOrder.DESC), n);
//	}
//
//	private List<Track> getTracks(Track currTrack, OrderSet order, int n)
//	{
//		Track previous = currTrack;
//		List<Track> trackList = new ArrayList<Track>();
//		for(int i = 0; i < n ; i++){
//			previous = get(previous, order);
//			trackList.add(previous);
//		}
//		return trackList;
//	}
//
//}
