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
 * @param <RESULT> Instance type
 * @param <S> Source type of row
 * @param <M> Source type of table
 */
public abstract class CreatorForList<TARGET, RESULT, S, M> implements ResultCreator<TARGET, List<RESULT>, M>
{
    private final Creator<? extends RESULT, S> creator;

    protected CreatorForList(Creator<? extends RESULT, S> creator)
    {
        this.creator = creator;
    }

    public List<RESULT> create(M queryResult)
    {
        List<RESULT> result = new ArrayList<RESULT>();

        while(hasNext(queryResult))
        {
            result.add(creator.create(next(queryResult)));
        }

        return result;
    }

    protected abstract boolean hasNext(M queryResult);

    protected abstract S next(M queryResult);
}
