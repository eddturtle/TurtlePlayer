package com.turtleplayer.persistance.framework.sort;

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

public class OrderSet<PROJECTION> implements Order<PROJECTION>
{
	private final List<Order<? super PROJECTION>> orders = new ArrayList<Order<? super PROJECTION>>();

	public OrderSet(Order<? super PROJECTION>... order)
	{
		Collections.addAll(orders, order);
	}

	public OrderSet(List<? extends Order<? super PROJECTION>> orders)
	{
		this.orders.addAll(orders);
	}

	/**
	 * @return never null, Set can be empty
	 */
	public List<Order<? super PROJECTION>> getOrders()
	{
		return orders;
	}

	public <R> R accept(OrderVisitor<? extends PROJECTION, R> visitor)
	{
		return visitor.visit(this);
	}

	public OrderSet<? super PROJECTION> removeLast()
	{
		if(orders.size() > 1)
		{
			return new OrderSet<PROJECTION>(orders.subList(0, orders.size()-1));
		}
		else
		{
			return new OrderSet<PROJECTION>(new ArrayList<Order<? super PROJECTION>>());
		}
	}

	public boolean isEmpty()
	{
		return orders.isEmpty();
	}

	@Override
	public String toString()
	{
		return Arrays.deepToString(orders.toArray());
	}
}
