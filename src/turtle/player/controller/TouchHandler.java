package turtle.player.controller;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import turtle.player.R;
import turtle.player.model.Track;
import turtle.player.persistance.framework.filter.*;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.persistance.turtle.filter.DirFilter;
import turtle.player.persistance.turtle.filter.TurtleFilterVisitor;
import turtle.player.player.ObservableOutput;
import turtle.player.playlist.Playlist;

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

public abstract class TouchHandler extends Playlist.PlaylistFilterChangeObserver implements View.OnTouchListener, ObservableOutput.PlayerObserver
{

	private enum Mode
	{
		MENU,
		TRACK
	}

	private enum BowMenuEntry
	{
		LEFT(){
			@Override
			public void setVisible(Activity activity,
										  boolean visible)
			{
				int visibility = visible ? View.VISIBLE : View.GONE;
				activity.findViewById(R.id.track_instant_filter_left).setVisibility(visibility);
				activity.findViewById(R.id.bowmenu_left_icon).setVisibility(visibility);
				activity.findViewById(R.id.bowmenu_left).setVisibility(visibility);
			}

			@Override
			public View getView(Activity activity)
			{
				return activity.findViewById(R.id.bowmenu_left);
			}

			@Override
			public void adapt(Track track, Activity activity)
			{
				((TextView)activity.findViewById(R.id.track_instant_filter_left)).setText(track.getAlbumName());
			}

			@Override
			public Filter<? super Tables.Tracks> getFilter(Track track)
			{
				return track == null ? null : new FieldFilter<Tables.Tracks, Track, String>(Tables.AlbumsReadable.ALBUM, Operator.EQ, Tables.AlbumsReadable.ALBUM.get(track));
			}

			@Override
			public void adapt(final Filter<? super Tables.Tracks> filter, final Track track, final boolean activated, final Activity activity)
			{
				filter.accept(new FilterVisitor<Tables.Tracks, Boolean>()
				{
					public <T, Z> Boolean visit(FieldFilter<? super Tables.Tracks, Z, T> fieldFilter)
					{
						if(Tables.AlbumsReadable.ALBUM.equals(fieldFilter.getField()))
						{
							setSettedFilter(activated ? filter : null);
							ImageView bow = (ImageView) activity.findViewById(R.id.bowmenu_left);
							bow.setImageResource(activated ? R.drawable.menubow_left_290_active : R.drawable.menubow_left_290);
							if(activated)
							{
								adapt(track, activity);
							}
							setVisible(activity, activated);
							return true;
						}
						return false;
					}

					public Boolean visit(FilterSet<? super Tables.Tracks> filterSet)
					{
						boolean adapted = false;
						for(Filter<? super Tables.Tracks> filter : filterSet.getFilters())
						{
							if(filter.accept(this)){
								adapted = true;
							}
						}
						return adapted;
					}

					public Boolean visit(NotFilter<? super Tables.Tracks> notFilter)
					{
						if(notFilter.getFilter().accept(this)){
							((TextView)activity.findViewById(R.id.track_instant_filter_left)).setTextColor(Color.RED);
							return activated;
						}
						return false;
					}
				});
			}
		},
		RIGHT{
			@Override
			public void setVisible(Activity activity,
										  boolean visible)
			{
				int visibility = visible ? View.VISIBLE : View.GONE;
				activity.findViewById(R.id.track_instant_filter_right).setVisibility(visibility);
				activity.findViewById(R.id.bowmenu_right_icon).setVisibility(visibility);
				activity.findViewById(R.id.bowmenu_right).setVisibility(visibility);
			}

			@Override
			public View getView(Activity activity)
			{
				return activity.findViewById(R.id.bowmenu_right);
			}

			@Override
			public void adapt(Track track, Activity activity)
			{
				((TextView)activity.findViewById(R.id.track_instant_filter_right)).setText(track.getArtistName());
			}

			@Override
			public Filter<? super Tables.Tracks> getFilter(Track track)
			{
				return track == null ? null : new FieldFilter<Tables.Tracks, Track, String>(Tables.ArtistsReadable.ARTIST, Operator.EQ, Tables.ArtistsReadable.ARTIST.get(track));
			}

			@Override
			public void adapt(final Filter<? super Tables.Tracks> filter, final Track track, final boolean activated, final Activity activity)
			{
				filter.accept(new FilterVisitor<Tables.Tracks, Boolean>()
				{
					public <T, Z> Boolean visit(FieldFilter<? super Tables.Tracks, Z, T> fieldFilter)
					{
						if(Tables.ArtistsReadable.ARTIST.equals(fieldFilter.getField()))
						{
							setSettedFilter(activated ? filter : null);
							ImageView bow = (ImageView) activity.findViewById(R.id.bowmenu_right);
							bow.setImageResource(activated ? R.drawable.menubow_right_290_active : R.drawable.menubow_right_290);
							if(activated)
							{
								adapt(track, activity);
							}
							setVisible(activity, activated);
							return true;
						}
						return false;
					}

					public Boolean visit(FilterSet<? super Tables.Tracks> filterSet)
					{
						boolean adapted = false;
						for(Filter<? super Tables.Tracks> filter : filterSet.getFilters())
						{
							if(filter.accept(this)){
								adapted = true;
							}
						}
						return adapted;
					}

					public Boolean visit(NotFilter<? super Tables.Tracks> notFilter)
					{
						if(notFilter.getFilter().accept(this)){
							((TextView)activity.findViewById(R.id.track_instant_filter_right)).setTextColor(Color.RED);
							return activated;
						}
						return false;
					}
				});
			}
		},
		TOPLINE{
			@Override
			public void setVisible(Activity activity,
										  boolean visible)
			{
				int visibility = visible ? View.VISIBLE : View.GONE;
				activity.findViewById(R.id.linearLayoutDir).setVisibility(visibility);
			}

			@Override
			public View getView(Activity activity)
			{
				return activity.findViewById(R.id.linearLayoutDir);
			}

			@Override
			public void adapt(Track track, Activity activity)
			{
				((TextView)activity.findViewById(R.id.track_instant_filter_topline)).setText(track.getPath());
			}

			@Override
			public Filter<? super Tables.Tracks> getFilter(Track track)
			{
				return track == null ? null : new DirFilter(Operator.LIKE, Tables.FsObjects.PATH.get(track) + "%");
			}

			@Override
			public void adapt(final Filter<? super Tables.Tracks> filter, final Track track, final boolean activated, final Activity activity)
			{
				filter.accept(new TurtleFilterVisitor<Tables.Tracks, Boolean>()
				{
					public <T, Z> Boolean visit(FieldFilter<? super Tables.Tracks, Z, T> fieldFilter)
					{
						return false;
					}

					public Boolean visit(FilterSet<? super Tables.Tracks> filterSet)
					{
						boolean adapted = false;
						for(Filter<? super Tables.Tracks> filter : filterSet.getFilters())
						{
							if(filter.accept(this)){
								adapted = true;
							}
						}
						return adapted;
					}

					public Boolean visit(NotFilter<? super Tables.Tracks> notFilter)
					{
						if(notFilter.getFilter().accept(this)){
							((TextView)activity.findViewById(R.id.track_instant_filter_topline)).setTextColor(Color.RED);
							return activated;
						}
						return false;
					}

					public Boolean visit(DirFilter dirFilter)
					{
						activity.findViewById(R.id.dir_filter_border).setBackgroundColor(activated ? Color.argb(177, 21, 164, 0) : Color.argb(177, 152, 152, 152));
						((TextView)activity.findViewById(R.id.track_instant_filter_topline)).setText(dirFilter.getValue().replaceAll("%", "*"));
						setSettedFilter(activated ? filter : null);
						if(!activated)
						{
							adapt(track, activity);
						}
						setVisible(activity, activated);
						return true;
					}
				});
			}
		},
		TOP{
			@Override
			public void setVisible(Activity activity,
										  boolean visible)
			{
				int visibility = visible ? View.VISIBLE : View.GONE;
				activity.findViewById(R.id.track_instant_filter_top).setVisibility(visibility);
				activity.findViewById(R.id.bowmenu_top_icon).setVisibility(visibility);
				activity.findViewById(R.id.bowmenu_top).setVisibility(visibility);
			}

			@Override
			public View getView(Activity activity)
			{
				return activity.findViewById(R.id.bowmenu_top);
			}

			@Override
			public void adapt(Track track, Activity activity)
			{
				((TextView)activity.findViewById(R.id.track_instant_filter_top)).setText(track.getGenreName());
			}

			@Override
			public Filter<? super Tables.Tracks> getFilter(Track track)
			{
				return track == null ? null : new FieldFilter<Tables.Tracks, Track, String>(Tables.GenresReadable.GENRE, Operator.EQ, Tables.GenresReadable.GENRE.get(track));
			}

			@Override
			public void adapt(final Filter<? super Tables.Tracks> filter, final Track track, final boolean activated, final Activity activity)
			{
				filter.accept(new FilterVisitor<Tables.Tracks, Boolean>()
				{
					public <T, Z> Boolean visit(FieldFilter<? super Tables.Tracks, Z, T> fieldFilter)
					{
						if(Tables.GenresReadable.GENRE.equals(fieldFilter.getField()))
						{
							setSettedFilter(activated ? filter : null);
							ImageView bow = (ImageView) activity.findViewById(R.id.bowmenu_top);
							bow.setImageResource(activated ? R.drawable.menubow_top_290_active : R.drawable.menubow_top_290);
							if(activated)
							{
								adapt(track, activity);
							}
							setVisible(activity, activated);
							return true;
						}
						return false;
					}

					public Boolean visit(FilterSet<? super Tables.Tracks> filterSet)
					{
						boolean adapted = false;
						for(Filter<? super Tables.Tracks> filter : filterSet.getFilters())
						{
							if(filter.accept(this)){
								adapted = true;
							}
						}
						return adapted;
					}

					public Boolean visit(NotFilter<? super Tables.Tracks> notFilter)
					{
						if(notFilter.getFilter().accept(this)){
							((TextView)activity.findViewById(R.id.track_instant_filter_top)).setTextColor(Color.RED);
							return activated;
						}
						return false;
					}
				});
			}
		};

