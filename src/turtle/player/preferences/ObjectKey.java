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

import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;

import java.io.*;

public class ObjectKey<T> extends AbstractKey<T, String>
{

	public ObjectKey(String key,
						  T defaultValue)
	{
		super(key, defaultValue);
	}

	@Override
	public String marshall(T object)
	{
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

		ObjectOutputStream objectOutput;
		try {
			objectOutput = new ObjectOutputStream(arrayOutputStream);
			objectOutput.writeObject(object);
			byte[] data = arrayOutputStream.toByteArray();
			objectOutput.close();
			arrayOutputStream.close();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Base64OutputStream b64 = new Base64OutputStream(out, Base64.DEFAULT);
			b64.write(data);
			b64.close();
			out.close();

			return new String(out.toByteArray());
		} catch (IOException e) {
			Log.e(Preferences.TAG, "error saving key " + getKey() + " to: " + object.toString());
			return "";
		}
	}

	@Override
	public T unmarshall(String object)
	{
		try
		{
			byte[] bytes = object.getBytes();
			if (bytes.length == 0) {
				return null;
			}
			ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
			Base64InputStream base64InputStream = new Base64InputStream(byteArray, Base64.DEFAULT);
			ObjectInputStream in = new ObjectInputStream(base64InputStream);
			return (T) in.readObject();
		} catch (ClassNotFoundException e)
		{
			Log.e(Preferences.TAG, "error restoring key " + getKey() + " from: " + object);
		} catch (IOException e)
		{
			Log.e(Preferences.TAG, "error restoring key " + getKey() + " from: " + object);
		}
		return getDefaultValue();
	}
}
