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
import turtle.player.TurtlePlayer;
import turtle.player.model.*;
import turtle.player.playlist.Playlist;
import turtle.player.playlist.filter.ChildsFilter;
import turtle.player.playlist.filter.PlaylistFilter;
import turtle.player.presentation.InstanceFormatter;
import turtle.player.util.GenericInstanceComperator;
import turtle.player.util.InstanceAdapter;

import java.util.Set;

public class FileChooser extends Playlist.PlaylistObserverAdapter
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

	public enum Type
	{
		Album,
		Artist,
		Track,
	}

	private Mode currMode;
	private Type currType;
	private TurtlePlayer tp;
	private ListActivity listActivity;

	private PlaylistFilter filter = null;

	public FileChooser(Mode currMode,
							 TurtlePlayer tp,
							 ListActivity listActivity)
	{
		this.currMode = currMode;
		this.tp = tp;
		this.listActivity = listActivity;

		change(currMode);

		init();
	}

	private void init()
	{

		tp.playlist.addObserver(this);
		for (final Mode currMode : Mode.values())
		{
			getButton(currMode).setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					change(currMode);
				}
			});
		}
	}

	/**
	 * @param instance
	 * @return null if no track was selected, track if trak was selected
	 */
	public Track choose(Instance instance)
	{
		Track selectedTrack = instance.accept(new InstanceVisitor<Track>()
		{
			@Override
			public Track visit(Track track)
			{
				return track;
			}

			@Override
			public Track visit(Album album)
			{
				currType = Type.Track;
				filter = new ChildsFilter(album, tp.playlist);
				return null;
			}

			@Override
			public Track visit(Artist artist)
			{
				currType = Type.Track;
				filter = new ChildsFilter(artist, tp.playlist);
				return null;
			}
		});
		update();
		return selectedTrack;
	}

	public void change(Mode mode)
	{
		currMode = mode;
		for (final Mode aMode : Mode.values())
		{
			final ImageView button = getButton(aMode);
			button.post(new Runnable()
			{
				@Override
				public void run()
				{
					button.setImageResource(aMode.equals(currMode) ? aMode.drawableActive : aMode.drawable);
				}
			});
		}

		switch (currMode)
		{
			case Album:
				currType = Type.Album;
				break;
			case Artist:
				currType = Type.Artist;
				break;
			case Track:
				currType = Type.Track;
				break;
			default:
				throw new RuntimeException(currMode.name() + " not expexted here");
		}
		filter = null;
		update();
	}

	public void update()
	{

		final Set<? extends Instance> instances;

		switch (currType)
		{
			case Album:
				instances = tp.playlist.getAlbums(filter);
				break;
			case Artist:
				instances = tp.playlist.getArtists(filter);
				break;
			case Track:
				instances = tp.playlist.getTracks(filter);
				break;
			default:
				throw new RuntimeException(currType.name() + " not expexted here");
		}

		listActivity.getListView().post(new Runnable()
		{
			@Override
			public void run()
			{
				listActivity.setListAdapter(
						  new InstanceAdapter(
									 tp.getApplicationContext(),
									 instances,
									 InstanceFormatter.LIST,
									 new GenericInstanceComperator()
						  )
				);
			}
		});
	}

	@Override
	public void endUpdatePlaylist()
	{
		update();
	}

	private ImageView getButton(Mode mode)
	{
		return (ImageView) listActivity.findViewById(mode.buttonId);
	}
}
