package fr.trovato.wissl.android.activities.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import fr.trovato.wissl.android.R;
import fr.trovato.wissl.android.adapters.HomeAdapter;
import fr.trovato.wissl.android.data.HomeItem;
import fr.trovato.wissl.android.remote.RemoteAction;
import fr.trovato.wissl.commons.data.Playlist;
import fr.trovato.wissl.commons.data.Song;

/**
 * Activity listing all action available, menu activity.
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class HomeActivity extends
		AbstractPlayerListActivity<HomeItem, HomeAdapter> {

	@Override
	protected List<Song> getSelectedSongs() {
		return null;
	}

	@Override
	protected void nextPage(HomeItem entity) {
		Intent intent = new Intent(this, entity.getIntentClass());

		if (entity.getText() == R.string.random) {
			Map<String, String> params = new HashMap<String, String>(2);
			params.put("name", "Random");
			params.put("number", "20");

			this.post(RemoteAction.RANDOM, RemoteAction.RANDOM.getRequestURI(),
					params);
		} else {
			this.startActivityIfNeeded(intent, 0);
		}
	}

	/**
	 * Start playing random
	 * 
	 * @param entity
	 *            playlist entity
	 */
	private void playRandom(Playlist entity) {
		Intent intent = new Intent(this, SongListActivity.class);
		intent.putExtra(RemoteAction.PLAYLIST_ID.name(), entity.getId());
		this.startActivityIfNeeded(intent, 0);
	}

	@Override
	protected void next(RemoteAction action, JSONObject object)
			throws JSONException {
		switch (action) {
		case RANDOM:
			Playlist entity = new Playlist(object.getJSONObject("playlist"));
			this.playRandom(entity);
			break;
		}
	}

	@Override
	protected HomeAdapter buildAdapter() {
		List<HomeItem> homeItemList = new ArrayList<HomeItem>();
		homeItemList.add(new HomeItem(R.drawable.no_artwork, R.string.playing,
				PlayingSongListActivity.class));
		homeItemList.add(new HomeItem(R.drawable.no_artwork,
				R.string.playlists, PlaylistActivity.class));
		homeItemList.add(new HomeItem(R.drawable.no_artwork, R.string.random,
				PlaylistActivity.class));
		homeItemList.add(new HomeItem(R.drawable.no_artwork, R.string.library,
				ArtistListActivity.class));

		return new HomeAdapter(this, homeItemList);
	}

	@Override
	protected void loadEntities() {
		this.stopWaiting();
	}

	@Override
	public void setPlayingSong(Song song) {

	}

}
