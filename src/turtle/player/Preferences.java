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


import turtle.player.dirchooser.DirChooserConstants;

import java.io.File;

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
	
	public File GetMediaPath()
	{
		return getExistingParentFolderFile(mediaPath);
	}

    private File getExistingParentFolderFile(String path){
        if(path == null || !path.startsWith(DirChooserConstants.PATH_SEPERATOR)){
            return new File(DirChooserConstants.PATH_SEPERATOR);
        }

        //Go up untill the string represents a valid directory
        while(!new File(path).isDirectory()){
            String pathWihoutTrailngSep = path.endsWith(DirChooserConstants.PATH_SEPERATOR) ?
                    path.substring(0, path.length() - DirChooserConstants.PATH_SEPERATOR.length()) :
                    path;

            path = pathWihoutTrailngSep.substring(0, path.lastIndexOf(DirChooserConstants.PATH_SEPERATOR));
        }
        return new File(path);
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
