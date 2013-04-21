package turtle.player.persistance.source.relational;

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

public abstract class FieldPersistable<RESULT, TYPE> extends Field
{
	public FieldPersistable(String name)
	{
		super(name);
	}

	public FieldPersistable(FieldPersistable<?, ?> fieldPersistable)
	{
		super(fieldPersistable.getName());
	}

	public abstract TYPE get(RESULT instance);

	public String getAsDisplayableString(RESULT instance){
		return get(instance).toString();
	}

	public abstract <R> R accept(FieldVisitor<R, RESULT> visitor);

}
