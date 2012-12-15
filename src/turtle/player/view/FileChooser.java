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

import android.app.ListActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import turtle.player.R;
import turtle.player.model.*;
import turtle.player.persistance.framework.filter.FieldFilter;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.filter.FilterSet;
import turtle.player.persistance.framework.filter.Operator;
import turtle.player.persistance.source.sql.query.WhereClause;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.util.DefaultAdapter;

import java.util.List;

public class FileChooser implements TurtleDatabase.DbObserver
{
	public enum Mode
	{
		Album(R.id.albumButton, R.drawable.album48, R.drawable.album48_active),
		Artist(R.id.artistButton, R.drawable.artist48, R.drawable.artist48_active),
		Track(R.id.trackButton, R.drawable.track48, R.drawable.track48_active);

		Mode(int buttonId,
			  int drawable,
			  int drawableActive)
		{
			this.drawable = drawable;
			this.drawableActive = drawableActive;
			this.buttonId = buttonId;
		}

		private int drawable;
		private int drawableActive;
		private int buttonId;
	}

	private Mode currMode;
	private TurtleDatabase database;
	private ListActivity listActivity;

	private Filter<WhereClause> filter = null;

	public FileChooser(Mode currMode,
							 TurtleDatabase db,
							 ListActivity listActivity)
	{
		this.currMode = currMode;
		this.database = db;
		this.listActivity = listActivity;

		change(currMode);

		init();
	}

	private void init()
	{
		database.addObserver(this);
		for (final Mode currMode : Mode.values())
		{
			getButton(currMode).setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					change(currMode);
				}
			});
		}
	}

	/**
	 * @param selection
	 * @return null if no track was selected, track if trak was selected
	 */
	public Track choose(String selection)
	{

		switch (currMode)
		{
			case Album:
				filter = new FilterSet<WhereClause>(
						  filter,
						  new FieldFilter<WhereClause>(Tables.TRACKS.ALBUM, Operator.EQ, selection)
				);
				currMode = Mode.Track;
				update();
				return null;

			case Artist:
				filter = new FilterSet<WhereClause>(
						  filter,
						  new FieldFilter<WhereClause>(Tables.TRACKS.ARTIST, Operator.EQ, selection)
				);
				currMode = Mode.Album;
				update();
				return null;

			case Track:
				return database.getTracks(
						  new FilterSet<WhereClause>(
									 filter,
									 new FieldFilter<WhereClause>(Tables.TRACKS.TITLE, Operator.EQ, selection)
						  )
				).iterator().next();
			
			default:
				throw new RuntimeException(currMode.name() + " not expexted here");
		}
	}

	public void change(Mode mode)
	{
		currMode = mode;
		for (final Mode aMode : Mode.values())
		{
			final ImageView button = getButton(aMode);
			button.post(new Runnable()
			{
				public void run()
				{
					button.setImageResource(aMode.equals(currMode) ? aMode.drawableActive : aMode.drawable);
				}
			});
		}
		filter = null;
		update();
	}

	public void update()
	{
		new Thread(new Runnable()
		{
			public void run()
			{

				final List<String> list;

				switch (currMode)
				{
					case Album:
						list = database.getAlbumList(filter);
						break;
					case Artist:
						list = database.getArtistList(filter);
						break;
					case Track:
						list = database.getTrackList(filter);
						break;
					default:
						throw new RuntimeException(currMode.name() + " not expexted here");
				}
				final ListAdapter listAdapter = new DefaultAdapter<String>(
						  listActivity.getApplicationContext(),
						  list.toArray(new String[list.size()])
				){
					@Override
					protected String format(String object)
					{
						return object;
					}
				};

				listActivity.runOnUiThread(new Runnable()
				{
					public void run()
					{
						//set List
						listActivity.setListAdapter(listAdapter);
					}

				});
			}
		}).start();
	}

	public void updated()
	{
		update();
	}

	private ImageView getButton(Mode mode)
	{
		return (ImageView) listActivity.findViewById(mode.buttonId);
	}
}
