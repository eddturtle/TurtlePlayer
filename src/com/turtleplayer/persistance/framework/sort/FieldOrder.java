package com.turtleplayer.persistance.framework.sort;

import com.turtleplayer.persistance.framework.filter.FieldFilter;
import com.turtleplayer.persistance.framework.filter.Filter;
import com.turtleplayer.persistance.framework.filter.Operator;
import com.turtleplayer.persistance.source.relational.FieldPersistable;

import java.util.ArrayList;
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

public class FieldOrder<PROJECTION, RESULT, TYPE> implements Order<PROJECTION>
{
	private final FieldPersistable<? super RESULT, TYPE> field;
	private final SortOrder order;

	public FieldOrder(FieldPersistable<? super RESULT, TYPE> field,
                      SortOrder order)
	{
		this.field = field;
		this.order = order;
	}

	public FieldPersistable<? super RESULT, TYPE> getField()
	{
		return field;
	}

    public SortOrder getOrder()
    {
        return order;
    }

	public <R> R accept(OrderVisitor<? extends PROJECTION, R> visitor)
	{
		return visitor.visit(this);
	}

	@Override
	public String toString()
	{
		return getField().getName() + " " + order;
	}

	public Filter<PROJECTION> asFilter(TYPE value, Operator op){
			return new FieldFilter<PROJECTION, RESULT, TYPE>(field, op, value);
	}

	public static <PROJECTION, RESULT, TYPE> Order<PROJECTION> getMultiFieldOrder(SortOrder order,
												FieldPersistable<? super RESULT, TYPE>... fields)
	{
		List<FieldOrder<PROJECTION, RESULT, TYPE>> orders = new ArrayList<FieldOrder<PROJECTION, RESULT, TYPE>>();
		for(FieldPersistable<? super RESULT, TYPE> field : fields)
		{
			orders.add(new FieldOrder<PROJECTION, RESULT, TYPE>(field, order));
		}
		return new OrderSet<PROJECTION>(orders);
	}
}
