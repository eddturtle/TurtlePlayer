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
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import turtle.player.common.MatchFilterVisitor;
import turtle.player.controller.BroadcastsHandler;
import turtle.player.controller.PhoneStateHandler;
import turtle.player.dirchooser.DirChooserConstants;
import turtle.player.model.Instance;
import turtle.player.model.Track;
import turtle.player.persistance.framework.db.ObservableDatabase;
import turtle.player.persistance.framework.filter.Filter;
import turtle.player.persistance.turtle.db.TurtleDatabase;
import turtle.player.persistance.turtle.db.structure.Tables;
import turtle.player.player.ObservableOutput;
import turtle.player.player.Output;
import turtle.player.player.OutputCommand;
import turtle.player.player.OutputUsingOnClickListener;
import turtle.player.playlist.Playlist;
import turtle.player.playlist.playorder.PlayOrderRandom;
import turtle.player.playlist.playorder.PlayOrderSorted;
import turtle.player.playlist.playorder.PlayOrderStrategy;
import turtle.player.preferences.AbstractKey;
import turtle.player.preferences.Keys;
import turtle.player.preferences.Preferences;
import turtle.player.preferences.PreferencesObserver;
import turtle.player.util.Shorty;
import turtle.player.view.AlbumArtView;
import turtle.player.view.FileChooser;

import java.util.HashSet;
import java.util.Set;

public class Player extends ListActivity
{

	public static final String DIR_CHOOSER_ACTION = "turtle.player.DIR_CHOOSER";
	public static final int DIR_CHOOSER_REQUEST = 0;

	public enum Slides
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
	LinearLayout rescanProgressBarState;
	TextView rescanProgressBarIndicatorTrack;
	TextView rescanProgressBarIndicatorState;
	TextView rescanProgressBarIndicatorAll;
	ImageView rescanTogglePause;

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
	private Set<BroadcastReceiver> broadcastReceivers = new HashSet<BroadcastReceiver>();

	// ========================================= //
	// 	OnCreate & OnDestroy
	// ========================================= //

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Log.i(Preferences.TAG, "Player.onCreate() called");

		setContentView(R.layout.main);

		SetupApplication();

		lookupViewElements();

		new AlbumArtView(this, tp, standartPlayOrderStrategy);

		SetupObservers();
		SetupButtons();
		SetupButtonListeners();
		SetupSlides();
		SetupList();
		SetupPhoneHandlers();

		resetLastTrack();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		Log.i(Preferences.TAG, "Player.onDestory() called");

		tp.playlist.pauseFsScan();

		tp.player.connectPlayer(new OutputCommand()
		{
			public void connected(Output output)
			{
				tp.playlist.preferences.set(Keys.EXIT_PLAY_TIME, output.getCurrentMillis());
				tp.playlist.preferences.set(Keys.FILTERS, tp.playlist.getFilter());
			}
		});

		TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		for(PhoneStateListener phoneStateListener : phoneStateListeners)
		{
			mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		}

		for(BroadcastReceiver broadcastReceiver : broadcastReceivers){
			unregisterReceiver(broadcastReceiver);
		}

