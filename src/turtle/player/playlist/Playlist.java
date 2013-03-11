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

package turtle.player.playlist;


import android.content.Context;
import android.util.Log;
import turtle.player.Stats;
import turtle.player.common.filefilter.FileFilters;
import turtle.player.model.Instance;
import turtle.player.model.Track;
import turtle.player.model.TrackBundle;
import turtle.player.persistance.framework.db.ObservableDatabase;
import turtle.player.persistance.framework.executor.OperationExecutor;
import turtle.player.persistance.framework.filter.FieldFilter;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.filter.FilterSet;
import turtle.player.persistance.framework.filter.Operator;
import turtle.player.persistance.framework.sort.RandomOrder;
import turtle.player.persistance.source.relational.FieldPersistable;
import turtle.player.persistance.source.sql.First;
import turtle.player.persistance.source.sqlite.QuerySqlite;
import turtle.player.persistance.turtle.FsReader;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.mapping.TrackCreator;
import turtle.player.playlist.playorder.PlayOrderSorted;
import turtle.player.playlist.playorder.PlayOrderStrategy;
import turtle.player.preferences.Preferences;
import turtle.player.util.dev.PerformanceMeasure;

import java.io.File;
import java.util.*;

public class Playlist
{

	//Log Constants
	private enum durations
	{
		NEXT,
		PREV,
		PULL
	}

	// Not in ClassDiagram
	public final Preferences preferences;
	public final Stats stats = new Stats();

	private final TurtleDatabase db;
	private final Set<Filter> filters = new HashSet<Filter>();

	public Playlist(Context mainContext, TurtleDatabase db)
	{
		// Location, Repeat, Shuffle (Remember Trailing / on Location)
		preferences = new Preferences(mainContext);
		this.db = db;
	}

	public <O> Filter addFilter(FieldPersistable<Track, O> field, Track track){
		Filter filter = new FieldFilter<Track, O>(field, Operator.EQ, field.get(track));
		filters.add(filter);
		for (PlaylistObserver observer : observers)
		{
			observer.filterAdded(filter);
		}
		return filter;
	}

	public void removeFilter(Filter filter){
		filters.remove(filter);
		for (PlaylistObserver observer : observers)
		{
			observer.filterRemoved(filter);
		}
	}

	public Filter getFilter()
	{
		return filters.isEmpty() ? new FilterSet() : new FilterSet(filters);
	}

	/**@
	 * param track
	 * @return adds additional information to track
	 */
	public TrackBundle enrich(PlayOrderStrategy strategy, Track track){
		return new TrackBundle(
				  track,
				  strategy.getNext(track),
				  strategy.getPrevious(track)
		);
	}

	public Track getTrack(String src)
	{
		return OperationExecutor.execute(
				  db,
				  new QuerySqlite<Track>(
							 new FilterSet(getFilter(), new FieldFilter<Track, String>(Tables.TRACKS.SRC, Operator.EQ, src)),
							 new First<Track>(Tables.TRACKS, new TrackCreator())
				  )
		);
	}


	public Track getNext(PlayOrderStrategy strategy, Track ofTrack)
	{
		return strategy.getNext(ofTrack);
	}

	public Track getPrevious(PlayOrderStrategy strategy, Track ofTrack)
	{
		return strategy.getPrevious(ofTrack);
	}

	public Track getRandom()
	{
		return OperationExecutor.execute(db,
				  new QuerySqlite<Track>(
							 getFilter(),
							 new RandomOrder(),
							 new First<Track>(Tables.TRACKS, new TrackCreator())));
	}

	public void UpdateList()
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				for (PlaylistObserver observer : observers)
				{
					observer.startUpdatePlaylist();
				}

				try
				{
					if (db.isEmpty(null))
					{
						try
						{
							final String mediaPath = preferences.getExitstingMediaPath().toString();

							Collection<String> mediaFilePaths = FsReader.getMediaFilesPaths(mediaPath, FileFilters.PLAYABLE_FILES_FILTER, true, false);

							for (PlaylistObserver observer : observers)
							{
								observer.startRescan(mediaFilePaths);
							}

							ObservableDatabase.DbObserver dbObserver = new ObservableDatabase.DbObserver()
							{
								public void updated(Instance instance)
								{
									for (PlaylistObserver observer : observers)
									{
										observer.trackAdded(instance);
									}

								}

								public void cleared()
								{
									//do nothing
								}
							};

							db.addObserver(dbObserver);

							FsReader.scanFiles(mediaFilePaths, db, mediaPath);
							db.removeObserver(dbObserver);

						}
						catch (NullPointerException e)
						{
							Log.v(Preferences.TAG, e.getMessage());
						}
						finally
						{
							for (PlaylistObserver observer : observers)
							{
								observer.endRescan();
							}
						}
					}
				} finally
				{
					for (PlaylistObserver observer : observers)
					{
						observer.endUpdatePlaylist();
					}
				}
			}
		}).start();
	}

	public Collection<Track> getCurrTracks()
	{
		return db.getTracks(getFilter());
	}

	public int Length()
	{
		return getCurrTracks().size();
	}

	public boolean IsEmpty()
	{
		return db.isEmpty(null);
	}

	public void DatabaseClear()
	{
		db.clear();
	}

	//------------------------------------------------------ 	Observable

	final List<PlaylistObserver> observers = new ArrayList<PlaylistObserver>();

	public interface PlaylistObserver
	{
		void trackAdded(Instance instance);

		void startRescan(Collection<String> mediaFilePaths);

		void endRescan();

		void startUpdatePlaylist();

		void endUpdatePlaylist();

		void filterAdded(Filter filter);

		void filterRemoved(Filter filter);

	}

	public static abstract class PlaylistFilterChangeObserver implements PlaylistObserver
	{
		public void trackAdded(Instance instance){/*doNothing*/}

		public void startRescan(Collection<String> mediaFilePaths){/*doNothing*/}

		public void endRescan(){/*doNothing*/}

		public void startUpdatePlaylist(){/*doNothing*/}

		public void endUpdatePlaylist(){/*doNothing*/}
	}

	public static abstract class PlaylistTrackChangeObserver implements PlaylistObserver{
		public void filterAdded(Filter filter){/*doNothing*/}

		public void filterRemoved(Filter filter){/*doNothing*/}
	}

	public void addObserver(PlaylistObserver observer)
	{
		observers.add(observer);
	}

	public void removeObserver(PlaylistObserver observer)
	{
		observers.remove(observer);
	}

}