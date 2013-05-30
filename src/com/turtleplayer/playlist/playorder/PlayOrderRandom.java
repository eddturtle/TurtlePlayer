package com.turtleplayer.playlist.playorder;

import com.turtleplayer.model.Track;
import com.turtleplayer.persistance.framework.executor.OperationExecutor;
import com.turtleplayer.persistance.framework.sort.RandomOrder;
import com.turtleplayer.persistance.source.sql.First;
import com.turtleplayer.persistance.source.sqlite.QuerySqlite;
import com.turtleplayer.persistance.turtle.db.TurtleDatabase;
import com.turtleplayer.persistance.turtle.db.structure.Tables;
import com.turtleplayer.persistance.turtle.mapping.TrackCreator;
import com.turtleplayer.playlist.Playlist;

public class PlayOrderRandom implements PlayOrderStrategy
{

	private final Playlist playlist;
	private final TurtleDatabase db;

	public PlayOrderRandom(TurtleDatabase db,
								  Playlist playlist)
	{
		this.playlist = playlist;
		this.db = db;
	}

	public Track getNext(Track currTrack)
	{
		return get();
	}

	public Track getPrevious(Track currTrack)
	{
		return get();
	}

	private Track get()
	{
		return OperationExecutor.execute(db,
				  new QuerySqlite<Track>(
							 playlist.getCompressedFilter(),
							 new RandomOrder(),
							 new First<Track>(Tables.TRACKS, new TrackCreator())));
	}
}
