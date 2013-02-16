package turtle.player.persistance.turtle.db.structure;

import turtle.player.model.Artist;
import turtle.player.model.Track;
import turtle.player.persistance.source.relational.Field;
import turtle.player.persistance.source.relational.FieldPersistable;
import turtle.player.persistance.source.relational.Table;
import turtle.player.persistance.source.relational.fieldtype.FieldPersistableAsDouble;
import turtle.player.persistance.source.relational.fieldtype.FieldPersistableAsInteger;
import turtle.player.persistance.source.relational.fieldtype.FieldPersistableAsString;
import turtle.player.util.TurtleUtil;

import java.util.ArrayList;
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

public class Tables
{
	public final static Tracks TRACKS = new Tracks();

	public static final class Tracks extends Table<Track>
	{

		public final Field ID = new Field("id");

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
				return instance.GetArtist().getName();
			}
		};

		public final FieldPersistable<Track, String> ALBUM = new FieldPersistableAsString<Track>("album")
		{
			public String get(Track instance)
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

		public final FieldPersistable<Track, Double> LENGTH = new FieldPersistableAsDouble<Track>("length")
		{
			public Double get(Track instance)
			{
				return instance.GetLength();
			}
		};

		public final FieldPersistable<Track, String> SRC = new FieldPersistableAsString<Track>("src")
		{
			public String get(Track instance)
			{
				return instance.GetSrc();
			}
		};

		public final FieldPersistable<Track, String> ROOTSRC = new FieldPersistableAsString<Track>("rootSrc")
		{

			public String get(Track instance)
			{
				return instance.GetRootSrc();
			}
		};

		public final FieldPersistable<Track, String> ALBUMART = new FieldPersistableAsString<Track>("hasAlbumArt")
		{
			public String get(Track instance)
			{
				return instance.albumArt();
			}
		};

		public Tracks()
		{
			super("Tracks");
		}

	}
}
