package turtle.player.presentation;

import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Track;
import turtle.player.util.Shorty;

public class ShortWithNumberInstanceFormatter extends ShortInstanceFormatter
{
    @Override
    public String visit(Track track)
    {
        String trackName = track.GetTitle();

        int number = track.GetNumber();

        if(!Shorty.isVoid(number))
        {
            return number + DELIMITER + trackName;
        }
        return  trackName;
    }
}
