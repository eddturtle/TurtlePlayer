package turtle.player.persistance.source.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import turtle.player.persistance.framework.query.OperationInsert;
import turtle.player.persistance.framework.mapping.Mapping;
import turtle.player.persistance.source.relational.Table;

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

public class InsertOperationSqlLite<I> implements OperationInsert<SQLiteDatabase, I>
{
    private final Mapping<Table, ContentValues, I> mapping;

    public InsertOperationSqlLite(Mapping<Table, ContentValues, I> mapping)
    {
        this.mapping = mapping;
    }

    public void insert(final SQLiteDatabase db, I instance)
	{
		db.insert(mapping.get().getName(), null, mapping.create(instance));
	}
}
