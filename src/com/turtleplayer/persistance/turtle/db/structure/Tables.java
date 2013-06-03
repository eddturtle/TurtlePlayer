package com.turtleplayer.persistance.turtle.db.structure;

import com.turtleplayer.model.*;
import com.turtleplayer.persistance.framework.creator.Creator;
import com.turtleplayer.persistance.source.relational.Field;
import com.turtleplayer.persistance.source.relational.FieldPersistable;
import com.turtleplayer.persistance.source.relational.Table;
import com.turtleplayer.persistance.source.relational.View;
import com.turtleplayer.persistance.source.relational.fieldtype.FieldPersistableAsInteger;
import com.turtleplayer.persistance.source.relational.fieldtype.FieldPersistableAsString;
import com.turtleplayer.util.Shorty;

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

	//Marker interface
	public interface AlbumsReadable extends View
	{
		FieldPersistable<Album, String> ALBUM = new FieldPersistableAsString<Album>("album", new Creator<String, Album>(){

			public String create(Album album)
			{
				return album.getAlbumId();
			}
		});
	}

	//Marker interface
	public interface GenresReadable extends View{
		FieldPersistable<Genre, String> GENRE = new FieldPersistableAsString<Genre>("genre", new Creator<String, Genre>(){

			public String create(Genre genre)
			{
				return genre.getGenreId();
			}
		});
	}

	//Marker interface
	public interface ArtistsReadable extends View{
		FieldPersistable<Artist, String> ARTIST = new FieldPersistableAsString<Artist>("artist", new Creator<String, Artist>(){

			public String create(Artist artist)
			{
				return artist.getArtistId();
			}
		});
	}

	//Marker interface
	public interface SongsReadable extends View{
		FieldPersistable<Song, String> TITLE = new FieldPersistableAsString<Song>("title", new Creator<String, Song>(){
			public String create(Song song)
			{
				return song.getSongId();
			}
		});
	}

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

		public Field[] getFields()
		{
			return new Field[]{NAME, PATH};
		}
	}

	public static final class Tracks extends FsObjects implements
			  AlbumsReadable,
			  ArtistsReadable,
			  GenresReadable,
			  SongsReadable
	{
		public static final FieldPersistable<Track, Integer> NUMBER = new FieldPersistableAsInteger<Track>("number", new Creator<Integer, Track>(){

			public Integer create(Track track)
			{
				return track.GetNumber();
			}
		});

		public Tracks()
		{
			super("Tracks");
		}

		@Override
		public Field[] getFields()
		{
			return Shorty.concatWith(super.getFields(), NUMBER, ALBUM, ARTIST, GENRE, TITLE);
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

		public Field[] getFields()
		{
			return new Field[]{PATH, ALBUM_ART_PATH};
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
