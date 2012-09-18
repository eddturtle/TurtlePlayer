package turtle.player.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Album implements Instance
{
    private final String name;

    public Album(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public Set<Track> getChilds(Set<Track> tracks)
    {
        Set<Track> trackOfArtist = new HashSet<Track>();
        for(Track track : tracks)
        {
            if(this.equals(track.GetAlbum()))
            {
                trackOfArtist.add(track);
            }
        }
        return trackOfArtist;
    }

    @Override
    public <R> R accept(InstanceVisitor<R> visitor)
    {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Album album = (Album) o;

        if (!name.equals(album.name)) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }
}
