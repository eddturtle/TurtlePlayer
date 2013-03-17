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

/**
 * @param <O> object type to store
 * @param <S> stored type
 */
public abstract class AbstractKey<O, S>
{

	private final String key;
	private final O defaultValue;

	AbstractKey(String key,
					O defaultValue)
	{
		this.defaultValue = defaultValue;
		this.key = key;
	}

	public String getKey()
	{
		return key;
	}

	public O getDefaultValue()
	{
		return defaultValue;
	}

	public abstract S marshall(O object);

	public abstract O unmarshall(S object);
}
