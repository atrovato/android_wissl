package fr.trovato.wissl.android.adapters;

import java.util.List;

import android.view.View;
import android.widget.TextView;
import fr.trovato.wissl.android.R;
import fr.trovato.wissl.android.activities.player.PlaylistActivity;
import fr.trovato.wissl.commons.data.Playlist;
import fr.trovato.wissl.commons.data.Song;
import fr.trovato.wissl.commons.utils.FormatUtil;

/**
 * Graphic adapter to manage a list of playlists
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class PlaylistAdapter extends AbstractAdapter<Playlist> {

	public PlaylistAdapter(PlaylistActivity context, List<Playlist> objects) {
		super(context, R.layout.playlist_item, objects);
	}

	@Override
	protected void completeView(Playlist currentPlaylist, View rowView) {
		TextView albumView = (TextView) rowView.findViewById(R.id.playlist);
		TextView nbSongsView = (TextView) rowView.findViewById(R.id.nb_songs);
		TextView durationView = (TextView) rowView.findViewById(R.id.duration);

		albumView.setText(currentPlaylist.getPlaylistName());
		nbSongsView.setText(String.valueOf(currentPlaylist.getNbSongs()));
		durationView.setText(FormatUtil.formatDuration(
				currentPlaylist.getDuration(),
				this.getContext().getString(R.string.unknown)));
	}

	@Override
	protected int getLayoutId() {
		return R.layout.playlist_item;
	}

	@Override
	public boolean isPlaying(Song playingSong, Playlist currentEntity) {
		return false;
	}

}
