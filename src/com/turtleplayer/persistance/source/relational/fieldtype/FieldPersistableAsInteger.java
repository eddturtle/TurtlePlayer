package com.turtleplayer.persistance.source.relational.fieldtype;

import com.turtleplayer.persistance.framework.creator.Creator;
import com.turtleplayer.persistance.source.relational.FieldPersistable;

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

public class FieldPersistableAsInteger<RESULT> extends FieldPersistable<RESULT, Integer>
{
	public FieldPersistableAsInteger(String name,
													Creator<Integer, RESULT> mapping)
	{
		super(name, mapping);
	}

	public FieldPersistableAsInteger(FieldPersistable<RESULT, Integer> fieldPersistable)
	{
		super(fieldPersistable);
	}

	@Override
	public <R> R accept(FieldVisitor<R, ? extends RESULT> visitor)
	{
		return visitor.visit(this);
	}
}
