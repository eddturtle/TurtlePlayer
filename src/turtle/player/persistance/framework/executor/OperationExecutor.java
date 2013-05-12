package turtle.player.persistance.framework.executor;

import turtle.player.persistance.framework.db.Database;
import turtle.player.persistance.framework.query.OperationDelete;
import turtle.player.persistance.framework.query.OperationRead;
import turtle.player.persistance.framework.query.OperationInsert;
import turtle.player.persistance.framework.mapping.Mapping;

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
	public static <I, Q, C> I execute(Database<Q, C, ?> db, final OperationRead<Q, C, I> operation){
		return db.read(operation.get(), new Database.DbReadOp<I, C>(){
			public I read(C c)
			{
				return operation.map(c);
			}
		});
	}

	public static <I, C, D, Q> int execute(Database<?, ?, D> db, final OperationInsert<D, I> operation, final I instance){
		return db.write(new Database.DbWriteOp<D, I>()
		{
			public int write(D target,
									I instance)
			{
				return operation.insert(target, instance);
			}
		}, instance);
	}

	public static <D, T> void execute(Database<?, ?, D> db, final OperationDelete<D, T> operation, T target){
		db.write(new Database.DbWriteOp<D, T>()
		{
			public int write(D target,
									T instance)
			{
				return operation.delete(target, instance);
			}
		}, target);
	}
}
