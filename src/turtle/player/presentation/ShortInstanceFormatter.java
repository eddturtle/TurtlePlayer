package turtle.player.presentation;

import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Track;

public class ShortInstanceFormatter extends InstanceFormatter
{
    @Override
    public String visit(Track track)
    {
        return track.GetTitle();
    }

    @Override
    public String visit(Album album)
    {
        return album.getName();
    }

    @Override
    public String visit(Artist artist)
    {
        return artist.getName();
    }
}
