package turtle.player.persistance.framework.selector;

import java.util.Map;

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

public class OrderSelector implements Selector
{
	public enum Order
	{
		ASC,
		DESC;
	}
	private final Map<String, Order> orders;

	public OrderSelector(Map<String, Order> orders)
	{
		this.orders = orders;
	}

	public Map<String, Order> getOrders()
	{
		return orders;
	}

	public Object accept(SelectorVisitor visitor)
	{
		return visitor.visit(this);
	}
}
