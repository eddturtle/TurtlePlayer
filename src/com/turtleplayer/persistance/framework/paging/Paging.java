package com.turtleplayer.persistance.framework.paging;

import com.turtleplayer.persistance.framework.filter.*;
import com.turtleplayer.persistance.framework.sort.*;

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

public abstract class Paging
{

	public static <I> Filter getFilter(Filter oldFilters,
										I instance,
										Order order)
	{
		if(instance != null)
		{
			return new FilterSet(order.accept(new PagingFilterBuilder<I>(instance)), oldFilters);
		}
		else
		{
			return oldFilters;
		}
	}
}
