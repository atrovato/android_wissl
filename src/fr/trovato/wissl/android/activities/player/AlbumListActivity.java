package fr.trovato.wissl.android.activities.player;

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
import fr.trovato.wissl.android.adapters.AlbumAdapter;
import fr.trovato.wissl.android.listeners.OnRemoteResponseListener;
import fr.trovato.wissl.android.remote.RemoteAction;
import fr.trovato.wissl.commons.data.Album;
import fr.trovato.wissl.commons.data.Song;

public class AlbumListActivity extends
		AbstractPlayerListActivity<Album, AlbumAdapter> implements
		OnRemoteResponseListener {

	@Override
	protected void loadEntities() {
		Intent intent = this.getIntent();

		if (intent != null) {
			int artistId = intent
					.getIntExtra(RemoteAction.ARTIST_ID.name(), -1);

			if (artistId >= 0) {
				this.get(RemoteAction.ALBUMS, String.valueOf(artistId));
			}
		}
	}

	@Override
	protected void next(RemoteAction action, JSONObject object)
			throws JSONException {
		switch (action) {
			case ALBUMS:
				this.getWisslAdapter().clear();
				List<Album> albumList = new ArrayList<Album>();

				JSONArray albumArray = object.getJSONArray(RemoteAction.ALBUMS
						.getRequestParam());

				for (int i = 0; i < albumArray.length(); i++) {
					albumList.add(new Album(albumArray.getJSONObject(i)));
				}

				this.getWisslAdapter().addAll(albumList);
				break;

			default:
				break;
		}

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
		intent.putExtra(RemoteAction.ALBUM_ID.name(), album.getId());
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
	protected void addSelectedToPlaylist() {
		this.get(RemoteAction.LOAD_PLAYLISTS, null);
	}
}
