package com.turtleplayer.persistance.source.sql;

import com.turtleplayer.persistance.framework.creator.Creator;
import com.turtleplayer.persistance.framework.mapping.Mapping;
import com.turtleplayer.persistance.source.relational.Table;
import com.turtleplayer.persistance.source.sql.query.Limit;
import com.turtleplayer.persistance.source.sql.query.Select;

import android.database.Cursor;

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

public class First<I> implements Mapping<Select, I, Cursor>
{
	private final Table<I> table;
	private final Creator<I, Cursor> creator;

	public First(Table<I> table,
					  Creator<I, Cursor> creator)
	{
		this.table = table;
		this.creator = creator;
	}

	public Select get()
	{
		Select select = new Select(table);
		select.setLimit(new Limit(1));
		return select;
	}

	public I create(Cursor queryResult)
	{
		if(queryResult.moveToFirst()){
			return creator.create(queryResult);
		}
		else
		{
			return null;
		}

	}
}
