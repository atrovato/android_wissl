package fr.trovato.wissl.android.activities.player;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import fr.trovato.wissl.android.R;
import fr.trovato.wissl.android.activities.LoginActivity;
import fr.trovato.wissl.android.adapters.AbstractAdapter;
import fr.trovato.wissl.android.listeners.OnRemoteResponseListener;
import fr.trovato.wissl.android.remote.Parameters;
import fr.trovato.wissl.android.remote.RemoteAction;
import fr.trovato.wissl.android.services.IPlayerServiceClient;
import fr.trovato.wissl.android.services.PlayerService;
import fr.trovato.wissl.android.services.PlayerService.PlayerBinder;
import fr.trovato.wissl.android.tasks.RemoteTask;
import fr.trovato.wissl.commons.data.Playlist;
import fr.trovato.wissl.commons.data.Song;

/**
 * Abstract class used for each activity to unify every processing.
 * 
 * @author Alexandre Trovato
 * 
 * @param <ENTITY>
 *            Entity to manage
 * @param <ADAPTER>
 *            Adapter used to display entities
 */
public abstract class AbstractPlayerListActivity<ENTITY, ADAPTER extends AbstractAdapter<ENTITY>>
		extends ListActivity implements OnRemoteResponseListener,
		OnItemClickListener, ServiceConnection, IPlayerServiceClient,
		OnErrorListener, OnPreparedListener, OnCompletionListener,
		OnClickListener, OnBufferingUpdateListener, OnSeekBarChangeListener {

	private final static String LOG_TAG = "MEDIA_CONTROLER";

	/** Wissl server URL */
	private String serverUrl;

	/** Background service playing music enable flag */
	private boolean playerServiceBound = false;

	/** Play button */
	private ImageButton playButton;
	/** Pause button */
	private ImageButton pauseButton;
	/** Previous button */
	private ImageButton previousButton;
	/** Next button */
	private ImageButton nextButton;
	/** Seek bar */
	private SeekBar seekBar;

	/** Activity settings */
	private SharedPreferences settings;

	/** Background remote task */
	private RemoteTask remoteTask;

	/** List of selected entities */
	private List<ENTITY> selectedItems;

	/** Add songs to playlist dialog */
	private ProgressDialog dialog;

	/** Playlists */
	private List<Playlist> playlists;

	/** Current Wissl entities list adapter */
	private ADAPTER listAdapter;

	private AlertDialog playlistDialog;

	private PlayerService playerService;

	private Song playingSong;

	/**
	 * Load settings and prepare player interface
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.enableHttpResponseCache();

		this.dialog = new ProgressDialog(this);
		this.dialog.setMessage("Loading...");
		this.dialog.setIndeterminate(true);
		this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.dialog.show();

		// Restore preferences
		this.settings = this.getSharedPreferences(Parameters.PREFS_NAME.name(),
				0);

		this.serverUrl = this.getSettings().getString(
				Parameters.SERVER_URL.name(), null);

		if (this.getSessionId() == null) {
			this.notLogged();
		}

		this.selectedItems = new ArrayList<ENTITY>();
		this.playlists = new ArrayList<Playlist>();

		this.setContentView(R.layout.wissl_list);

		this.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		this.getListView().setOnItemClickListener(this);

		this.listAdapter = this.buildAdapter();
		this.setListAdapter(this.listAdapter);

		ConnectivityManager connMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			this.loadEntities();
		} else {
			this.showErrorDialog(this.getString(R.string.no_connection));
		}

		this.initializePlayer();

	}

	public void onResume() {
		this.getListView().invalidate();
		super.onResume();
	}

	private void initializePlayer() {
		this.playButton = (ImageButton) this.findViewById(R.id.play);
		this.playButton.setOnClickListener(this);
		this.playButton.setEnabled(false);

		this.pauseButton = (ImageButton) this.findViewById(R.id.pause);
		this.pauseButton.setOnClickListener(this);
		this.pauseButton.setEnabled(false);
		this.pauseButton.setVisibility(View.GONE);

		this.nextButton = (ImageButton) this.findViewById(R.id.next);
		this.nextButton.setOnClickListener(this);
		this.nextButton.setEnabled(false);

		this.previousButton = (ImageButton) this.findViewById(R.id.previous);
		this.previousButton.setOnClickListener(this);
		this.previousButton.setEnabled(false);

		this.seekBar = (SeekBar) this.findViewById(R.id.seeker);
		this.seekBar.setEnabled(false);

		if (this.playerServiceBound) {
			this.drawButtons(this.playerService.getPlayer());
		}
	}

	/**
	 * Bind player service
	 */
	@Override
	protected void onStart() {
		super.onStart();

		Intent intent = new Intent(this, PlayerService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	/**
	 * Unbind player service
	 */
	@Override
	protected void onStop() {
		super.onStop();

		// Unbind from the service
		if (this.playerServiceBound) {
			this.unbindService(this);
			this.playerServiceBound = false;
		}
	}

	/**
	 * Closes unbinds from service, stops the service, and calls finish()
	 */
	public void shutdownActivity() {
		if (this.playerServiceBound) {
			this.playerService.stop();
			this.unbindService(this);
			this.playerServiceBound = false;
		}

		Intent intent = new Intent(this, PlayerService.class);
		stopService(intent);
		finish();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.play:
			this.play();
			break;
		case R.id.pause:
			this.pause();
			break;
		case R.id.next:
			this.playNext();
			break;
		case R.id.previous:
			this.playPrevious();
			break;
		default:
			break;
		}

		this.drawButtons(this.playerService.getPlayer());
	}

	private void pause() {
		this.playerService.pause();
	}

	/**
	 * Add selected songs to current playing list
	 */
	protected void addSelectedToPlaylist() {
		int nbAdded = 0;

		AbstractPlayerListActivity.this.clearPlaying();

		for (Song song : this.getSelectedSongs()) {
			AbstractPlayerListActivity.this.addSong(song);
			nbAdded++;
		}

		Toast.makeText(this.getApplicationContext(),
				nbAdded + " song(s) added", Toast.LENGTH_SHORT).show();
	}

	protected abstract List<Song> getSelectedSongs();

	protected void clearPlaying() {
		this.playerService.clear();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		ENTITY entity = this.getWisslAdapter().getItem(position);
		this.nextPage(entity);
	}

	protected abstract void nextPage(ENTITY entity);

	public SharedPreferences getSettings() {
		return this.settings;
	}

	public String getSessionId() {
		return this.getSettings().getString(
				RemoteAction.SESSION_ID.getRequestURI(), null);
	}

	/**
	 * Get the Wissl server URL
	 * 
	 * @returnThe server URL
	 */
	public String getServerUrl() {
		return this.serverUrl + "/" + RemoteAction.WISSL_ENTRY_POINT;
	}

	/**
	 * Send POST request to Wissl server
	 * 
	 * @param uri
	 *            Request URI
	 */
	public void post(RemoteAction action, String uri) {
		this.post(action, uri, new HashMap<String, String>());
	}

	public void showPlaying() {
		Intent intent = new Intent(this, PlayingSongListActivity.class);
		this.startActivityIfNeeded(intent, 0);
	}

	/**
	 * Send POST request to Wissl server
	 * 
	 * @param uri
	 *            Request URI
	 * @param params
	 *            Parameters to send
	 */
	public void post(RemoteAction action, String uri, Map<String, String> params) {
		try {
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();

			if (params != null) {
				for (Entry<String, String> line : params.entrySet()) {
					paramList.add(new BasicNameValuePair(line.getKey(), line
							.getValue()));
				}
			}

			paramList.add(new BasicNameValuePair("sessionId", this
					.getSessionId()));

			HttpEntity entity = new UrlEncodedFormEntity(paramList);
			HttpPost request = new HttpPost(this.getServerUrl() + "/" + uri);
			request.setEntity(entity);

			this.connect(action, request);
		} catch (UnsupportedEncodingException e) {
			this.showErrorDialog(e.getMessage());
		}
	}

	/**
	 * Send GET request to Wissl server
	 * 
	 * @param uri
	 *            Request URI
	 */
	public void get(RemoteAction action, String suffix) {
		this.connect(action,
				new HttpGet(this.getServerUrl() + "/" + action.getRequestURI()
						+ (suffix != null ? "/" + suffix : "")));
	}

	private void connect(RemoteAction action, HttpRequestBase request) {
		request.addHeader(RemoteAction.SESSION_ID.getRequestURI(),
				this.getSessionId());

		this.remoteTask = new RemoteTask(action, this);
		this.remoteTask.execute(request);
	}

	public void showErrorDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Error")
				.setMessage(message)
				.setNeutralButton(this.getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).create().show();
	}

	public void notLogged() {
		SharedPreferences.Editor editor = this.getSettings().edit();
		editor.remove(RemoteAction.SESSION_ID.getRequestURI());
		editor.commit();

		Intent myIntent = new Intent(this.getBaseContext(), LoginActivity.class);
		this.startActivityIfNeeded(myIntent, 0);
	}

	protected List<Song> getPlayingSongList() {
		return this.playerService.getSongList();
	}

	public void play() {
		if (!this.playerService.isPlaying()) {
			this.playerService.play();
		}
	}

	public void play(int position) {
		try {
			this.playerService.play(position);
		} catch (IllegalArgumentException e) {
			this.showErrorDialog(e.getMessage());
		} catch (SecurityException e) {
			this.showErrorDialog(e.getMessage());
		} catch (IllegalStateException e) {
			this.showErrorDialog(e.getMessage());
		} catch (IOException e) {
			this.showErrorDialog(e.getMessage());
		}
	}

	/**
	 * Get the binded player service
	 * 
	 * @return the binded player service, or <code>null</code> if not binded
	 */
	protected PlayerService getPlayerService() {
		return this.playerService;
	}

	/**
	 * This method allow to add a song to the player service song list
	 * 
	 * @param song
	 *            song to add
	 */
	protected void addSong(Song song) {
		try {
			this.getPlayerService().add(song);
		} catch (IllegalArgumentException e) {
			this.showErrorDialog(e.getMessage());
		} catch (SecurityException e) {
			this.showErrorDialog(e.getMessage());
		} catch (IllegalStateException e) {
			this.showErrorDialog(e.getMessage());
		} catch (IOException e) {
			this.showErrorDialog(e.getMessage());
		}
	}

	protected void addSongs(List<Song> songList) {
		try {
			this.playerService.addAll(songList);
		} catch (IllegalArgumentException e) {
			this.showErrorDialog(e.getMessage());
		} catch (SecurityException e) {
			this.showErrorDialog(e.getMessage());
		} catch (IllegalStateException e) {
			this.showErrorDialog(e.getMessage());
		} catch (IOException e) {
			this.showErrorDialog(e.getMessage());
		}
	}

	public void playNext() {
		try {
			this.playerService.playNext();
		} catch (IllegalArgumentException e) {
			this.showErrorDialog(e.getMessage());
		} catch (SecurityException e) {
			this.showErrorDialog(e.getMessage());
		} catch (IllegalStateException e) {
			this.showErrorDialog(e.getMessage());
		} catch (IOException e) {
			this.showErrorDialog(e.getMessage());
		}
	}

	public void playPrevious() {
		try {
			this.playerService.playPrevious();
		} catch (IllegalArgumentException e) {
			this.showErrorDialog(e.getMessage());
		} catch (SecurityException e) {
			this.showErrorDialog(e.getMessage());
		} catch (IllegalStateException e) {
			this.showErrorDialog(e.getMessage());
		} catch (IOException e) {
			this.showErrorDialog(e.getMessage());
		}
	}

	public void stop() {
		this.playerService.stop();
	}

	@Override
	public final void onPostExecute(RemoteAction action, JSONArray jsonArray,
			int statusCode, String errorMessage) {
		if (statusCode != 200) {
			this.showErrorDialog(errorMessage);

			if (statusCode == 401) {
				this.notLogged();
			}
		}

		try {
			int arraySize = jsonArray.length();
			for (int i = 0; i < arraySize; i++) {
				switch (action) {
				case LOAD_PLAYLISTS:
					if (this.playlists.isEmpty()) {
						JSONObject object = jsonArray.getJSONObject(i);

						JSONArray albumArray = object
								.getJSONArray(RemoteAction.LOAD_PLAYLISTS
										.getRequestURI());
						int nbAlbums = albumArray.length();

						CharSequence[] playlists = new CharSequence[nbAlbums];

						for (int j = 0; j < nbAlbums; j++) {
							Playlist playlist = new Playlist(
									albumArray.getJSONObject(j));

							this.playlists.add(playlist);

							playlists[j] = playlist.getPlaylistName();
						}

						AlertDialog.Builder builder = new AlertDialog.Builder(
								this);
						builder.setTitle(R.string.playlists);
						builder.setSingleChoiceItems(playlists, -1,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int item) {
										switch (item) {
										case 0:
											AbstractPlayerListActivity.this
													.addSelectedToPlaylist();
											break;

										default:
											break;
										}
									}
								});
						this.playlistDialog = builder.create();
					}

					this.playlistDialog.show();
					break;
				default:
					this.next(action, jsonArray.getJSONObject(i));
					break;
				}
			}

			this.stopWaiting();
		} catch (JSONException e) {
			this.showErrorDialog(e.getMessage());
		}
	}

	protected abstract void next(RemoteAction action, JSONObject object)
			throws JSONException;

	protected abstract ADAPTER buildAdapter();

	protected abstract void loadEntities();

	protected ADAPTER getWisslAdapter() {
		return this.listAdapter;
	}

	public void checkItemHandler(View view) {
		CheckBox checkBox = (CheckBox) view;
		int position = (Integer) checkBox.getTag();

		ENTITY entity = this.getWisslAdapter().getItem(position);

		if (checkBox.isChecked()) {
			this.selectedItems.add(entity);
		} else {
			this.selectedItems.remove(entity);
		}
	}

	protected List<ENTITY> getSelectedItems() {
		return this.selectedItems;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		PlayerBinder binder = (PlayerBinder) service;
		this.playerService = binder.getService();
		this.playerServiceBound = true;

		this.playerService.setClient(this);
		this.drawButtons(this.playerService.getPlayer());
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		this.playerServiceBound = false;
	}

	protected void stopWaiting() {
		this.dialog.dismiss();
	}

	private void enableHttpResponseCache() {
		try {
			long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
			File httpCacheDir = new File(getCacheDir(), "http");
			Class.forName("android.net.http.HttpResponseCache")
					.getMethod("install", File.class, long.class)
					.invoke(null, httpCacheDir, httpCacheSize);
		} catch (Exception httpResponseCacheNotAvailable) {
		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
		Log.d(LOG_TAG, "Buffering " + percent + "%");

		this.seekBar.setSecondaryProgress(percent * mediaPlayer.getDuration()
				/ 100);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.e(LOG_TAG, "Song error (" + what + ", " + extra + ")");
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer player) {
		Log.d(LOG_TAG, "Song prepared");

		this.playerService.play();

		this.drawButtons(player);
	}

	@Override
	public void onCompletion(MediaPlayer player) {
		Log.d(LOG_TAG, "Song completed");

		this.playNext();

		this.drawButtons(player);
	}

	private void drawButtons(MediaPlayer player) {
		boolean isPlaying = player.isPlaying();
		boolean isPaused = this.playerService.isPaused();
		boolean hasNext = this.playerService.hasNext();
		boolean hasPrevious = this.playerService.hasPrevious();
		boolean hasSongs = this.playerService.hasSongs();

		this.seekBar.setEnabled(isPlaying || isPaused);
		if (isPlaying || isPaused) {
			this.seekBar.setMax(player.getDuration());
			this.seekBar.setProgress(player.getCurrentPosition());
		} else {
			this.seekBar.setMax(100);
		}

		this.playButton.setEnabled(isPaused || hasSongs);
		this.playButton.setVisibility(isPlaying ? View.GONE : View.VISIBLE);

		this.pauseButton.setEnabled(isPlaying);
		this.pauseButton.setVisibility(isPlaying ? View.VISIBLE : View.GONE);

		this.nextButton.setEnabled(hasNext);
		this.previousButton.setEnabled(hasPrevious);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int pos, boolean arg2) {
		int seekTo = seekBar.getProgress();
		if (this.seekBar.getSecondaryProgress() > seekTo) {
			seekBar.cancelLongPress();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		int seekTo = seekBar.getProgress();
		if (this.seekBar.getSecondaryProgress() > seekTo) {
			this.playerService.seekTo(seekTo);
		}
	}

	public void setPlayingSong(Song song) {
		this.setSelectedSong(this.playingSong, false);
		this.setSelectedSong(song, true);

		this.playingSong = song;
	}

	private void setSelectedSong(Song song, boolean select) {
		if (song != null) {
			int nbItems = this.getWisslAdapter().getCount();

			for (int i = 0; i < nbItems; i++) {
				if (this.getWisslAdapter().isPlaying(song,
						this.getWisslAdapter().getItem(i))) {
					this.setSelectedItem(i, select);
					break;
				}
			}
		}
	}

	private void setSelectedItem(int position, boolean select) {
		int color = Color.TRANSPARENT;

		if (select) {
			// FIXME selected song background : GREEN
			color = Color.GREEN;
			this.getListView().setSelection(position);
		}
		// this.getListView().getChildAt(position).setBackgroundColor(color);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.quit:
			this.shutdownActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
