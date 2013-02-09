package fr.trovato.wissl.android.activities.player;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import fr.trovato.wissl.android.adapters.ArtistAdapter;
import fr.trovato.wissl.android.remote.RemoteAction;
import fr.trovato.wissl.commons.data.Artist;
import fr.trovato.wissl.commons.data.Song;

public class ArtistListActivity extends
		AbstractPlayerListActivity<Artist, ArtistAdapter> {

	@Override
	protected void next(RemoteAction action, JSONObject object)
			throws JSONException {
		switch (action) {
			case ARTISTS:
				this.getWisslAdapter().clear();

				List<Artist> artistList = new ArrayList<Artist>();

				JSONArray artists = object.getJSONArray(RemoteAction.ARTISTS
						.getRequestURI());

				for (int i = 0; i < artists.length(); i++) {
					JSONObject obj = artists.getJSONObject(i);
					artistList.add(new Artist(obj
							.getJSONObject(RemoteAction.ARTIST
									.getRequestURI())));
				}

				this.getWisslAdapter().addAll(artistList);
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
	protected ArtistAdapter buildAdapter() {
		return new ArtistAdapter(this, new ArrayList<Artist>());
	}

	@Override
	protected void loadEntities() {
		this.get(RemoteAction.ARTISTS, null);
	}

	@Override
	protected void nextPage(Artist entity) {
		Intent intent = new Intent(this, AlbumListActivity.class);
		intent.putExtra(RemoteAction.ARTIST_ID.name(), entity.getId());
		this.startActivityIfNeeded(intent, 0);
	}

}
