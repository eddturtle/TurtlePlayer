package com.turtleplayer.persistance.source.sql.query;


import java.util.Arrays;
import java.util.List;

import com.turtleplayer.persistance.source.relational.Field;

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

public class WhereClauseField implements WhereClausePart
{

	final Field field;
	final Object value;
	final Operator op;

	public WhereClauseField(Field field,
									Object value,
									Operator op)
	{
		this.field = field;
		this.value = value;
		this.op = op;
	}

	public String toSql()
	{
		return " " + field.getName() + op + " ? ";
	}

	public List<Object> getParams()
	{
		return Arrays.asList(value);
	}
}
