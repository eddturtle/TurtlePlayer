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

import turtle.player.model.*;
import turtle.player.util.Shorty;

class ListInstanceFormatter extends InstanceFormatter
{
    public String visit(Track track)
    {
        int number = track.GetNumber();
        String title = track.GetTitle();
        String artist = track.GetArtist().getName();

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

	public String visit(TrackDigest track)
	{
		return track.getName();
	}

    public String visit(Album album)
    {
        return album.getName();
    }

	public String visit(Genre genre)
	{
		return genre.getName();
	}

	public String visit(Artist artist)
    {
        return artist.getName();
    }
}
