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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Artist implements Instance
{
	private final String name;

	public Artist(String name)
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
		for (Track track : tracks)
		{
			if (this.equals(track.GetArtist()))
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

		Artist artist = (Artist) o;

		if (!name.equals(artist.name)) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
}
