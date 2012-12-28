package fr.trovato.wissl.android.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import fr.trovato.wissl.android.handlers.PlayerHandler;
import fr.trovato.wissl.commons.data.Song;
import fr.trovato.wissl.commons.utils.Player;

/**
 * Service playing music in background
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class PlayerService extends Service implements OnPreparedListener,
		OnCompletionListener, OnErrorListener {

	/** Server URL */
	public final static String SERVER_URL = "SERVER_URL";
	/** Session ID */
	public final static String SESSION_ID = "SESSION_ID";
	private List<Song> queue;
	private MediaPlayer player;
	private int currentPosition;
	private String serverUrl;
	private String sessionId;
	private ScheduledExecutorService myScheduledExecutorService;

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder binder = new PlayerBinder();
	private PlayerHandler playerHandler;

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class PlayerBinder extends Binder {
		public PlayerService getService() {
			return PlayerService.this;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate() {
		super.onCreate();

		this.player = new MediaPlayer();
		this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		this.player.setOnPreparedListener(this);
		this.player.setOnCompletionListener(this);
		this.player.setOnErrorListener(this);

		this.currentPosition = 0;

		this.queue = new ArrayList<Song>();

		this.myScheduledExecutorService = Executors.newScheduledThreadPool(1);
		this.myScheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				if (PlayerService.this.player.isPlaying()) {
					PlayerService.this.playerHandler
							.sendMessage(PlayerService.this.playerHandler
									.obtainMessage(Player.SEEK.ordinal(),
											PlayerService.this.player
													.getCurrentPosition(),
											PlayerService.this.player
													.getDuration()));
				}
			}
		}, 200, 200, TimeUnit.MILLISECONDS);
	}

	@Override
	public IBinder onBind(Intent intent) {
		this.serverUrl = intent.getStringExtra(PlayerService.SERVER_URL);
		this.sessionId = intent.getStringExtra(PlayerService.SESSION_ID);

		return this.binder;
	}

	@Override
	public void onPrepared(MediaPlayer player) {
		this.play();
	}

	public void clear() {
		this.playerHandler
				.sendMessage(this.playerHandler.obtainMessage(
						Player.QUEUE.ordinal(), this.queue.size(),
						this.currentPosition));
		this.queue.clear();
	}

	public void addAll(List<Song> songList) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {
		this.queue.addAll(songList);

		this.songAdded();
	}

	public void add(Song song) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {
		this.queue.add(song);

		this.songAdded();
	}

	private void songAdded() throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {
		this.playerHandler
				.sendMessage(this.playerHandler.obtainMessage(
						Player.QUEUE.ordinal(), this.queue.size(),
						this.currentPosition));

		if (!this.player.isPlaying()) {
			this.playSong();
		}
	}

	@Override
	public void onDestroy() {
		this.player.release();
		this.player = null;
	}

	private void playSong() throws IllegalArgumentException, SecurityException,
			IllegalStateException, IOException {

		if (this.currentPosition >= 0 && this.queue.size() > 0
				&& this.currentPosition < this.queue.size()) {

			Song song = this.queue.get(this.currentPosition);

			Uri uri = this.buildUri(song);
			Log.d(this.getClass().getSimpleName(),
					"Streaming " + uri.toString());

			this.player.reset();
			this.player.setDataSource(this, uri);
			this.player.prepareAsync();
		} else {
			this.stop();
		}
	}

	private Uri buildUri(Song song) {
		Uri uri = Uri.parse(this.serverUrl + "/song/" + song.getId()
				+ "/stream?sessionId=" + this.sessionId);

		return uri;
	}

	@Override
	public void onCompletion(MediaPlayer player) {
		this.currentPosition++;

		try {
			this.playSong();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playNext() throws IllegalArgumentException, SecurityException,
			IllegalStateException, IOException {
		this.stop();
		this.currentPosition++;

		this.playSong();
	}

	public void stop() {
		this.currentPosition = 0;
		this.player.stop();
		this.playerHandler.sendMessage(this.playerHandler
				.obtainMessage(Player.STOP.ordinal()));
	}

	public void playPrevious() throws IllegalStateException,
			IllegalArgumentException, SecurityException, IOException {
		this.player.stop();
		this.currentPosition--;

		this.playSong();
	}

	public boolean isPlaying() {
		return this.player.isPlaying();
	}

	public void pause() {
		this.player.pause();
		this.playerHandler.sendMessage(this.playerHandler
				.obtainMessage(Player.PAUSE.ordinal()));
	}

	public void play() {
		if (!this.player.isPlaying()) {
			this.player.start();
		}

		this.playerHandler.sendMessage(this.playerHandler
				.obtainMessage(Player.PLAY.ordinal()));
	}

	public List<Song> getSongList() {
		return this.queue;
	}

	public void play(int position) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {
		this.currentPosition = position;

		this.playSong();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		return this.playerHandler.sendMessage(this.playerHandler.obtainMessage(
				Player.ERROR.ordinal(), what, extra));
	}

	public void seekTo(int progress) {
		this.player.seekTo(progress);
	}

	public void setHandler(PlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	public Song getPlayingSong() {
		if (this.isPlaying()) {
			return this.queue.get(this.currentPosition);
		} else {
			return null;
		}
	}
}
