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
import android.widget.ImageView;
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

import java.util.ArrayList;

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

		private final int drawable;
		private final int drawableActive;
		private final int buttonId;
	}

	private Mode currMode;
	private final TurtleDatabase database;
	private final ListActivity listActivity;
	final DefaultAdapter<String> listAdapter;

	private Filter filter = null;

	public FileChooser(Mode currMode,
							 TurtleDatabase db,
							 ListActivity listActivity)
	{
		this.currMode = currMode;
		this.database = db;
		this.listActivity = listActivity;
		listAdapter = new DefaultAdapter<String>(
				  listActivity.getApplicationContext(),
				  new ArrayList<String>(),
				  listActivity,
				  false
		){
			@Override
			protected String format(String object)
			{
				return object;
			}
		};

		listActivity.setListAdapter(listAdapter);

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
				filter = new FilterSet(
						  filter,
						  new FieldFilter(Tables.TRACKS.ALBUM, Operator.EQ, selection)
				);
				currMode = Mode.Track;
				update();
				return null;

			case Artist:
				filter = new FilterSet(
						  filter,
						  new FieldFilter(Tables.TRACKS.ARTIST, Operator.EQ, selection)
				);
				currMode = Mode.Album;
				update();
				return null;

			case Track:
				return database.getTracks(
						  new FilterSet(
									 filter,
									 new FieldFilter(Tables.TRACKS.TITLE, Operator.EQ, selection)
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
				switch (currMode)
				{
					case Album:
						listAdapter.replace(database.getAlbumList(filter));
						break;
					case Artist:
						listAdapter.replace(database.getArtistList(filter));
						break;
					case Track:
						listAdapter.replace(database.getTrackList(filter));
						break;
					default:
						throw new RuntimeException(currMode.name() + " not expexted here");
				}
			}
		}).start();
	}

	public void updated(Instance instance)
	{
		final String toAdd;
		switch (currMode)
		{
			case Album:
				toAdd = instance.accept(new InstanceVisitor<String>()
				{
					public String visit(Track track)
					{
						return track.GetAlbum().getName();
					}

					public String visit(Album album)
					{
						throw new UnsupportedOperationException();
					}

					public String visit(Artist artist)
					{
						throw new UnsupportedOperationException();
					}
				});
				break;
			case Artist:
				toAdd = instance.accept(new InstanceVisitor<String>()
				{
					public String visit(Track track)
					{
						return track.GetArtist().getName();
					}

					public String visit(Album album)
					{
						throw new UnsupportedOperationException();
					}

					public String visit(Artist artist)
					{
						throw new UnsupportedOperationException();
					}
				});
				break;
			case Track:
				toAdd = instance.accept(new InstanceVisitor<String>()
				{
					public String visit(Track track)
					{
						return track.GetTitle();
					}

					public String visit(Album album)
					{
						throw new UnsupportedOperationException();
					}

					public String visit(Artist artist)
					{
						throw new UnsupportedOperationException();
					}
				});
				break;
			default:
				throw new RuntimeException(currMode.name() + " not expexted here");
		}

		//TODO: filter

		listAdapter.add(toAdd);

	}

	public void cleared()
	{
		listAdapter.clear();
	}

	private ImageView getButton(Mode mode)
	{
		return (ImageView) listActivity.findViewById(mode.buttonId);
	}
}
