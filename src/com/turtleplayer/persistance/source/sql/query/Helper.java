package com.turtleplayer.persistance.source.sql.query;

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

public abstract class Helper
{
	public static String getSeparatedList(String separator, String... values){

		String result = "";

		for(String value : values)
		{
			result += value + separator;
		}
		return removeLast(result, separator);
	}

    public static String getSeparatedList(String separator, SqlFragment... fragments){

        String result = "";

        for(SqlFragment fragment : fragments)
        {
            result += fragment.toSql() + separator;
        }
        return removeLast(result, separator);
    }

	public static String removeLast(String s, String pattern){
		return s.endsWith(pattern) ?
				  s.substring(0, s.length() - pattern.length()) :
				  s; //cut off last pattern
	}
}
