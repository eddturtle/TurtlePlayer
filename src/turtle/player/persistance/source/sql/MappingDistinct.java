package turtle.player.persistance.source.sql;

import turtle.player.persistance.source.relational.Field;
import turtle.player.persistance.source.relational.Table;
import turtle.player.persistance.source.sql.query.Select;
import turtle.player.persistance.source.sql.query.Sql;
import turtle.player.persistance.source.sqlite.MappingForSetSqlite;

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

public abstract class MappingDistinct<I> extends MappingForSetSqlite<I>
{
	private final Table table;
	private final Field field;

	protected MappingDistinct(Table table,
									  Field field)
	{
		this.table = table;
		this.field = field;
	}

	public Select get()
	{
		return new Select(table, field);
	}
}
