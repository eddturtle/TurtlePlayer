package turtle.player.persistance.source.sql.query;

import turtle.player.persistance.source.relational.Field;

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

public class FieldsPart implements SqlFragment
{
	final Field[] fields;

	public FieldsPart(Field[] fields)
	{
		this.fields = fields;
	}

	public String toSql()
	{
		String[] fieldNames = new String[fields.length];
		int i = 0;
		for(Field field : fields)
		{
			fieldNames[i++] = field.getName();
		}
		return Helper.getSeparatedList(", ", fieldNames);
	}
}
