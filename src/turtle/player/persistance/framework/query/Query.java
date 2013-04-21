package turtle.player.persistance.framework.query;

import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.filter.FilterVisitor;
import turtle.player.persistance.framework.sort.Order;
import turtle.player.persistance.framework.sort.OrderVisitor;

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

public abstract class Query<QUERY, WHERE, ORDER, RESULT, CURSOR, TARGET> implements FilterVisitor<TARGET, WHERE>, OperationRead<QUERY, CURSOR, RESULT>, OrderVisitor<TARGET, ORDER>
{
	private final Filter<TARGET> filter;
	private final Order order;

    protected Query()
    {
        this.filter = null;
        order = null;
    }

    protected Query(Filter<TARGET> filter)
    {
        this.filter = filter;
        order = null;
    }

    protected Query(Order order)
    {
        this.order = order;
        this.filter = null;
    }

    public Query(Filter<TARGET> filter, Order order)
	{
		this.filter = filter;
		this.order = order;
	}

    /**
     * @return can be null
     */
	public Filter<TARGET> getFilter()
	{
		return filter;
	}

    /**
     * @return can be null
     */
    public Order<TARGET> getOrder()
    {
        return order;
    }
}
