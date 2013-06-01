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

public class FieldFilter<PROJECTION, RESULT, TYPE> implements Filter<PROJECTION>
{
	protected final FieldPersistable<? super RESULT, TYPE> field;
	protected final Operator operator;
	protected final TYPE value;

	public FieldFilter(FieldPersistable<? super RESULT, TYPE> field,
							 Operator operator,
							 TYPE value)
	{
		this.field = field;
		this.operator = operator;
		this.value = value;
	}

	public FieldPersistable<? super RESULT, TYPE> getField()
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
	public <R> R accept(FilterVisitor<? extends PROJECTION, R> visitor)
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
		if (!(o instanceof FieldFilter)) return false;

		FieldFilter that = (FieldFilter) o;

		if (!field.equals(that.field)) return false;
		if (operator != that.operator) return false;
		if (value != null ? !value.equals(that.value) : that.value != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = field.hashCode();
		result = 31 * result + operator.hashCode();
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}
}
