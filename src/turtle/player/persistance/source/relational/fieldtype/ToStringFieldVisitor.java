package turtle.player.persistance.source.relational.fieldtype;

import turtle.player.persistance.framework.creator.Creator;
import turtle.player.persistance.framework.mapping.Mapping;
import turtle.player.persistance.source.sql.QueryGeneratorTable;

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

public class ToStringFieldVisitor<RESULT> implements  FieldVisitor<String, RESULT>
{
	final RESULT result;

	public ToStringFieldVisitor(RESULT result)
	{
		this.result = result;
	}

	public String visit(FieldPersistableAsString<? super RESULT> field)
	{
		return field.get(result);
	}

	public String visit(FieldPersistableAsDouble<? super RESULT> field)
	{
		return field.get(result).toString();
	}

	public String visit(FieldPersistableAsInteger<? super RESULT> field)
	{
		return field.get(result).toString();
	}
}
