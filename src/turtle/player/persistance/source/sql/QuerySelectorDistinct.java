package turtle.player.persistance.source.sql;

import turtle.player.persistance.source.relational.Field;
import turtle.player.persistance.source.relational.Table;
import turtle.player.persistance.source.sqlite.QuerySelectorForSetSqlite;

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

public abstract class QuerySelectorDistinct<I> extends QuerySelectorForSetSqlite<I>
{
	private final Table table;
	private final Field field;

	protected QuerySelectorDistinct(Table table,
											  Field field)
	{
		this.table = table;
		this.field = field;
	}

	@Override
	public Sql get()
	{
		return new Sql("SELECT DISTINCT " + field.getName() + " FROM " + table.getName());
	}
}
