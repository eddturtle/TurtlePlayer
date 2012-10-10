package turtle.player.playlist.filter;

import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Track;

public class ArtistFilter implements PlaylistFilter
{

	final Artist artist;

	public ArtistFilter(Artist artist)
	{
		this.artist = artist;
	}

	@Override
	public boolean accept(Track track)
	{
		return artist.equals(track.GetAlbum());
	}

	@Override
	public int getForce()
	{
		return 2;
	}
}
