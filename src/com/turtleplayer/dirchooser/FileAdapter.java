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

package com.turtleplayer.dirchooser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;


public class FileAdapter extends ArrayAdapter<File>
{

	public final FileSorter fileSorter = new FileSorter();

	public FileAdapter(Context context,
							 File[] files)
	{
		super(context, com.turtleplayerv2.R.layout.file_list_entry, files);
		sort(fileSorter);
	}

	@Override
	public View getView(int position,
							  View convertView,
							  ViewGroup parent)
	{

		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(com.turtleplayerv2.R.layout.file_list_entry, parent, false);

		File currFile = getItem(position);

		TextView textView = (TextView) rowView.findViewById(com.turtleplayerv2.R.id.label);
		textView.setText(currFile.getName());

		ImageView icon = (ImageView) rowView.findViewById(com.turtleplayerv2.R.id.icon);
		if (currFile.isDirectory())
		{
			icon.setImageResource(com.turtleplayerv2.R.drawable.folder48);
		} else
		{
			icon.setImageResource(android.R.color.transparent);
		}

		return rowView;
	}
}
