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

package turtle.player.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import turtle.player.R;
import turtle.player.model.*;
import turtle.player.persistance.framework.filter.*;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.filter.DirFilter;
import turtle.player.persistance.turtle.filter.TurtleFilterVisitor;
import turtle.player.presentation.InstanceFormatter;

import java.util.List;


public abstract class FilterListAdapter extends ArrayAdapter<Filter<? super Tables.Tracks>>
{
	public FilterListAdapter(Context context,
									 List<Filter<? super Tables.Tracks>> objects)
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

		final Filter<? super Tables.Tracks> currFilter = getItem(position);

		final TextView textView = (TextView) rowView.findViewById(R.id.label);
		final ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
		final ImageView deleteIcon = (ImageView) rowView.findViewById(R.id.delete);
		final LinearLayout chooseFilterArea = (LinearLayout) rowView.findViewById(R.id.chooseFilterArea);

		currFilter.accept(new TurtleFilterVisitor<Tables.Tracks, Void>()
		{
			public <T, Z> Void visit(FieldFilter<? super Tables.Tracks, Z, T> fieldFilter)
			{
				final Instance instance;
				if (Tables.ArtistsReadable.ARTIST.equals(fieldFilter.getField()))
				{
					instance = new SongDigest(fieldFilter.getValue().toString());
					icon.setImageResource(R.drawable.artist24);
				}
				else if (Tables.AlbumsReadable.ALBUM.equals(fieldFilter.getField()))
				{
					instance = new AlbumDigest(fieldFilter.getValue().toString());
					icon.setImageResource(R.drawable.album24);
				}
				else if (Tables.GenresReadable.GENRE.equals(fieldFilter.getField()))
				{
					instance = new GenreDigest(fieldFilter.getValue().toString());
					icon.setImageResource(R.drawable.genre24);
				}
				else
				{
					throw new RuntimeException("Unknown Filter: " + fieldFilter);
				}

				textView.setText(instance.accept(InstanceFormatter.SHORT));

				return null;
			}

			public Void visit(FilterSet<? super Tables.Tracks> filterSet)
			{
				//nothing
				return null;
			}

			public Void visit(NotFilter<? super Tables.Tracks> notFilter)
			{
				notFilter.getFilter().accept(this);
				textView.setText("! " + textView.getText());
				return null;
			}

			public Void visit(DirFilter dirFilter)
			{
				String withWildcard = dirFilter.getValue().replaceAll("%", "*");
				int indexOfLastSlash = withWildcard.lastIndexOf('/');
				String allAfterTrailingSlash = withWildcard.substring(indexOfLastSlash + 1);
				if(allAfterTrailingSlash.equals("*") || allAfterTrailingSlash.isEmpty())
				{
					String allTillLastSlash = withWildcard.substring(0, indexOfLastSlash);
					indexOfLastSlash = allTillLastSlash.lastIndexOf('/');
					allAfterTrailingSlash = allTillLastSlash.substring(indexOfLastSlash + 1);
				}
				textView.setText(allAfterTrailingSlash);
				icon.setImageResource(R.drawable.dir24);
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

	protected abstract void removeFilter(Filter<? super Tables.Tracks> filter);

	protected abstract void chooseFilter(Filter<? super Tables.Tracks> filter);


}
