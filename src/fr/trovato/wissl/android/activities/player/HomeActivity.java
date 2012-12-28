package fr.trovato.wissl.android.activities.player;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import fr.trovato.wissl.android.R;
import fr.trovato.wissl.android.adapters.HomeAdapter;
import fr.trovato.wissl.android.data.HomeItem;
import fr.trovato.wissl.android.remote.RemoteAction;
import fr.trovato.wissl.commons.data.Song;

public class HomeActivity extends AbstractPlayerListActivity<HomeItem, HomeAdapter> {

	@Override
	protected List<Song> getSelectedSongs() {
		return null;
	}

	@Override
	protected void nextPage(HomeItem entity) {
		Intent intent = new Intent(this, entity.getIntentClass());

		if (entity.getText() == R.string.random) {
			intent.putExtra(RemoteAction.RANDOM.name(), true);
		}
		this.startActivityIfNeeded(intent, 0);
	}

	@Override
	protected void next(RemoteAction action, JSONObject object)
			throws JSONException {
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

}
