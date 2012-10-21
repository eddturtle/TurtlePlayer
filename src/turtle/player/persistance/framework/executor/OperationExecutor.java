package turtle.player.persistance.framework.executor;

import turtle.player.persistance.framework.db.Database;
import turtle.player.persistance.framework.query.OperationRead;
import turtle.player.persistance.framework.query.OperationWrite;
import turtle.player.persistance.framework.selector.Mapping;

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

public abstract class OperationExecutor
{
	public static <I, Q, C> I execute(Database<Q, C, ?> db, final OperationRead<Q, C, I> operation, final Mapping<Q, I, C> mapping){
		return db.read(operation.get(mapping), new Database.DbReadOp<I, C>(){

			@Override
			public I read(C c)
			{
				return operation.map(c, mapping);
			}
		});
	}

	public static <I, C, D, Q> void execute(Database<?, ?, D> db, final OperationWrite<D, Mapping<Q, C, I>, I> operation, final Mapping<Q, C, I> mapping, final I instance){
		db.write(new Database.DbWriteOp<D, I>()
		{
			@Override
			public void write(D target,
									I instance)
			{
				operation.map(target, mapping, instance);
			}
		}, instance);
	}
}
