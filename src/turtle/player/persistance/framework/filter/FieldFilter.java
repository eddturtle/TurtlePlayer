package turtle.player.persistance.framework.filter;

import turtle.player.persistance.source.relational.Field;
import turtle.player.persistance.source.relational.FieldPersistable;

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
	private final String value;

	public FieldFilter(FieldPersistable<I, T> field,
							 Operator operator,
							 String value)
	{
		this.field = field;
		this.operator = operator;
		this.value = value;
	}

	public FieldPersistable<I, T> getField()
	{
		return field;
	}

	public String getFieldValue()
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
}
