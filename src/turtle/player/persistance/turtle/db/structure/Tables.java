package turtle.player.persistance.turtle.db.structure;

import turtle.player.persistance.source.relational.Field;
import turtle.player.persistance.source.relational.Table;

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

	public static final class Tracks extends Table{
		public Tracks()
		{
			super("Tracks");
		}

		public final Field ID = new Field("id");
		public final Field TITLE = new Field("title");
		public final Field NUMBER = new Field("number");
		public final Field ARTIST = new Field("artist");
		public final Field ALBUM = new Field("album");
		public final Field LENGTH = new Field("length");
		public final Field SRC = new Field("src");
		public final Field ROOTSRC = new Field("rootSrc");
		public final Field ALBUMART = new Field("hasAlbumArt");
	}
}
