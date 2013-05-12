package turtle.player.persistance.turtle.db.structure;

import turtle.player.model.*;
import turtle.player.persistance.source.relational.*;
import turtle.player.persistance.source.relational.fieldtype.FieldPersistableAsString;
import turtle.player.util.Shorty;

import java.util.Set;

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

public class Views
{
	public final static Albums ALBUMS = new Albums();
	public final static Artists ARTISTS = new Artists();
	public final static Genres GENRES = new Genres();
	public final static Songs SONGS = new Songs();

	//Marker interface
	public interface AlbumsReadable extends View{
		public static final FieldPersistable<Album, String> NAME = new FieldPersistableAsString<Album>(Tables.Tracks.ALBUM);
	}

	//Marker interface
	public interface GenresReadable extends View{
		public static final FieldPersistable<Genre, String> NAME = new FieldPersistableAsString<Genre>(Tables.Tracks.GENRE);
	}

	//Marker interface
	public interface ArtistsReadable extends View{
		public static final FieldPersistable<Artist, String> NAME = new FieldPersistableAsString<Artist>(Tables.Tracks.ARTIST);
	}

	//Marker interface
	public interface SongsReadable extends View{
		public static final FieldPersistable<Song, String> NAME = new FieldPersistableAsString<Song>(Tables.Tracks.TITLE);
	}

	public static final class Albums implements AlbumsReadable
	{
		public Set<Tables.Tracks> getTables()
		{
			return Shorty.oneElementSet(Tables.TRACKS);
		}
	}

	public static final class Artists implements ArtistsReadable
	{
		public Set<Tables.Tracks> getTables()
		{
			return Shorty.oneElementSet(Tables.TRACKS);
		}
	}

	public static final class Genres  implements GenresReadable
	{
		public Set<Tables.Tracks> getTables()
		{
			return Shorty.oneElementSet(Tables.TRACKS);
		}
	}

	public static final class Songs  implements SongsReadable
	{
		public Set<Tables.Tracks> getTables()
		{
			return Shorty.oneElementSet(Tables.TRACKS);
		}
	}

}
