package turtle.player.persistance.turtle.db.structure;

import turtle.player.model.*;
import turtle.player.persistance.source.relational.FieldPersistable;
import turtle.player.persistance.source.relational.Table;
import turtle.player.persistance.source.relational.View;
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

public class Views
{
	public final static Albums ALBUMS = new Albums();
	public final static Artists ARTISTS = new Artists();
	public final static Genres GENRES = new Genres();
	public final static TrackDigests TRACK_DIGEST = new TrackDigests();

	public static final class Albums extends View<Album>
	{
		public final FieldPersistable<Album, String> NAME = new FieldPersistableAsString<Album>(Tables.TRACKS.ALBUM)
		{
			public String get(Album instance)
			{
				return instance.getId();
			}
		};

		@Override
		public Table<?>[] getTables()
		{
			return new Table<?>[]{Tables.TRACKS};
		}
	}

	public static final class Artists extends View<Artist>
	{
		public final FieldPersistable<Artist, String> NAME = new FieldPersistableAsString<Artist>(Tables.TRACKS.ARTIST)
		{
			public String get(Artist instance)
			{
				return instance.getId();
			}
		};

		@Override
		public Table<?>[] getTables()
		{
			return new Table<?>[]{Tables.TRACKS};
		}
	}

	public static final class Genres extends View<Genre>
	{
		public final FieldPersistable<Genre, String> NAME = new FieldPersistableAsString<Genre>(Tables.TRACKS.GENRE)
		{
			public String get(Genre instance)
			{
				return instance.getId();
			}
		};

		@Override
		public Table<?>[] getTables()
		{
			return new Table<?>[]{Tables.TRACKS};
		}
	}

	public static final class TrackDigests extends View<TrackDigest>
	{
		public final FieldPersistable<TrackDigest, String> NAME = new FieldPersistableAsString<TrackDigest>(Tables.TRACKS.TITLE)
		{
			public String get(TrackDigest instance)
			{
				return instance.getName();
			}
		};

		@Override
		public Table<?>[] getTables()
		{
			return new Table<?>[]{Tables.TRACKS};
		}
	}

}
