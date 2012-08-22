package fr.trovato.wissl.android.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import fr.trovato.wissl.android.OnReceiveRemoteSongListener;
import fr.trovato.wissl.android.adapter.AlbumAdapter;
import fr.trovato.wissl.android.tasks.LoadSongsTask;
import fr.trovato.wissl.commons.data.Album;
import fr.trovato.wissl.commons.data.Song;

public class AlbumListActivity extends
		AbstractListActivity<Album, AlbumAdapter> implements
		OnReceiveRemoteSongListener {

	private LoadSongsTask loadSongsTask;

	@Override
	protected void loadEntities() {
		Intent intent = this.getIntent();

		if (intent != null) {
			int artistId = intent.getIntExtra(AbstractListActivity.ARTIST_ID,
					-1);

			if (artistId >= 0) {
				this.get("albums/" + artistId);
			}
		}
	}

	@Override
	protected void next(JSONObject object) throws JSONException {
		this.getWisslAdapter().clear();
		List<Album> albumList = new ArrayList<Album>();

		JSONArray albumArray = object.getJSONArray("albums");

		for (int i = 0; i < albumArray.length(); i++) {
			albumList.add(new Album(albumArray.getJSONObject(i)));
		}

		this.getWisslAdapter().addAll(albumList);
	}

	@Override
	protected AlbumAdapter buildAdapter() {
		return new AlbumAdapter(this, new ArrayList<Album>());
	}

	@Override
	protected List<Song> getSelectedSongs() {

		return null;
	}

	@Override
	protected void nextPage(Album album) {
		Intent intent = new Intent(this, SongListActivity.class);
		intent.putExtra(AbstractListActivity.ALBUM_ID, album.getId());
		this.startActivityIfNeeded(intent, 0);
	}

	public HttpRequestBase[] loadSongs() {
		List<Album> albumList = this.getSelectedItems();
		int albumSize = albumList.size();

		HttpRequestBase[] results = new HttpRequestBase[albumSize];

		for (int i = 0; i < albumSize; i++) {
			Uri sessionUri = Uri.parse(this.getServerUrl() + "/songs/"
					+ albumList.get(i));
			Builder uriBuilder = sessionUri.buildUpon();
			uriBuilder.appendQueryParameter("sessionId", this.getSessionId());
			results[i] = new HttpGet(uriBuilder.build().toString());
		}

		return results;
	}

	@Override
	public void onReceiveSongs(JSONArray object, int statusCode, String message) {
		if (statusCode != 200) {
			this.showErrorDialog(message);

			if (statusCode == 401) {
				this.notLogged();
			}
		}

		try {
			int arraySize = object.length();
			for (int i = 0; i < arraySize; i++) {
				JSONObject currentObj = object.getJSONObject(i);

			}
		} catch (JSONException e) {
			this.showErrorDialog(e.getMessage());
		}
	}

	@Override
	protected void playSelectedSongs() {
		this.loadSongsTask = (LoadSongsTask) new LoadSongsTask(this)
				.execute(this.loadSongs());
	}
}
