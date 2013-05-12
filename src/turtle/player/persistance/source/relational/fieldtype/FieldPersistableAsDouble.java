package turtle.player.persistance.source.relational.fieldtype;

import turtle.player.persistance.framework.creator.Creator;
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

public class FieldPersistableAsDouble<RESULT> extends FieldPersistable<RESULT, Double>
{
	public FieldPersistableAsDouble(String name,
												  Creator<Double, RESULT> mapping)
	{
		super(name, mapping);
	}

	public FieldPersistableAsDouble(FieldPersistable<RESULT, Double> fieldPersistable)
	{
		super(fieldPersistable);
	}

	@Override
	public <R> R accept(FieldVisitor<R, ? extends RESULT> visitor)
	{
		return visitor.visit(this);
	}
}
