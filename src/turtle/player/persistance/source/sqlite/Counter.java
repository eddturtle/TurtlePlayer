package turtle.player.persistance.source.sqlite;

import android.database.Cursor;
import turtle.player.persistance.framework.selector.Selector;
import turtle.player.persistance.source.sql.Sql;

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

public class Counter implements Selector<Sql, Integer, Cursor>
{
	private final String tableName;

	public Counter(String tableName)
	{
		this.tableName = tableName;
	}

	@Override
	public Sql get()
	{
		return new Sql("SELECT count(*) FROM " + tableName);
	}

	@Override
	public Integer create(Cursor queryResult)
	{
		queryResult.moveToFirst();
		return queryResult.getInt(0);
	}
}
