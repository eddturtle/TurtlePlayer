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
							  final TurtlePlayer tp,
							  final PlayOrderStrategy playOrderStrategy)
	{
		albumArtViewGroup = activity.findViewById(R.id.relativeLayout_albumArt);

		albumArt = new AlbumArt(albumArtViewGroup, AlbumArt.Type.CENTER, tp.db);
		albumArtLeft = new AlbumArt(albumArtViewGroup, AlbumArt.Type.LEFT, tp.db);
		albumArtRight = new AlbumArt(albumArtViewGroup, AlbumArt.Type.RIGHT, tp.db);

		tp.player.addObserver(new Player.PlayerObserver()
		{
			public void trackChanged(Track track, int lengthInMillis)
			{
				TrackBundle trackBundle = tp.playlist.enrich(playOrderStrategy, track);
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
				tp.player.play(tp.playlist.getNext(playOrderStrategy, tp.player.getCurrTrack()));
			}

			@Override
			protected void previousGestureRecognized()
			{
				tp.player.play(tp.playlist.getPrevious(playOrderStrategy, tp.player.getCurrTrack()));
			}


			@Override
			protected void filterSelected(FieldPersistable<Track, ?> field)
			{
				Filter previousFilter = settedFilters.get(field);
				String msg = field.getName();
				if(previousFilter == null)
				{
					settedFilters.put(field, tp.playlist.addFilter(field, tp.player.getCurrTrack()));
					msg += " " + activity.getString(R.string.added);
				}
				else
				{
					tp.playlist.removeFilter(previousFilter);
					settedFilters.remove(field);
					msg += " " + activity.getString(R.string.removed);
				}

				Toast.makeText(activity.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}
		};

		albumArt.getAlbumArtView().setOnTouchListener(touchHandler);
		tp.playlist.addObserver(touchHandler);
		tp.player.addObserver(touchHandler);
	}
}
