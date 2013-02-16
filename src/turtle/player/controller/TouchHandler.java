package turtle.player.controller;

import android.app.Activity;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import turtle.player.R;
import turtle.player.model.Track;
import turtle.player.persistance.framework.filter.FieldFilter;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.framework.filter.FilterSet;
import turtle.player.persistance.framework.filter.FilterVisitor;
import turtle.player.persistance.source.relational.FieldPersistable;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.player.Player;
import turtle.player.playlist.Playlist;
import turtle.player.presentation.InstanceFormatter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

public abstract class TouchHandler extends Playlist.PlaylistFilterChangeObserver implements View.OnTouchListener, Player.PlayerObserver
{

	private enum Mode
	{
		MENU,
		TRACK
	}

	private enum BowMenuEntry
	{
		LEFT(R.id.bowmenu_left, R.drawable.menubow_left_290_active, R.drawable.menubow_left_290, R.id.track_instant_filter_left, Tables.TRACKS.ALBUM),
		RIGHT(R.id.bowmenu_right, R.drawable.menubow_right_290_active, R.drawable.menubow_right_290, R.id.track_instant_filter_right, Tables.TRACKS.ARTIST),
		BOTTOM(R.id.bowmenu_bottom, R.drawable.menubow_bottom_290_active, R.drawable.menubow_bottom_290, R.id.track_instant_filter_bottom, Tables.TRACKS.GENRE),
		TOP(R.id.bowmenu_top, R.drawable.menubow_top_290_active, R.drawable.menubow_top_290, R.id.track_instant_filter_top, Tables.TRACKS.ROOTSRC);

		final int layoutId;
		final int layoutIdOnPic;
		final int layoutIdOffPic;
		final int layoutIdText;
		final FieldPersistable<Track, ?> field;

		private BowMenuEntry(int layoutId,
									int layoutOnIdPic,
									int layoutOffIdPic,
									int layoutIdText,
									FieldPersistable<Track, ?> field)
		{
			this.layoutId = layoutId;
			this.layoutIdOnPic = layoutOnIdPic;
			this.layoutIdOffPic = layoutOffIdPic;
			this.layoutIdText = layoutIdText;
			this.field = field;
		}

		public int getLayoutId()
		{
			return layoutId;
		}

		public FieldPersistable<Track, ?> getField()
		{
			return field;
		}

		public int getLayoutPic(boolean active)
		{
			return active ? layoutIdOnPic : layoutIdOffPic;
		}

		public int getLayoutIdText()
		{
			return layoutIdText;
		}
	}

	//Filter
	private final Map<BowMenuEntry, ImageView> bowMenuEntries = new HashMap<BowMenuEntry, ImageView>();
	private final Map<BowMenuEntry, TextView> bowMenuTextEntries = new HashMap<BowMenuEntry, TextView>();
	private final Set<View> bowMenuViews = new HashSet<View>();
	private final ImageView pointer;

	private final View[] scrollingViews;
	private final Point[] initalScrollingOfScrollingViews;

	private final GestureDetector gestureDetector;

	private final int[] pointerLocationOnScreen = new int[2];

	private Mode currMode = Mode.TRACK;


	protected TouchHandler(Activity activity, View... scrollingViews)
	{
		pointer = (ImageView) activity.findViewById(R.id.pointer);

		for(BowMenuEntry bowMenuEntry : BowMenuEntry.values()){
			ImageView view = (ImageView) activity.findViewById(bowMenuEntry.getLayoutId());
			TextView viewText = (TextView) activity.findViewById(bowMenuEntry.getLayoutIdText());

			bowMenuEntries.put(bowMenuEntry, view);
			bowMenuTextEntries.put(bowMenuEntry, viewText);
			bowMenuViews.add(view);
			bowMenuViews.add(viewText);
		}

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

			for(View view : bowMenuViews){
				view.setVisibility(View.VISIBLE);
			}

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
							for(Map.Entry<BowMenuEntry, ImageView> entry : bowMenuEntries.entrySet()){
								if(isPointInsideOf(entry.getValue(), event.getRawX(), event.getRawY())){
									filterSelected(entry.getKey().getField());
								}
							}
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
				for(View view : bowMenuViews){
					view.setVisibility(View.INVISIBLE);
				}

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

	public void trackChanged(Track track)
	{
		for(Map.Entry<BowMenuEntry, TextView> bowMenuEntry : bowMenuTextEntries.entrySet()){
			bowMenuEntry.getValue().setText(bowMenuEntry.getKey().getField().getAsString(track));
		}
	}

	public void started()
	{
		//do nothing
	}

	public void stopped()
	{
		//do nothing
	}

	public void filterAdded(Filter filter)
	{
		filterChanged(filter, true);
	}

	public void filterRemoved(Filter filter)
	{
		filterChanged(filter, false);
	}

	private void filterChanged(Filter filter, final boolean activated){
		filter.accept(new FilterVisitor<Object, Void>()
		{
			public <T> Void visit(FieldFilter<Object, T> fieldFilter)
			{
				for(BowMenuEntry entry : BowMenuEntry.values())
				{
					if(entry.getField().equals(fieldFilter.getField()))
					{
						bowMenuEntries.get(entry).setImageResource(entry.getLayoutPic(activated));
					}
				}
				return null;
			}

			public Void visit(FilterSet filterSet)
			{
				for(Filter filter : filterSet.getFilters()){
					filter.accept(this);
				}
				return null;
			}
		});
	}


	protected abstract void nextGestureRecognized();
	protected abstract void previousGestureRecognized();
	protected abstract void filterSelected(FieldPersistable<Track, ?> field);
}
