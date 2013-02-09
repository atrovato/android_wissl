package fr.trovato.wissl.android.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import fr.trovato.wissl.android.activities.player.AbstractPlayerListActivity;
import fr.trovato.wissl.android.remote.Parameters;
import fr.trovato.wissl.android.remote.RemoteAction;
import fr.trovato.wissl.commons.data.Song;

/**
 * Service playing music in background
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class PlayerService extends Service {

	/** Server URL */
	public final static String SERVER_URL = "SERVER_URL";
	/** Session ID */
	public final static String SESSION_ID = "SESSION_ID";

	private final static String LOG_TAG = "PLAYER_SERVICE";

	private List<Song> queue;
	private MediaPlayer player;
	private int currentPosition;
	private String serverUrl;
	private String sessionId;
	private AbstractPlayerListActivity<?, ?> client;

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder binder = new PlayerBinder();

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class PlayerBinder extends Binder {
		public PlayerService getService() {
			return PlayerService.this;
		}
	}

	public void setClient(AbstractPlayerListActivity<?, ?> client) {
		this.client = client;

		player.setOnCompletionListener(this.client);
		player.setOnPreparedListener(this.client);
		player.setOnErrorListener(this.client);
		player.setOnBufferingUpdateListener(this.client);
	}

	public MediaPlayer getPlayer() {
		return this.player;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate() {
		super.onCreate();

		Log.d(LOG_TAG, "Creating... start");

		this.initializePlayer();

		// Restore preferences
		SharedPreferences settings = this.getSharedPreferences(
				Parameters.PREFS_NAME.name(), 0);
		this.sessionId = settings.getString(
				RemoteAction.SESSION_ID.getRequestURI(), null);
		this.serverUrl = settings.getString(Parameters.SERVER_URL.name(), null)
				+ "/" + RemoteAction.WISSL_ENTRY_POINT;

		this.currentPosition = 0;

		this.queue = new ArrayList<Song>();

		Log.d(LOG_TAG, "Creating... end");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return this.binder;
	}

	public void clear() {
		this.queue.clear();
	}

	public void addAll(List<Song> songList) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {
		Log.d(LOG_TAG, "Add all (" + songList.size() + ") songs");

		this.queue.addAll(songList);

		this.songAdded();
	}

	public void add(Song song) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {
		Log.d(LOG_TAG, "Add song : " + song.getTitle());

		this.queue.add(song);

		this.songAdded();
	}

	private void songAdded() throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {
		Log.d(LOG_TAG, "Song added");

		if (!this.getPlayer().isPlaying()) {
			this.playSong();
		}
	}

	@Override
	public void onDestroy() {
		this.stop();
		this.getPlayer().release();
		this.player = null;
	}

	private void playSong() throws IllegalArgumentException, SecurityException,
			IllegalStateException, IOException {
		Log.d(LOG_TAG, "Play song");

		if (this.currentPosition >= 0 && this.queue.size() > 0
				&& this.currentPosition < this.queue.size()) {
			Song song = this.queue.get(this.currentPosition);

			Log.d(LOG_TAG, "Play song " + this.currentPosition);

			String uri = this.buildUri(song);
			Log.d(this.getClass().getSimpleName(),
					"Streaming " + uri.toString());

			this.getPlayer().reset();
			this.getPlayer().setDataSource(uri);
			this.getPlayer().setAudioStreamType(AudioManager.STREAM_MUSIC);
			this.getPlayer().prepareAsync();
		} else {
			this.stop();
		}
	}

	private String buildUri(Song song) {
		return this.serverUrl + "/song/" + song.getId() + "/stream?sessionId="
				+ this.sessionId;
	}

	public void playNext() throws IllegalArgumentException, SecurityException,
			IllegalStateException, IOException {
		Log.d(LOG_TAG, "Play next");

		this.stop();
		this.currentPosition++;

		this.playSong();
	}

	public void stop() {
		Log.d(LOG_TAG, "Stop");

		this.currentPosition = 0;

		if (this.isPlaying()) {
			this.getPlayer().stop();
		}
	}

	public void playPrevious() throws IllegalStateException,
			IllegalArgumentException, SecurityException, IOException {
		Log.d(LOG_TAG, "Play previous");

		this.stop();
		this.currentPosition--;

		this.playSong();
	}

	public boolean isPlaying() {
		return this.getPlayer().isPlaying();
	}

	public void pause() {
		this.getPlayer().pause();
	}

	public void play() {
		Log.d(LOG_TAG, "Play");

		if (!this.getPlayer().isPlaying()) {
			this.getPlayer().start();
		}
	}

	public List<Song> getSongList() {
		return this.queue;
	}

	public void play(int position) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {
		this.currentPosition = position;

		this.playSong();
	}

	public void seekTo(int progress) {
		this.getPlayer().seekTo(progress);
	}

	public Song getPlayingSong() {
		if (this.isPlaying()) {
			return this.queue.get(this.currentPosition);
		} else {
			return null;
		}
	}

	/**
	 * Initializes a StatefulMediaPlayer for streaming playback of the provided
	 * StreamStation
	 * 
	 * @param station
	 *            The StreamStation representing the station to play
	 */
	private void initializePlayer() {
		player = new MediaPlayer();

		player.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
	}

	public boolean hasNext() {
		return this.currentPosition >= 0
				&& this.currentPosition < this.queue.size();
	}

	public boolean hasPrevious() {
		return this.currentPosition > 0
				&& this.currentPosition > this.queue.size();
	}

	public int getDuration() {
		return this.getPlayer().getDuration();
	}

	public int getCurrentPosition() {
		return this.getPlayer().getCurrentPosition();
	}

}
