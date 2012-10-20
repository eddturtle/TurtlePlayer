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
 * @param <D> DB for write operations
 */
public interface Database<Q, C, D>
{
	abstract void read(Q query, DbReadOp<C> readOp);
	abstract void write(DbWriteOp<D> writeOp);

	interface DbReadOp<C>
	{
		public void read(C db);
	}

	interface DbWriteOp<D>
	{
		public void write(D db);
	}
}
