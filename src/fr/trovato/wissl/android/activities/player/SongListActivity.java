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

public class SongListActivity extends AbstractPlayerListActivity<Song, SongAdapter> {

	@Override
	protected void loadEntities() {
		Intent intent = this.getIntent();

		if (intent != null) {
			int albumId = intent.getIntExtra(RemoteAction.ALBUM_ID.name(), -1);
			int playlistId = intent.getIntExtra(
					RemoteAction.PLAYLIST_ID.name(), -1);

			if (albumId >= 0) {
				this.get(RemoteAction.SONGS, String.valueOf(albumId));
			} else if (playlistId >= 0) {
				this.get(RemoteAction.PLAYLIST, String.valueOf(playlistId)
						+ "/songs");
			}
		}
	}

	@Override
	protected void next(RemoteAction action, JSONObject object)
			throws JSONException {
		switch (action) {
		case SONGS:
			this.addSongs(object.getJSONArray("songs"));
			break;
		case PLAYLIST:
			this.addSongs(object.getJSONArray("playlist"));
			this.playAll();
			break;
		}

	}

	private void playAll() {
		int nbSongs = this.getWisslAdapter().getCount();

		for (int i = 0; i < nbSongs; i++) {
			super.addSong(this.getWisslAdapter().getItem(i));
		}
	}

	private void addSongs(JSONArray songArray) throws JSONException {
		this.getWisslAdapter().clear();

		for (int i = 0; i < songArray.length(); i++) {
			this.getWisslAdapter().add(new Song(songArray.getJSONObject(i)));
		}
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
		this.addSong(song);
	}

}
