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
import turtle.player.persistance.framework.db.ObservableDatabase;
import turtle.player.persistance.source.sql.query.WhereClause;
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
	public Preferences preferences;
	public Stats stats = new Stats();

	private PlayOrderStrategy playOrderStrategy;
	private TurtleDatabase db;
	private Set<Filter<WhereClause>> filters = new HashSet<Filter<WhereClause>>();

	private Track currTrack = null;

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

	public Filter<WhereClause> getFilter()
	{
		return filters.isEmpty() ? null : new FilterSet<WhereClause>(filters);
	}

	public Track getNext()
	{
		PerformanceMeasure.start(durations.NEXT.name());

		Track track = playOrderStrategy.getNext(getCurrTrack());

		PerformanceMeasure.stop(durations.NEXT.name());

		return track;
	}

	public Track getPrevious()
	{
		PerformanceMeasure.start(durations.PREV.name());

		Track track = playOrderStrategy.getPrevious(getCurrTrack());

		PerformanceMeasure.stop(durations.PREV.name());

		return track;
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
							Log.v(preferences.GetTag(), e.getMessage());
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

	public Track getCurrTrack()
	{
		return currTrack;
	}

	public void setCurrTrack(Track currTrack)
	{
		this.currTrack = currTrack;
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

	List<PlaylistObserver> observers = new ArrayList<PlaylistObserver>();

	public interface PlaylistObserver
	{
		void trackAdded();

		void startRescan(File mediaPath);

		void endRescan();

		void startUpdatePlaylist();

		void endUpdatePlaylist();

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