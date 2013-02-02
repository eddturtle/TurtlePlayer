package turtle.player.controller;

import android.app.Activity;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import turtle.player.R;
import turtle.player.model.Track;
import turtle.player.persistance.source.relational.FieldPersistable;
import turtle.player.persistance.turtle.db.structure.Tables;

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

public abstract class TouchHandler implements View.OnTouchListener
{

	private enum Mode
	{
		MENU,
		TRACK
	}

	//Filter
	private final ImageView bowMenuTop;
	private final ImageView bowMenuLeft;
	private final ImageView bowMenuRight;
	private final ImageView bowMenuBottom;
	private final ImageView pointer;

	private final View[] scrollingViews;
	private final Point[] initalScrollingOfScrollingViews;

	private final GestureDetector gestureDetector;

	private final int[] pointerLocationOnScreen = new int[2];

	private Mode currMode = Mode.TRACK;


	protected TouchHandler(Activity activity, View... scrollingViews)
	{
		pointer = (ImageView) activity.findViewById(R.id.pointer);
		bowMenuTop = (ImageView) activity.findViewById(R.id.bowmenu_top);
		bowMenuLeft = (ImageView) activity.findViewById(R.id.bowmenu_left);
		bowMenuBottom = (ImageView) activity.findViewById(R.id.bowmenu_bottom);
		bowMenuRight = (ImageView) activity.findViewById(R.id.bowmenu_right);

		initalScrollingOfScrollingViews = new Point[scrollingViews.length];
		this.scrollingViews = scrollingViews;

		gestureDetector = new GestureDetector(activity, gestureListener);
		gestureDetector.setIsLongpressEnabled(false);
	}

	GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
		@Override
		public boolean onScroll(MotionEvent e1,
										MotionEvent e2,
										float distanceX,
										float distanceY)
		{
			switch (currMode)
			{
				case MENU:
					break;
				case TRACK:
					scrollScrollingViewsBy((int)(distanceX*1.5), 0);
					pointer.removeCallbacks(pointerShower);
					break;
			}

			pointer.scrollBy((int)distanceX, (int)distanceY);
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1,
									  MotionEvent e2,
									  float velocityX,
									  float velocityY)
		{
			switch (currMode)
			{
				case MENU:
					break;
				case TRACK:
					if(velocityX < 0)
					{
						if(e1.getX() > e2.getX())
						{
							nextGestureRecognized();
							return true;
						}
					}
					else
					{
						if(e1.getX() < e2.getX())
						{
							previousGestureRecognized();
							return true;
						}
					}
					break;
			}
			return false;
		}
	};


	final Runnable pointerShower = new Runnable()
	{
		public void run()
		{
			pointer.setVisibility(View.VISIBLE);
			bowMenuTop.setVisibility(View.VISIBLE);
			bowMenuLeft.setVisibility(View.VISIBLE);
			bowMenuBottom.setVisibility(View.VISIBLE);
			bowMenuRight.setVisibility(View.VISIBLE);

			currMode = Mode.MENU;
		}
	};

	public boolean onTouch(View v,
								  MotionEvent event)
	{
		boolean wasConsumed = gestureDetector.onTouchEvent(event);
		boolean consumed = false;

		if(MotionEvent.ACTION_DOWN == event.getAction())
		{
			pointer.getLocationOnScreen(pointerLocationOnScreen);
			pointer.scrollTo(
					  -((int)event.getX() - pointerLocationOnScreen[0] - pointer.getWidth()/2),
					  -((int)event.getY() - pointerLocationOnScreen[0] - pointer.getHeight()/2)
			);
			pointer.postDelayed(pointerShower, 500);
			consumed = true;
		}
		else{

			if (MotionEvent.ACTION_UP == event.getAction())
			{
				if(!wasConsumed)
				{
					switch (currMode)
					{
						case MENU:
							if(isPointInsideOf(bowMenuTop, event.getRawX(), event.getRawY())) filterSelected(Tables.TRACKS.LENGTH); //TODO
							if(isPointInsideOf(bowMenuLeft, event.getRawX(), event.getRawY())) filterSelected(Tables.TRACKS.ALBUM);
							if(isPointInsideOf(bowMenuRight, event.getRawX(), event.getRawY())) filterSelected(Tables.TRACKS.ARTIST);
							if(isPointInsideOf(bowMenuBottom, event.getRawX(), event.getRawY())) filterSelected(Tables.TRACKS.ROOTSRC);
							break;

						case TRACK:
							if(scrollingViews[0].getScrollX() > scrollingViews[0].getWidth()*2/3){
								nextGestureRecognized();
								consumed = true;
							}
							else if(-scrollingViews[0].getScrollX() > scrollingViews[0].getWidth()*2/3)
							{
								previousGestureRecognized();
								consumed = true;
							}
							break;
					}
				}

				resetScrollingViews();
				pointer.scrollTo(0,0);
				pointer.setVisibility(View.INVISIBLE);
				bowMenuTop.setVisibility(View.INVISIBLE);
				bowMenuLeft.setVisibility(View.INVISIBLE);
				bowMenuBottom.setVisibility(View.INVISIBLE);
				bowMenuRight.setVisibility(View.INVISIBLE);

				pointer.removeCallbacks(pointerShower);
				currMode = Mode.TRACK;
			}
		}
		return consumed;
	}

	private boolean isPointInsideOf(View view, float x, float y){
		int location[] = new int[2];
		view.getLocationOnScreen(location);
		int viewX = location[0];
		int viewY = location[1];

		//point is inside view bounds
		if(( x > viewX && x < (viewX + view.getWidth())) &&
				  ( y > viewY && y < (viewY + view.getHeight()))){
			return true;
		} else {
			return false;
		}
	}

	private void resetScrollingViews(){
		saveInitalPositions();
		for(int i = 0; i < scrollingViews.length; i++){
			scrollingViews[i].scrollTo(initalScrollingOfScrollingViews[i].x, initalScrollingOfScrollingViews[i].y);
		}
	}

	private void scrollScrollingViewsBy(int x, int y){
		saveInitalPositions();
		for(View scrollingView : scrollingViews){
			scrollingView.scrollBy(x, y);
		}
	}

	private void saveInitalPositions(){
		if(initalScrollingOfScrollingViews[0] == null)
		{
			for(int i = 0; i < scrollingViews.length; i++)
			{
				initalScrollingOfScrollingViews[i] = new Point(scrollingViews[i].getScrollX(), scrollingViews[i].getScrollY());
			}
		}
	}


	protected abstract void nextGestureRecognized();
	protected abstract void previousGestureRecognized();
	protected abstract void filterSelected(FieldPersistable<Track, ?> field);
}
