package fr.trovato.wissl.android.activities.player;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import fr.trovato.wissl.android.adapters.PlaylistAdapter;
import fr.trovato.wissl.android.remote.RemoteAction;
import fr.trovato.wissl.commons.data.Playlist;
import fr.trovato.wissl.commons.data.Song;

public class PlaylistActivity extends
		AbstractPlayerListActivity<Playlist, PlaylistAdapter> {

	@Override
	protected void loadEntities() {
		this.get(RemoteAction.PLAYLISTS, null);
	}

	@Override
	protected void next(RemoteAction action, JSONObject object)
			throws JSONException {
		switch (action) {
		case PLAYLISTS:
			this.getWisslAdapter().clear();

			List<Playlist> playlistList = new ArrayList<Playlist>();

			JSONArray artists = object.getJSONArray(action.getRequestURI());

			for (int i = 0; i < artists.length(); i++) {
				JSONObject obj = artists.getJSONObject(i);
				playlistList.add(new Playlist(obj));
			}

			this.getWisslAdapter().addAll(playlistList);
			break;
		default:
			break;
		}
	}

	@Override
	protected List<Song> getSelectedSongs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void nextPage(Playlist entity) {
		Intent intent = new Intent(this, SongListActivity.class);
		intent.putExtra(RemoteAction.PLAYLIST_ID.name(), entity.getId());
		this.startActivityIfNeeded(intent, 0);
	}

	@Override
	protected PlaylistAdapter buildAdapter() {
		return new PlaylistAdapter(this, new ArrayList<Playlist>());
	}

}
