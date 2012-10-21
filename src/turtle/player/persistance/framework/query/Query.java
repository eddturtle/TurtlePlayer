package turtle.player.persistance.framework.query;

import turtle.player.persistance.framework.db.Database;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.filter.FilterVisitor;
import turtle.player.persistance.framework.selector.Mapping;

/**
 * TURTLE PLAYER
 * <p/>
 * Licensed under MIT & GPL
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 * <p/>
 * More Information @ www.turtle-player.co.uk
 *
 * @author Simon Honegger (Hoene84)
 */

public abstract class Query<Q, I, C> implements FilterVisitor<Q>, OperationRead<Q, C, I>
{
	private final Filter<Q> filter;

	public Query(Filter<Q> filter)
	{
		this.filter = filter;
	}

	public Filter<Q> getFilter()
	{
		return filter;
	}

	protected abstract Q get(Mapping<Q, I, C> mapping);
}
