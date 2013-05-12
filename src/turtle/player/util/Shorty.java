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

package turtle.player.util;

import java.util.HashSet;
import java.util.Set;

public class Shorty
{
	public static boolean isVoid(String string)
	{
		return string == null || string.length() == 0;
	}

	public static boolean isVoid(Integer integer)
	{
		return integer == null || integer == 0;
	}

	public static String avoidNull(String s)
	{
		return isVoid(s) ? "" : s;
	}

	public static <E> Set<E> oneElementSet(E element)
	{
		final Set<E> result = new HashSet<E>();
		result.add(element);
		return result;
	}

	@SuppressWarnings({"unchecked"})
	public static <T> T[] concat(T[]... arrays)
	{
		int resultLength = 0;
		for(T[] array : arrays)
		{
			resultLength += array.length;
		}

		T[] result = (T[]) new Object[resultLength];

		int pos = 0;
		for(T[] array : arrays)
		{
			System.arraycopy(array, 0, result, pos, array.length);
			pos += array.length;
		}

		return result;
	}

	public static <T> T[] concatWith(T[] arrays, T... elements)
	{
		return concat(arrays, elements);
	}
}
