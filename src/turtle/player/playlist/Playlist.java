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
import turtle.player.model.Instance;
import turtle.player.model.Track;
import turtle.player.model.TrackBundle;
import turtle.player.persistance.framework.db.ObservableDatabase;
import turtle.player.persistance.framework.filter.FieldFilter;
import turtle.player.persistance.framework.filter.Operator;
import turtle.player.persistance.source.relational.FieldPersistable;
import turtle.player.persistance.turtle.FsReader;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.filter.FilterSet;
import turtle.player.playlist.playorder.PlayOrderRandom;
import turtle.player.playlist.playorder.PlayOrderSorted;
import turtle.player.playlist.playorder.PlayOrderStrategy;
import turtle.player.preferences.Key;
import turtle.player.preferences.Keys;
import turtle.player.preferences.Preferences;
import turtle.player.preferences.PreferencesObserver;
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

	private PlayOrderStrategy playOrderStrategy;
	private final TurtleDatabase db;
	private final Set<Filter> filters = new HashSet<Filter>();

	public Playlist(Context mainContext, TurtleDatabase db)
	{
		// Location, Repeat, Shuffle (Remember Trailing / on Location)
		preferences = new Preferences(mainContext);
		this.db = db;
		init();
	}

	private void init()
	{
		preferences.addObserver(new PreferencesObserver()
		{
			public void changed(Key key)
			{
				if (key.equals(Keys.SHUFFLE))
				{
					setPlayOrderStrategyAccordingPreferences();
				}
			}
		});
		setPlayOrderStrategyAccordingPreferences();
	}

	private void setPlayOrderStrategyAccordingPreferences()
	{
		playOrderStrategy = preferences.GetShuffle() ?
				  new PlayOrderRandom(db, this):
				  new PlayOrderSorted(db, this);
	}

	public <O> Filter addFilter(FieldPersistable<Track, O> field, Track track){
		Filter filter = new FieldFilter<Track, O>(field, Operator.EQ, field.getAsString(track));
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
	public TrackBundle enrich(Track track){
		return new TrackBundle(
				  track,
				  playOrderStrategy.getNext(track),
				  playOrderStrategy.getPrevious(track)
		);
	}


	public TrackBundle getNext(Track ofTrack)
	{
		PerformanceMeasure.start(durations.NEXT.name());
		List<Track> nextCandidates = playOrderStrategy.getNext(ofTrack, 2);

		TrackBundle trackBundle = new TrackBundle(
				  nextCandidates.size() > 0 ? nextCandidates.get(0) : null,
				  nextCandidates.size() > 1 ? nextCandidates.get(1) : null,
				  ofTrack
		);

		PerformanceMeasure.stop(durations.NEXT.name());

		return trackBundle;
	}

	public TrackBundle getPrevious(Track ofTrack)
	{
		PerformanceMeasure.start(durations.PREV.name());

		List<Track> previousCandidates = playOrderStrategy.getPrevious(ofTrack, 2);

		TrackBundle trackBundle = new TrackBundle(
				  previousCandidates.size() > 0 ? previousCandidates.get(0) : null,
				  ofTrack,
				  previousCandidates.size() > 1 ? previousCandidates.get(1) : null
		);

		PerformanceMeasure.stop(durations.PREV.name());

		return trackBundle;
	}

	public int countAvailableTracks(){
		return db.countAvailableTracks(getFilter());
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
							final File mediaPath = preferences.GetMediaPath();

							for (PlaylistObserver observer : observers)
							{
								observer.startRescan(mediaPath);
							}

							ObservableDatabase.DbObserver dbObserver = new ObservableDatabase.DbObserver()
							{
								public void updated(Instance instance)
								{
									for (PlaylistObserver observer : observers)
									{
										observer.trackAdded();
									}

								}

								public void cleared()
								{
									//do nothing
								}
							};

							db.addObserver(dbObserver);
							FsReader.scanDir(db, mediaPath);
							db.removeObserver(dbObserver);

						} catch (NullPointerException e)
						{
							Log.v(Preferences.TAG, e.getMessage());
						} finally
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
		void trackAdded();

		void startRescan(File mediaPath);

		void endRescan();

		void startUpdatePlaylist();

		void endUpdatePlaylist();

		void filterAdded(Filter filter);

		void filterRemoved(Filter filter);

	}

	public static abstract class PlaylistFilterChangeObserver implements PlaylistObserver
	{
		public void trackAdded(){/*doNothing*/}

		public void startRescan(File mediaPath){/*doNothing*/}

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