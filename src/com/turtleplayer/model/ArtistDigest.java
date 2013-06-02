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

import com.turtleplayer.util.Shorty;

public class ArtistDigest implements Artist
{
	private static final String EMPTY_REPLACMENT= "Unknown";
	public static final ArtistDigest NO_ARTIST = new ArtistDigest(null);

	private final String id;

	public ArtistDigest(String id)
	{
		this.id = Shorty.avoidNull(id);
	}

	public String getArtistId()
	{
		return id;
	}

	public String getArtistName()
	{
		return Shorty.isVoid(id) ? EMPTY_REPLACMENT : id;
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

		ArtistDigest artist = (ArtistDigest) o;

		return id.equals(artist.id);

	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}
}
