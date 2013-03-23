package turtle.player.presentation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import turtle.player.model.AlbumArtLocation;
import turtle.player.model.Track;
import turtle.player.persistance.framework.db.Database;
import turtle.player.persistance.framework.executor.OperationExecutor;
import turtle.player.persistance.framework.filter.FieldFilter;
import turtle.player.persistance.framework.filter.Operator;
import turtle.player.persistance.framework.paging.Paging;
import turtle.player.persistance.framework.sort.RandomOrder;
import turtle.player.persistance.source.sql.First;
import turtle.player.persistance.source.sqlite.QuerySqlite;
import turtle.player.persistance.turtle.FsReader;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.mapping.AlbumArtLocationCreator;
import turtle.player.persistance.turtle.mapping.TrackCreator;
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

public abstract class AlbumArtResolver extends AsyncTask<Track, Void, Bitmap>
{
	private final TurtleDatabase db;

	protected AlbumArtResolver(TurtleDatabase db)
	{
		this.db = db;
	}


	@Override
	protected Bitmap doInBackground(Track... params)
	{

		for(LookupStrategy lookupStrategy : new LookupStrategy[]{
				  new CachedFsLookupStrategy(),
				  new IdTagLookupStrategy(),
				  new FsLookupStrategy()
		})
		{
			Bitmap albumArt = lookupStrategy.lookup(params[0]);
			if(albumArt != null) return albumArt;
		}
		return null;
	}

	private interface LookupStrategy
	{
		Bitmap lookup(Track track);
	}

	private class CachedFsLookupStrategy implements LookupStrategy
	{

		public Bitmap lookup(Track track)
		{
			AlbumArtLocation albumArtLocation = OperationExecutor.execute(
					  db,
					  new QuerySqlite<AlbumArtLocation>(new FieldFilter<AlbumArtLocation, String>(Tables.ALBUM_ART_LOCATIONS.PATH, Operator.EQ, track.GetRootSrc()),
								 new First<AlbumArtLocation>(Tables.ALBUM_ART_LOCATIONS, new AlbumArtLocationCreator())));

			if(albumArtLocation != null)
			{
				return BitmapFactory.decodeFile(albumArtLocation.getAlbumArtpath());
			}
			return null;
		}
	}


	private class FsLookupStrategy implements LookupStrategy
	{

		public Bitmap lookup(Track track)
		{
			String albumArtPath = FsReader.getAlbumArt(track.GetRootSrc(), db);
			if(!Shorty.isVoid(albumArtPath))
			{
				return BitmapFactory.decodeFile(albumArtPath);
			}
			return null;
		}
	}

	private class IdTagLookupStrategy implements LookupStrategy
	{
		public Bitmap lookup(Track track)
		{
			try
			{
				synchronized (Mp3File.class)
				{
					Mp3File mp3file = new Mp3File(track.GetSrc(), false);
					if(mp3file.hasId3v2Tag() && mp3file.getId3v2Tag().getAlbumImage() != null)
					{
						byte[] albumImage = mp3file.getId3v2Tag().getAlbumImage();
						return BitmapFactory.decodeByteArray(albumImage, 0, albumImage.length);
					}
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
