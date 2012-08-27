package fr.trovato.wissl.android.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import fr.trovato.wissl.android.adapter.SongAdapter;
import fr.trovato.wissl.android.remote.RemoteAction;
import fr.trovato.wissl.commons.data.Song;

public class SongListActivity extends AbstractListActivity<Song, SongAdapter> {

	@Override
	protected void loadEntities() {
		Intent intent = this.getIntent();

		if (intent != null) {
			int albumId = intent.getIntExtra(RemoteAction.ALBUM_ID.name(), -1);

			if (albumId >= 0) {
				this.get(RemoteAction.SONGS, String.valueOf(albumId));
			}
		}
	}

	@Override
	protected void next(RemoteAction action, JSONObject object)
			throws JSONException {
		switch (action) {
			case SONGS:

				this.getWisslAdapter().clear();

				JSONArray songArray = object.getJSONArray("songs");

				for (int i = 0; i < songArray.length(); i++) {
					this.getWisslAdapter().add(
							new Song(songArray.getJSONObject(i)));
				}
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
		this.clearPlaying();
		this.addSong(song);
	}

}
