package fr.trovato.wissl.android.adapters;

import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import fr.trovato.wissl.android.R;
import fr.trovato.wissl.android.activities.player.SongListActivity;
import fr.trovato.wissl.commons.data.Song;

/**
 * Graphic adapter to manage a list of songs
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class SongAdapter extends AbstractAdapter<Song> {

	public SongAdapter(SongListActivity context, List<Song> objects) {
		super(context, R.layout.song_item, objects);
	}

	@Override
	protected void completeView(Song currentSong, View rowView) {
		TextView titleView = (TextView) rowView.findViewById(R.id.title);
		TextView albumView = (TextView) rowView.findViewById(R.id.album);
		TextView artistView = (TextView) rowView.findViewById(R.id.artist);
		ImageView artworkView = (ImageView) rowView.findViewById(R.id.artwork);

		titleView.setText(currentSong.getTitle());
		albumView.setText(currentSong.getAlbumName());
		artistView.setText(currentSong.getArtistName());

		this.loadArtwork(currentSong.getAlbumId(), artworkView);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.song_item;
	}

	@Override
	public boolean isPlaying(Song playingSong, Song currentEntity) {
		return currentEntity.getId() == playingSong.getId();
	}

}
