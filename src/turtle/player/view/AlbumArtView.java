package turtle.player.view;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;
import turtle.player.R;
import turtle.player.TurtlePlayer;
import turtle.player.controller.TouchHandler;
import turtle.player.model.Track;
import turtle.player.model.TrackBundle;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.source.relational.FieldPersistable;
import turtle.player.player.Player;
import turtle.player.playlist.Playlist;
import turtle.player.playlist.playorder.PlayOrderSorted;
import turtle.player.playlist.playorder.PlayOrderStrategy;

import java.util.HashMap;
import java.util.Map;

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

public class AlbumArtView
{

	private final View albumArtViewGroup;

	private final Map<FieldPersistable<Track, ?>, Filter> settedFilters =
			  new HashMap<FieldPersistable<Track, ?>, Filter>();

	//AlbumArt
	private final AlbumArt albumArt;
	private final AlbumArt albumArtLeft;
	private final AlbumArt albumArtRight;

	public AlbumArtView(final Activity activity,
							  final Player player,
							  final PlayOrderStrategy playOrderStrategy,
							  final Playlist playlist)
	{
		albumArtViewGroup = activity.findViewById(R.id.relativeLayout_albumArt);

		albumArt = new AlbumArt(albumArtViewGroup, AlbumArt.Type.CENTER);
		albumArtLeft = new AlbumArt(albumArtViewGroup, AlbumArt.Type.LEFT);
		albumArtRight = new AlbumArt(albumArtViewGroup, AlbumArt.Type.RIGHT);

		player.addObserver(new Player.PlayerObserver()
		{
			public void trackChanged(Track track)
			{
				TrackBundle trackBundle = playlist.enrich(playOrderStrategy, track);
				albumArt.setTrack(trackBundle.getTrack());
				albumArtRight.setTrack(trackBundle.getTrackAfter());
				albumArtLeft.setTrack(trackBundle.getTrackBefore());
			}

			public void started()
			{
				//ignore
			}

			public void stopped()
			{
				//ignore
			}
		});


		TouchHandler touchHandler = new TouchHandler(
				  activity,
				  albumArt.getAlbumArtView(),
				  albumArtLeft.getAlbumArtView(),
				  albumArtRight.getAlbumArtView()
		){
			@Override
			protected void nextGestureRecognized()
			{
				player.play(playlist.getNext(playOrderStrategy, player.getCurrTrack()));
			}

			@Override
			protected void previousGestureRecognized()
			{
				player.play(playlist.getPrevious(playOrderStrategy, player.getCurrTrack()));
			}


			@Override
			protected void filterSelected(FieldPersistable<Track, ?> field)
			{
				Filter previousFilter = settedFilters.get(field);
				String msg = field.getName();
				if(previousFilter == null)
				{
					settedFilters.put(field, playlist.addFilter(field, player.getCurrTrack()));
					msg += " " + activity.getString(R.string.added);
				}
				else
				{
					playlist.removeFilter(previousFilter);
					settedFilters.remove(field);
					msg += " " + activity.getString(R.string.removed);
				}

				Toast.makeText(activity.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}
		};

		albumArt.getAlbumArtView().setOnTouchListener(touchHandler);
		playlist.addObserver(touchHandler);
		player.addObserver(touchHandler);
	}
}
