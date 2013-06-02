package turtle.player.model;

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

public class TrackBundle
{
	private final Track track;
	private final Track trackAfter;
	private final Track trackBefore;

	/**
	 * Empty Track Bundle
	 */
	public TrackBundle()
	{
		this.track = null;
		this.trackAfter = null;
		this.trackBefore = null;
	}

	/**
	 * @param track can be null
	 * @param trackAfter can be null
	 * @param trackBefore can be null
	 */
	public TrackBundle(Track track,
							 Track trackAfter,
							 Track trackBefore)
	{
		this.track = track;
		this.trackAfter = trackAfter;
		this.trackBefore = trackBefore;
	}

	/**
	 * @return null if not available
	 */
	public Track getTrackBefore()
	{
		return trackBefore;
	}

	/**
	 * @return null if not available
	 */
	public Track getTrack()
	{
		return track;
	}

	/**
	 * @return null if not available
	 */
	public Track getTrackAfter()
	{
		return trackAfter;
	}
}
