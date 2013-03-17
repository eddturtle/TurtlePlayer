package turtle.player.presentation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import turtle.player.model.Track;
import turtle.player.persistance.turtle.FsReader;
import turtle.player.util.Shorty;

import java.io.IOException;
import java.util.List;

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

public class AlbumArtResolver extends AsyncTask<Track, Void, Bitmap>
{
	private static LookupStrategy[] lookupStrategies = new LookupStrategy[]{
			  new IdTagLookupStrategy(),
			  new FsLookupStrategy()
	};

	@Override
	protected Bitmap doInBackground(Track... params)
	{
		for(LookupStrategy lookupStrategy : lookupStrategies){
			Bitmap albumArt = lookupStrategy.lookup(params[0]);
			if(albumArt != null) return albumArt;
		}
		return null;
	}

	private interface LookupStrategy
	{
		Bitmap lookup(Track track);
	}

	private static class FsLookupStrategy implements LookupStrategy
	{

		public Bitmap lookup(Track track)
		{
			String albumArtPath = FsReader.getAlbumArt(track.GetRootSrc());
			if(!Shorty.isVoid(albumArtPath))
			{
				return BitmapFactory.decodeFile(albumArtPath);
			}
			return null;
		}
	}

	private static class IdTagLookupStrategy implements LookupStrategy
	{
		public Bitmap lookup(Track track)
		{
			try
			{
				Mp3File mp3file = new Mp3File(track.GetSrc(), false);
				if(mp3file.hasId3v2Tag() && mp3file.getId3v2Tag().getAlbumImage() != null)
				{
					byte[] albumImage = mp3file.getId3v2Tag().getAlbumImage();
					return BitmapFactory.decodeByteArray(albumImage, 0, albumImage.length);
				}
			}
			catch (IOException e)
			{
				return null;
			}
			catch (UnsupportedTagException e)
			{
				return null;
			}
			catch (InvalidDataException e)
			{
				return null;
			}
			return null;
		}
	}
}
