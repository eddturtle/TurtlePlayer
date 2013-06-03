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

package com.turtleplayer.playlist;


import android.content.Context;
import android.util.Log;
import com.turtleplayer.Stats;
import com.turtleplayer.common.filefilter.FileFilters;
import com.turtleplayer.controller.Observer;
import com.turtleplayer.model.*;
import com.turtleplayer.persistance.framework.executor.OperationExecutor;
import com.turtleplayer.persistance.framework.filter.FieldFilter;
import com.turtleplayer.persistance.framework.filter.Filter;
import com.turtleplayer.persistance.framework.filter.FilterSet;
import com.turtleplayer.persistance.framework.filter.Operator;
import com.turtleplayer.persistance.framework.sort.RandomOrder;
import com.turtleplayer.persistance.source.sql.First;
import com.turtleplayer.persistance.source.sqlite.QuerySqlite;
import com.turtleplayer.persistance.turtle.FsReader;
import com.turtleplayer.persistance.turtle.db.TurtleDatabase;
import com.turtleplayer.persistance.turtle.db.structure.Tables;
import com.turtleplayer.persistance.turtle.mapping.TrackCreator;
import com.turtleplayer.playlist.playorder.PlayOrderStrategy;
import com.turtleplayer.preferences.Keys;
import com.turtleplayer.preferences.Preferences;
import com.turtleplayer.util.Shorty;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class Playlist
{

	//Log Constants
	/*
	{
		NEXT,
		PREV,
		PULL
	}
	*/

	// Not in ClassDiagram
	public final Preferences preferences;
	public final Stats stats = new Stats();

	private final TurtleDatabase db;
	private final Set<Filter<? super Tables.Tracks>> filters = new HashSet<Filter<? super Tables.Tracks>>();

	private final ExecutorService fsScannerExecutorService = Executors.newSingleThreadExecutor();
	private Future<?> currentFuture = null;

	public Playlist(Context mainContext, TurtleDatabase db)
	{
		// Location, Repeat, Shuffle (Remember Trailing / on Location)
		preferences = new Preferences(mainContext);
		this.db = db;
	}

	/**
	 * @return true if the filter was not already there
	 */
	public boolean addFilter(Filter<? super Tables.Tracks> filter){
		boolean modified = filters.add(filter);
		if(modified)
		{
			for (PlaylistObserver observer : observers.values())
			{
				observer.filterAdded(filter);
			}
		}
		return modified;
	}

	public void clearFilters(){

		final Set<Filter<? super Tables.Tracks>> filtersToDelete = new HashSet<Filter<? super Tables.Tracks>>(filters);
		for(Filter<? super Tables.Tracks> filter : filtersToDelete)
		{
			removeFilter(filter);
		}
	}

	public boolean removeFilter(Filter<? super Tables.Tracks> filter){
		boolean modified = filters.remove(filter);

		if(modified)
		{
			for (PlaylistObserver observer : observers.values())
			{
				observer.filterRemoved(filter);
			}
		}
		return modified;
	}

	public Filter<? super Tables.Tracks> getCompressedFilter()
	{
		return filters.isEmpty() ? new FilterSet<Tables.Tracks>() : new FilterSet<Tables.Tracks>(filters);
	}

	public Set<Filter<? super Tables.Tracks>> getFilter()
	{
		return Collections.unmodifiableSet(filters);
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
		FSobject fsObject = new FSobject(src);
		return OperationExecutor.execute(
				  db,
				  new QuerySqlite<Tables.Tracks, Tables.Tracks, Track>(
							 new FilterSet<Tables.Tracks>(
										getCompressedFilter(),
										new FieldFilter<Tables.Tracks, Track, String>(Tables.FsObjects.NAME, Operator.EQ, fsObject.getPath()),
										new FieldFilter<Tables.Tracks, Track, String>(Tables.FsObjects.PATH, Operator.EQ, fsObject.getName())),
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
				  new QuerySqlite<Tables.Tracks, Tables.Tracks, Track>(
							 getCompressedFilter(),
							 new RandomOrder<Tables.Tracks>(),
							 new First<Track>(Tables.TRACKS, new TrackCreator())));
	}

	/**
	 * reads the stored state and calls the obsrver methods to adjust the ui,
	 */
	public void notifyInitialState()
	{
		if(!Shorty.isVoid(preferences.get(Keys.FS_SCAN_INTERRUPT_PATH))){
			for (PlaylistObserver observer : observers.values())
			{
				observer.startUpdatePlaylist();
				observer.startRescan(preferences.get(Keys.FS_SCAN_INTERRUPT_COUNT_ALL));
				observer.trackAdded(
						  preferences.get(Keys.FS_SCAN_INTERRUPT_PATH),
						  preferences.get(Keys.FS_SCAN_INTERRUPT_COUNT_PROCESSED)
				);
				observer.pauseRescan();
			}
		}
	}

	public void toggleFsScanPause()
	{
		if(fsScanActive())
		{
			interruptFsScan();
		}
		else
		{
			runFsScan();
		}
	}

	public boolean isFsScanNotStarted(){
		return !fsScanActive() && Shorty.isVoid(preferences.get(Keys.FS_SCAN_INTERRUPT_PATH));
	}

	private boolean fsScanActive()
	{
		return currentFuture != null && !currentFuture.isCancelled() && !currentFuture.isDone();
	}

	public void pauseFsScan(){
		interruptFsScan();
	}

	public void stopFsScan()
	{
		boolean interrupted = interruptFsScan();

		if(interrupted)
		{
			try
			{
				synchronized (currentFuture)
				{
					currentFuture.wait(3000);
				}
			}
			catch (InterruptedException e)
			{
				//expected
			}

			preferences.set(Keys.FS_SCAN_INTERRUPT_PATH, null);

			for (PlaylistObserver observer : observers.values())
			{
				observer.endRescan();
			}
		}
		preferences.set(Keys.FS_SCAN_INTERRUPT_PATH, null);
	}

	public void startFsScan()
	{
		stopFsScan();
		runFsScan();
	}

	private boolean interruptFsScan()
	{
		return currentFuture != null && currentFuture.cancel(true);
	}

	private void runFsScan()
	{
		interruptFsScan();

		currentFuture = fsScannerExecutorService.submit(new Runnable()
		{
			public void run()
			{
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

				for (PlaylistObserver observer : observers.values())
				{
					observer.startUpdatePlaylist();
				}

				boolean wasPaused = false;
				try
				{
					final String mediaPath = preferences.getExitstingMediaPath().toString();
					final String lastFsScanInterruptPath = preferences.get(Keys.FS_SCAN_INTERRUPT_PATH);

					if(lastFsScanInterruptPath != null)
					{
						for (PlaylistObserver observer : observers.values())
						{
							observer.unpauseRescanInitializing();
						}
					}

					List<String> mediaFilePaths = FsReader.getMediaFilesPaths(mediaPath, FileFilters.PLAYABLE_FILES_FILTER, true, false);

					final List<String> mediaFilePathsToScan;
					final int lastFsScanInterruptPathIndex = mediaFilePaths.indexOf(lastFsScanInterruptPath);

					if(lastFsScanInterruptPath == null || lastFsScanInterruptPathIndex < 0)
					{
						mediaFilePathsToScan = mediaFilePaths;
						for (PlaylistObserver observer : observers.values())
						{
							observer.startRescan(mediaFilePathsToScan.size());
						}
					}
					else
					{
						mediaFilePathsToScan = new ArrayList<String>();
						for(int i = lastFsScanInterruptPathIndex+1; i < mediaFilePaths.size(); i++){
							mediaFilePathsToScan.add(mediaFilePaths.get(i));
						}
						for (PlaylistObserver observer : observers.values())
						{
							observer.unpauseRescan(lastFsScanInterruptPathIndex+1, mediaFilePathsToScan.size());
						}
					}

					scanFiles(mediaFilePathsToScan, db, lastFsScanInterruptPathIndex);
				}
				catch (InterruptedException e)
				{
					wasPaused = true;
					for (PlaylistObserver observer : observers.values())
					{
						observer.pauseRescan();
					}
				}
				finally
				{
					if(!wasPaused)
					{
						for (PlaylistObserver observer : observers.values())
						{
							observer.endRescan();
						}
					}
				}
			}
		});
	}

	public void scanFiles(Collection<String> mediaFilePaths, TurtleDatabase db, int allreadyProcessed) throws InterruptedException
	{
		Set<String> encounteredRootSrcs = new HashSet<String>();
		int countProcessed = allreadyProcessed;

		for(String mediaFilePath : mediaFilePaths)
		{
			boolean trackAdded = false;
			try
			{
				trackAdded = FsReader.scanFile(mediaFilePath, db, encounteredRootSrcs);
			}
			catch (IOException e)
			{
				//log and go on with next File
				Log.v(Preferences.TAG, "failed to process " + mediaFilePath);
			}
			finally
			{
				countProcessed++;

				if (Thread.currentThread().isInterrupted()) {
					preferences.set(Keys.FS_SCAN_INTERRUPT_PATH, mediaFilePath);
					preferences.set(Keys.FS_SCAN_INTERRUPT_COUNT_PROCESSED, countProcessed);
					preferences.set(Keys.FS_SCAN_INTERRUPT_COUNT_ALL, mediaFilePaths.size());
					throw new InterruptedException();
				}

				Thread.sleep(10);
			}

			if(trackAdded)
			{
				for (PlaylistObserver observer : observers.values())
				{
					observer.trackAdded(mediaFilePath, countProcessed);
				}
			}
		}

		preferences.set(Keys.FS_SCAN_INTERRUPT_PATH, null);
		for (PlaylistObserver observer : observers.values())
		{
			observer.endUpdatePlaylist();
		}
	}

	public Collection<? extends Track> getCurrTracks()
	{
		return db.getTracks(getCompressedFilter());
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

	final Map<String, PlaylistObserver> observers = new HashMap<String, PlaylistObserver>();

	public interface PlaylistObserver extends Observer
	{
		void trackAdded(final String filePath, final int allreadyProcessed);

		void startRescan(int toProcess);

		public void endRescan();

		void pauseRescan();

		void unpauseRescanInitializing();

		void unpauseRescan(int alreadyProcessed, int toProcess);

		void startUpdatePlaylist();

		void endUpdatePlaylist();

		void filterAdded(Filter<? super Tables.Tracks> filter);

		void filterRemoved(Filter<? super Tables.Tracks> filter);

	}

	public static abstract class PlaylistFilterChangeObserver implements PlaylistObserver
	{
		public void trackAdded(final String filePath, final int allreadyProcessed){/*doNothing*/}

		public void startRescan(int toProcess){/*doNothing*/}

		public void endRescan(){/*doNothing*/}

		public void startUpdatePlaylist(){/*doNothing*/}

		public void endUpdatePlaylist(){/*doNothing*/}

		public void unpauseRescanInitializing(){/*doNothing*/}

		public void unpauseRescan(int alreadyProcessed, int toProcess){/*doNothing*/}

		public void pauseRescan(){/*doNothing*/}
	}

	public static abstract class PlaylistTrackChangeObserver implements PlaylistObserver{
		public void filterAdded(Filter<? super Tables.Tracks> filter){/*doNothing*/}

		public void filterRemoved(Filter<? super Tables.Tracks> filter){/*doNothing*/}
	}

	public void addObserver(PlaylistObserver observer)
	{
		observers.put(observer.getId(), observer);
	}

	public void removeObserver(Observer observer)
	{
		observers.remove(observer.getId());
	}

}