package fr.trovato.wissl.android.activities.player;

import android.content.ComponentName;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;

public class PlayingSongListActivity extends SongListActivity {

	private boolean bound = false;

//	@Override
//	public void onClick(View view) {
//		switch (view.getId()) {
//		case R.id.playing:
//			this.finish();
//			break;
//		default:
//			super.onClick(view);
//		}
//	}

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
		this.hideWaiting();
	}

	@Override
	public void onResume() {
		super.onResume();
		this.updateSongList();
	}

	protected void updateSongList() {
		if (this.bound) {
			this.getWisslAdapter().addAll(this.getPlayingSongList());
		}
	}

}
