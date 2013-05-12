package turtle.player.persistance.turtle.mapping;

import android.database.Cursor;
import turtle.player.model.SongDigest;
import turtle.player.persistance.framework.creator.ResultCreator;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.db.structure.Views;

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

public class SongCreator implements ResultCreator<Views.SongsReadable , SongDigest, Cursor>
{

    public SongDigest create(Cursor source)
    {
        return new SongDigest(source.getString(source.getColumnIndex(Views.SongsReadable.NAME.getName())));
    }
}
