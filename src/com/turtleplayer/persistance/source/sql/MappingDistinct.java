package com.turtleplayer.persistance.source.sql;

import android.database.Cursor;

import java.util.List;

import com.turtleplayer.persistance.framework.creator.CreatorForList;
import com.turtleplayer.persistance.framework.mapping.Mapping;
import com.turtleplayer.persistance.source.relational.Field;
import com.turtleplayer.persistance.source.relational.Table;
import com.turtleplayer.persistance.source.sql.query.Select;

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