		private Filter<? super Tables.Tracks> settedFilter = null;

		public boolean isActive()
		{
			return settedFilter != null;
		}

		public void setSettedFilter(Filter<? super Tables.Tracks> settedFilter)
		{
			this.settedFilter = settedFilter;
		}

		public Filter<? super Tables.Tracks> getSettedFilter()
		{
			return settedFilter;
		}

		public abstract void setVisible(Activity activity,
												  boolean visible);
		public abstract View getView(Activity activity);
		public abstract void adapt(Track track, Activity activity);
		public abstract Filter<? super Tables.Tracks> getFilter(Track track);
		public abstract void adapt(Filter<? super Tables.Tracks> filter, Track track, boolean activated, Activity activity);
	}

	//Filter
	private final ImageView pointer;

	private final View[] scrollingViews;
	private final Point[] initalScrollingOfScrollingViews;
	private final Activity activity;

	private final GestureDetector gestureDetector;

	private final int[] pointerLocationOnScreen = new int[2];

	private Mode currMode = Mode.TRACK;
	private Track currTrack = null;


	protected TouchHandler(final Activity activity, View... scrollingViews)
	{
		this.activity = activity;
		pointerShower = new Runnable()
		{
			public void run()
			{
				pointer.setVisibility(View.VISIBLE);

				for(BowMenuEntry bowMenuEntry : BowMenuEntry.values()){
					bowMenuEntry.setVisible(activity, true);
				}

				currMode = Mode.MENU;
			}
		};

		pointer = (ImageView) activity.findViewById(R.id.pointer);

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


	final Runnable pointerShower;

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
					for(BowMenuEntry bowMenuEntry : BowMenuEntry.values()){
						if(isPointInsideOf(bowMenuEntry.getView(activity), event.getRawX(), event.getRawY()) &&
								  (bowMenuEntry.isActive() && scrollingViews[0].getScrollX() == 0 || Mode.MENU.equals(currMode))){
							if(bowMenuEntry.isActive())
							{
								filterSelected(bowMenuEntry.getSettedFilter(), true);
							}
							else
							{
								filterSelected(bowMenuEntry.getFilter(currTrack), false);
							}
						}
					}

					if (Mode.TRACK.equals(currMode))
					{
						if(scrollingViews[0].getScrollX() > scrollingViews[0].getWidth()*2/3){
							nextGestureRecognized();
							consumed = true;
						}
						else if(-scrollingViews[0].getScrollX() > scrollingViews[0].getWidth()*2/3)
						{
							previousGestureRecognized();
							consumed = true;
						}
					}
				}

