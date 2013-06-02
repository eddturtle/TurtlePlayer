package com.turtleplayer.util;

import android.content.Context;

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

public class AndroidUtils
{

	/**
	 * @return null if not existing
	 */
	public static String getResourceString(Context context, String name) {

		int nameResourceID = context.getResources().getIdentifier(name, "string", context.getApplicationInfo().packageName);

		if (nameResourceID == 0)
		{
			return null;
		}
		else
		{
			return context.getString(nameResourceID);
		}
	}

	/**
	 * @return string of fallBackRessource param if not existing
	 */
	public static String getResourceString(Context context, String name, int fallBackRessource) {
		String string = getResourceString(context, name);
		return string == null ? context.getString(fallBackRessource) : string;
	}
}
