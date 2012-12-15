package turtle.player.persistance.source.sql;

import android.database.Cursor;
import turtle.player.persistance.framework.creator.Creator;
import turtle.player.persistance.framework.mapping.Mapping;
import turtle.player.persistance.source.relational.Table;
import turtle.player.persistance.source.sql.query.Limit;
import turtle.player.persistance.source.sql.query.OrderClauseRandom;
import turtle.player.persistance.source.sql.query.Select;

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
	private final Table table;
	private final Creator<I, Cursor> creator;

	public First(Table table,
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
