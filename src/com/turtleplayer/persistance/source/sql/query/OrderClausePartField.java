package com.turtleplayer.persistance.source.sql.query;

import com.turtleplayer.persistance.framework.sort.SortOrder;
import com.turtleplayer.persistance.source.relational.Field;

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

public class OrderClausePartField implements OrderClausePart
{
	final Field field;
	final SqlOrder order;

	public OrderClausePartField(Field field,
										 SortOrder order)
	{
		this.field = field;

		switch (order)
		{
			case ASC:
				this.order = SqlOrder.ASC;
				break;
			case DESC:
				this.order = SqlOrder.DESC;
				break;
			default:
				this.order = SqlOrder.ASC;
		}
	}

	public String toSql()
	{
		return " " + field.getName() + " " + order.toSql() + " ";
	}
}
