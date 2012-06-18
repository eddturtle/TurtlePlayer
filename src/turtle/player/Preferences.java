/*
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
 * Created by Edd Turtle (www.eddturtle.co.uk)
 * More Information @ www.turtle-player.co.uk
 * 
 */

// Package
package turtle.player;
 

public class Preferences {

	
	// ========================================= //
	// 	Attributes
	// ========================================= //
	
	private String mediaPath;
	private boolean repeat;
	private boolean shuffle;
	
	// Not in ClassDiagram
	
	private static final String TAG = "TurtlePlayer";
	
	
	// ========================================= //
	// 	Constructor & Destructor
	// ========================================= //
	
	public Preferences(String nMediaPath, boolean nRepeat, boolean nShuffle)
	{
		mediaPath = nMediaPath;
		repeat = nRepeat;
		shuffle = nShuffle;
	}
	
	
	// ========================================= //
	// 	MediaPath
	// ========================================= //
	
	public String GetMediaPath()
	{
		return mediaPath;
	}
	
	public void SetMediaPath(String nMediaPath)
	{
		mediaPath = nMediaPath;
	}

	
	// ========================================= //
	// 	Repeat
	// ========================================= //
	
	public boolean GetRepeat()
	{
		return repeat;
	}
	
	public void SetRepeat(boolean nRepeat)
	{
		repeat = nRepeat;
	}

	
	// ========================================= //
	// 	Shuffle
	// ========================================= //
	
	public boolean GetShuffle()
	{
		return shuffle;
	}
	
	public void SetShuffle(boolean nShuffle)
	{
		shuffle = nShuffle;
	}
	
	
	// ========================================= //
	// 	Tag
	// ========================================= //
	
	public String GetTag()
	{
		return TAG;
	}
	
}
