package turtle.player.persistance.turtle.db.structure;

import turtle.player.model.AlbumArtLocation;
import turtle.player.model.FSobject;
import turtle.player.model.Track;
import turtle.player.persistance.source.relational.Field;
import turtle.player.persistance.source.relational.FieldPersistable;
import turtle.player.persistance.source.relational.Table;
import turtle.player.persistance.source.relational.fieldtype.FieldPersistableAsInteger;
import turtle.player.persistance.source.relational.fieldtype.FieldPersistableAsString;
import turtle.player.util.TurtleUtil;

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

public class Tables
{
	public final static Tracks TRACKS = new Tracks();
	public final static AlbumArtLocations ALBUM_ART_LOCATIONS = new AlbumArtLocations();
	public final static Dirs DIRS = new Dirs();

	public static abstract class FsObjects<I extends FSobject> extends Table<I>
	{
		public FsObjects(String name)
		{
			super(name);
		}

		public final FieldPersistable<I, String> NAME = new FieldPersistableAsString<I>("name")
		{
			public String get(I fsObject)
			{
				return fsObject.getName();
			}
		};

		public final FieldPersistable<I, String> PATH = new FieldPersistableAsString<I>("path")
		{
			public String get(FSobject fsObject)
			{
				return fsObject.getPath();
			}
		};
	}

	public static final class Tracks extends FsObjects<Track>
	{

		public final FieldPersistable<Track, String> TITLE = new FieldPersistableAsString<Track>("title")
		{
			public String get(Track instance)
			{
				return instance.GetTitle();
			}
		};

		public final FieldPersistable<Track, Integer> NUMBER = new FieldPersistableAsInteger<Track>("number")
		{
			public Integer get(Track instance)
			{
				return instance.GetNumber();
			}
		};

		public final FieldPersistable<Track, String> ARTIST = new FieldPersistableAsString<Track>("artist")
		{

			public String get(Track instance)
			{
				return instance.GetArtist().getId();
			}

			@Override
			public String getAsDisplayableString(Track instance)
			{
				return instance.GetArtist().getName();
			}
		};

		public final FieldPersistable<Track, String> ALBUM = new FieldPersistableAsString<Track>("album")
		{
			public String get(Track instance)
			{
				return instance.GetAlbum().getId();
			}

			@Override
			public String getAsDisplayableString(Track instance)
			{
				return instance.GetAlbum().getName();
			}
		};

		public final FieldPersistable<Track, String> GENRE = new FieldPersistableAsString<Track>("genre")
		{
			public String get(Track instance)
			{
				return instance.GetGenre().getId();
			}

			@Override
			public String getAsDisplayableString(Track instance)
			{
				return TurtleUtil.translateGenreId(get(instance));
			}
		};

		public Tracks()
		{
			super("Tracks");
		}

	}

	public static final class AlbumArtLocations extends Table<AlbumArtLocation>
	{

		public final FieldPersistable<AlbumArtLocation, String> PATH = new FieldPersistableAsString<AlbumArtLocation>("path")
		{
			public String get(AlbumArtLocation albumArtLocation)
			{
				return albumArtLocation.getPath();
			}
		};

		public final FieldPersistable<AlbumArtLocation, String> ALBUM_ART_PATH = new FieldPersistableAsString<AlbumArtLocation>("albumArtpath")
		{
			public String get(AlbumArtLocation albumArtLocation)
			{
				return albumArtLocation.getAlbumArtpath();
			}
		};

		public AlbumArtLocations()
		{
			super("AlbumArt");
		}
	}

	public static final class Dirs extends FsObjects<FSobject>
	{
		public Dirs()
		{
			super("Dirs");
		}
	}
}
