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

package turtle.player;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import turtle.player.common.filefilter.FileFilters;
import turtle.player.controller.PhoneStateHandler;
import turtle.player.dirchooser.DirChooserConstants;
import turtle.player.model.Instance;
import turtle.player.model.Track;
import turtle.player.persistance.framework.db.ObservableDatabase;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.playlist.Playlist;
import turtle.player.playlist.playorder.PlayOrderRandom;
import turtle.player.playlist.playorder.PlayOrderSorted;
import turtle.player.playlist.playorder.PlayOrderStrategy;
import turtle.player.preferences.Key;
import turtle.player.preferences.Keys;
import turtle.player.preferences.PreferencesObserver;
import turtle.player.util.Shorty;
import turtle.player.view.AlbumArtView;
import turtle.player.view.FileChooser;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Player extends ListActivity
{

	public static final String DIR_CHOOSER_ACTION = "turtle.player.DIR_CHOOSER";
	public static final int DIR_CHOOSER_REQUEST = 0;

	private enum Slides
	{
		SETTINGS,
		PLAYLIST,
		NOW_PLAYING
	}

	// ========================================= //
	// 	Attributes
	// ========================================= //

	private TurtlePlayer tp;
	private FileChooser fileChooser;

	// Header Bar
	private ImageView list;
	private ImageView logo;
	private ImageView settings;

	// Slides
	private RelativeLayout nowPlayingSlide;
	private RelativeLayout playlistSlide;
	private RelativeLayout settingsSlide;

	// NowPlaying Slide
	private ImageView backButton;
	private ImageView playButton;
	private ImageView nextButton;
	private ImageView shuffleButton;

	// Settings Slide
	CheckBox shuffleCheckBox;
	ImageView rescan;
	ProgressBar rescanProgressBar;
	TextView mediaDir;
	ImageView chooseMediaDir;

	private TextView duration;
	private TextView currTrackPosition;

	// Progress Bar Attributes
	private Runnable progressBarRunnable;
	private SeekBar progressBar;

	private PlayOrderStrategy standartPlayOrderStrategy; //default interactive next/prev strategy
	private PlayOrderStrategy shufflePlayOrderStrategy;
	private PlayOrderStrategy playOrderStrategy; //strategy after tracks is over, normally one of the above

	private Slides currSlide = Slides.NOW_PLAYING;

	private Set<PhoneStateListener> phoneStateListeners = new HashSet<PhoneStateListener>();

	// ========================================= //
	// 	OnCreate & OnDestroy
	// ========================================= //

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		SetupApplication();

		lookupViewElements();

		new AlbumArtView(this, tp.player, standartPlayOrderStrategy, tp.playlist);

		SetupObservers();
		SetupButtons();
		SetupButtonListeners();
		SetupSlides();
		SetupList();
		SetupTelephoneChecker();

		resetLastTrack();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		tp.playlist.preferences.set(Keys.EXIT_PLAY_TIME, tp.player.getCurrentMillis());
		tp.player.release();

		TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		for(PhoneStateListener phoneStateListener : phoneStateListeners)
		{
			mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
	}

	private void lookupViewElements(){

		progressBar = (SeekBar) findViewById(R.id.progressBar);

		list = (ImageView) findViewById(R.id.listButton);
		logo = (ImageView) findViewById(R.id.logoButton);
		settings = (ImageView) findViewById(R.id.settingsButton);

		backButton = (ImageView) findViewById(R.id.backButton);
		playButton = (ImageView) findViewById(R.id.playButton);
		nextButton = (ImageView) findViewById(R.id.nextButton);

		shuffleButton = (ImageView) findViewById(R.id.shuffleButton);
		shuffleCheckBox = (CheckBox) findViewById(R.id.shuffleCheckBox);

		rescan = (ImageView) findViewById(R.id.rescan);
		mediaDir = (TextView) findViewById(R.id.mediaDir);
		rescanProgressBar = (ProgressBar) findViewById(R.id.rescanProgressBar);
		chooseMediaDir = (ImageView) findViewById(R.id.chooseMediaDir);
	}

	@Override
	public void onBackPressed() {
		if(!Slides.NOW_PLAYING.equals(currSlide))
		{
			SwitchToNowPlayingSlide();
		}
		else
		{
			super.onBackPressed();
		}
	}

	// ========================================= //
	// 	Setup Application - Part of Init()
	// ========================================= //

	private void SetupApplication()
	{
		tp = (TurtlePlayer) getApplication();
		tp.db = new TurtleDatabase(tp.getApplicationContext());
		tp.playlist = new Playlist(tp.getApplicationContext(), tp.db);
		fileChooser = new FileChooser(FileChooser.Mode.Track, tp.db, this);

		standartPlayOrderStrategy = new PlayOrderSorted(tp.db, tp.playlist);
		shufflePlayOrderStrategy = new PlayOrderRandom(tp.db, tp.playlist);
		playOrderStrategy = tp.playlist.preferences.get(Keys.SHUFFLE) ?
				  shufflePlayOrderStrategy : standartPlayOrderStrategy;
		
		this.registerReceiver(new BroadcastsHandler(), new IntentFilter(Intent.ACTION_HEADSET_PLUG));
	}

	// ========================================= //
	// 	Setup Buttons - Part of Init()
	// ========================================= //

	private void SetupButtons()
	{
		shuffleButton.setImageDrawable(
				  tp.playlist.preferences.get(Keys.SHUFFLE) ?
							 getResources().getDrawable(R.drawable.dice48_active) :
							 getResources().getDrawable(R.drawable.dice48)
		);

		shuffleCheckBox.setChecked(tp.playlist.preferences.get(Keys.SHUFFLE));

		mediaDir.setText(tp.playlist.preferences.getExitstingMediaPath().toString());

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
				} else
				{
					Toast.makeText(getApplicationContext(), "Empty Playlist", Toast.LENGTH_SHORT).show();
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
				tp.player.play(tp.playlist.getPrevious(standartPlayOrderStrategy, tp.player.getCurrTrack()));
			}
		});

		playButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				tp.player.toggle();
			}
		});

		nextButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				tp.player.play(tp.playlist.getNext(standartPlayOrderStrategy, tp.player.getCurrTrack()));
			}
		});

		shuffleButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				tp.player.play(tp.playlist.getNext(shufflePlayOrderStrategy, tp.player.getCurrTrack()));
			}
		});
		shuffleButton.setOnLongClickListener(new View.OnLongClickListener()
		{
			public boolean onLongClick(View v)
			{
				tp.playlist.preferences.set(Keys.SHUFFLE, !tp.playlist.preferences.get(Keys.SHUFFLE));
				return true;
			}
		});

		shuffleCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView,
												  boolean isChecked)
			{
				tp.playlist.preferences.set(Keys.SHUFFLE, isChecked);
			}
		});

		// [settings.xml]
		rescan.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				rescan();
			}
		});

		chooseMediaDir.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent dirChooserIntent = new Intent(DIR_CHOOSER_ACTION);
				dirChooserIntent.putExtra(DirChooserConstants.ACTIVITY_PARAM_KEY_DIR_CHOOSER_INITIAL_DIR,
						  tp.playlist.preferences.getExitstingMediaPath().toString());
				startActivityForResult(dirChooserIntent, DIR_CHOOSER_REQUEST);
			}
		});
	}

	private void SetupObservers()
	{

		//Rescan Progress Bar
		tp.playlist.addObserver(new Playlist.PlaylistTrackChangeObserver()
		{
			public void startUpdatePlaylist()
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						rescanProgressBar.setVisibility(View.VISIBLE);
						rescanProgressBar.setIndeterminate(true);
						rescan.setVisibility(View.INVISIBLE);
					}
				});
			}

			public void startRescan(File mediaPath)
			{

				final int[] numberOfTracks = new int[]{0};
				try
				{
					Process p = Runtime.getRuntime().exec(new String[]{"ls", "-R", mediaPath.toString()});
					BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line = "";
					while (line != null)
					{
						line = br.readLine();
						if (line != null && FileFilters.PLAYABLE_FILES_FILTER.accept(null, line))
						{
							numberOfTracks[0] = numberOfTracks[0] + 1;
						}
					}
				} catch (IOException e)
				{
					//Empty
				}

				//start sync anim
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						rescanProgressBar.setIndeterminate(false);
						rescanProgressBar.setProgress(0);
						rescanProgressBar.setMax(numberOfTracks[0]);
					}
				});
			}

			public void trackAdded()
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						rescanProgressBar.setProgress(rescanProgressBar.getProgress() + 1);
					}
				});
			}

			public void endRescan()
			{
				//start sync anim
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						rescanProgressBar.setIndeterminate(true);
					}
				});
			}

			public void endUpdatePlaylist()
			{
				//stop sync anim
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						rescanProgressBar.setVisibility(View.GONE);
						rescan.setVisibility(View.VISIBLE);
						if (!tp.playlist.IsEmpty())
						{
							SwitchToPlaylistSlide();
						} else
						{
							Toast.makeText(getApplicationContext(), "No MP3s Found on SD Card", Toast.LENGTH_LONG).show();
						}
					}
				});
			}
		});

		tp.playlist.preferences.addObserver(new PreferencesObserver()
		{
			public void changed(Key key)
			{
				if (key.equals(Keys.SHUFFLE))
				{
					final boolean shuffle = tp.playlist.preferences.get(Keys.SHUFFLE);
					playOrderStrategy = shuffle ? shufflePlayOrderStrategy : standartPlayOrderStrategy;
					//Update UI states
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							shuffleButton.setImageDrawable(getResources().getDrawable(
									  shuffle ? R.drawable.dice48_active : R.drawable.dice48));
							shuffleCheckBox.setChecked(shuffle);
						}
					});
				}
			}
		});

		tp.player.addObserver(new turtle.player.player.Player.PlayerObserver()
		{
			public void trackChanged(Track track)
			{
				progressBar.setMax((int)track.GetLength());
				duration.setText(ConvertToMinutes((int)track.GetLength()));
				tp.playlist.preferences.set(Keys.LAST_TRACK_PLAYED, track.GetSrc());
			}

			public void started()
			{
				playButton.setImageDrawable(getResources().getDrawable(R.drawable.pause96));
			}

			public void stopped()
			{
				playButton.setImageDrawable(getResources().getDrawable(R.drawable.play96));
			}
		});

		progressBarRunnable = new Runnable()
		{
			public void run()
			{
				progressBar.setProgress(tp.player.getCurrentMillis());
				currTrackPosition.setText(ConvertToMinutes(tp.player.getCurrentMillis()));
				progressBar.postDelayed(this, 1000);
			}
		};
		progressBar.postDelayed(progressBarRunnable, 1000);

		progressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				tp.player.goToMillis(seekBar.getProgress());
				progressBar.postDelayed(progressBarRunnable, 1000);
			}

			public void onStartTrackingTouch(SeekBar seekBar)
			{
				progressBar.removeCallbacks(progressBarRunnable);
			}

			public void onProgressChanged(SeekBar seekBar,
													int progress,
													boolean fromUser)
			{
				// Needed, Although Blank
			}
		});

		tp.player.setOnCompletionListener(new OnCompletionListener()
		{
			public void onCompletion(MediaPlayer mplayer)
			{
				tp.player.play(tp.playlist.getNext(playOrderStrategy, tp.player.getCurrTrack()));
			}
		});

		tp.db.addObserver(new ObservableDatabase.DbObserver()
		{
			public void updated(Instance instance)
			{
				//doNothing
			}

			public void cleared()
			{
				Toast.makeText(getApplicationContext(), getString(R.string.toastRescan), Toast.LENGTH_LONG).show();
				tp.playlist.UpdateList();
			}
		});
	}


	// ========================================= //
	// 	Setup Slides - Part of Init()
	// ========================================= //

	private void SetupSlides()
	{
		nowPlayingSlide = (RelativeLayout) findViewById(R.id.now_playing_slide);
		playlistSlide = (RelativeLayout) findViewById(R.id.playlist_slide);
		settingsSlide = (RelativeLayout) findViewById(R.id.settings_slide);

		SwitchToNowPlayingSlide();
	}

	private void SwitchToNowPlayingSlide()
	{
		duration = (TextView) findViewById(R.id.trackDuration);
		currTrackPosition = (TextView) findViewById(R.id.trackCurrPostion);

		ResetHeaderButtons();
		logo.setImageDrawable(getResources().getDrawable(R.drawable.logo128_active));
		currSlide = Slides.NOW_PLAYING;

		nowPlayingSlide.setVisibility(LinearLayout.VISIBLE);
		playlistSlide.setVisibility(LinearLayout.INVISIBLE);
		settingsSlide.setVisibility(LinearLayout.INVISIBLE);
	}

	private void SwitchToPlaylistSlide()
	{
		ResetHeaderButtons();

		list.setImageDrawable(getResources().getDrawable(R.drawable.list64_active));
		currSlide = Slides.PLAYLIST;

		nowPlayingSlide.setVisibility(LinearLayout.INVISIBLE);
		playlistSlide.setVisibility(LinearLayout.VISIBLE);
		settingsSlide.setVisibility(LinearLayout.INVISIBLE);
	}

	private void SwitchToSettingsSlide()
	{
		ResetHeaderButtons();

		settings.setImageDrawable(getResources().getDrawable(R.drawable.settings48_active));
		currSlide = Slides.SETTINGS;

		nowPlayingSlide.setVisibility(LinearLayout.INVISIBLE);
		playlistSlide.setVisibility(LinearLayout.INVISIBLE);
		settingsSlide.setVisibility(LinearLayout.VISIBLE);
	}


	private void resetLastTrack()
	{
		String lastTrack = tp.playlist.preferences.get(Keys.LAST_TRACK_PLAYED);

		Track restoredTrack = null;
		if(!Shorty.isVoid(lastTrack))
		{
			restoredTrack = tp.playlist.getTrack(lastTrack);
		}
		if(restoredTrack == null){
			tp.player.change(tp.playlist.getNext(playOrderStrategy, null));
		}
		else
		{
			tp.player.change(restoredTrack);
			tp.player.goToMillis(tp.playlist.preferences.get(Keys.EXIT_PLAY_TIME));
		}

	}

	// ========================================= //
	// 	Setup Progress Bar - Part of Init()
	// ========================================= //

	private void SetupList()
	{
		if (tp.playlist.IsEmpty())
		{
			tp.playlist.UpdateList();
		}
	}


	// ========================================= //
	// 	Setup Telephony Service - Part of Init()
	// ========================================= //

	private void SetupTelephoneChecker()
	{
		TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		PhoneStateListener phoneStateListener = new PhoneStateHandler(tp.player);
		mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		phoneStateListeners.add(phoneStateListener);
	}

	@Override
	protected void onListItemClick(ListView l,
											 View v,
											 int position,
											 long id)
	{
		Track trackSelected = fileChooser.choose((Instance) l.getItemAtPosition(position));
		if (trackSelected != null)
		{
			tp.player.play(trackSelected);
			SwitchToNowPlayingSlide();
		}
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


	// ========================================= //
	// 	Get Duration String
	// ========================================= //

	private String ConvertToMinutes(int time)
	{
		String duration;

		int seconds = (int) (time / 1000) % 60;
		int minutes = (int) ((time / (1000 * 60)) % 60);

		if (seconds < 10)
		{
			duration = Integer.toString(minutes) + ":0" + Integer.toString(seconds);
		} else
		{
			duration = Integer.toString(minutes) + ":" + Integer.toString(seconds);
		}

		return duration;
	}

	/**
	 * Async rescan, returns immediately, use {@link turtle.player.playlist.Playlist.PlaylistObserver} to receive changes
	 */
	protected void rescan()
	{
		tp.playlist.DatabaseClear();
	}


	@Override
	protected void onActivityResult(int requestCode,
											  int resultCode,
											  Intent data)
	{
		if (requestCode == DIR_CHOOSER_REQUEST)
		{
			if (resultCode == RESULT_OK)
			{
				tp.playlist.preferences.set(Keys.MEDIA_DIR,
						  data.getStringExtra(DirChooserConstants.ACTIVITY_RETURN_KEY_DIR_CHOOSER_CHOOSED_DIR));
				mediaDir.setText(tp.playlist.preferences.getExitstingMediaPath().toString());
				rescan();
			}
		} else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	
	
	
	public class BroadcastsHandler extends BroadcastReceiver {
		private boolean headsetConnected = false;
		public void onReceive(Context context, Intent intent)
		{
			if (intent.hasExtra("state"))
			{
				if (headsetConnected && intent.getIntExtra("state", 0) == 0)
				{
					headsetConnected = false;
					Toast.makeText(context, "Headphones UnPlugged", Toast.LENGTH_LONG).show();
					tp.player.pause();
				}
				else if (!headsetConnected && intent.getIntExtra("state", 0) == 1)
				{
				    headsetConnected = true;
					tp.player.play();
				}
			}
		}
	}
	
}

