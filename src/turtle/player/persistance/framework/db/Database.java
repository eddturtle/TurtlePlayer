package turtle.player.persistance.framework.db;

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

import java.util.ArrayList;
import java.util.List;

/**
 * @param <C> eg Cursor
 * @param <Q> eg sql as String
 * @param <D> DB object for write operations
 */
public interface Database<Q, C, D>
{
	abstract <I> I read(Q query, DbReadOp<I, C> readOp);
	abstract <I> void write(DbWriteOp<D, I> writer, I instance);

	interface DbReadOp<I, C>
	{
		public I read(C db);
	}

	interface DbWriteOp<D, I>
	{
		public void write(D target, I instance);
	}
}
