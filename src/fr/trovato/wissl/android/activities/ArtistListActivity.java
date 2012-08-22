package fr.trovato.wissl.android.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import fr.trovato.wissl.android.adapter.ArtistAdapter;
import fr.trovato.wissl.commons.data.Artist;
import fr.trovato.wissl.commons.data.Song;

public class ArtistListActivity extends
		AbstractListActivity<Artist, ArtistAdapter> {

	@Override
	protected void next(JSONObject object) throws JSONException {
		this.getWisslAdapter().clear();

		List<Artist> artistList = new ArrayList<Artist>();

		JSONArray artists = object.getJSONArray("artists");

		for (int i = 0; i < artists.length(); i++) {
			JSONObject obj = artists.getJSONObject(i);
			artistList.add(new Artist(obj.getJSONObject("artist")));
		}

		this.getWisslAdapter().addAll(artistList);
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
		this.get("artists");
	}

	@Override
	protected void nextPage(Artist entity) {
		Intent intent = new Intent(this, AlbumListActivity.class);
		intent.putExtra(AbstractListActivity.ARTIST_ID, entity.getId());
		this.startActivityIfNeeded(intent, 0);
	}

}
