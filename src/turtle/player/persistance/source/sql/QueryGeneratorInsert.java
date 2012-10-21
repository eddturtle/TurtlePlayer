package turtle.player.persistance.source.sql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import turtle.player.persistance.framework.db.Database;
import turtle.player.persistance.framework.selector.OperationSelector;
import turtle.player.persistance.framework.selector.QueryGenerator;
import turtle.player.persistance.framework.selector.QuerySelector;
import turtle.player.persistance.source.relational.Table;
import turtle.player.persistance.turtle.db.structure.Tables;

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

public abstract class QueryGeneratorInsert<I> implements QuerySelector<Table, ContentValues, I>
{
	final Table table;

	public QueryGeneratorInsert(Table table)
	{
		this.table = table;
	}

	@Override
	public Table get()
	{
		return table;
	}
}
