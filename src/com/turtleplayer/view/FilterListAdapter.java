/**
 *
 * TURTLE PLAYER
 *
 * Licensed under MIT & GPL
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * More Information @ www.turtle-player.co.uk
 *
 * @author Simon Honegger (Hoene84)
 */

package com.turtleplayer.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.turtleplayer.R;
import com.turtleplayer.persistance.framework.filter.FieldFilter;
import com.turtleplayer.persistance.framework.filter.Filter;
import com.turtleplayer.persistance.framework.filter.FilterSet;
import com.turtleplayer.persistance.framework.filter.FilterVisitor;
import com.turtleplayer.persistance.turtle.db.structure.Tables;
import com.turtleplayer.util.TurtleUtil;


public abstract class FilterListAdapter extends ArrayAdapter<Filter>
{
	public FilterListAdapter(Context context,
									 List<Filter> objects)
	{
		super(context, R.layout.filter_list_entry, objects);
	}

	@Override
	public View getView(int position,
							  View convertView,
							  ViewGroup parent)
	{

		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.filter_list_entry, parent, false);

		final Filter currFilter = getItem(position);

		final TextView textView = (TextView) rowView.findViewById(R.id.label);
		final ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
		final ImageView deleteIcon = (ImageView) rowView.findViewById(R.id.delete);
		final LinearLayout chooseFilterArea = (LinearLayout) rowView.findViewById(R.id.chooseFilterArea);

		currFilter.accept(new FilterVisitor<Object, Void>()
		{

			public <T> Void visit(FieldFilter<Object, T> fieldFilter)
			{
				if (Tables.TRACKS.ARTIST.equals(fieldFilter.getField()))
				{
					icon.setImageResource(R.drawable.artist24);
					textView.setText(fieldFilter.getValue().toString());
				} else if (Tables.TRACKS.ALBUM.equals(fieldFilter.getField()))
				{
					icon.setImageResource(R.drawable.album24);
					textView.setText(fieldFilter.getValue().toString());
				} else if (Tables.TRACKS.GENRE.equals(fieldFilter.getField()))
				{
					icon.setImageResource(R.drawable.genre24);
					textView.setText(TurtleUtil.translateGenreId(fieldFilter.getValue().toString()));
				}

				return null;
			}

			public Void visit(FilterSet filterSet)
			{
				//nothing
				return null;
			}
		});

		deleteIcon.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				removeFilter(currFilter);
			}
		});

		chooseFilterArea.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				chooseFilter(currFilter);
			}
		});

		return rowView;
	}

	protected abstract void removeFilter(Filter filter);

	protected abstract void chooseFilter(Filter filter);


}
