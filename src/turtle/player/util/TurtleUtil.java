package turtle.player.util;

import turtle.player.R;
import turtle.player.TurtlePlayer;

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

public class TurtleUtil
{
	private static final String RES_GENRE_PREFIX = "tag.genre.";

	public static String translateGenreId(String id){

		try{
			return AndroidUtils.getResourceString(
					  TurtlePlayer.getStaticInstance(),
					  RES_GENRE_PREFIX + Integer.parseInt(id),
					  R.string.tag_genre_unknown);
		}
		catch (NumberFormatException ex)
		{
			return id;
		}
	}
}
