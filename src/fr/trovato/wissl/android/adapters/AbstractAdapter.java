package fr.trovato.wissl.android.adapters;

import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import fr.trovato.wissl.android.activities.player.AbstractPlayerListActivity;
import fr.trovato.wissl.android.listeners.OnRemoteArtworkListener;
import fr.trovato.wissl.android.remote.RemoteAction;
import fr.trovato.wissl.android.tasks.RemoteArtworkTask;
import fr.trovato.wissl.commons.data.Song;
import fr.trovato.wissl.commons.data.WisslEntity;

public abstract class AbstractAdapter<ENTITY extends WisslEntity> extends
		ArrayAdapter<ENTITY> implements OnRemoteArtworkListener {

	private SparseArray<Bitmap> cache;

	public AbstractAdapter(
			AbstractPlayerListActivity<ENTITY, ? extends ArrayAdapter<ENTITY>> context,
			int layout, List<ENTITY> objects) {
		super(context, layout, objects);

		this.cache = new SparseArray<Bitmap>();
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

	protected void loadArtwork(int albumId, ImageView view) {
		if (this.cache.get(albumId) == null) {
			RemoteArtworkTask task = new RemoteArtworkTask(albumId, this);
			HttpRequestBase params = new HttpGet(
					((AbstractPlayerListActivity<?, ?>) this.getContext())
							.getServerUrl()
							+ "/"
							+ RemoteAction.ARTWORK.getRequestURI()
							+ "/"
							+ albumId);
			task.execute(params);
		} else {
			view.setImageBitmap(this.cache.get(albumId));
		}
	}

	@Override
	public void onArtworkLoaded(int albumId, Bitmap bitmap) {
		this.cache.append(albumId, bitmap);
	}

}
