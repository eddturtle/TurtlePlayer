package turtle.player.persistance.framework.sort;

import turtle.player.persistance.framework.filter.FieldFilter;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.filter.Operator;
import turtle.player.persistance.source.relational.Field;
import turtle.player.persistance.source.relational.FieldPersistable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class FieldOrder<TARGET, RESULT, TYPE> implements Order<TARGET>
{
	private final FieldPersistable<RESULT, TYPE> field;
	private final SortOrder order;

	public FieldOrder(FieldPersistable<RESULT, TYPE> field,
                      SortOrder order)
	{
		this.field = field;
		this.order = order;
	}

	public FieldPersistable<RESULT, TYPE> getField()
	{
		return field;
	}

    public SortOrder getOrder()
    {
        return order;
    }

	public <R> R accept(OrderVisitor<TARGET, R> visitor)
	{
		return visitor.visit(this);
	}

	@Override
	public String toString()
	{
		return getField().getName() + " " + order;
	}

	public Filter<TARGET> asFilter(TYPE value, Operator op){
			return new FieldFilter<TARGET, RESULT, TYPE>(field, op, value);
	}

	public static <TARGET, RESULT, TYPE> Order<TARGET> getMultiFieldOrder(SortOrder order,
												FieldPersistable<RESULT, TYPE>... fields)
	{
		List<FieldOrder<TARGET, RESULT, TYPE>> orders = new ArrayList<FieldOrder<TARGET, RESULT, TYPE>>();
		for(FieldPersistable<RESULT, TYPE> field : fields)
		{
			orders.add(new FieldOrder<TARGET, RESULT, TYPE>(field, order));
		}
		return new OrderSet<TARGET>(orders);
	}
}
