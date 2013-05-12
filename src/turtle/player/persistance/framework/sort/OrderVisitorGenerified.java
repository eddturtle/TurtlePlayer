package turtle.player.persistance.framework.sort;

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

public abstract class OrderVisitorGenerified<TARGET, RESULT, TYPE, R> implements OrderVisitor<TARGET, R>
{
	public abstract R visit(FieldOrder<TARGET, RESULT, TYPE> fieldOrder,
									FieldPersistable<RESULT, TYPE> field);

	final public <T, Z> R visit(FieldOrder<? super TARGET, Z, T> fieldOrder)
	{
		return visit((FieldOrder<TARGET, RESULT, TYPE>)fieldOrder, (FieldPersistable<RESULT, TYPE>)fieldOrder.getField());
	}
}
