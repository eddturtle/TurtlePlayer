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
import turtle.player.preferences.Key;
import turtle.player.preferences.Keys;
import turtle.player.preferences.PreferencesObserver;
import turtle.player.view.AlbumArtView;
import turtle.player.view.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Player extends ListActivity
{

	public static final String DIR_CHOOSER_ACTION = "turtle.player.DIR_CHOOSER";
	public static final int DIR_CHOOSER_REQUEST = 0;

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
	private TextView currTrackPosition;

	// Progress Bar Attributes
	private Runnable progressBarRunnable;
	private SeekBar progressBar;

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
		new AlbumArtView(this, tp.player, tp.playlist);
		SetupObservers();
		SetupButtons();
		SetupButtonListeners();
		SetupSlides();
		SetupList();
		SetupTelephoneChecker();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		tp.player.release();
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

		repeatButton = (ImageView) findViewById(R.id.repeatButton);

		shuffleCheckBox = (CheckBox) findViewById(R.id.shuffleCheckBox);
		repeatCheckBox = (CheckBox) findViewById(R.id.repeatCheckBox);

		rescan = (ImageView) findViewById(R.id.rescan);
		mediaDir = (TextView) findViewById(R.id.mediaDir);
		rescanProgressBar = (ProgressBar) findViewById(R.id.rescanProgressBar);
		chooseMediaDir = (ImageView) findViewById(R.id.chooseMediaDir);
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

	// ========================================= //
	// 	Setup Buttons - Part of Init()
	// ========================================= //

	private void SetupButtons()
	{
		shuffleButton.setImageDrawable(
				  tp.playlist.preferences.setShuffle() ?
							 getResources().getDrawable(R.drawable.shuffle48_active) :
							 getResources().getDrawable(R.drawable.shuffle48)
		);

		shuffleCheckBox.setChecked(tp.playlist.preferences.setShuffle());
		repeatCheckBox.setChecked(tp.playlist.preferences.setRepeat());

		mediaDir.setText(tp.playlist.preferences.setMediaPath().toString());

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
					tp.player.play(tp.playlist.getPrevious(tp.player.getCurrTrack()).getTrack());
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
					tp.player.toggle();
				}
				else
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
					tp.player.play(tp.playlist.getNext(tp.player.getCurrTrack()).getTrack());
				}
			}
		});

		shuffleButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				tp.playlist.preferences.setShuffle(!tp.playlist.preferences.setShuffle());
			}
		});

		shuffleCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView,
												  boolean isChecked)
			{
				tp.playlist.preferences.setShuffle(isChecked);
			}
		});

		repeatButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				tp.playlist.preferences.setRepeat(!tp.playlist.preferences.setRepeat());
			}
		});

		repeatCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView,
												  boolean isChecked)
			{
				tp.playlist.preferences.setRepeat(isChecked);
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
						  tp.playlist.preferences.setMediaPath().toString());
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
				if (key.equals(Keys.REPEAT))
				{
					//Update UI states
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							boolean repeat = tp.playlist.preferences.setRepeat();
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
							boolean shuffle = tp.playlist.preferences.setShuffle();
							shuffleButton.setImageDrawable(getResources().getDrawable(
									  shuffle ? R.drawable.shuffle48_active : R.drawable.shuffle48));
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
			}

			public void started()
			{
				playButton.setImageDrawable(getResources().getDrawable(R.drawable.pause64));
			}

			public void stopped()
			{
				playButton.setImageDrawable(getResources().getDrawable(R.drawable.play64));
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
				tp.player.play(tp.playlist.getNext(tp.player.getCurrTrack()).getTrack());
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
	// 	Setup Telephony Service - Part of Init()
	// ========================================= //

	private void SetupTelephoneChecker()
	{
		TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		mgr.listen(new PhoneStateHandler(tp.player), PhoneStateListener.LISTEN_CALL_STATE);
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
				tp.playlist.preferences.setMediaPath(
						  data.getStringExtra(DirChooserConstants.ACTIVITY_RETURN_KEY_DIR_CHOOSER_CHOOSED_DIR));
				mediaDir.setText(tp.playlist.preferences.setMediaPath().toString());
				rescan();
			}
		} else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}