package turtle.player.playlist.playorder;

import turtle.player.model.Track;
import turtle.player.persistance.framework.executor.OperationExecutor;
import turtle.player.persistance.framework.sort.RandomOrder;
import turtle.player.persistance.source.sql.First;
import turtle.player.persistance.source.sql.Limited;
import turtle.player.persistance.source.sqlite.CreatorForListSqlite;
import turtle.player.persistance.source.sqlite.QuerySqlite;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.mapping.TrackCreator;
import turtle.player.playlist.Playlist;

import java.util.List;

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
				  new QuerySqlite<Tables.Tracks, Tables.Tracks, Track>(
							 playlist.getCompressedFilter(),
							 new RandomOrder<Tables.Tracks>(),
							 new First<Track>(Tables.TRACKS, new TrackCreator())));
	}
}
