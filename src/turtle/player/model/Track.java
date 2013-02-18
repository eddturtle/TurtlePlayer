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

import java.util.*;


public class Track implements Instance
{

	private final String title;
	private final int number;
	//private String number;
	private final Artist artist;
	private final Album album;
	private final Genre genre;
	private final double length;
	private final String src;
	private final String rootSrc;
	private final String albumArt;

	public Track(String title,
					 int number,
					 Artist artist,
					 Album album,
					 Genre genre,
					 double length,
					 String src,
					 String rootSrc,
					 String albumArt)
	{
		this.title = title;
		this.number = number;
		this.artist = artist;
		this.album = album;
		this.genre = genre;
		this.length = length;
		this.src = src;
		this.rootSrc = rootSrc;
		this.albumArt = albumArt;
	}

	public String GetTitle()
	{
		return title;
	}

	public int GetNumber()
	{
		return number;
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

	public double GetLength()
	{
		return length;
	}

	public String GetSrc()
	{
		return src;
	}

	public String GetRootSrc()
	{
		return rootSrc;
	}

	public String albumArt()
	{
		return albumArt;
	}

	public <R> R accept(InstanceVisitor<R> visitor)
	{
		return visitor.visit(this);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Track track = (Track) o;

		return src.equals(track.src);

	}

	@Override
	public int hashCode()
	{
		return src.hashCode();
	}
}