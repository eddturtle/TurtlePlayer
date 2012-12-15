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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import turtle.player.R;
import turtle.player.model.Instance;
import turtle.player.presentation.InstanceFormatter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

/**
 * Should be created outside the UI Thread, the initial sorting in the constructor can take a long time
 * for big lists.
 */
public abstract class DefaultAdapter<T> extends ArrayAdapter<T>
{
	public DefaultAdapter(
			  Context context,
			  T[] objects)
	{
		super(context, R.layout.file_list_entry, objects);
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
		textView.setText(format(currObject));

		return rowView;
	}

	protected abstract String format(T object);
}
