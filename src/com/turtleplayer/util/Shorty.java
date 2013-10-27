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

package com.turtleplayer.util;

import java.util.*;

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

	public static <T extends Collection<?>>  boolean isVoid(T collection)
	{
		return collection == null || collection.size() == 0;
	}

	public static String avoidNull(String s)
	{
		return isVoid(s) ? "" : s;
	}

	public static <T extends Collection<?>> T avoidNull(T collection, T emptyCollections)
	{
		return isVoid(collection) ? emptyCollections : collection;
	}

	public static <E> Set<E> oneElementSet(E element)
	{
		final Set<E> result = new HashSet<E>();
		result.add(element);
		return result;
	}

	@SuppressWarnings({"unchecked"})
	public static <T> List<T> concat(List<T>... lists)
	{
		List<T> result = new ArrayList<T>();
		for(List<T> list : lists)
		{
			result.addAll(list);
		}

		return result;
	}

	public static <T> List<T> concatWith(List<T> arrays, T... elements)
	{
		return concat(arrays, Arrays.asList(elements));
	}
}
