package turtle.player.persistance.turtle.db.structure;

import turtle.player.model.*;
import turtle.player.persistance.framework.creator.Creator;
import turtle.player.persistance.source.relational.FieldPersistable;
import turtle.player.persistance.source.relational.Table;
import turtle.player.persistance.source.relational.fieldtype.FieldPersistableAsInteger;
import turtle.player.persistance.source.relational.fieldtype.FieldPersistableAsString;

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

	public static abstract class FsObjects extends Table
	{
		public FsObjects(String name)
		{
			super(name);
		}

		public static final FieldPersistable<FSobject, String> NAME = new FieldPersistableAsString<FSobject>("name", new Creator<String, FSobject>(){
			public String create(FSobject fSobject)
			{
				return fSobject.getName();
			}
		});
		public static final FieldPersistable<FSobject, String> PATH = new FieldPersistableAsString<FSobject>("path", new Creator<String, FSobject>(){
			public String create(FSobject fSobject)
			{
				return fSobject.getPath();
			}
		});
	}

	public static final class Tracks extends FsObjects implements
			  Views.AlbumsReadable,
			  Views.ArtistsReadable,
			  Views.GenresReadable,
			  Views.SongsReadable
	{
		public static final FieldPersistable<Song, String> TITLE = new FieldPersistableAsString<Song>("title", new Creator<String, Song>(){
			public String create(Song song)
			{
				return song.getSongId();
			}
		});
		public static final FieldPersistable<Track, Integer> NUMBER = new FieldPersistableAsInteger<Track>("number", new Creator<Integer, Track>(){

			public Integer create(Track track)
			{
				return track.GetNumber();
			}
		});
		public static final FieldPersistable<Artist, String> ARTIST = new FieldPersistableAsString<Artist>("artist", new Creator<String, Artist>(){

			public String create(Artist artist)
			{
				return artist.getArtistId();
			}
		});
		public static final FieldPersistable<Album, String> ALBUM = new FieldPersistableAsString<Album>("album", new Creator<String, Album>(){

			public String create(Album album)
			{
				return album.getAlbumId();
			}
		});
		public static final FieldPersistable<Genre, String> GENRE = new FieldPersistableAsString<Genre>("genre", new Creator<String, Genre>(){

			public String create(Genre genre)
			{
				return genre.getGenreId();
			}
		});

		public Tracks()
		{
			super("Tracks");
		}

	}

	public static final class AlbumArtLocations extends Table
	{

		public static final FieldPersistable<AlbumArtLocation, String> PATH = new FieldPersistableAsString<AlbumArtLocation>("path", new Creator<String, AlbumArtLocation>(){
			public String create(AlbumArtLocation albumArtLocation)
			{
				return albumArtLocation.getPath();
			}
		});
		public static final FieldPersistable<AlbumArtLocation, String> ALBUM_ART_PATH = new FieldPersistableAsString<AlbumArtLocation>("albumArtpath", new Creator<String, AlbumArtLocation>(){
			public String create(AlbumArtLocation albumArtLocation)
			{
				return albumArtLocation.getAlbumArtpath();
			}
		});

		public AlbumArtLocations()
		{
			super("AlbumArt");
		}
	}

	public static final class Dirs extends FsObjects
	{
		public Dirs()
		{
			super("Dirs");
		}
	}
}
