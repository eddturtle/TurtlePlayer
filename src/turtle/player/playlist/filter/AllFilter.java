package turtle.player.playlist.filter;

import turtle.player.model.Track;

public class AllFilter implements PlaylistFilter
{
    @Override
    public boolean accept(Track track)
    {
        return true;
    }

    @Override
    public int getForce()
    {
        return 1;
    }
}
