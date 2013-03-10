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
import turtle.player.common.MatchFilterVisitor;
import turtle.player.model.*;
import turtle.player.persistance.framework.filter.FieldFilter;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.filter.FilterSet;
import turtle.player.persistance.framework.filter.Operator;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.presentation.InstanceFormatter;
import turtle.player.util.DefaultAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FileChooser implements TurtleDatabase.DbObserver
{
	public enum Mode
	{
		Album(R.id.albumButton, R.drawable.album48, R.drawable.album48_active),
		Artist(R.id.artistButton, R.drawable.artist48, R.drawable.artist48_active),
		Track(R.id.trackButton, R.drawable.track48, R.drawable.track48_active),
		Genre(R.id.genreButton, R.drawable.genre48, R.drawable.genre48_active);

		private Mode(int buttonId,
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
	final DefaultAdapter<Instance> listAdapter;

	private Set<Filter> filters = new HashSet<Filter>();

	public FileChooser(Mode currMode,
							 TurtleDatabase db,
							 ListActivity listActivity)
	{
		this.currMode = currMode;
		this.database = db;
		this.listActivity = listActivity;
		listAdapter = new DefaultAdapter<Instance>(
				  listActivity.getApplicationContext(),
				  new ArrayList<Instance>(),
				  listActivity,
				  false,
				  InstanceFormatter.SHORT);

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

	private Filter getFilter()
	{
		return new FilterSet(filters);
	}

	/**
	 * @param selection
	 * @return null if no track was selected, track if trak was selected
	 */
	public Track choose(Instance selection)
	{

		return selection.accept(new InstanceVisitor<Track>()
		{
			public Track visit(Track track)
			{
				filters.add(new FieldFilter<Track, String>(Tables.TRACKS.TITLE, Operator.EQ, track.GetTitle()));
				return chooseFirst();
			}

			public Track visit(TrackDigest track)
			{
				filters.add(new FieldFilter<Track, String>(Tables.TRACKS.TITLE, Operator.EQ, track.getName()));
				return chooseFirst();
			}

			public Track visit(Album album)
			{
				filters.add(new FieldFilter<Track, String>(Tables.TRACKS.ALBUM, Operator.EQ, album.getId()));
				currMode = Mode.Track;
				update();
				return null;
			}

			public Track visit(Genre genre)
			{
				filters.add(new FieldFilter<Track, String>(Tables.TRACKS.GENRE, Operator.EQ, genre.getId()));
				currMode = Mode.Artist;
				update();
				return null;
			}

			public Track visit(Artist artist)
			{
				filters.add(new FieldFilter<Track, String>(Tables.TRACKS.ARTIST, Operator.EQ, artist.getId()));
				currMode = Mode.Album;
				update();
				return null;
			}
		});
	}

	public Track chooseFirst()
	{
		return database.getTracks(getFilter()).iterator().next();
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
		filters.clear();
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
						listAdapter.replace(database.getAlbumList(getFilter()));
						break;
					case Artist:
						listAdapter.replace(database.getArtistList(getFilter()));
						break;
					case Genre:
						listAdapter.replace(database.getGenreList(getFilter()));
						break;
					case Track:
						listAdapter.replace(database.getTrackList(getFilter()));
						break;
					default:
						throw new RuntimeException(currMode.name() + " not expexted here");
				}
			}
		}).start();
	}

	public void updated(final Instance instance)
	{
		if(getFilter().accept(new MatchFilterVisitor<Instance>(instance)))
		{
			Instance instanceToAdd = instance.accept(new InstanceVisitor<Instance>()
			{
				public Instance visit(Track track)
				{
					switch (currMode)
					{
						case Album:
							return track.GetAlbum();
						case Artist:
							return track.GetArtist();
						case Genre:
							return track.GetGenre();
						case Track:
							return track;
						default:
							throw new RuntimeException(currMode.name() + " not expexted here");
					}
				}

				public Instance visit(TrackDigest track)
				{
					return Mode.Track.equals(currMode) ? track : null;
				}

				public Instance visit(Album album)
				{
					return Mode.Album.equals(currMode) ? album : null;
				}

				public Instance visit(Genre genre)
				{
					return Mode.Genre.equals(currMode) ? genre : null;
				}

				public Instance visit(Artist artist)
				{
					return Mode.Artist.equals(currMode) ? artist : null;
				}
			});

			if(instanceToAdd != null)
			{
				listAdapter.add(instanceToAdd);
			}
		}
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
