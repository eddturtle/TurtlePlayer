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

public class WhereClause implements WhereClausePart
{
	private List<Object> params = new ArrayList<Object>();
	private String sql;

	public WhereClause(WhereClauseField part)
	{
		sql = part.toSql();
		params.addAll(part.getParams());
	}

	private WhereClause(String sql, List<Object> params)
	{
		this.sql = sql;
		this.params = params;
	}

	public WhereClause apply(BoolOperator op, WhereClausePart part){

		List<Object> newParams = new ArrayList<Object>(params);
		newParams.addAll(part.getParams());

		String newSql = " (" + sql + ") " + op + " (" + part.toSql() + ") ";
		return new WhereClause(newSql, newParams);
	}

	public String toSql(){
		return sql;
	}

	public List<Object> getParams()
	{
		return params;
	}
}
