package turtle.player.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
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
import turtle.player.preferences.Preferences;
import turtle.player.presentation.AlbumArtResolver;

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

	//AlbumArt
	private final AlbumArt albumArt;
	private final AlbumArt albumArtLeft;
	private final AlbumArt albumArtRight;

	private AsyncTask<Track, Void, TrackBundle> actualAsyncTask = null;

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
			public void trackChanged(final Track track, int lengthInMillis)
			{
				albumArt.setTrackDigest(track);
				albumArtLeft.setTrackDigest(null);
				albumArtRight.setTrackDigest(null);

				actualAsyncTask = new AsyncTask<Track, Void, TrackBundle>(){

					@Override
					protected TrackBundle doInBackground(Track... params)
					{
						if(actualAsyncTask == this)
						{
							return tp.playlist.enrich(playOrderStrategy, params[0]);
						}
						return null;
					}

					@Override
					protected void onPostExecute(TrackBundle trackBundle)
					{
						if(actualAsyncTask == this && trackBundle != null)
						{
							albumArt.setTrack(trackBundle.getTrack());
							albumArtRight.setTrack(trackBundle.getTrackAfter());
							albumArtLeft.setTrack(trackBundle.getTrackBefore());
						}
					}
				}.execute(track);

			}

			public void started()
			{
				//ignore
			}

			public void stopped()
			{
				//ignore
			}

			public String getId()
			{
				return "AlbumArtUpdater";
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
				boolean added = tp.playlist.toggleFilter(field, tp.player.getCurrTrack());

				String msg = field.getName();
				msg += " " + (added ? activity.getString(R.string.added) : activity.getString(R.string.removed));
				Toast.makeText(activity.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}

			public String getId()
			{
				return "SwipeAndInstantFilterSelectionDetector";
			}
		};

		albumArt.getAlbumArtView().setOnTouchListener(touchHandler);
		tp.playlist.addObserver(touchHandler);
		tp.player.addObserver(touchHandler);
	}
}
