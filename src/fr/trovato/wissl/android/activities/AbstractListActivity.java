package fr.trovato.wissl.android.activities;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.IBinder;
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
import fr.trovato.wissl.android.LoginActivity;
import fr.trovato.wissl.android.R;
import fr.trovato.wissl.android.adapter.AbstractAdapter;
import fr.trovato.wissl.android.handlers.PlayerHandler;
import fr.trovato.wissl.android.listeners.OnRemoteResponseListener;
import fr.trovato.wissl.android.remote.Parameters;
import fr.trovato.wissl.android.remote.RemoteAction;
import fr.trovato.wissl.android.services.PlayerService;
import fr.trovato.wissl.android.services.PlayerService.PlayerBinder;
import fr.trovato.wissl.android.tasks.RemoteTask;
import fr.trovato.wissl.commons.data.Playlist;
import fr.trovato.wissl.commons.data.Song;
import fr.trovato.wissl.commons.data.WisslEntity;

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
public abstract class AbstractListActivity<ENTITY extends WisslEntity, ADAPTER extends AbstractAdapter<ENTITY>>
		extends ListActivity implements OnClickListener,
		OnRemoteResponseListener, OnItemClickListener, ServiceConnection,
		OnSeekBarChangeListener {

	/** Wissl server URL */
	private String serverUrl;

	/** Background service playing music */
	private PlayerService playerService;
	/** Background service playing music enable flag */
	private boolean playerServiceBound = false;

	/** Add to playlist button */
	private ImageButton addToPlaylistButton;
	/** Playing playlist button */
	private ImageButton playingButton;
	/** Play button */
	private ImageButton playButton;
	/** Stop button */
	private ImageButton stopButton;
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

	/** Handler to display player status */
	private PlayerHandler playerHandler;

	/** Current Wissl entities list adapter */
	private ADAPTER listAdapter;

	private AlertDialog playlistDialog;

	/**
	 * Load settings and prepare player interface
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

		this.addToPlaylistButton = (ImageButton) this
				.findViewById(R.id.add_to_playlist);
		this.addToPlaylistButton.setOnClickListener(this);
		this.addToPlaylistButton.setEnabled(!this.selectedItems.isEmpty());

		this.playingButton = (ImageButton) this.findViewById(R.id.playing);
		this.playingButton.setOnClickListener(this);
		this.playingButton.setEnabled(false);

		this.playButton = (ImageButton) this.findViewById(R.id.play);
		this.playButton.setOnClickListener(this);
		this.playButton.setEnabled(false);

		this.stopButton = (ImageButton) this.findViewById(R.id.stop);
		this.stopButton.setOnClickListener(this);
		this.stopButton.setEnabled(false);

		this.nextButton = (ImageButton) this.findViewById(R.id.next);
		this.nextButton.setOnClickListener(this);
		this.nextButton.setEnabled(false);

		this.previousButton = (ImageButton) this.findViewById(R.id.previous);
		this.previousButton.setOnClickListener(this);
		this.previousButton.setEnabled(false);

		this.seekBar = (SeekBar) this.findViewById(R.id.seeker);
		this.seekBar.setOnSeekBarChangeListener(this);
		this.seekBar.setEnabled(false);

		this.playerHandler = new PlayerHandler(this.seekBar, this.playButton,
				this.stopButton, this.nextButton, this.previousButton,
				this.playingButton);

		ConnectivityManager connMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			this.loadEntities();
		} else {
			this.showErrorDialog(this.getString(R.string.no_connection));
		}
	}

	/**
	 * Bind player service
	 */
	@Override
	protected void onStart() {
		super.onStart();

		Intent playerIntent = new Intent(this, PlayerService.class);
		playerIntent.putExtra(PlayerService.SERVER_URL, this.getServerUrl());
		playerIntent.putExtra(PlayerService.SESSION_ID, this.getSessionId());

		this.bindService(playerIntent, this, Context.BIND_AUTO_CREATE);
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

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.add_to_playlist:
				this.getPlaylists();
				break;
			case R.id.playing:
				if (this instanceof PlayingSongListActivity) {
					this.finish();
				} else {
					this.showPlaying();
				}
				break;
			case R.id.play:
				this.play();
				break;
			case R.id.stop:
				this.playButton.setImageResource(R.drawable.play);
				this.stop();
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
	}

	private void getPlaylists() {
		new RemoteTask(RemoteAction.LOAD_PLAYLISTS, null);
	}

	private void showPlaylists(CharSequence[] items) {
		this.playlistDialog.show();
	}

	protected void addSelectedToPlaylist() {
		int nbAdded = 0;

		AbstractListActivity.this.clearPlaying();

		for (Song song : this.getSelectedSongs()) {
			AbstractListActivity.this.addSong(song);
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

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		this.playerService.seekTo(seekBar.getProgress());
	}

	protected abstract void nextPage(ENTITY entity);

	public SharedPreferences getSettings() {
		return this.settings;
	}

	public String getSessionId() {
		return this.getSettings().getString(
				RemoteAction.SESSION_ID.getRequestParam(), null);
	}

	/**
	 * Get the Wissl server URL
	 * 
	 * @returnThe server URL
	 */
	public String getServerUrl() {
		return this.serverUrl + "/wissl";
	}

	/**
	 * Send POST request to Wissl server
	 * 
	 * @param uri
	 *            Request URI
	 */
	public void post(RemoteAction action, String uri) {
		this.post(action, uri, new HashMap<String, String>(1));
	}

	public void showPlaying() {
		Intent intent = new Intent(this, PlayingSongListActivity.class);
		this.startActivity(intent);
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
		Uri sessionUri = Uri.parse(this.getServerUrl() + "/"
				+ action.getRequestParam()
				+ (suffix != null ? "/" + suffix : ""));
		Builder uriBuilder = sessionUri.buildUpon();
		uriBuilder.appendQueryParameter(
				RemoteAction.SESSION_ID.getRequestParam(), this.getSessionId());

		this.connect(action, new HttpGet(uriBuilder.build().toString()));
	}

	private void connect(RemoteAction action, HttpRequestBase request) {
		this.remoteTask = new RemoteTask(action, this);
		this.remoteTask.execute(request);
	}

	public void showErrorDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setNeutralButton(
				this.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.create();
		builder.show();
	}

	public void notLogged() {
		SharedPreferences.Editor editor = this.getSettings().edit();
		editor.remove(RemoteAction.SESSION_ID.getRequestParam());
		editor.commit();

		Intent myIntent = new Intent(this.getBaseContext(), LoginActivity.class);
		this.startActivityIfNeeded(myIntent, 0);
	}

	protected List<Song> getPlayingSongList() {
		return this.playerService.getSongList();
	}

	public void play() {
		if (this.playerService.isPlaying()) {
			this.playerService.pause();
		} else {
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

	public void addSong(Song song) {
		try {
			this.playerService.add(song);
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
											.getRequestParam());
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
										public void onClick(
												DialogInterface dialog, int item) {
											switch (item) {
												case 0:
													AbstractListActivity.this
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

	// Fonction appelée au clic d'une des checkbox
	public void checkItemHandler(View view) {
		CheckBox checkBox = (CheckBox) view;
		int position = (Integer) checkBox.getTag();

		ENTITY entity = this.getWisslAdapter().getItem(position);

		if (checkBox.isChecked()) {
			this.selectedItems.add(entity);
		} else {
			this.selectedItems.remove(entity);
		}

		this.addToPlaylistButton.setEnabled(!this.selectedItems.isEmpty());
	}

	protected List<ENTITY> getSelectedItems() {
		return this.selectedItems;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		PlayerBinder binder = (PlayerBinder) service;
		this.playerService = binder.getService();
		this.playerService.setHandler(this.playerHandler);
		this.playerServiceBound = true;

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		this.playerServiceBound = false;
	}

	protected void stopWaiting() {
		this.dialog.dismiss();
	}

}
