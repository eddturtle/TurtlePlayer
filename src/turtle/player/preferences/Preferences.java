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
package turtle.player.preferences;


import android.content.Context;
import turtle.player.dirchooser.DirChooserConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Preferences
{

	// Not in ClassDiagram

	public static final String TAG = "TurtlePlayer";

	final Context context;

	public Preferences(Context context)
	{
		this.context = context;
	}

	public File GetMediaPath()
	{
		String mediaPath = SharedPreferencesAccess.getValue(context, Keys.MEDIA_DIR);
		return getExistingParentFolderFile(mediaPath);
	}

	public void SetMediaPath(String nMediaPath)
	{
		SharedPreferencesAccess.putValue(context, Keys.MEDIA_DIR, nMediaPath);
		notify(Keys.MEDIA_DIR);
	}

	public boolean GetRepeat()
	{
		return SharedPreferencesAccess.getValue(context, Keys.REPEAT);
	}

	public void SetRepeat(boolean nRepeat)
	{
		SharedPreferencesAccess.putValue(context, Keys.REPEAT, nRepeat);
		notify(Keys.REPEAT);
	}

	public boolean GetShuffle()
	{
		return SharedPreferencesAccess.getValue(context, Keys.SHUFFLE);
	}

	public void SetShuffle(boolean nShuffle)
	{
		SharedPreferencesAccess.putValue(context, Keys.SHUFFLE, nShuffle);
		notify(Keys.SHUFFLE);
	}

	public String GetTag()
	{
		return TAG;
	}

	public Context getContext()
	{
		return context;
	}

	private File getExistingParentFolderFile(String path)
	{
		if (path == null || !path.startsWith(DirChooserConstants.PATH_SEPERATOR))
		{
			return new File(DirChooserConstants.PATH_SEPERATOR);
		}

		//Go up untill the string represents a valid directory
		while (!new File(path).isDirectory())
		{
			String pathWihoutTrailngSep = path.endsWith(DirChooserConstants.PATH_SEPERATOR) ?
					  path.substring(0, path.length() - DirChooserConstants.PATH_SEPERATOR.length()) :
					  path;

			path = pathWihoutTrailngSep.substring(0, path.lastIndexOf(DirChooserConstants.PATH_SEPERATOR));
		}
		return new File(path);
	}

	//Observable -------------------------------------------------

	List<PreferencesObserver> observers = new ArrayList<PreferencesObserver>();

	private void notify(Key key)
	{
		for (PreferencesObserver observer : observers)
		{
			observer.changed(key);
		}
	}

	public void addObserver(PreferencesObserver observer)
	{
		observers.add(observer);
	}

	public void removeObserver(PreferencesObserver observer)
	{
		observers.remove(observer);
	}
}
