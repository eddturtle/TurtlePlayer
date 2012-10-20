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


package turtle.player.persistance.turtle;

import org.junit.Test;
import turtle.player.persistance.turtle.FsReader;

import static junit.framework.Assert.assertEquals;

public class FsReaderTest
{
    @Test
    public void testParseTrackNumber() throws Exception
    {
        assertEquals(1, FsReader.parseTrackNumber("1"));
        assertEquals(1, FsReader.parseTrackNumber("01"));
        assertEquals(1, FsReader.parseTrackNumber("1/2"));
        assertEquals(1, FsReader.parseTrackNumber("1,2"));
        assertEquals(1, FsReader.parseTrackNumber("1;2"));
        assertEquals(0, FsReader.parseTrackNumber("00"));
        assertEquals(0, FsReader.parseTrackNumber("0"));
        assertEquals(0, FsReader.parseTrackNumber("0/0"));
        assertEquals(0, FsReader.parseTrackNumber(""));
        assertEquals(0, FsReader.parseTrackNumber("asdad"));
        assertEquals(10, FsReader.parseTrackNumber("10"));
        assertEquals(10, FsReader.parseTrackNumber("010"));
    }
}
