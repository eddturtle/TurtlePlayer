package turtle.player.playlist.playorder;

import turtle.player.model.Track;
import turtle.player.persistance.framework.executor.OperationExecutor;
import turtle.player.persistance.framework.sort.RandomOrder;
import turtle.player.persistance.source.sql.Limited;
import turtle.player.persistance.source.sqlite.CreatorForListSqlite;
import turtle.player.persistance.source.sqlite.QuerySqlite;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.mapping.TrackCreator;
import turtle.player.playlist.Playlist;

import java.util.List;

public class PlayOrderRandom extends AbstractPlayOrderStrategy
{

	private final Playlist playlist;
	private final TurtleDatabase db;

	public PlayOrderRandom(TurtleDatabase db,
								  Playlist playlist)
	{
		this.playlist = playlist;
		this.db = db;
	}

	public List<Track> getNext(Track currTrack, int n)
	{
		return get(n);
	}

	public List<Track> getPrevious(Track currTrack, int n)
	{
		return get(n);
	}

	private List<Track> get(int n)
	{
		return OperationExecutor.execute(db,
				  new QuerySqlite<List<Track>>(
							 playlist.getFilter(),
							 new RandomOrder(),
							 new Limited<Track>(Tables.TRACKS, new CreatorForListSqlite<Track>(new TrackCreator()), n)));
	}
}
