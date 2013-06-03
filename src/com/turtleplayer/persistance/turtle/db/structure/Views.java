package com.turtleplayer.persistance.turtle.db.structure;

import com.turtleplayer.persistance.source.relational.Field;
import com.turtleplayer.util.Shorty;

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

	public static final class Albums implements Tables.AlbumsReadable
	{
		public Set<Tables.Tracks> getTables()
		{
			return Shorty.oneElementSet(Tables.TRACKS);
		}

		public Field[] getFields()
		{
			return new Field[]{ALBUM};
		}
	}

	public static final class Artists implements Tables.ArtistsReadable
	{
		public Set<Tables.Tracks> getTables()
		{
			return Shorty.oneElementSet(Tables.TRACKS);
		}

		public Field[] getFields()
		{
			return new Field[]{ARTIST};
		}
	}

	public static final class Genres  implements Tables.GenresReadable
	{
		public Set<Tables.Tracks> getTables()
		{
			return Shorty.oneElementSet(Tables.TRACKS);
		}

		public Field[] getFields()
		{
			return new Field[]{GENRE};
		}
	}

	public static final class Songs  implements Tables.SongsReadable
	{
		public Set<Tables.Tracks> getTables()
		{
			return Shorty.oneElementSet(Tables.TRACKS);
		}

		public Field[] getFields()
		{
			return new Field[]{TITLE};
		}
	}

}
