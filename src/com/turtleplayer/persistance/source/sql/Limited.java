package com.turtleplayer.persistance.source.sql;

import android.database.Cursor;
import com.turtleplayer.persistance.framework.creator.Creator;
import com.turtleplayer.persistance.source.relational.Table;
import com.turtleplayer.persistance.source.sql.query.Limit;
import com.turtleplayer.persistance.source.sql.query.Select;

import java.util.ArrayList;
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

public class Limited<I> extends First<List<I>>
{
	private final int limit;

	public Limited(Table table,
						Creator<List<I>, Cursor> creator,
						int limit)
	{
		super(table, creator);
		this.limit = limit;
	}

	public Select get()
	{
		Select select = super.get();
		select.setLimit(new Limit(limit));
		return select;
	}

	public List<I> create(Cursor queryResult)
	{
		List<I> result = super.create(queryResult);
		return result == null ? new ArrayList<I>() : result;
	}
}
