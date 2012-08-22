package fr.trovato.wissl.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import fr.trovato.wissl.android.R;
import fr.trovato.wissl.commons.data.Album;
import fr.trovato.wissl.commons.utils.FormatUtil;

public class AlbumAdapter extends AbstractAdapter<Album> {

	public AlbumAdapter(Context context, List<Album> objects) {
		super(context, R.layout.album_item, objects);
	}

	@Override
	protected void completeView(Album currentAlbum, View rowView) {
		TextView albumView = (TextView) rowView.findViewById(R.id.album);
		TextView yearView = (TextView) rowView.findViewById(R.id.year);
		TextView nbSongsView = (TextView) rowView.findViewById(R.id.nb_songs);
		TextView durationView = (TextView) rowView.findViewById(R.id.duration);
		TextView artistView = (TextView) rowView.findViewById(R.id.artist);

		albumView.setText(currentAlbum.getName());
		yearView.setText(currentAlbum.getYear());
		nbSongsView.setText(currentAlbum.getNbSongs());
		durationView.setText(FormatUtil.formatDuration(
				currentAlbum.getDuration(),
				this.getContext().getString(R.string.unknown)));
		artistView.setText(currentAlbum.getArtistName());
	}

	@Override
	protected int getLayoutId() {
		return R.layout.album_item;
	}
}
