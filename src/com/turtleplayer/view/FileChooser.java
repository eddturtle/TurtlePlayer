/**
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
 * More Information @ www.turtle-player.co.uk
 *
 * @author Simon Honegger (Hoene84)
 */

package com.turtleplayer.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import com.turtleplayer.Player;
import com.turtleplayer.R;
import com.turtleplayer.TurtlePlayer;
import com.turtleplayer.common.MatchFilterVisitor;
import com.turtleplayer.model.*;
import com.turtleplayer.persistance.framework.filter.*;
import com.turtleplayer.persistance.turtle.db.TurtleDatabase;
import com.turtleplayer.persistance.turtle.db.structure.Tables;
import com.turtleplayer.persistance.turtle.filter.*;
import com.turtleplayer.preferences.AbstractKey;
import com.turtleplayer.preferences.Keys;
import com.turtleplayer.preferences.Preferences;
import com.turtleplayer.preferences.PreferencesObserver;
import com.turtleplayer.presentation.InstanceFormatter;
import com.turtleplayer.util.DefaultAdapter;

import java.util.*;

public abstract class FileChooser implements TurtleDatabase.DbObserver
{

	public enum Mode
	{
		Album(R.id.albumButton, R.drawable.album48, R.drawable.album48_active),
		Artist(R.id.artistButton, R.drawable.artist48, R.drawable.artist48_active),
		Track(R.id.trackButton, R.drawable.track48, R.drawable.track48_active),
		Genre(R.id.genreButton, R.drawable.genre48, R.drawable.genre48_active),
		Dir(R.id.dirButton, R.drawable.dir48, R.drawable.dir48_active);

		private Mode(int buttonId,
			  int drawable,
			  int drawableActive)
		{
			this.drawable = drawable;
			this.drawableActive = drawableActive;
			this.buttonId = buttonId;
		}

		private final int drawable;
		private final int drawableActive;
		private final int buttonId;
	}

	private Mode currMode;
	private final TurtleDatabase database;
	private final Player listActivity;
	private final Preferences preferences;
	final DefaultAdapter<Instance> listAdapter;
	final FilterListAdapter filterListAdapter;

	ListView filterList = null;

	private Set<Filter<? super Tables.Tracks>> filters = new HashSet<Filter<? super Tables.Tracks>>();
	private Set<Filter<? super Tables.Tracks>> permanentFilters = new HashSet<Filter<? super Tables.Tracks>>();
	private Map<Mode, List<Filter<? super Tables.Tracks>>> filtersAddWithMode = new HashMap<Mode, List<Filter<? super Tables.Tracks>>>();

	public FileChooser(Mode currMode,
							 final TurtlePlayer tp,
							 Player listActivity)
	{
		for(Mode mode : Mode.values()){
			filtersAddWithMode.put(mode, new ArrayList<Filter<? super Tables.Tracks>>());
		}

		this.currMode = currMode;
		this.database = tp.db;
		this.preferences = tp.playlist.preferences;
		this.listActivity = listActivity;

		tp.playlist.preferences.addObserver(new PreferencesObserver()
		{
			public void changed(AbstractKey<?, ?> key)
			{
				if (key.equals(Keys.MEDIA_DIR))
				{
					final String mediaDir = tp.playlist.preferences.get(Keys.MEDIA_DIR);
					getFilter().accept(new TurtleFilterVisitor<Tables.Tracks, Void>()
					{
						public Void visit(DirFilter dirFilter)
						{
							if(!dirFilter.filtersInPath(mediaDir))
							{
								removeFilter(dirFilter);
								update();
							}
							return null;
						}

						public <T, Z> Void visit(FieldFilter<? super Tables.Tracks, Z, T> fieldFilter)
						{
							return null;
						}

						public Void visit(FilterSet<? super Tables.Tracks> filterSet)
						{
							return null;
						}

						public Void visit(NotFilter<? super Tables.Tracks> notFilter)
						{
							return null;
						}
					});
				}
			}

			public String getId()
			{
				return "OutdatedFilterUpdater";
			}
		});

		filterList = (ListView) listActivity.findViewById (R.id.filterlist);
		filterListAdapter = new FilterListAdapter(listActivity.getApplicationContext(), new ArrayList<Filter<? super Tables.Tracks>>(getFilters()))
		{
			@Override
			protected void removeFilter(final Filter<? super Tables.Tracks> filter)
			{
				FileChooser.this.removeFilter(filter);
				update();
			}

			@Override
			protected void chooseFilter(Filter<? super Tables.Tracks> filter)
			{
				filterChoosen(filter);
			}
		};

		filterList.setAdapter(filterListAdapter);

		listAdapter = new DefaultAdapter<Instance>(
				  listActivity.getApplicationContext(),
				  new ArrayList<Instance>(),
				  listActivity,
				  false,
				  InstanceFormatter.SHORT);

		listActivity.setListAdapter(listAdapter);

		change(currMode, null, false);

		init();
	}

