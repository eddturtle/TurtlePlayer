package turtle.player.persistance.framework.creator;

import java.util.*;

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

/**
 * @param <I>
 * @param <S> Source
 */
public abstract class CreatorForList<I, S, M> implements Creator<List<I>, M>
{
    private final Creator<I, S> creator;

    protected CreatorForList(Creator<I, S> creator)
    {
        this.creator = creator;
    }

    public List<I> create(M queryResult)
    {
        List<I> result = new ArrayList<I>();

        while(hasNext(queryResult))
        {
            result.add(creator.create(next(queryResult)));
        }

        return result;
    }

    protected abstract boolean hasNext(M queryResult);

    protected abstract S next(M queryResult);
}
