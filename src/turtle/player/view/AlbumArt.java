package turtle.player.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import turtle.player.R;
import turtle.player.model.Track;
import turtle.player.presentation.InstanceFormatter;

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

public class AlbumArt
{
	public enum Type
	{
		RIGHT(-1, R.id.albumArtRight), // FIXME: should be 1, if your know why its inverted, change it
		LEFT(1, R.id.albumArtLeft), // FIXME: should be -1, your know why its inverted, change it
		CENTER(0, R.id.albumArt);

		private final double horizontalShift;
		private final int rId;

		Type(double horizontalShift, int rId)
		{
			this.horizontalShift = horizontalShift;
			this.rId = rId;
		}

		public double getHorizontalShift()
		{
			return horizontalShift;
		}

		public int getRId()
		{
			return rId;
		}
	}

	private final View albumArtView;

	private final Type type;

	private final ImageView albumArt;
	private final TextView title;
	private final TextView artist;

	public AlbumArt(Activity activity, Type type)
	{

		albumArtView = activity.findViewById(type.getRId());
		//activity.findViewById(type.getRId())
		this.type = type;

		albumArt = (ImageView) albumArtView.findViewById(R.id.picture);
		title = (TextView) albumArtView.findViewById(R.id.trackTitle);
		artist = (TextView) albumArtView.findViewById(R.id.trackArtist);

		albumArtView.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener()
		{
			public void onGlobalFocusChanged(View oldFocus,
														View newFocus)
			{
				setInitialPositions();
			}
		});
	}

	public View getAlbumArtView()
	{
		return albumArtView;
	}

	public void setTrack(Track track)
	{
		if(track != null){

			title.setText(track.accept(InstanceFormatter.SHORT_WITH_NUMBER));
			artist.setText(track.GetAlbum().accept(InstanceFormatter.SHORT));

			if (track.albumArt() != null)
			{
				Bitmap bmp = BitmapFactory.decodeFile(track.albumArt());
				albumArt.setImageBitmap(bmp);
			} else
			{
				albumArt.setImageDrawable(albumArtView.getResources().getDrawable(R.drawable.blank_album_art));
			}

		}
		else
		{
			title.setText("");
			artist.setText("");
			albumArt.setImageDrawable(albumArtView.getResources().getDrawable(R.drawable.blank_album_art));
		}
	}

	public void setInitialPositions()
	{
		albumArtView.scrollTo((int)(type.getHorizontalShift() * albumArtView.getWidth()), 0);
	}
}
