package turtle.player.playlist.playorder;

import turtle.player.model.Track;
import turtle.player.persistance.framework.executor.OperationExecutor;
import turtle.player.persistance.source.sql.Random;
import turtle.player.persistance.source.sqlite.QuerySqlite;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.mapping.TrackCreator;
import turtle.player.playlist.Playlist;
import turtle.player.preferences.Preferences;

public class PlayOrderRandom implements PlayOrderStrategy
{

    private Preferences preferences;
    private Playlist playlist;
	private TurtleDatabase db;

    public PlayOrderStrategy connect(Preferences preferences, Playlist playlist, TurtleDatabase db){
        this.preferences = preferences;
        this.playlist = playlist;
		 this.db = db;

        return this;
    }

    public void disconnect()
    {
        //empty
    }

    public Track getNext(Track currTrack) {
		 return OperationExecutor.execute(db, new QuerySqlite<Track>(playlist.getFilter()), new Random<Track>(Tables.TRACKS, new TrackCreator()));
    }

    public Track getPrevious(Track currTrack)
    {
		 return OperationExecutor.execute(db, new QuerySqlite<Track>(playlist.getFilter()), new Random<Track>(Tables.TRACKS, new TrackCreator()));
    }
}
