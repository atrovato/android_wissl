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
	private boolean paused;

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

	/**
	 * Get used MediaPlayer to play songs
	 * 
	 * @return used MediaPlayer
	 */
	public MediaPlayer getPlayer() {
		return this.player;
	}

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

	@Override
	public boolean onUnbind(Intent intent) {
		this.setClient(null);
		return super.onUnbind(intent);
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

	/**
	 * Play the song at the current position or stop media player if the
	 * position is invalid
	 * 
	 * @throws IllegalStateException
	 *             if it is called in an invalid state
	 * @throws IOException
	 *             if song cannot be read
	 */
	private void playSong() throws IllegalStateException, IOException {
		// Song list is not empty and playing is in range
		if (this.hasSongs() && this.currentPosition <= this.queue.size()) {

			Song song = this.queue.get(this.currentPosition);

			String url = this.buildStreamingURL(song);
			Log.d(LOG_TAG, "Streaming song (" + this.currentPosition + ") "
					+ url.toString());

			this.getPlayer().reset();
			this.getPlayer().setDataSource(url);
			this.getPlayer().setAudioStreamType(AudioManager.STREAM_MUSIC);
			this.getPlayer().prepare();

			this.client.setPlayingSong(song);
		} else {
			this.stop();
		}
	}

	/**
	 * Build URL for song streaming
	 * 
	 * @param song
	 *            song to load
	 * @return song streaming URL
	 */
	private String buildStreamingURL(Song song) {
		return this.serverUrl + "/song/" + song.getId() + "/stream?sessionId="
				+ this.sessionId;
	}

	/**
	 * Play next song
	 * 
	 * @throws IllegalStateException
	 *             if it is called in an invalid state
	 * @throws IOException
	 *             if song cannot be read
	 */
	public void playNext() throws IllegalStateException, IOException {
		Log.d(LOG_TAG, "Play next song");

		this.stop();
		this.currentPosition++;

		this.playSong();
	}

	public void playPrevious() throws IllegalStateException,
			IllegalArgumentException, SecurityException, IOException {
		Log.d(LOG_TAG, "Play previous");

		this.stop();
		this.currentPosition--;

		this.playSong();
	}

	/**
	 * Stop playing song and reset position
	 */
	public void stop() {
		this.paused = false;

		if (this.isPlaying()) {
			Log.d(LOG_TAG, "Stop");

			this.currentPosition = 0;

			this.getPlayer().stop();

			if (this.client != null) {
				this.client.setPlayingSong(null);
			}
		}
	}

	/**
	 * Checks whether the MediaPlayer is playing
	 * 
	 * @return <code>true</code> if playing, <code>false</code> otherwise
	 */
	public boolean isPlaying() {
		return this.getPlayer().isPlaying();
	}

	public boolean isPaused() {
		return this.paused;
	}

	public void pause() {
		this.getPlayer().pause();
		this.paused = true;
	}

	public void play() {
		Log.d(LOG_TAG, "Play");

		if (!this.getPlayer().isPlaying()) {
			this.getPlayer().start();
			this.paused = false;
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

	public boolean hasSongs() {
		return this.queue.size() > 0;
	}

}
