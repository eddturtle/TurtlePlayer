package turtle.player.persistance.framework.filter;

import turtle.player.persistance.source.relational.FieldPersistable;
import turtle.player.persistance.source.relational.fieldtype.FieldPersistableAsDouble;
import turtle.player.persistance.source.relational.fieldtype.FieldPersistableAsInteger;
import turtle.player.persistance.source.relational.fieldtype.FieldPersistableAsString;
import turtle.player.persistance.source.relational.fieldtype.FieldVisitor;

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

public class FieldFilter<TARGET, RESULT, TYPE> implements Filter<TARGET>
{
	private final FieldPersistable<RESULT, TYPE> field;
	private final Operator operator;
	private final TYPE value;

	public FieldFilter(FieldPersistable<RESULT, TYPE> field,
							 Operator operator,
							 TYPE value)
	{
		this.field = field;
		this.operator = operator;
		this.value = value;
	}

	public FieldPersistable<RESULT, TYPE> getField()
	{
		return field;
	}

	public TYPE getValue()
	{
		return value;
	}

	public Operator getOperator()
	{
		return operator;
	}

	public <R> R accept(FilterVisitor<TARGET, R> visitor)
	{
		return visitor.visit(this);
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

	/**
	 * TODO refactor
	 * @param <R>
	 */
	public abstract class FieldVisitorField<R> implements FieldVisitor<R, RESULT>
	{
		public abstract R visit(FieldPersistableAsString<RESULT> field, String filterValue);
		public abstract R visit(FieldPersistableAsDouble<RESULT> field, Double filterValue);
		public abstract R visit(FieldPersistableAsInteger<RESULT> field, Integer filterValue);

		public R visit(FieldPersistableAsString<RESULT> field)
		{
			return visit(field, (String)value);
		}

		public R visit(FieldPersistableAsDouble<RESULT> field)
		{
			if(value instanceof Double){
				return visit(field, (Double)value);
			}
			return visit(field, (Double.valueOf(value.toString())));
		}

		public R visit(FieldPersistableAsInteger<RESULT> field)
		{
			if(value instanceof Integer){
				return visit(field, (Integer)value);
			}
			return visit(field, (Integer.valueOf(value.toString())));
		}
	}
}
