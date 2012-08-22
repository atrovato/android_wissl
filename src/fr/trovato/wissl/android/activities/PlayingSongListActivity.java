package fr.trovato.wissl.android.activities;

import android.content.ComponentName;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;

public class PlayingSongListActivity extends SongListActivity {

	private boolean bound = false;

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		this.play(position);
	}

	@Override
	protected void loadEntities() {

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		super.onServiceConnected(name, service);

		this.bound = true;
		this.updateSongList();
		this.stopWaiting();
	}

	@Override
	public void onResume() {
		this.updateSongList();
	}

	protected void updateSongList() {
		if (this.bound) {
			this.getWisslAdapter().addAll(this.getPlayingSongList());
		}
	}

}
