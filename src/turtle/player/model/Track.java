/**
 *
 * TURTLE PLAYER
 *
 * Licensed under MIT & GPL
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * More Information @ www.turtle-player.co.uk
 *
 * @author Simon Honegger (Hoene84)
 */

package turtle.player.model;

public class Track extends FSobject implements Album, Artist, Genre, Song
{

	private final int number;
	//private String number;
	private final Artist artist;
	private final Album album;
	private final Genre genre;
	private final Song song;

	public Track(
					 Song song,
					 Artist artist,
					 Album album,
					 Genre genre,
					 int number,
					 String path)
	{
		super(path);
		this.song = song;
		this.number = number;
		this.artist = artist;
		this.album = album;
		this.genre = genre;
	}

	public Track(
			  Song song,
			  Artist artist,
			  Album album,
			  Genre genre,
			  int number,
			  String dir,
			  String name)
	{
		super(dir, name);
		this.song = song;
		this.number = number;
		this.artist = artist;
		this.album = album;
		this.genre = genre;
	}

	public int GetNumber()
	{
		return number;
	}

	public Song getSong()
	{
		return song;
	}

	public Artist GetArtist()
	{
		return artist;
	}

	public Album GetAlbum()
	{
		return album;
	}

	public Genre GetGenre()
	{
		return genre;
	}

	public String getAlbumId()
	{
		return album.getAlbumId();
	}

	public String getAlbumName()
	{
		return album.getAlbumName();
	}

	public String getArtistId()
	{
		return artist.getArtistId();
	}

	public String getArtistName()
	{
		return artist.getArtistName();
	}

	public String getGenreId()
	{
		return genre.getGenreId();
	}

	public String getGenreName()
	{
		return genre.getGenreName();
	}

	public String getSongId()
	{
		return song.getSongId();
	}

	public String getSongName()
	{
		return song.getSongName();
	}

	public <R> R accept(InstanceVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}