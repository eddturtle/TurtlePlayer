package turtle.player.persistance.framework.sort;

import java.util.*;

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

public class OrderSet<Q> implements Order<Q>
{
	private final List<Order<Q>> orders = new ArrayList<Order<Q>>();

	public OrderSet(Order<Q>... order)
	{
		Collections.addAll(orders, order);
	}

	public OrderSet(List<Order<Q>> orders)
	{
		this.orders.addAll(orders);
	}

	/**
	 * @return never null, Set can be empty
	 */
	public List<Order<Q>> getOrders()
	{
		return orders;
	}

	public <R, I> R accept(OrderVisitor<I, R, Q> visitor)
	{
		return visitor.visit(this);
	}

	public OrderSet<Q> removeLast(){
		if(orders.size() > 1)
		{
			return new OrderSet<Q>(orders.subList(0, orders.size()-1));
		}
		else
		{
			return new OrderSet<Q>(new ArrayList<Order<Q>>());
		}
	}

	public boolean isEmpty(){
		return orders.isEmpty();
	}

	@Override
	public String toString()
	{
		return Arrays.deepToString(orders.toArray());
	}
}
