package com.turtleplayer.persistance.framework.filter;

import com.turtleplayer.persistance.source.relational.FieldPersistable;
import com.turtleplayer.persistance.source.relational.fieldtype.FieldPersistableAsDouble;
import com.turtleplayer.persistance.source.relational.fieldtype.FieldPersistableAsInteger;
import com.turtleplayer.persistance.source.relational.fieldtype.FieldPersistableAsString;
import com.turtleplayer.persistance.source.relational.fieldtype.FieldVisitor;

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

public class FieldFilter<I, T> implements Filter
{
	private final FieldPersistable<I, T> field;
	private final Operator operator;
	private final T value;

	public FieldFilter(FieldPersistable<I, T> field,
							 Operator operator,
							 T value)
	{
		this.field = field;
		this.operator = operator;
		this.value = value;
	}

	public FieldPersistable<I, T> getField()
	{
		return field;
	}

	public T getValue()
	{
		return value;
	}

	public Operator getOperator()
	{
		return operator;
	}

	public <R, I> R accept(FilterVisitor<I, R> visitor)
	{
		return visitor.visit((FieldFilter<I, T>)this);
	}

	@Override
	public String toString()
	{
		return getField().getName() + " " + operator.name() + " " + value;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FieldFilter that = (FieldFilter) o;

		if (!field.equals(that.field)) return false;
		if (operator != that.operator) return false;
		if (!value.equals(that.value)) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = field.hashCode();
		result = 31 * result + operator.hashCode();
		result = 31 * result + value.hashCode();
		return result;
	}

	public abstract class FieldVisitorField<R> implements FieldVisitor<R, I>
	{
		public abstract R visit(FieldPersistableAsString<I> field, String filterValue);
		public abstract R visit(FieldPersistableAsDouble<I> field, Double filterValue);
		public abstract R visit(FieldPersistableAsInteger<I> field, Integer filterValue);

		public R visit(FieldPersistableAsString<I> field)
		{
			return visit(field, (String)value);
		}

		public R visit(FieldPersistableAsDouble<I> field)
		{
			return visit(field, (Double)value);
		}

		public R visit(FieldPersistableAsInteger<I> field)
		{
			return visit(field, (Integer)value);
		}
	}
}
