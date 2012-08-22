package fr.trovato.wissl.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import fr.trovato.wissl.android.R;
import fr.trovato.wissl.commons.data.Song;

public class SongAdapter extends AbstractAdapter<Song> {

	public SongAdapter(Context context, List<Song> objects) {
		super(context, R.layout.song_item, objects);
	}

	@Override
	protected void completeView(Song currentSong, View rowView) {
		TextView titleView = (TextView) rowView.findViewById(R.id.title);
		TextView albumView = (TextView) rowView.findViewById(R.id.album);
		TextView artistView = (TextView) rowView.findViewById(R.id.artist);

		titleView.setText(currentSong.getTitle());
		albumView.setText(currentSong.getAlbumName());
		artistView.setText(currentSong.getArtistName());
	}

	@Override
	protected int getLayoutId() {
		return R.layout.song_item;
	}

}
