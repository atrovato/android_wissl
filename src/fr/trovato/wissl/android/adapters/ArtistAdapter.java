package fr.trovato.wissl.android.adapters;

import java.util.List;

import android.view.View;
import android.widget.TextView;
import fr.trovato.wissl.android.R;
import fr.trovato.wissl.android.activities.player.ArtistListActivity;
import fr.trovato.wissl.commons.data.Artist;
import fr.trovato.wissl.commons.data.Song;
import fr.trovato.wissl.commons.utils.FormatUtil;

/**
 * Graphic adapter to manage a list of artists
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class ArtistAdapter extends AbstractAdapter<Artist> {

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The application context
	 * @param objects
	 *            The list of artists to manage
	 */
	public ArtistAdapter(ArtistListActivity context, List<Artist> objects) {
		super(context, R.layout.artist_item, objects);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void completeView(Artist currentArtist, View rowView) {
		// Artist name
		TextView textView = (TextView) rowView.findViewById(R.id.artist);
		textView.setText(currentArtist.getName());

		// Number of albums
		textView = (TextView) rowView.findViewById(R.id.nb_albums);
		textView.setText(String.valueOf(currentArtist.getNbAlbums()));

		// Number of songs
		textView = (TextView) rowView.findViewById(R.id.nb_songs);
		textView.setText(String.valueOf(currentArtist.getNbSongs()));

		// Global playing time
		textView = (TextView) rowView.findViewById(R.id.duration);
		textView.setText(FormatUtil.formatDuration(currentArtist.getDuration(),
				this.getContext().getString(R.string.unknown)));
	}

	@Override
	protected int getLayoutId() {
		return R.layout.artist_item;
	}

	@Override
	public boolean isPlaying(Song playingSong, Artist currentEntity) {
		return currentEntity.getId() == playingSong.getArtistId();
	}

}
