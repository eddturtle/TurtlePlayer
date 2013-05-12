package turtle.player.persistance.source.sql;

import android.database.Cursor;
import turtle.player.persistance.framework.creator.CreatorForList;
import turtle.player.persistance.framework.mapping.Mapping;
import turtle.player.persistance.source.relational.Field;
import turtle.player.persistance.source.relational.View;
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

public class MappingDistinct<TARGET, PROJECTION extends View, RESULT> implements Mapping<Select, List<RESULT>, Cursor>
{
	private final PROJECTION view;
	private final Field[] field;
	private final CreatorForList<? super TARGET, RESULT, Cursor, Cursor> creator;

	public MappingDistinct(PROJECTION view,
								  CreatorForList<? super TARGET, RESULT, Cursor, Cursor> creator,
								  Field... field)
	{
		this.view = view;
		this.field = field;
      this.creator = creator;
	}

	public Select get()
	{
		return new Select(view, Select.SelectMethod.DISTINCT, field);
	}

    public List<RESULT> create(Cursor queryResult)
    {
        return creator.create(queryResult);
    }
}
