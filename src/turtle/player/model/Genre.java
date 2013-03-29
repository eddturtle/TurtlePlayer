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

import android.content.Context;
import turtle.player.R;
import turtle.player.TurtlePlayer;
import turtle.player.util.AndroidUtils;
import turtle.player.util.Shorty;
import turtle.player.util.TurtleUtil;

import java.text.Collator;
import java.util.HashSet;
import java.util.Set;

public class Genre implements Instance
{
	private final String id;

	public Genre(String id)
	{
		this.id = Shorty.avoidNull(id);
	}

	public String getId()
	{
		return id;
	}

	public String getName()
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

		Genre genre = (Genre) o;

		return id.equals(genre.id);

	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}
}
