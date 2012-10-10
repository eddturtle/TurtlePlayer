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

	private String title;
	private int number;
	//private String number;
	private Artist artist;
	private Album album;
	private double length;
	private String src;
	private String rootSrc;
	private String albumArt;

	public Track(String title,
					 int number,
					 Artist artist,
					 Album album,
					 double length,
					 String src,
					 String rootSrc,
					 String albumArt)
	{
		this.title = title;
		this.number = number;
		this.artist = artist;
		this.album = album;
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

	/**
	 * Track has no childs normally
	 */
	@Override
	public Set<? extends Instance> getChilds(Set<Track> tracks)
	{
		return new HashSet<Instance>();
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

		Track track = (Track) o;

		if (!src.equals(track.src)) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		return src.hashCode();
	}
}