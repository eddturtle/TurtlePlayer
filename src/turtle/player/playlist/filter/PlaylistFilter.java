package turtle.player.playlist.filter;

import turtle.player.model.Track;

public interface PlaylistFilter {

    public static final PlaylistFilter ALL = new AllFilter();

    public boolean accept(Track track);

    public int getForce();

}
