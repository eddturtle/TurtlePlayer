package turtle.player.persistance.framework.sort;

import turtle.player.persistance.source.relational.Field;
import turtle.player.persistance.source.relational.FieldPersistable;

import java.util.Arrays;

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

public class FieldOrder<I, T> implements Order
{
	private final FieldPersistable<I, T> field;
	private final SortOrder order;

	public FieldOrder(FieldPersistable<I, T> field,
                      SortOrder order)
	{
		this.field = field;
		this.order = order;
	}

	public FieldPersistable<I, T> getField()
	{
		return field;
	}

    public SortOrder getOrder()
    {
        return order;
    }

	public <R, I> R accept(OrderVisitor<I, R> visitor)
	{
		return visitor.visit((FieldOrder<I,T>) this);
	}

	@Override
	public String toString()
	{
		return getField().getName() + " " + order;
	}
}
