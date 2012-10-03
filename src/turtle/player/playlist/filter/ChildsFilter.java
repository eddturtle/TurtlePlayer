package turtle.player.playlist.filter;

import turtle.player.playlist.Playlist;
import turtle.player.model.*;

public class ChildsFilter implements PlaylistFilter
{

    final Instance instance;
    final Playlist playlist;

    public ChildsFilter(Instance instance, Playlist playlist)
    {
        this.instance = instance;
        this.playlist = playlist;
    }

    @Override
    public boolean accept(final Track candidate)
    {
        if(instance == null){
            return true;
        }

        return instance.getChilds(playlist.getTracks(PlaylistFilter.ALL)).contains(candidate);
    }

    @Override
    public int getForce()
    {
        return 2;
    }
}
