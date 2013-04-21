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

package turtle.player.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import turtle.player.R;
import turtle.player.model.*;
import turtle.player.presentation.InstanceFormatter;

import java.util.Collections;
import java.util.List;

public class DefaultAdapter<T extends Instance> extends ArrayAdapter<T>
{
	private final List<T> objects;
	private final Activity activity;
	private final boolean allowsDuplicates;
	private final InstanceFormatter formatter;
	final Handler instanceAddedhandler = new Handler(Looper.getMainLooper());

	/**
	 * @param context needed by super
	 * @param objects has to be prepared according allowsDuplicates and has to be sorted (if needed)
	 * @param startingActivity to join UI Thread
	 * @param allowsDuplicates remove duplicates by adding items
	 * @param formatter
	 */
	public DefaultAdapter(
			  Context context,
			  List<T> objects,
			  Activity startingActivity,
			  boolean allowsDuplicates,
			  InstanceFormatter formatter)
	{
		super(context, R.layout.file_list_entry, objects);
		this.activity = startingActivity;
		this.objects = objects;
		this.allowsDuplicates = allowsDuplicates;
		this.formatter = formatter;
	}

	public void add(final T object)
	{
		if(allowsDuplicates || !objects.contains(object)){
			activity.runOnUiThread(new Runnable()
			{
				public void run()
				{
					if(allowsDuplicates || !objects.contains(object)){
						objects.add(object);
						if(!instanceAddedhandler.hasMessages(0)){
							instanceAddedhandler.postDelayed(new Runnable()
							{
								public void run()
								{
									Collections.sort(objects, new FormattedInstanceComparator(formatter));
									notifyDataSetChanged();
								}
							}, 2000);
						}
					}
				}
			});
		}
	}

	/**
	 * @param object objects has to be prepared according allowsDuplicates and has to be sorted (if needed)
	 */
	public void replace(final List<? extends T> object)
	{
		activity.runOnUiThread(new Runnable()
		{
			public void run()
			{
				objects.clear();
				objects.addAll(object);
				notifyDataSetChanged();
			}
		});
	}

	public void clear()
	{
		activity.runOnUiThread(new Runnable()
		{
			public void run()
			{
				objects.clear();
				notifyDataSetChanged();
			}
		});
	}

@Override
	public View getView(int position,
							  View convertView,
							  ViewGroup parent)
	{

		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.file_list_entry, parent, false);

		T currObject = getItem(position);

		TextView textView = (TextView) rowView.findViewById(R.id.label);
		textView.setText(currObject.accept(formatter));

		final ImageView icon = (ImageView) rowView.findViewById(R.id.icon);

		currObject.accept(new InstanceVisitor<Object>()
		{
			public Object visit(Track track)
			{
				icon.setImageResource(R.drawable.track24);
				return null;
			}

			public Object visit(TrackDigest track)
			{
				icon.setImageResource(R.drawable.track24);
				return null;
			}

			public Object visit(Album album)
			{
				icon.setImageResource(R.drawable.album24);
				return null;
			}

			public Object visit(Genre genre)
			{
				icon.setImageResource(R.drawable.genre24);
				return null;
			}

			public Object visit(Artist artist)
			{
				icon.setImageResource(R.drawable.artist24);
				return null;
			}

			public Object visit(FSobject FSobject)
			{
				icon.setImageResource(R.drawable.dir24);
				return null;
			}
		});

		return rowView;
	}
}
