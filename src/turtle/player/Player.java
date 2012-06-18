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
package turtle.player;

// Import - Java
import java.io.IOException;

// Import - Android System
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

// Import - Android Widgets
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

// Import - Android Activity
import android.app.ListActivity;
import android.app.ProgressDialog;

// Import - Android Media
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

// Import - Android View
import android.view.View;
import android.view.View.OnClickListener;

// Import - Android Graphics
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

// Import - Android Telephony
import android.telephony.TelephonyManager;
import android.telephony.PhoneStateListener;


public class Player extends ListActivity
{
	
	// ========================================= //
	// 	Attributes
	// ========================================= //
	
	private TurtlePlayer tp;
	
	// Header Bar
	private ImageView list;
	private ImageView logo;
	private ImageView settings;
	
	// Slides
	private RelativeLayout nowPlayingSlide;
	private RelativeLayout playlistSlide;
	private RelativeLayout settingsSlide;
	
	// Playlist Slide
	private ImageView trackButton;
	private ImageView artistButton;
	private ImageView albumButton;
	
	// NowPlaying Slide
	private ImageView backButton;
	private ImageView playButton;
	private ImageView nextButton;
	private ImageView shuffleButton;
	private ImageView repeatButton;
	
	private TextView duration;
	
	// Progress Bar Attributes
	private Handler handler = new Handler();
	private Runnable progressBarRunnable;
	private SeekBar progressBar;
	
