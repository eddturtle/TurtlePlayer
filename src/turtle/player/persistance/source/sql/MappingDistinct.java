package turtle.player.persistance.source.sql;

import android.database.Cursor;
import turtle.player.persistance.framework.creator.CreatorForList;
import turtle.player.persistance.framework.mapping.Mapping;
import turtle.player.persistance.source.relational.Field;
import turtle.player.persistance.source.relational.Table;
import turtle.player.persistance.source.sql.query.Select;

import java.util.List;

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

public class MappingDistinct<I> implements Mapping<Select, List<I>, Cursor>
{
	private final Table table;
	private final Field field;
	private final CreatorForList<I, Cursor, Cursor> creator;

	public MappingDistinct(Table table,
								  Field field,
								  CreatorForList<I, Cursor, Cursor> creator)
	{
		this.table = table;
		this.field = field;
      this.creator = creator;
	}

	public Select get()
	{
		return new Select(table, Select.SelectMethod.DISTINCT, field);
	}

    public List<I> create(Cursor queryResult)
    {
        return creator.create(queryResult);
    }
}
