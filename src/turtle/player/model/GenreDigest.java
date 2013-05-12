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

import turtle.player.util.Shorty;
import turtle.player.util.TurtleUtil;

public class GenreDigest implements Genre
{
	private final String id;
	public static final GenreDigest NO_GENRE = new GenreDigest(null);

	public GenreDigest(String id)
	{
		this.id = Shorty.avoidNull(id);
	}

	public String getGenreId()
	{
		return id;
	}

	public String getGenreName()
	{
		return TurtleUtil.translateGenreId(id);
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

		GenreDigest genre = (GenreDigest) o;

		return id.equals(genre.id);

	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}
}
