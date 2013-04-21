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

package turtle.player.model;

import turtle.player.util.Shorty;

import java.io.File;

public class FSobject implements Instance
{
	private final String name;
	private final String path;

	public FSobject(String path,
						 String name)
	{
		this.name = Shorty.avoidNull(name);
		this.path = Shorty.avoidNull(path);
	}

	public FSobject(String path)
	{
		int index = path.lastIndexOf("/");
		this.name = Shorty.avoidNull(path.substring(0, index));
		this.path = Shorty.avoidNull(path.substring(index, path.length()));
	}

	public String getName()
	{
		return path;
	}

	public String getFullPath()
	{
		return path;
	}

	public File getAsFile()
	{
		return new File(getFullPath());
	}

	public File getPathAsFile()
	{
		return new File(path);
	}

	public String getPath()
	{
		return path;
	}


	public <R> R accept(InstanceVisitor<R> visitor)
	{
		return visitor.visit(this);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof FSobject)) return false;

		FSobject FSobject = (FSobject) o;

		if (!name.equals(FSobject.name)) return false;
		if (!path.equals(FSobject.path)) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = name.hashCode();
		result = 31 * result + path.hashCode();
		return result;
	}
}