	private void init()
	{
		database.addObserver(this);

		for (final Mode currMode : Mode.values())
		{
			getButton(currMode).setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					filters.clear();
					filterListAdapter.clear();
					for(Filter<? super Tables.Tracks> permanentFilter : permanentFilters)
					{
						filterListAdapter.add(permanentFilter);
					}
					List<Filter<? super Tables.Tracks>> filtersToRemove = new ArrayList<Filter<? super Tables.Tracks>>();
					for(Filter<? super Tables.Tracks> filterAddedWithMode : filtersAddWithMode.get(currMode))
					{
						if(!permanentFilters.contains(filterAddedWithMode))
						{
							filtersToRemove.add(filterAddedWithMode);
						}
					}

					filtersAddWithMode.get(currMode).removeAll(filtersToRemove);

					change(currMode, null, false);
				}
			});
		}
	}

	private Filter<Tables.Tracks> getFilter()
	{
		return new FilterSet<Tables.Tracks>(getFilters());
	}

	private Set<Filter<? super Tables.Tracks>> getFilters()
	{
		Set<Filter<? super Tables.Tracks>> allFilters = new HashSet<Filter<? super Tables.Tracks>>();
		allFilters.addAll(filters);
		allFilters.addAll(permanentFilters);
		return allFilters;
	}

	private DirFilter getDirFilter()
	{
		DirFilter deepestDirFilter = new DirFilter( Operator.EQ, preferences.get(Keys.MEDIA_DIR));
		for(Filter<? super Tables.Tracks> filter : getFilters())
		{
			final DirFilter finalDeepestDirFilter = deepestDirFilter;
			deepestDirFilter = filter.accept(new TurtleFilterVisitor<Tables.Tracks, DirFilter>()
			{
				public DirFilter visit(DirFilter dirFilter)
				{
					return finalDeepestDirFilter.getValue().contains(dirFilter.getValueWithoutWildcards()) ? finalDeepestDirFilter : new DirFilter(Operator.EQ, dirFilter.getValueWithoutWildcards());
				}

				public DirFilter visit(NotFilter<? super Tables.Tracks> notFilter)
				{
					return finalDeepestDirFilter;
				}

				public <T, Z> DirFilter visit(FieldFilter<? super Tables.Tracks, Z, T> fieldFilter)
				{
					return finalDeepestDirFilter;
				}

				public DirFilter visit(FilterSet<? super Tables.Tracks> filterSet)
				{
					return finalDeepestDirFilter;
				}
			});
		}
		return deepestDirFilter;
	}

	/**
	 * @param selection
	 * @return null if no track was selected, track if trak was selected
	 */
	public Track choose(Instance selection)
	{

		return selection.accept(new InstanceVisitor<Track>()
		{
			public Track visit(Track track)
			{
				return track;
			}

			public Track visit(SongDigest track)
			{
				Filter<Tables.SongsReadable> trackFilter = new FieldFilter<Tables.SongsReadable, Track, String>(Tables.SongsReadable.TITLE, Operator.EQ, track.getSongName());
				return database.getTracks(new FilterSet<Tables.Tracks>(getFilter(), trackFilter)).iterator().next();
			}

			public Track visit(Album album)
			{
				Filter<Tables.AlbumsReadable> filter = new FieldFilter<Tables.AlbumsReadable, Track, String>(Tables.AlbumsReadable.ALBUM, Operator.EQ, album.getAlbumId());
				change(Mode.Track, filter, false);
				return null;
			}

			public Track visit(GenreDigest genre)
			{
				Filter<Tables.GenresReadable> filter = new FieldFilter<Tables.GenresReadable, Track, String>(Tables.GenresReadable.GENRE, Operator.EQ, genre.getGenreId());
				change(Mode.Artist, filter, false);
				return null;
			}

			public Track visit(ArtistDigest artist)
			{
				Filter<Tables.ArtistsReadable> filter = new FieldFilter<Tables.ArtistsReadable, Track, String>(Tables.ArtistsReadable.ARTIST, Operator.EQ, artist.getArtistId());
				change(Mode.Album, filter, false);
				return null;
			}

			public Track visit(FSobject FSobject)
			{
				Filter<Tables.FsObjects> filter = new DirFilter(Operator.LIKE, FSobject.getFullPath() + "/%");
				change(Mode.Dir, filter, true);
				return null;
			}
		});
	}

	/**
	 * @param toMode
	 * @param filter - filter to add, can be null
	 */
	public void change(Mode toMode, final Filter<? super Tables.Tracks> filter, boolean permanant)
	{
		if(filter != null)
		{
			filtersAddWithMode.get(currMode).add(filter);
			addFilter(filter, permanant);
		}

		currMode = toMode;
		for (final Mode aMode : Mode.values())
		{
			final ImageView button = getButton(aMode);
			button.post(new Runnable()
			{
				public void run()
				{
					button.setImageResource(aMode.equals(currMode) ? aMode.drawableActive : aMode.drawable);
				}
			});
		}
		update();
	}

	public void removeFilter(final Filter<? super Tables.Tracks> filter)
	{
		filters.remove(filter);
		permanentFilters.remove(filter);
		filterListAdapter.remove(filter);
	}

	public void addFilter(final Filter<? super Tables.Tracks> filter, boolean permanant)
	{
		(permanant ? permanentFilters : filters).add(filter);
		filterList.post(new Runnable()
		{
			public void run()
			{
				filterListAdapter.add(filter);
			}
		});
	}

	public void update()
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				switch (currMode)
				{
					case Album:
						List<Instance> albums = new ArrayList<Instance>(database.getAlbums(getFilter()));
						albums.remove(AlbumDigest.NO_ALBUM);
						albums.addAll(database.getTracks(new FilterSet<Tables.Tracks>(getFilter(), new FieldFilter<Tables.Tracks, Album, String>(Tables.AlbumsReadable.ALBUM, Operator.EQ, ""))));
						listAdapter.replace(albums);
						break;
					case Artist:
						List<Instance> artists = new ArrayList<Instance>(database.getArtists(getFilter()));
						artists.remove(ArtistDigest.NO_ARTIST);
						artists.addAll(database.getTracks(new FilterSet<Tables.Tracks>(getFilter(), new FieldFilter<Tables.Tracks, Artist, String>(Tables.ArtistsReadable.ARTIST, Operator.EQ, ""))));
						listAdapter.replace(artists);
						break;
					case Genre:
						List<Instance> genres = new ArrayList<Instance>(database.getGenres(getFilter()));
						genres.remove(AlbumDigest.NO_ALBUM);
						genres.addAll(database.getTracks(new FilterSet<Tables.Tracks>(getFilter(), new FieldFilter<Tables.Tracks, Genre, String>(Tables.GenresReadable.GENRE, Operator.EQ, ""))));
						listAdapter.replace(genres);
						break;
					case Track:
						listAdapter.replace(database.getTracks(getFilter()));
						break;
					case Dir:
						List<Instance> instances = new ArrayList<Instance>(database.getDirList(getDirFilter()));
						instances.addAll(database.getTracks(getDirFilter()));
						listAdapter.replace(instances);
						break;
					default:
						throw new RuntimeException(currMode.name() + " not expexted here");
				}
			}
		}).start();
	}

	public void updated(final Instance instance)
	{
		if(!Player.Slides.PLAYLIST.equals(listActivity.getCurrSlide()))
		{
			return;
		}

		Instance instanceToAdd = instance.accept(new InstanceVisitor<Instance>()
		{
			public Instance visit(Track track)
			{
				if(getFilter().accept(new MatchFilterVisitor<Track, Tables.Tracks>(track)))
				{
					switch (currMode)
					{
						case Album:
							return track.GetAlbum() == AlbumDigest.NO_ALBUM ? track : track.GetAlbum();
						case Artist:
							return track.GetArtist() == ArtistDigest.NO_ARTIST ? track : track.GetArtist();
						case Genre:
							return track.GetGenre() == GenreDigest.NO_GENRE ? track : track.GetGenre();
						case Track:
							return track;
						case Dir:
							return getDirFilter().accept(new MatchFilterVisitor<Track, Tables.Tracks>(track)) ? track : null;
						default:
							throw new RuntimeException(currMode.name() + " not expexted here");
					}
				}
				return null;
			}

			public Instance visit(SongDigest track)
			{
				throw new RuntimeException("not supported yet");
			}

			public Instance visit(Album album)
			{
				throw new RuntimeException("not supported yet");
			}

			public Instance visit(GenreDigest genre)
			{
				throw new RuntimeException("not supported yet");
			}

			public Instance visit(ArtistDigest artist)
			{
				throw new RuntimeException("not supported yet");
			}

			public Instance visit(FSobject dir)
			{
				return getDirFilter().accept(new MatchFilterVisitor<FSobject, Tables.Dirs>(dir)) && Mode.Dir.equals(currMode) ? dir : null;
			}
		});

		if(instanceToAdd != null)
		{
			listAdapter.add(instanceToAdd);
		}
	}

	public boolean back(){
		Mode backMode;
		switch (currMode)
		{
			case Album:
				backMode = Mode.Artist;
				break;
			case Artist:
				backMode = Mode.Genre;
				break;
			case Genre:
				backMode = null;
				break;
			case Track:
				backMode = Mode.Album;
				break;
			case Dir:
				backMode = Mode.Dir;
				break;
			default:
				throw new RuntimeException(currMode.name() + " not expexted here");
		}
		final List<Filter<? super Tables.Tracks>> filtersAddedByBack = filtersAddWithMode.get(backMode);
		if(backMode == null || filtersAddedByBack.isEmpty())
		{
			return true;
		}
		else
		{
			final Filter<? super Tables.Tracks> filterAddedByBack = filtersAddedByBack.remove(filtersAddedByBack.size()-1);
			filterList.post(new Runnable()
			{
				public void run()
				{
					removeFilter(filterAddedByBack);
				}
			});
			change(backMode, null, false);
			return false;
		}
	}

	public void cleared()
	{
		listAdapter.clear();
	}

	public String getId()
	{
		return "FileChooserUpdater";
	}

	private ImageView getButton(Mode mode)
	{
		return (ImageView) listActivity.findViewById(mode.buttonId);
	}

	protected abstract void filterChoosen(Filter<? super Tables.Tracks> filter);
}
