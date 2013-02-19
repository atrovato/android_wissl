package fr.trovato.wissl.android.adapters;

import java.util.List;

import android.view.View;
import android.widget.TextView;
import fr.trovato.wissl.android.R;
import fr.trovato.wissl.android.activities.player.HomeActivity;
import fr.trovato.wissl.android.data.HomeItem;
import fr.trovato.wissl.commons.data.Song;

/**
 * Graphic adapter to manage a list of home menu
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class HomeAdapter extends AbstractAdapter<HomeItem> {

	public HomeAdapter(HomeActivity context, List<HomeItem> objects) {
		super(context, R.layout.home_item, objects);
		this.setSelectable(false);
	}

	@Override
	public boolean isPlaying(Song playingSong, HomeItem currentEntity) {
		return false;
	}

	@Override
	protected void completeView(HomeItem currentEntity, View rowView) {
		TextView textView = (TextView) rowView.findViewById(R.id.text);
		textView.setText(currentEntity.getText());
	}

	@Override
	protected int getLayoutId() {
		return R.layout.home_item;
	}

}
