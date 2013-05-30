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

package com.turtleplayer.model;

import java.util.HashSet;
import java.util.Set;

import com.turtleplayer.util.Shorty;

public class Album implements Instance
{
	private static final String EMPTY_REPLACMENT= "Unknown";
	public static final Album NO_ALBUM = new Album(null);

	private final String id;

	public Album(String id)
	{
		this.id = Shorty.avoidNull(id);
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return Shorty.isVoid(id) ? EMPTY_REPLACMENT : id;
	}

	public Set<Track> getChilds(Set<Track> tracks)
	{
		Set<Track> trackOfArtist = new HashSet<Track>();
		for (Track track : tracks)
		{
			if (this.equals(track.GetAlbum()))
			{
				trackOfArtist.add(track);
			}
		}
		return trackOfArtist;
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

		Album album = (Album) o;

		return id.equals(album.id);

	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}
}