		tp.player.releasePlayer();
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
		rescanProgressBarState = (LinearLayout) findViewById(R.id.rescanState);
		rescanProgressBarIndicatorAll = (TextView) findViewById(R.id.rescanProgressBarIndicatorAll);
		rescanProgressBarIndicatorState = (TextView) findViewById(R.id.rescanProgressBarIndicatorState);
		rescanProgressBarIndicatorTrack = (TextView) findViewById(R.id.rescanProgressBarIndicatorTrack);
		rescanTogglePause = (ImageView) findViewById(R.id.togglePause);
		chooseMediaDir = (ImageView) findViewById(R.id.chooseMediaDir);
	}

	@Override
	public void onBackPressed() {
		if (Slides.PLAYLIST.equals(currSlide))
		{
			boolean switchToPlayer = fileChooser.back();
			if(switchToPlayer)
			{
				SwitchToNowPlayingSlide();
			}
		}
		else if(!Slides.NOW_PLAYING.equals(currSlide))
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
		fileChooser = new FileChooser(FileChooser.Mode.Genre, tp.db, this)
		{
			@Override
			protected void filterChoosen(Filter<? super Tables.Tracks> filter)
			{
				tp.playlist.clearFilters();
				tp.playlist.addFilter(filter);
				SwitchToNowPlayingSlide();
			}
		};

		standartPlayOrderStrategy = new PlayOrderSorted(tp.db, tp.playlist);
		shufflePlayOrderStrategy = new PlayOrderRandom(tp.db, tp.playlist);
		playOrderStrategy = tp.playlist.preferences.get(Keys.SHUFFLE) ?
				  shufflePlayOrderStrategy : standartPlayOrderStrategy;
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




		backButton.setOnClickListener(new OutputUsingOnClickListener(tp.player)
		{
			@Override
			public void onClick(View v,Output output)
			{
				output.play(tp.playlist.getPrevious(standartPlayOrderStrategy, output.getCurrTrack()));
			}
		});

		playButton.setOnClickListener(new OutputUsingOnClickListener(tp.player)
		{
			@Override
			public void onClick(View v,Output output)
			{
				output.toggle();
			}
		});

		nextButton.setOnClickListener(new OutputUsingOnClickListener(tp.player)
		{
			@Override
			public void onClick(View v,Output output)
			{
				output.play(tp.playlist.getNext(standartPlayOrderStrategy, output.getCurrTrack()));
			}
		});

		shuffleButton.setOnClickListener(new OutputUsingOnClickListener(tp.player)
		{
			@Override
			public void onClick(View v,Output output)
			{
				output.play(tp.playlist.getNext(shufflePlayOrderStrategy, output.getCurrTrack()));
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
				if(tp.playlist.isFsScanNotStarted())
				{
					rescan();
				}
				else{
					tp.playlist.stopFsScan();
				}
			}
		});

		rescanTogglePause.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				tp.playlist.toggleFsScanPause();
			}
		});

		chooseMediaDir.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent dirChooserIntent = new Intent(DIR_CHOOSER_ACTION);
				dirChooserIntent.putExtra(DirChooserConstants.ACTIVITY_PARAM_KEY_DIR_CHOOSER_INITIAL_DIR,
						  tp.playlist.preferences.getExitstingMediaPath().toString());
				Player.this.startActivityForResult(dirChooserIntent, DIR_CHOOSER_REQUEST);
			}
		});
	}

	private void SetupObservers()
	{

		final Handler trackAddedhandler = new Handler(Looper.getMainLooper());

		//Rescan Progress Bar
		tp.playlist.addObserver(new Playlist.PlaylistTrackChangeObserver()
		{
			public void startUpdatePlaylist()
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						rescanProgressBar.setIndeterminate(true);
						rescan.setImageDrawable(getResources().getDrawable(R.drawable.fs_scan_stop48));
						rescanProgressBar.setVisibility(View.VISIBLE);
					}
				});
			}

			public void startRescan(final int toProcess)
			{
				//start sync anim
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						rescanProgressBar.setIndeterminate(false);
						rescanProgressBar.setMax(toProcess);
						rescanProgressBar.setProgress(0);

						rescanTogglePause.setImageDrawable(getResources().getDrawable(R.drawable.fs_scan_pause48));
						rescanProgressBarIndicatorState.setText("");
						rescanProgressBarIndicatorAll.setText(String.valueOf(rescanProgressBar.getMax()));
						rescanProgressBarIndicatorTrack.setText("");
						rescanProgressBarState.setVisibility(View.VISIBLE);
					}
				});
			}

			public void unpauseRescanInitializing()
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						rescanProgressBar.setIndeterminate(true);
						rescanTogglePause.setImageDrawable(getResources().getDrawable(R.drawable.fs_scan_pause48));
						rescanProgressBarState.setVisibility(View.VISIBLE);
					}
				});
			}

			public void unpauseRescan(final int alreadyProcessed,
											  final int toProcess)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						rescanProgressBar.setIndeterminate(false);
						rescanProgressBar.setMax(alreadyProcessed + toProcess);
						rescanProgressBar.setProgress(alreadyProcessed);
						rescanProgressBarState.setVisibility(View.VISIBLE);
						rescanProgressBarIndicatorAll.setText(String.valueOf(rescanProgressBar.getMax()));
						rescanTogglePause.setImageDrawable(getResources().getDrawable(R.drawable.fs_scan_pause48));
					}
				});
			}

			public void trackAdded(final String filePath, final int allreadyProcessed)
			{

				if(!trackAddedhandler.hasMessages(0)){
					trackAddedhandler.postDelayed(new Runnable()
					{
						public void run()
						{
							rescanProgressBar.setProgress(allreadyProcessed);
							rescanProgressBarIndicatorState.setText(String.valueOf(rescanProgressBar.getProgress()));
							rescanProgressBarIndicatorTrack.setText(filePath);
						}
					}, 250);
				}
			}

			public void pauseRescan()
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						rescanTogglePause.setImageDrawable(getResources().getDrawable(R.drawable.fs_scan_unpause48));
					}
				});
			}

			public void endRescan()
			{
				//stop sync anim
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						rescanProgressBar.setVisibility(View.GONE);
						rescanProgressBarState.setVisibility(View.GONE);
						rescan.setImageDrawable(getResources().getDrawable(R.drawable.fs_scan_start48));
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

			public String getId()
			{
				return "FsScanProgressUpdater";
			}
		});

		tp.playlist.addObserver(new Playlist.PlaylistFilterChangeObserver()
		{
			public void filterAdded(final Filter<? super Tables.Tracks> filter)
			{
				tp.player.connectPlayer(new OutputCommand()
				{
					public void connected(Output output)
					{
						Track currTrack = output.getCurrTrack();
						if(currTrack == null || !filter.accept(new MatchFilterVisitor<Track, Tables.Tracks>(currTrack)))
						{
							output.change(tp.playlist.getNext(playOrderStrategy, null));
						}
					}
				});
			}

			public void filterRemoved(Filter<? super Tables.Tracks> filter)
			{
				//do nothing
			}

			public String getId()
			{
				return "currTrackMatchFilterVerifier";
			}
		});
		tp.playlist.notifyInitialState();

		tp.playlist.preferences.addObserver(new PreferencesObserver()
		{
			public void changed(AbstractKey<?, ?> key)
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

			public String getId()
			{
				return "SettingsButtonUpdater";
			}
		});

		tp.player.addObserver(new ObservableOutput.PlayerObserver()
		{
			public void trackChanged(Track track, int lengthInMillis)
			{
				progressBar.setMax(lengthInMillis);
				duration.setText(ConvertToMinutes(lengthInMillis));
				tp.playlist.preferences.set(Keys.LAST_TRACK_PLAYED, track.getFullPath());
			}

			public void started()
			{
				playButton.setImageDrawable(getResources().getDrawable(R.drawable.pause96));
			}

			public void stopped()
			{
				playButton.setImageDrawable(getResources().getDrawable(R.drawable.play96));
			}

			public String getId()
			{
				return "PlayViewComponentsUpdater";
			}
		});

		progressBarRunnable = new Runnable()
		{
			public void run()
			{
				tp.player.connectPlayer(new OutputCommand()
				{
					public void connected(Output output)
					{
						progressBar.setProgress(output.getCurrentMillis());
						currTrackPosition.setText(ConvertToMinutes(output.getCurrentMillis()));
						progressBar.postDelayed(progressBarRunnable, 1000);
					}
				});
			}
		};
		progressBar.postDelayed(progressBarRunnable, 1000);

		progressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			public void onStopTrackingTouch(final SeekBar seekBar)
			{
				tp.player.connectPlayer(new OutputCommand()
				{
					public void connected(Output output)
					{
						output.goToMillis(seekBar.getProgress());
						progressBar.postDelayed(progressBarRunnable, 1000);
					}
				});
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

		tp.player.connectPlayer(new OutputCommand()
		{
			public void connected(final Output output)
			{
				output.setOnCompletionListener(new OnCompletionListener()
				{
					public void onCompletion(MediaPlayer mplayer)
					{
						output.play(tp.playlist.getNext(playOrderStrategy, output.getCurrTrack()));
					}
				});
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
				tp.playlist.startFsScan();
			}

			public String getId()
			{
				return "restartAfterDBClean";
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

		fileChooser.update();

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
		Set<Filter<? super Tables.Tracks>> filtersFromPref = tp.playlist.preferences.get(Keys.FILTERS);
		for(Filter<? super Tables.Tracks> filter : filtersFromPref)
		{
			tp.playlist.addFilter(filter);
		}

		tp.player.connectPlayer(new OutputCommand()
		{
			public void connected(Output output)
			{
				String lastTrack = tp.playlist.preferences.get(Keys.LAST_TRACK_PLAYED);

				Track restoredTrack = null;
				if(!Shorty.isVoid(lastTrack))
				{
					restoredTrack = tp.playlist.getTrack(lastTrack);
				}
				if(restoredTrack == null){
					output.change(tp.playlist.getNext(playOrderStrategy, null));
				}
				else
				{
					output.change(restoredTrack);
					output.goToMillis(tp.playlist.preferences.get(Keys.EXIT_PLAY_TIME));
				}
			}
		});

	}

	public Slides getCurrSlide()
	{
		return currSlide;
	}

	// ========================================= //
	// 	Setup Progress Bar - Part of Init()
	// ========================================= //

	private void SetupList()
	{
		if (tp.playlist.IsEmpty())
		{
			tp.playlist.startFsScan();
		}
	}


	// ========================================= //
	// 	Setup Telephony Service - Part of Init()
	// ========================================= //

	private void SetupPhoneHandlers()
	{
		TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		PhoneStateListener phoneStateListener = new PhoneStateHandler(tp.player);
		phoneStateListeners.add(phoneStateListener);
		mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

		BroadcastReceiver broadcastsHandler = new BroadcastsHandler(tp.player);
		broadcastReceivers.add(broadcastsHandler);
		registerReceiver(broadcastsHandler, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
	}

	@Override
	protected void onListItemClick(ListView l,
											 View v,
											 int position,
											 long id)
	{
		final Track trackSelected = fileChooser.choose((Instance) l.getItemAtPosition(position));
		if (trackSelected != null)
		{
			if(!tp.playlist.getCompressedFilter().accept(new MatchFilterVisitor<Track, Tables.Tracks>(trackSelected)))
			{
				tp.playlist.clearFilters();
			}
			tp.player.connectPlayer(new OutputCommand()
			{
				public void connected(Output output)
				{
					output.play(trackSelected);
				}
			});
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

}

