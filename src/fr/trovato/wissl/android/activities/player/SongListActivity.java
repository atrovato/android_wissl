package fr.trovato.wissl.android.activities.player;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import fr.trovato.wissl.android.adapters.SongAdapter;
import fr.trovato.wissl.android.remote.RemoteAction;
import fr.trovato.wissl.commons.data.Song;

/**
 * Activity listing songs
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class SongListActivity extends
		AbstractPlayerListActivity<Song, SongAdapter> {

	@Override
	protected void loadEntities() {
		Intent intent = this.getIntent();

		if (intent != null) {
			Integer albumId = intent.getIntExtra(RemoteAction.ALBUM_ID.name(),
					-1);
			Integer playlistId = intent.getIntExtra(
					RemoteAction.PLAYLIST_ID.name(), -1);

			if (albumId >= 0) {
				this.get(RemoteAction.SONGS, String.valueOf(albumId));
			} else if (playlistId >= 0) {
				this.get(RemoteAction.PLAYLIST, playlistId + "/songs");
			}
		}
	}

	@Override
	protected void next(RemoteAction action, JSONObject object)
			throws JSONException {
		switch (action) {
		case SONGS:
			this.transformSongs(object.getJSONArray("songs"));
			break;
		case PLAYLIST:
			List<Song> songList = this.transformSongs(object
					.getJSONArray("playlist"));
			super.addSongs(songList);
			break;
		}

	}

	/**
	 * Add new songs to list
	 * 
	 * @param songArray
	 *            songs from server
	 * @return list of all songs
	 * @throws JSONException
	 *             error during transformation
	 */
	private List<Song> transformSongs(JSONArray songArray) throws JSONException {
		this.getWisslAdapter().clear();
		List<Song> songList = new ArrayList<Song>();

		for (int i = 0; i < songArray.length(); i++) {
			Song song = new Song(songArray.getJSONObject(i));
			this.getWisslAdapter().add(song);
			songList.add(song);
		}

		return songList;
	}

	@Override
	protected SongAdapter buildAdapter() {
		return new SongAdapter(this, new ArrayList<Song>());
	}

	@Override
	protected List<Song> getSelectedSongs() {
		return this.getSelectedItems();
	}

	@Override
	protected void nextPage(Song song) {
		this.stop();
		this.clearPlaying();

		int nbSongs = this.getListAdapter().getCount();
		int position = 0;
		List<Song> songList = new ArrayList<Song>();

		for (int i = 0; i < nbSongs; i++) {
			songList.add((Song) this.getListAdapter().getItem(i));

			if (song.equals(songList.get(i))) {
				position = i;
			}
		}

		super.addSongs(songList);
		this.play(position);
	}

}
