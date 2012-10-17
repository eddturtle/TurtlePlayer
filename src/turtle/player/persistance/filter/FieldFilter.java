package turtle.player.persistance.filter;

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

public class FieldFilter<Q> implements Filter<Q>
{
	private final String fieldName;
	private final String fieldValue;

	public FieldFilter(String fieldName,
							 String fieldValue)
	{
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	public String getFieldName()
	{
		return fieldName;
	}

	public String getFieldValue()
	{
		return fieldValue;
	}

	@Override
	public Q accept(Q query, FilterVisitor<Q> visitor)
	{
		return visitor.visit(query, this);
	}
}
