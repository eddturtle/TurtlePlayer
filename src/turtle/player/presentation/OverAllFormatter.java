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

package turtle.player.presentation;

import turtle.player.model.Album;
import turtle.player.model.Artist;
import turtle.player.model.Track;
import turtle.player.util.Shorty;

public class OverAllFormatter extends InstanceFormatter
{
    private final static String DELIMITER = " - ";

    @Override
    public String visit(Track track)
    {
        String artist = track.GetArtist().getName();
        String album = track.GetAlbum().getName();
        int number = track.GetNumber();
        String title = track.GetTitle();

        if(!Shorty.isVoid(artist) && !Shorty.isVoid(title))
        {
            StringBuilder label = new StringBuilder();
            if(number > 0){
                label.append(number).append(DELIMITER);
            }
            label.append(artist).append(DELIMITER);
            label.append(title);
            return label.toString();
        }
        else
        {
            String filename = track.GetRootSrc();

            // "/path/path/file/"

            if(filename.lastIndexOf('/') == filename.length()-1)
            {
                filename = filename.substring(0, filename.length()-1);
            }

            // "/path/path/file"

            return filename.substring(filename.lastIndexOf('/'));

            // "file"
        }

    }

    @Override
    public String visit(Album album)
    {
        return album.getName();
    }

    @Override
    public String visit(Artist artist)
    {
        return artist.getName();
    }
}
