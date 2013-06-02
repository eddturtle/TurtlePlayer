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
package com.turtleplayer;


public class Stats
{
	// ========================================= //
	// 	Attributes
	// ========================================= //

	private int totalPlayCount;


	// ========================================= //
	// 	Constructor
	// ========================================= //

	public Stats()
	{
		totalPlayCount = 0;
	}


	// ========================================= //
	// 	TotalPlayCount
	// ========================================= //

	public int GetTotalPlayCount()
	{
		return totalPlayCount;
	}

	public void SetTotalPlayCount(int nTotalPlayCount)
	{
		totalPlayCount = nTotalPlayCount;
	}

	public void IncrementPlayCount()
	{
		totalPlayCount++;
	}
}
