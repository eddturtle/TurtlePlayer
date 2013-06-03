package com.turtleplayer.persistance.turtle.mapping;

import android.content.ContentValues;
import com.turtleplayer.model.FSobject;
import com.turtleplayer.persistance.source.sql.QueryGeneratorTable;
import com.turtleplayer.persistance.turtle.db.structure.Tables;

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

public class FsObjectToDbMapper extends QueryGeneratorTable<FSobject>
{
	public FsObjectToDbMapper()
	{
		super(Tables.DIRS);
	}

	public ContentValues create(FSobject fsObject)
	{
		final ContentValues values = new ContentValues();

		values.put(Tables.Dirs.NAME.getName(), fsObject.getName());
		values.put(Tables.Dirs.PATH.getName(), fsObject.getPath());

		return  values;
	}
}
