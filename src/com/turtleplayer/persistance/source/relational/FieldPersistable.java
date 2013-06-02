package com.turtleplayer.persistance.source.relational;

import com.turtleplayer.persistance.framework.creator.Creator;
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

public abstract class FieldPersistable<RESULT, TYPE> extends Field
{
	private final Creator<TYPE, RESULT> mapping;

	protected FieldPersistable(String name,
										Creator<TYPE, RESULT> mapping)
	{
		super(name);
		this.mapping = mapping;
	}

	public FieldPersistable(FieldPersistable<RESULT, TYPE> fieldPersistable)
	{
		this(fieldPersistable.getName(), fieldPersistable.mapping);
	}

	public TYPE get(RESULT type)
	{
		return mapping.create(type);
	}

	public abstract <R> R accept(FieldVisitor<R, ? extends RESULT> visitor);
}
