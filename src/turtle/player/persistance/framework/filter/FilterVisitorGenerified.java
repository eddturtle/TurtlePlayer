package turtle.player.persistance.framework.filter;

import turtle.player.persistance.framework.sort.FieldOrder;
import turtle.player.persistance.framework.sort.OrderVisitor;
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

public abstract class FilterVisitorGenerified<TARGET, RESULT, TYPE, R> implements FilterVisitor<TARGET, R>
{
	public abstract R visit(FieldFilter<TARGET, RESULT, TYPE> fieldFilter,
									FieldPersistable<RESULT, TYPE> field);

	final public <T, Z> R visit(FieldFilter<? super TARGET, Z, T> fieldFilter)
	{
		return visit((FieldFilter<TARGET, RESULT, TYPE>) fieldFilter, (FieldPersistable<RESULT, TYPE>) fieldFilter.getField());
	}
}
