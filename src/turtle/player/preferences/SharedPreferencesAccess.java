/**
 *
 * TURTLE PLAYER
 *
 * Licensed under MIT & GPL
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * More Information @ www.turtle-player.co.uk
 *
 * @author Simon Honegger (Hoene84)
 */

package turtle.player.preferences;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class SharedPreferencesAccess
{

	public static <T> T getValue(Context mainContext,
										  Key<T> key)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainContext);
		if (prefs.contains(key.getKey()))
		{
			return (T) prefs.getAll().get(key.getKey());
		}
		return key.getDefaultValue();
	}

	public static <T> void putValue(Context mainContext,
											  Key<T> key,
											  T value)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainContext);
		SharedPreferences.Editor edit = prefs.edit();
		prefs.edit();

		if (value == null)
		{
			edit.remove(key.getKey());
		} else if (value instanceof Boolean)
		{
			edit.putBoolean(key.getKey(), (Boolean) value);
		} else if (value instanceof Float)
		{
			edit.putFloat(key.getKey(), (Float) value);
		} else if (value instanceof Integer)
		{
			edit.putInt(key.getKey(), (Integer) value);
		} else if (value instanceof Long)
		{
			edit.putLong(key.getKey(), (Long) value);
		} else if (value instanceof String)
		{
			edit.putString(key.getKey(), (String) value);
		} else
		{
			throw new IllegalArgumentException(
					  value.getClass().toString() + " is not supported by PreferenceManager");
		}
		edit.commit();
	}
}
