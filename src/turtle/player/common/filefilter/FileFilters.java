/**
 *
 * TURTLE PLAYER
 *
 * Licensed under MIT & GPL
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * More Information @ www.turtle-player.co.uk
 *
 * @author Simon Honegger (Hoene84)
 */

package turtle.player.common.filefilter;

import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

public interface FileFilters
{
	final static FilenameFilter PLAYABLE_FILES_FILTER = new NameFilter(".*.mp3", ".*.ogg");

	final static List<? extends FilenameFilter> folderArtFilters = Arrays.asList(
			  new NameFilter("folder.jpg"),
			  new NameFilter(".*front.*.jpg", ".*front.*.jpeg", ".*front.*.gif"),
			  new NameFilter(".*cover.*.jpg", ".*cover.*.jpeg", ".*cover.*.gif"),
			  new NameFilter(".*.jpg", ".*.jpeg", ".*.gif")
	);
}