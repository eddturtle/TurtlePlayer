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

package com.turtleplayer.preferences;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public abstract class SharedPreferencesAccess
{

	public static <T, S> T getValue(Context mainContext,
										  AbstractKey<T, S> key)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainContext);
		if (prefs.contains(key.getKey()))
		{
			try
			{
				return key.unmarshall((S)prefs.getAll().get(key.getKey()));
			}
			catch (AbstractKey.UnmarshallExcpetion unmarshallExcpetion)
			{
				Log.e(Preferences.TAG, "error restoring key " + key.getKey() + ", Exception: " + unmarshallExcpetion);
				putValue(mainContext, key, null);
			}
		}
		return key.getDefaultValue();
	}

	public static <T, S> void putValue(Context mainContext,
											  AbstractKey<T, S> key,
											  T value)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainContext);
		SharedPreferences.Editor edit = prefs.edit();
		prefs.edit();

		S marshalledValue = value == null ? null : key.marshall(value);

		if (marshalledValue  == null)
		{
			edit.remove(key.getKey());
		} else if (marshalledValue  instanceof Boolean)
		{
			edit.putBoolean(key.getKey(), (Boolean) marshalledValue );
		} else if (marshalledValue  instanceof Float)
		{
			edit.putFloat(key.getKey(), (Float) marshalledValue );
		} else if (marshalledValue  instanceof Integer)
		{
			edit.putInt(key.getKey(), (Integer) marshalledValue );
		} else if (marshalledValue  instanceof Long)
		{
			edit.putLong(key.getKey(), (Long) marshalledValue );
		} else if (marshalledValue  instanceof String)
		{
			edit.putString(key.getKey(), (String) marshalledValue );
		} else
		{
			throw new IllegalArgumentException(
					  marshalledValue .getClass().toString() + " is not supported by PreferenceManager");
		}
		edit.commit();
	}
}
