package com.turtleplayer.persistance.framework.db;

import java.util.HashMap;
import java.util.Map;

import com.turtleplayer.controller.Observer;
import com.turtleplayer.model.Instance;

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

	private final Map<String, DbObserver> observers = new HashMap<String, DbObserver>();

	public void notifyUpdate(Instance instance){
		for(DbObserver observer : observers.values()){
			observer.updated(instance);
		}
	}

	public void notifyCleared(){
		for(DbObserver observer : observers.values()){
			observer.cleared();
		}
	}

	public interface DbObserver extends Observer
	{
		void updated(Instance instance);
		void cleared();
	}

	public void addObserver(DbObserver observer)
	{
		observers.put(observer.getId(), observer);
	}

	public void removeObserver(DbObserver observer)
	{
		observers.remove(observer);
	}
}
