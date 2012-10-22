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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import turtle.player.common.filefilter.FileFilters;
import turtle.player.dirchooser.DirChooserConstants;
import turtle.player.model.Instance;
import turtle.player.model.Track;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.playlist.Playlist;
import turtle.player.preferences.Key;
import turtle.player.preferences.Keys;
import turtle.player.preferences.PreferencesObserver;
import turtle.player.presentation.InstanceFormatter;
import turtle.player.view.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Player extends ListActivity
{

	public static String DIR_CHOOSER_ACTION = "turtle.player.DIR_CHOOSER";
	public static int DIR_CHOOSER_REQUEST = 0;

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
	private ImageView repeatButton;

	// Settings Slide
	CheckBox shuffleCheckBox;
	CheckBox repeatCheckBox;
	ImageView rescan;
	ProgressBar rescanProgressBar;
	TextView mediaDir;
	ImageView chooseMediaDir;

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

		SetupObservers();
		SetupButtons();
		SetupButtonListeners();
		SetupSlides();
		SetupList();
		SetupProgressBar();
		SetupTelephoneChecker();

		//tp.playlist.DatabasePush();
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
	}


	// ========================================= //Pla
	// 	Setup Buttons - Part of Init()
	// ========================================= //

	private void SetupButtons()
	{
		// Header Buttons
		list = (ImageView) findViewById(R.id.listButton);
		logo = (ImageView) findViewById(R.id.logoButton);
		settings = (ImageView) findViewById(R.id.settingsButton);

		// Now_Playing Footer Buttons
		backButton = (ImageView) findViewById(R.id.backButton);
		playButton = (ImageView) findViewById(R.id.playButton);
		nextButton = (ImageView) findViewById(R.id.nextButton);

		shuffleButton = (ImageView) findViewById(R.id.shuffleButton);
		shuffleButton.setImageDrawable(
				  tp.playlist.preferences.GetShuffle() ?
							 getResources().getDrawable(R.drawable.shuffle48_active) :
							 getResources().getDrawable(R.drawable.shuffle48)
		);
		repeatButton = (ImageView) findViewById(R.id.repeatButton);

		// Settings Slide
		shuffleCheckBox = (CheckBox) findViewById(R.id.shuffleCheckBox);
		shuffleCheckBox.setChecked(tp.playlist.preferences.GetShuffle());

		repeatCheckBox = (CheckBox) findViewById(R.id.repeatCheckBox);
		repeatCheckBox.setChecked(tp.playlist.preferences.GetRepeat());

		rescan = (ImageView) findViewById(R.id.rescan);
		mediaDir = (TextView) findViewById(R.id.mediaDir);
		rescanProgressBar = (ProgressBar) findViewById(R.id.rescanProgressBar);
		chooseMediaDir = (ImageView) findViewById(R.id.chooseMediaDir);
		mediaDir.setText(tp.playlist.preferences.GetMediaPath().toString());

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
				if (!tp.playlist.IsEmpty())
				{
					Play(tp.playlist.getPrevious());
				} else
				{
					Toast.makeText(getApplicationContext(), "Empty Playlist", Toast.LENGTH_SHORT).show();
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
						} else
						{
							Pause();
						}
					} else
					{
						Play(tp.playlist.getNext());
					}
				} else
				{
					Toast.makeText(getApplicationContext(), "Empty Playlist", Toast.LENGTH_SHORT).show();
				}
			}
		});

		nextButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if (!tp.playlist.IsEmpty())
				{
					Track t = tp.playlist.getNext();
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
				tp.playlist.preferences.SetShuffle(!tp.playlist.preferences.GetShuffle());
			}
		});

		shuffleCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView,
												  boolean isChecked)
			{
				tp.playlist.preferences.SetShuffle(isChecked);
			}
		});

		repeatButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				tp.playlist.preferences.SetRepeat(!tp.playlist.preferences.GetRepeat());
			}
		});

		repeatCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView,
												  boolean isChecked)
			{
				tp.playlist.preferences.SetRepeat(isChecked);
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
						  tp.playlist.preferences.GetMediaPath().toString());
				startActivityForResult(dirChooserIntent, DIR_CHOOSER_REQUEST);
			}
		});
	}

	private void SetupObservers()
	{

		//Rescan Progress Bar
		tp.playlist.addObserver(new Playlist.PlaylistObserverAdapter()
		{

			@Override
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

			@Override
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

			@Override
			public void trackAdded(Track track)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						rescanProgressBar.setProgress(rescanProgressBar.getProgress() + 1);
					}
				});
			}

			@Override
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

			@Override
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
				if (key.equals(Keys.REPEAT))
				{
					//Update UI states
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							boolean repeat = tp.playlist.preferences.GetRepeat();
							repeatButton.setImageDrawable(getResources().getDrawable(
									  repeat ? R.drawable.repeat48_active : R.drawable.repeat48));
							repeatCheckBox.setChecked(repeat);
						}
					});
				}
				else if (key.equals(Keys.SHUFFLE))
				{
					//Update UI states
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							boolean shuffle = tp.playlist.preferences.GetShuffle();
							shuffleButton.setImageDrawable(getResources().getDrawable(
									  shuffle ? R.drawable.shuffle48_active : R.drawable.shuffle48));
							shuffleCheckBox.setChecked(shuffle);
						}
					});
				}
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
	// 	Setup Progress Bar - Part of Init()
	// ========================================= //

	private void SetupProgressBar()
	{
		progressBar = (SeekBar) findViewById(R.id.progressBar);
		progressBarRunnable = new Runnable()
		{
			public void run()
			{
				if (tp.isInitialised)
				{
					progressBar.setProgress(tp.mp.getCurrentPosition());
					duration.setText(ConvertToMinutes(tp.mp.getCurrentPosition()) + " / " + ConvertToMinutes(tp.mp.getDuration()));
				} else
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
		mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		phoneStateListener = new PhoneStateListener()
		{
			@Override
			public void onCallStateChanged(int state,
													 String incomingNumber)
			{
				if (state == TelephonyManager.CALL_STATE_RINGING)
				{
					if (tp.mp.isPlaying())
					{
						Pause();
					}
				} else if (state == TelephonyManager.CALL_STATE_IDLE)
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

	@Override
	protected void onListItemClick(ListView l,
											 View v,
											 int position,
											 long id)
	{
		Track trackSelected = fileChooser.choose((Instance) l.getItemAtPosition(position));
		if (trackSelected != null)
		{
			Play(trackSelected);
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

	private void Play(Track t)
	{
		if (t != null)
		{
			try
			{
				tp.mp.reset();
				tp.mp.setDataSource(t.GetSrc());

				tp.playlist.setCurrTrack(t);

				tp.mp.prepare();
				tp.mp.start();

				tp.playlist.stats.IncrementPlayCount();

				tp.isPaused = false;
				tp.isInitialised = true;

				TogglePlayButton();

				UpdateScreenInfo(t);
				UpdateProgressBar();

				tp.mp.setOnCompletionListener(new OnCompletionListener()
				{
					public void onCompletion(MediaPlayer mplayer)
					{
						Play(tp.playlist.getNext());
					}
				});
			} catch (IOException e)
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
		TextView title = (TextView) findViewById(R.id.trackTitle);
		TextView artist = (TextView) findViewById(R.id.trackArtist);

		title.setVisibility(View.VISIBLE);
		artist.setVisibility(View.VISIBLE);
		duration.setVisibility(View.VISIBLE);

		title.setText(t.accept(InstanceFormatter.SHORT_WITH_NUMBER));
		artist.setText(t.GetAlbum().accept(InstanceFormatter.SHORT));

		if (tp.isInitialised)
		{
			duration.setText(ConvertToMinutes(tp.mp.getCurrentPosition()) + " / " + ConvertToMinutes(tp.mp.getDuration()));
		} else
		{
			title.setText("Welcome to");
			artist.setText("Turtle Player");
			duration.setText("0:00");
		}

		ImageView iv = (ImageView) findViewById(R.id.albumArt);


		if (t.albumArt() != null)
		{
			Bitmap bmp = BitmapFactory.decodeFile(t.albumArt());
			iv.setImageBitmap(bmp);
		} else
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
		} else
		{
			progressBar.setMax(0);
		}

		handler.postDelayed(progressBarRunnable, 100);

		progressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				handler.removeCallbacks(progressBarRunnable);
				tp.mp.seekTo(seekBar.getProgress());
				UpdateProgressBar();
			}

			public void onStartTrackingTouch(SeekBar seekBar)
			{
				handler.removeCallbacks(progressBarRunnable);
			}

			public void onProgressChanged(SeekBar seekBar,
													int progress,
													boolean fromUser)
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
		int seconds = (int) (milliseconds / 1000) % 60;
		int minutes = (int) ((milliseconds / (1000 * 60)) % 60);

		if (seconds < 10)
		{
			duration = Integer.toString(minutes) + ":0" + Integer.toString(seconds);
		} else
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
		} else
		{
			playButton.setImageDrawable(getResources().getDrawable(R.drawable.pause64));
		}

	}

	/**
	 * Async rescan, returns immediately, use {@link turtle.player.playlist.Playlist.PlaylistObserver} to receive changes
	 */
	protected void rescan()
	{
		if (tp.isInitialised)
		{
			Stop();
		}
		tp.playlist.DatabaseClear(); // Don't Delete DB
		tp.playlist.UpdateList();
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
				tp.playlist.preferences.SetMediaPath(
						  data.getStringExtra(DirChooserConstants.ACTIVITY_RETURN_KEY_DIR_CHOOSER_CHOOSED_DIR));
				mediaDir.setText(tp.playlist.preferences.GetMediaPath().toString());
				rescan();
			}
		} else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}