	// Telephony
	private TelephonyManager mgr;
	private PhoneStateListener phoneStateListener;
	
	
	// ========================================= //
	// 	OnCreate & OnDestroy
	// ========================================= //
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Call Main Initialise Function
        this.Init();
    }
    
    @Override
    public void onDestroy()
    {
	    if (mgr != null)
	    {
	    	mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
	    }
	    
	    super.onDestroy();
    }
	
	// ========================================= //
	// 	OnStart & OnStop
	// ========================================= //
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	
    	RefreshList(tp.playlist.GetList());
    	
    	UpdateScreenInfo(tp.currentlyPlaying);
    	UpdateProgressBar();
    	
    	if (tp.isInitialised && tp.isPaused)
    	{
    		Pause();
    	}
    }
		    
    @Override
    public void onStop()
    {
    	super.onStop();
    }
    
    
	// ========================================= //
	// 	onSaveInstanceState & onRestoreInstanceState
	// ========================================= //
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
    	super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
    	super.onRestoreInstanceState(savedInstanceState);
    }
    
    
	// ========================================= //
	// 	Initialise Player
	// ========================================= //
    
    private void Init()
    {
    	SetupApplication();
        SetupButtons();
    	SetupButtonListeners();
    	SetupSlides();
    	SetupList();
        SetupProgressBar();
        SetupTelephoneChecker();
    }
    
    
	// ========================================= //
	// 	Setup Application - Part of Init()
	// ========================================= //
    
    private void SetupApplication()
    {
    	tp = ((TurtlePlayer)this.getApplication());
    	tp.playlist.SetContext(tp.getApplicationContext());
    }
    
    
    // ========================================= //
 	// 	Setup Buttons - Part of Init()
	// ========================================= //
     
	private void SetupButtons()
     {
     	// Header Buttons
     	list = (ImageView)findViewById(R.id.listButton);
     	logo = (ImageView)findViewById(R.id.logoButton);
     	settings = (ImageView)findViewById(R.id.settingsButton);

     	// Now_Playing Footer Buttons
     	backButton = (ImageView)findViewById(R.id.backButton);
     	playButton = (ImageView)findViewById(R.id.playButton);
     	nextButton = (ImageView)findViewById(R.id.nextButton);
     	
		shuffleButton = (ImageView)findViewById(R.id.shuffleButton);
		if (tp.playlist.preferences.GetShuffle())
		{
			shuffleButton.setImageDrawable(getResources().getDrawable(R.drawable.shuffle48_active));
		}
		
		repeatButton = (ImageView)findViewById(R.id.repeatButton);
		if (tp.playlist.preferences.GetRepeat())
		{
			repeatButton.setImageDrawable(getResources().getDrawable(R.drawable.repeat48_active));
		}

     	// Playlist Footer Buttons
     	trackButton = (ImageView)findViewById(R.id.trackButton);
     	artistButton = (ImageView)findViewById(R.id.artistButton);
     	albumButton = (ImageView)findViewById(R.id.albumButton);
     	
		// 1 = Artist
		// 2 = Album
		// 3 = Inside Artist
		// 4 = Inside Album
     	
     	if (tp.playlist.GetReturnType() == 1 || tp.playlist.GetReturnType() == 3)
     	{
     		artistButton.setImageDrawable(getResources().getDrawable(R.drawable.artist48_active));
     	}
     	else if (tp.playlist.GetReturnType() == 2 || tp.playlist.GetReturnType() == 4)
     	{
     		albumButton.setImageDrawable(getResources().getDrawable(R.drawable.album48_active));
     	}
     	else
     	{
         	trackButton.setImageDrawable(getResources().getDrawable(R.drawable.track48_active));
     	}
	}
    
    
	// ========================================= //
 	// 	Setup Button Listener Functions - Part of Init()
 	// ========================================= //
     
	private void SetupButtonListeners()
     {
     	// [header.xml] Header Button
     	
     	list.setOnClickListener(new OnClickListener()
     	{
     	    public void onClick(View v)
     	    {
     	    	if (!tp.playlist.IsEmpty())
     	    	{
 	    	    	SwitchToPlaylistSlide();
     	    	}
     	    }
     	});
     	
     	logo.setOnClickListener(new OnClickListener()
     	{
     	    public void onClick(View v)
     	    {
     	    	SwitchToNowPlayingSlide();
     	    }
     	});
     	
     	settings.setOnClickListener(new OnClickListener()
     	{
     	    public void onClick(View v)
     	    {
     	    	 SwitchToSettingsSlide();
     	    }
     	});
     	
     	
     	// [now_playing.xml] Footer Buttons
     	
     	backButton.setOnClickListener(new OnClickListener()
     	{
     	    public void onClick(View v)
     	    {
     	    	if (!tp.playlist.IsEmpty())
     	    	{
     	    		Play(tp.playlist.PreviousTrack());
     	    	}
     	    }
     	});
     	
     	playButton.setOnClickListener(new OnClickListener()
     	{
     	    public void onClick(View v)
     	    {
     	    	if (!tp.playlist.IsEmpty())
     	    	{
     	    		if (tp.isInitialised == true)
     	    		{
 		    	    	if (tp.isPaused == true)
 		    	    	{
 		    	    		UnPause();
 		    	    	}
 		    	    	else
 		    	    	{
 		    	    		Pause();
 		    	    	}
     	    		}
     	    		else
     	    		{
     	    			Play(tp.playlist.GetTrack(0));
     	    		}
     	    	}
     	    }
     	});
     	
     	nextButton.setOnClickListener(new OnClickListener()
     	{
     	    public void onClick(View v)
     	    {
     	    	if (!tp.playlist.IsEmpty())
     	    	{
     	    		Track t = tp.playlist.NextTrack();
     	    		if (t != null)
     	    		{
     	    			Play(t);
     	    		}
     	    	}
     	    }
     	});
     	
     	shuffleButton.setOnClickListener(new OnClickListener()
     	{
     	    public void onClick(View v)
     	    {
     	    	ToggleShuffle();
     	    }
     	});
     	
     	repeatButton.setOnClickListener(new OnClickListener()
     	{
     	    public void onClick(View v)
     	    {
     	    	ToggleRepeat();
     	    }
     	});
     	
     	
     	// [playlist.xml] Footer Buttons
     	
     	trackButton.setOnClickListener(new OnClickListener()
     	{
     	    public void onClick(View v)
     	    {
     	    	if (!tp.playlist.IsEmpty())
     	    	{
     	    		tp.playlist.SortByTitle();
     	    		RefreshList(tp.playlist.GetList());
     	    		
     	    		ResetFooterButtons();
     	        	trackButton.setImageDrawable(getResources().getDrawable(R.drawable.track48_active));
     	    	}
     	    }
     	});
     	
     	artistButton.setOnClickListener(new OnClickListener()
     	{
     	    public void onClick(View v)
     	    {
     	    	if (!tp.playlist.IsEmpty())
     	    	{
     	    		tp.playlist.SortByArtist();
     	    		RefreshList(tp.playlist.GetList());
     	    		
     	    		ResetFooterButtons();
     	        	artistButton.setImageDrawable(getResources().getDrawable(R.drawable.artist48_active));
     	    	}
     	    }
     	});
     	
     	albumButton.setOnClickListener(new OnClickListener()
     	{
     	    public void onClick(View v)
     	    {
     	    	if (!tp.playlist.IsEmpty())
     	    	{
     	    		tp.playlist.SortByAlbum();
     	    		RefreshList(tp.playlist.GetList());
     	    		
     	    		ResetFooterButtons();
     	    		albumButton.setImageDrawable(getResources().getDrawable(R.drawable.album48_active));
     	    	}
     	    }
     	});
     	
     	
     	// [settings.xml]
     	
     	Button rescan = (Button)findViewById(R.id.rescan);
     	rescan.setOnClickListener(new OnClickListener()
     	{
     	    public void onClick(View v)
     	    {
     	    	Stop();
     	    	
     	    	tp.playlist.DatabaseClear(); // Don't Delete DB
     	    	
     	    	try
     	    	{
         	    	UpdatePlayList();    	    		
     	    	}
     			catch (NullPointerException e)
     			{
     				Log.v(tp.playlist.preferences.GetTag(), e.getMessage());
     			}
     	    	
     	    	if (!tp.playlist.IsEmpty())
     	    	{
     		    	SwitchToPlaylistSlide();
     	    	}
     	    }
     	});
     }
     
     
 	// ========================================= //
 	// 	Setup Slides - Part of Init()
 	// ========================================= //

	private void SetupSlides()
     {
     	nowPlayingSlide = (RelativeLayout)findViewById(R.id.now_playing_slide);
     	playlistSlide = (RelativeLayout)findViewById(R.id.playlist_slide);
     	settingsSlide = (RelativeLayout)findViewById(R.id.settings_slide);
     	
     	SwitchToNowPlayingSlide();
     }
     
	private void SwitchToNowPlayingSlide()
     {
        duration = (TextView)findViewById(R.id.trackDuration);
        
		ResetHeaderButtons();
     	logo.setImageDrawable(getResources().getDrawable(R.drawable.logo128_active));

     	nowPlayingSlide.setVisibility(LinearLayout.VISIBLE);
     	playlistSlide.setVisibility(LinearLayout.INVISIBLE);
     	settingsSlide.setVisibility(LinearLayout.INVISIBLE);
     }
     
	private void SwitchToPlaylistSlide()
     {
		ResetHeaderButtons();
     	list.setImageDrawable(getResources().getDrawable(R.drawable.list64_active));
     	
     	nowPlayingSlide.setVisibility(LinearLayout.INVISIBLE);
     	playlistSlide.setVisibility(LinearLayout.VISIBLE);
     	settingsSlide.setVisibility(LinearLayout.INVISIBLE);
     }
     
	private void SwitchToSettingsSlide()
     {
		ResetHeaderButtons();
     	settings.setImageDrawable(getResources().getDrawable(R.drawable.settings48_active));
     	
     	nowPlayingSlide.setVisibility(LinearLayout.INVISIBLE);
     	playlistSlide.setVisibility(LinearLayout.INVISIBLE);
     	settingsSlide.setVisibility(LinearLayout.VISIBLE);
     	
     	CheckBox shuffleCheckBox = (CheckBox)findViewById(R.id.shuffleCheckBox);
     	CheckBox repeatCheckBox = (CheckBox)findViewById(R.id.repeatCheckBox);
     	
     	if (tp.playlist.preferences.GetShuffle())
     	{
     		shuffleCheckBox.setChecked(true);
     	}
     	
     	if (tp.playlist.preferences.GetRepeat())
     	{
     		repeatCheckBox.setChecked(true);
     	}
     	
     	shuffleCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
     	{
     		@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
     		{
     			tp.playlist.preferences.SetShuffle(isChecked);
     		}
     	});
     	
     	repeatCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
     	{
     		@Override
     		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
     		{
     			tp.playlist.preferences.SetRepeat(isChecked);
     		}
     	});

     }

    
	// ========================================= //
	// 	Setup Progress Bar - Part of Init()
	// ========================================= //
    
    private void SetupList()
    {
        if (tp.playlist.IsEmpty())
        {
	    	UpdatePlayList();
	    	
	    	if (!tp.playlist.IsEmpty())
	    	{
		    	SwitchToPlaylistSlide();
	    	}
	    	else
	    	{
	    		Toast toast = Toast.makeText(getApplicationContext(), "No MP3s Found on SD Card", Toast.LENGTH_LONG);
	    		toast.show();
	    	}
        }
    }
    
    
	// ========================================= //
	// 	Setup Progress Bar - Part of Init()
	// ========================================= //
    
	private void SetupProgressBar()
    {
    	progressBar = (SeekBar)findViewById(R.id.progressBar);
        progressBarRunnable = new Runnable()
        {
        	public void run()
        	{
        		if (tp.isInitialised)
        		{
        			progressBar.setProgress(tp.mp.getCurrentPosition());
                	duration.setText(ConvertToMinutes(tp.mp.getCurrentPosition()) + " / " + ConvertToMinutes(tp.mp.getDuration()));
        		}
        		else
        		{
        			progressBar.setProgress(0);
        		}
        		handler.postDelayed(this, 100);
        	}
        };
    }
    
	
	// ========================================= //
	// 	Setup Telephony Service - Part of Init()
	// ========================================= //
    
    private void SetupTelephoneChecker()
    {
        mgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener()
        {
        	@Override
        	public void onCallStateChanged(int state, String incomingNumber)
        	{
        		if (state == TelephonyManager.CALL_STATE_RINGING)
        		{
        			if (tp.mp.isPlaying())
        			{
        				Pause();
        			}
        		}
        		else if (state == TelephonyManager.CALL_STATE_IDLE)
        		{
        			if (tp.isInitialised && !tp.isPaused)
        			{
        				UnPause();
        			}
        		}
        		super.onCallStateChanged(state, incomingNumber);
        	}
        };
        mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }
    

	// ========================================= //
	// 	Reset Buttons
	// ========================================= //
    
    private void ResetHeaderButtons()
    {
    	list.setImageDrawable(getResources().getDrawable(R.drawable.list64));
    	logo.setImageDrawable(getResources().getDrawable(R.drawable.logo128));
    	settings.setImageDrawable(getResources().getDrawable(R.drawable.settings48));
    }
    
    private void ResetFooterButtons()
    {
     	trackButton.setImageDrawable(getResources().getDrawable(R.drawable.track48));
     	artistButton.setImageDrawable(getResources().getDrawable(R.drawable.artist48));
     	albumButton.setImageDrawable(getResources().getDrawable(R.drawable.album48));
    }
    
    
	// ========================================= //
	// 	Update TrackList
	// ========================================= //
    
    private void UpdatePlayList()
    {
    	ProgressDialog progressDialog;
    	progressDialog = new ProgressDialog(tp.getApplicationContext());
    	progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    	progressDialog.setMessage("Loading...");
    	progressDialog.setCancelable(false);
    	
    	tp.playlist.UpdateList();
    	tp.playlist.SortByTitle();
        this.RefreshList(tp.playlist.GetList());
    }
    
    private void RefreshList(String[] strList)
    {
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.item, strList));
    }
    
    
	// ========================================= //
	// 	Play Track
	// ========================================= //
    
    private void Play(Track t)
    {
    	if (t != null)
    	{
	    	try
	    	{
	    		tp.mp.reset();
	    		tp.mp.setDataSource(t.GetSrc());
	
	    		tp.mp.prepare();
	    		tp.mp.start();
		    	
	    		tp.playlist.AddToHistory();
	    		tp.playlist.stats.IncrementPlayCount();
		    	
	    		tp.isPaused = false;
	    		tp.isInitialised = true;
		    	
		    	TogglePlayButton();
		    	
		    	UpdateScreenInfo(t);
		    	UpdateProgressBar();
		    	
		    	tp.currentlyPlaying = t;
		    	
		    	tp.mp.setOnCompletionListener(new OnCompletionListener()
		    	{
		    		public void onCompletion(MediaPlayer mplayer)
		    		{
		    			Play(tp.playlist.NextTrack());
		    		}
		    	});
	    	}
	    	catch (IOException e)
	    	{
	    		Log.v(tp.playlist.preferences.GetTag(), e.getMessage());
	    	}
    	}
    }
    
    
	// ========================================= //
	// 	Update Progress Bar
	// ========================================= //
    
    private void UpdateScreenInfo(Track t)
    {
    	TextView title = (TextView)findViewById(R.id.trackTitle);
    	TextView artist = (TextView)findViewById(R.id.trackArtist);
    	
    	title.setVisibility(View.VISIBLE);
    	artist.setVisibility(View.VISIBLE);
    	duration.setVisibility(View.VISIBLE);
    	
    	title.setText(t.GetTitle());
    	artist.setText(t.GetArtist());
    	
    	if (tp.isInitialised)
    	{
    		duration.setText(ConvertToMinutes(tp.mp.getCurrentPosition()) + " / " + ConvertToMinutes(tp.mp.getDuration()));
    	}
    	else
    	{
    		duration.setText("0:00");
    	}
    	
		ImageView iv = (ImageView)findViewById(R.id.albumArt);
		
		
    	if (t.HasAlbumArt() && t.GetRootSrc() != null)
    	{
    		String location = t.GetRootSrc() + "Folder.jpg";
    		Bitmap bmp = BitmapFactory.decodeFile(location);
    		
    		iv.setImageBitmap(bmp);
    	}
    	else
    	{
    		iv.setImageDrawable(getResources().getDrawable(R.drawable.blank_album_art));
    	}
    }
    
    
	// ========================================= //
	// 	Update Progress Bar
	// ========================================= //
    
    private void UpdateProgressBar()
    {
    	// http://www.androidhive.info/2012/03/android-building-audio-player-tutorial/
    	//
    	if (tp.isInitialised)
    	{
    		progressBar.setMax(tp.mp.getDuration());
    	}
    	else
    	{
    		progressBar.setMax(0);
    	}
    	
    	handler.postDelayed(progressBarRunnable, 100);
    	
    	progressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
    	{
    		@Override
    		public void onStopTrackingTouch(SeekBar seekBar)
    		{
    			handler.removeCallbacks(progressBarRunnable);
    			tp.mp.seekTo(seekBar.getProgress());
    			UpdateProgressBar();
    		}
    		
    		@Override
    		public void onStartTrackingTouch(SeekBar seekBar)
    		{
    			handler.removeCallbacks(progressBarRunnable);
    		}
    		
    		@Override
    		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    		{
    			// Needed, Although Blank
    		}
    	});
    	
    }
    
    
	// ========================================= //
	// 	Get Duration String
	// ========================================= //
    
    private String ConvertToMinutes(int time)
    {
    	String duration;
    	
    	int milliseconds = time;
    	int seconds = (int) (milliseconds / 1000) % 60 ;
    	int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
    	
    	if (seconds < 10)
    	{
    		duration = Integer.toString(minutes) + ":0" + Integer.toString(seconds);
    	}
    	else
    	{
    		duration = Integer.toString(minutes) + ":" + Integer.toString(seconds);
    	}
    	
    	return duration;
    }
    
    
	// ========================================= //
	// 	Pause & UnPause
	// ========================================= //
    
    public void Pause()
    {
    	tp.mp.pause();
    	tp.isPaused = true;
    	TogglePlayButton();
    }
    
    public void UnPause()
    {
    	tp.mp.start();
    	tp.isPaused = false;
    	TogglePlayButton();
    }
    
    public void Stop()
    {
    	tp.isPaused = true;
    	tp.isInitialised = false;
    	tp.mp.stop();
    }
    
    
	// ========================================= //
	// 	Toggle Play / Pause Button
	// ========================================= //
    
    public void TogglePlayButton()
    {
    	if (tp.isPaused == true)
    	{
    		playButton.setImageDrawable(getResources().getDrawable(R.drawable.play64));
    	}
    	else
    	{
    		playButton.setImageDrawable(getResources().getDrawable(R.drawable.pause64));	
    	}
    	
    }
    
    
	// ========================================= //
	// 	Toggle Shuffle
	// ========================================= //
    
    public void ToggleShuffle()
    {
		Toast toast = Toast.makeText(this, "" , Toast.LENGTH_SHORT);
		
    	if (tp.playlist.preferences.GetShuffle())
    	{
    		// Turn Shuffle Off
    		shuffleButton.setImageDrawable(getResources().getDrawable(R.drawable.shuffle48));
    		tp.playlist.preferences.SetShuffle(false);
    		
    		toast.setText("Shuffle Off");
    		toast.show();
    	}
    	else
    	{
    		// Turn Shuffle On
    		shuffleButton.setImageDrawable(getResources().getDrawable(R.drawable.shuffle48_active));
    		tp.playlist.preferences.SetShuffle(true);
    		
    		toast.setText("Shuffle On");
    		toast.show();
    	}
    }
    
    
    
	// ========================================= //
	// 	Toggle Repeat
	// ========================================= //
    
    public void ToggleRepeat()
    {
		Toast toast = Toast.makeText(this, "" , Toast.LENGTH_SHORT);;
		
    	if (tp.playlist.preferences.GetRepeat())
    	{
    		// Turn Shuffle Off
    		repeatButton.setImageDrawable(getResources().getDrawable(R.drawable.repeat48));
    		tp.playlist.preferences.SetRepeat(false);
    		
    		toast.setText("Repeat Off");
    		toast.show();
    	}
    	else
    	{
    		// Turn Shuffle On
    		repeatButton.setImageDrawable(getResources().getDrawable(R.drawable.repeat48_active));
    		tp.playlist.preferences.SetRepeat(true);
    		
    		toast.setText("Repeat On");
    		toast.show();
    	}
    }
    
    
	// ========================================= //
	// 	List Item Listener
	// ========================================= //
    
    protected void onListItemClick(ListView list, View view, int position, long id)
    {
    	// Return Type:
		// 0 = Track
		// 1 = Artist
		// 2 = Album
		// 3 = Inside Artist
		// 4 = Inside Album
    	
    	if (tp.playlist.GetReturnType() == 0)
    	{
	    	Track toPlay = tp.playlist.GetTrack(position);
	    	Play(toPlay);
	    	SwitchToNowPlayingSlide();
    	}
    	else if (tp.playlist.GetReturnType() == 1)
    	{
        	this.RefreshList(tp.playlist.GetTracksByArtist(position));
    	}
    	else if (tp.playlist.GetReturnType() == 2)
    	{
    		this.RefreshList(tp.playlist.GetTracksByAlbum(position));
    	}
    	else if (tp.playlist.GetReturnType() == 3)
    	{
    		Track toPlay = tp.playlist.GetTrack(position);
	    	Play(toPlay);
	    	SwitchToNowPlayingSlide();
    	}
    	else if (tp.playlist.GetReturnType() == 4)
    	{
    		Track toPlay = tp.playlist.GetTrack(position);
	    	Play(toPlay);
	    	SwitchToNowPlayingSlide();
    	}	
    }
   
}