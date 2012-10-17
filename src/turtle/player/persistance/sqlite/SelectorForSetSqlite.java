package turtle.player.persistance.sqlite;

import android.database.Cursor;
import turtle.player.persistance.selector.SelectorForSet;

import java.util.HashSet;
import java.util.Set;

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

/**
 * @param <Q> eg sql String
 * @param <I> resulting set contains instance I
 */
public abstract class SelectorForSetSqlite<Q, I> implements SelectorForSet<Q, I, Cursor, Cursor>
{
	@Override
	public Set<I> create(Cursor cursor)
	{
		Set<I> result = new HashSet<I>();

		if (cursor.moveToFirst())
		{
			do
			{
				result.add(createPart(cursor));

			} while (cursor.moveToNext());
		}

		return result;
	}
}
