package turtle.player.persistance.sql;

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

public class Sql
{
	private String sql;
	private List<String> params = new ArrayList<String>();

	public Sql(String sql)
	{
		this.sql = sql;
	}

	public Sql append(String partialSql, String... params){
		sql += partialSql;
		for(String param : params)
		{
			this.params.add(param);
		}
		return this;
	}

	public void append(Sql sql){
		this.sql += sql.getSql();
		for(String param : sql.getParams())
		{
			this.params.add(param);
		}
	}

	public String getSql(){
		return sql;
	}

	public List<String> getParams()
	{
		return params;
	}

	public void removeLast(String pattern){
		sql = sql.endsWith(pattern) ?
				  sql.substring(0, sql.length() - pattern.length()) :
				  sql; //cut off last pattern
	}
}
