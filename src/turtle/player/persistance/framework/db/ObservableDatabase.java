package turtle.player.persistance.framework.db;

import turtle.player.persistance.framework.db.Database;

import java.util.ArrayList;
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

public abstract class ObservableDatabase<Q,C,D> implements Database<Q,C,D>
{

	//--------------------------------------------- Observable

	private List<DbObserver> observers = new ArrayList<DbObserver>();

	public void notifyUpdate(){
		for(DbObserver observer : observers){
			observer.updated();
		}
	}

	public interface DbObserver
	{
		void updated();
	}

	public void addObserver(DbObserver observer)
	{
		observers.add(observer);
	}

	public void removeObserver(DbObserver observer)
	{
		observers.remove(observer);
	}
}
