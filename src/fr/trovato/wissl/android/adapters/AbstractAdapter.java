package fr.trovato.wissl.android.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import fr.trovato.wissl.android.activities.player.AbstractPlayerListActivity;
import fr.trovato.wissl.commons.data.Song;
import fr.trovato.wissl.commons.data.WisslEntity;

public abstract class AbstractAdapter<ENTITY extends WisslEntity> extends
		ArrayAdapter<ENTITY> {

	public AbstractAdapter(
			AbstractPlayerListActivity<ENTITY, ? extends ArrayAdapter<ENTITY>> context,
			int layout, List<ENTITY> objects) {
		super(context, layout, objects);
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(this.getLayoutId(), parent, false);

		ENTITY currentEntity = this.getItem(position);

		CheckBox checkBox = (CheckBox) rowView
				.findViewById(android.R.id.checkbox);
		checkBox.setTag(position);

		this.completeView(currentEntity, rowView);

		// Song playingSong = this.playerService.getPlayingSong();
		// if (playingSong != null && this.isPlaying(playingSong,
		// currentEntity)) {
		// rowView.setBackgroundColor(this.getContext().getResources()
		// .getColor(android.R.color.background_dark));
		// }

		return rowView;
	}

	protected abstract boolean isPlaying(Song playingSong, ENTITY currentEntity);

	protected abstract void completeView(ENTITY currentEntity, View rowView);

	protected abstract int getLayoutId();

	public void addAll(List<ENTITY> entityList) {
		for (ENTITY entity : entityList) {
			this.add(entity);
		}
	}

}
