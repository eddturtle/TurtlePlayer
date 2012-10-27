package turtle.player.persistance.source.sqlite;

import android.database.Cursor;
import turtle.player.persistance.framework.creator.Creator;
import turtle.player.persistance.framework.creator.CreatorForSet;

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
 * @param <I> resulting set contains instance I
 */
public class CreatorForSetSqlite<I> extends CreatorForSet<I, Cursor, Cursor>
{
    public CreatorForSetSqlite(Creator<I, Cursor> creator)
    {
        super(creator);
    }

    @Override
    public boolean hasNext(Cursor queryResult)
    {
        return !queryResult.isLast();
    }

    @Override
    public Cursor next(Cursor queryResult)
    {
        queryResult.moveToNext();
        return queryResult;
    }
}
