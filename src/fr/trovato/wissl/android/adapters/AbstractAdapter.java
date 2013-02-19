package fr.trovato.wissl.android.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import fr.trovato.wissl.android.activities.player.AbstractPlayerListActivity;
import fr.trovato.wissl.android.data.CacheStore;
import fr.trovato.wissl.commons.data.Song;

public abstract class AbstractAdapter<ENTITY> extends ArrayAdapter<ENTITY> {

	private boolean selectable;

	public AbstractAdapter(
			AbstractPlayerListActivity<ENTITY, ? extends ArrayAdapter<ENTITY>> context,
			int layout, List<ENTITY> objects) {
		super(context, layout, objects);

		this.setSelectable(true);
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(this.getLayoutId(), parent, false);

		ENTITY currentEntity = this.getItem(position);

		if (this.isSelectable()) {
			CheckBox checkBox = (CheckBox) rowView
					.findViewById(android.R.id.checkbox);
			checkBox.setTag(position);
		}

		this.completeView(currentEntity, rowView);

		return rowView;
	}

	protected boolean isSelectable() {
		return this.selectable;
	}

	protected void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public abstract boolean isPlaying(Song playingSong, ENTITY currentEntity);

	protected abstract void completeView(ENTITY currentEntity, View rowView);

	protected abstract int getLayoutId();

	public void addAll(List<ENTITY> entityList) {
		for (ENTITY entity : entityList) {
			this.add(entity);
		}
	}

	protected void loadArtwork(int albumId, ImageView view) {
		Bitmap artwork = CacheStore.getInstance().getArtowrk(albumId);

		if (artwork != null) {
			view.setImageBitmap(artwork);
		}
	}
	
}
