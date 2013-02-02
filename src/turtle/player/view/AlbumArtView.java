package turtle.player.view;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import turtle.player.R;
import turtle.player.controller.*;
import turtle.player.controller.TouchHandler;
import turtle.player.model.Track;
import turtle.player.model.TrackBundle;
import turtle.player.player.Player;
import turtle.player.playlist.Playlist;

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


	//AlbumArt
	private final AlbumArt albumArt;
	private final AlbumArt albumArtLeft;
	private final AlbumArt albumArtRight;

	public AlbumArtView(final Activity activity,
							  final Player player,
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
				TrackBundle trackBundle = playlist.enrich(track);
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



		albumArt.getAlbumArtView().setOnTouchListener(new TouchHandler(
				  activity,
				  albumArt.getAlbumArtView(),
				  albumArtLeft.getAlbumArtView(),
				  albumArtRight.getAlbumArtView()
		)

		{
			@Override
			protected void nextGestureRecognized()
			{
				player.play(playlist.getNext(player.getCurrTrack()).getTrack());
			}

			@Override
			protected void previousGestureRecognized()
			{
				player.play(playlist.getPrevious(player.getCurrTrack()).getTrack());
			}

			@Override
			protected void filterSelected(ChoosableFilter choosableFilter)
			{
				Toast.makeText(activity.getApplicationContext(), choosableFilter.name(), Toast.LENGTH_SHORT).show();
			}
		});
	}
}
