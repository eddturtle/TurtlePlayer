//package turtle.player.persistance.source.sql;
//
//import android.database.Cursor;
//import turtle.player.persistance.framework.creator.Creator;
//import turtle.player.persistance.framework.db.Database;
//import turtle.player.persistance.framework.executor.OperationExecutor;
//import turtle.player.persistance.framework.filter.FieldFilter;
//import turtle.player.persistance.framework.filter.Filter;
//import turtle.player.persistance.framework.filter.FilterSet;
//import turtle.player.persistance.framework.mapping.Mapping;
//import turtle.player.persistance.framework.sort.SortOrder;
//import turtle.player.persistance.source.relational.FieldPersistable;
//import turtle.player.persistance.source.relational.Table;
//import turtle.player.persistance.source.sql.query.Limit;
//import turtle.player.persistance.source.sql.query.Select;
//import turtle.player.persistance.source.sql.query.WhereClause;
//import turtle.player.persistance.source.sqlite.CounterSqlite;
//import turtle.player.persistance.source.sqlite.QuerySqlite;
//
///**
// * TURTLE PLAYER
// * <p/>
// * Licensed under MIT & GPL
// * <p/>
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
// * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
// * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
// * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
// * OR OTHER DEALINGS IN THE SOFTWARE.
// * <p/>
// * More Information @ www.turtle-player.co.uk
// *
// * @author Simon Honegger (Hoene84)
// */
//
//public abstract class PagedSingle<I> implements Mapping<Select, I, Cursor>
//{
//	private final Table<I> table;
//	private final Creator<I, Cursor> creator;
//	private final Database<Select, Cursor, ?> db;
//
//	private int n = 0;
//	private SortOrder order = SortOrder.ASC;
//
//	public PagedSingle(Database<Select, Cursor, ?> db,
//							 Table<I> table,
//							 Creator<I, Cursor> creator,
//							 I startPoint)
//	{
//		this.db = db;
//		this.table = table;
//		this.creator = creator;
//
//		if(startPoint != null){
//			n = 0;
//		}
//	}
//
//	public void setOrder(SortOrder order)
//	{
//		this.order = order;
//	}
//
//	public Select get()
//	{
//		Select select = new Select(table);
//		select.setLimit(new Limit(getNextIndex(), 1));
//		return select;
//	}
//
//	public I create(Cursor queryResult)
//	{
//		return creator.create(queryResult);
//	}
//
//	private int getNextIndex()
//	{
//		switch (order)
//		{
//			case ASC:
//				n = adaptMinMaxOverflow(n+1);
//				break;
//			case DESC:
//				n = adaptMinMaxOverflow(n-1);
//				break;
//			default:
//				throw new RuntimeException("Unknown Order");
//		}
//		return n;
//	}
//
//	private int adaptMinMaxOverflow(int i)
//	{
//		int maxIndex = getMax()-1;
//		if(i < 0)
//		{
//			return maxIndex;
//		}
//		if(i > maxIndex)
//		{
//			return 0;
//		}
//		return i;
//	}
//
//	/**
//	 * max results, 1 based
//	 */
//	protected int getMax(){
//		return OperationExecutor.execute(
//				  db,
//				  new QuerySqlite<Integer>(getFilter(), new CounterSqlite(table)));
//	}
//
//	/**
//	 * max results, 1 basedT
//	 */
//	protected int getRowNumberOf(I instance){
//		Filter<WhereClause> filter = getFilter();
//		for(FieldPersistable<I, ?> field : table.getPrimaryKeyFields()){
//			filter = new FilterSet<WhereClause>(getFilter(), new FieldFilter<WhereClause>(field, , field.get(instance).toString()));
//		}
//
//		return OperationExecutor.execute(
//				  db,
//				  new QuerySqlite<Integer>(filter, new CounterSqlite(table)));
//	}
//
//	protected abstract Filter<WhereClause> getFilter();
//}