				resetScrollingViews();
				pointer.scrollTo(0,0);
				pointer.setVisibility(View.GONE);
				for(BowMenuEntry bowMenuEntry : BowMenuEntry.values()){
					if(!bowMenuEntry.isActive())
					{
						bowMenuEntry.setVisible(activity, false);
					}
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

	public void trackChanged(Track track, int lengthInMillis)
	{
		for(BowMenuEntry bowMenuEntry : BowMenuEntry.values()){
			if(!bowMenuEntry.isActive())
			{
				bowMenuEntry.adapt(track, activity);
			}
		}
		this.currTrack = track;
	}

	public void started()
	{
		//do nothing
	}

	public void stopped()
	{
		//do nothing
	}

	public void filterAdded(Filter<? super Tables.Tracks> filter)
	{
		filterChanged(filter, true);
	}

	public void filterRemoved(Filter<? super Tables.Tracks> filter)
	{
		filterChanged(filter, false);
	}

	private void filterChanged(final Filter<? super Tables.Tracks> filter, final boolean activated){
		for(final BowMenuEntry entry : BowMenuEntry.values())
		{
			entry.adapt(filter, currTrack, activated, activity);
		}
	}


	protected abstract void nextGestureRecognized();
	protected abstract void previousGestureRecognized();
	protected abstract void filterSelected(Filter<? super Tables.Tracks> filter, boolean wasActive);
}
