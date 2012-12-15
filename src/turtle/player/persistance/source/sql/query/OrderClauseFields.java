package turtle.player.persistance.source.sql.query;

import java.util.ArrayList;
import java.util.List;

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

public class OrderClauseFields implements OrderClause
{
	private String sql;

	public OrderClauseFields(OrderClausePart... parts)
	{
        sql = Helper.getSeparatedList(" , ", parts);
	}

    private OrderClauseFields(OrderClause orderClause, OrderClause orderClause2)
    {
        sql = orderClause.toSql() + " , " + orderClause2.toSql();
    }

	public String toSql(){
		return sql;
	}

    public OrderClause apply(OrderClause orderClause)
    {
        return new OrderClauseFields(this, orderClause);
    }
}
