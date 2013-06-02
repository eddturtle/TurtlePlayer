package com.turtleplayer.persistance.framework.mapping;

import com.turtleplayer.persistance.framework.creator.Creator;

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
 *
 * Knows how to create I's from C's  which are dependent from Q
 * Eg: Knows How to create an Instance I from Query result Cursor C from Sql Q
 *
 * @param <Q> eg sql String
 * @param <I> resulting instance
 * @param <C> eg cursor
 */
public interface Mapping<Q, I, C> extends Creator<I, C>, QueryGenerator<Q>
{
	Q get();
	I create(C queryResult);
}
