package fr.trovato.wissl.android.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import fr.trovato.wissl.android.remote.Parameters;
import fr.trovato.wissl.android.remote.RemoteAction;

public class CacheStore {

	private static final String LOGGER_ID = "CacheStore";

	private static final String BITMAP_URL = "/Android/data/fr.trovato.wissl";
	private static final String ARTWORK_FILENAME = "artwork";
	private static final String ARTWORK_EXT = ".png";

	private static CacheStore instance;
	private String sessionId;
	private String serverUrl;

	public static CacheStore getInstance(Context context) {
		if (instance == null) {
			instance = new CacheStore();
			instance.init(context);
		}

		return instance;
	}

	private SparseArray<Bitmap> artworkCache;

	private CacheStore() {
		this.artworkCache = new SparseArray<Bitmap>();
	}

	private void init(Context context) {
		// Restore preferences
		SharedPreferences settings = context.getSharedPreferences(
				Parameters.PREFS_NAME.name(), 0);
		this.sessionId = settings.getString(
				RemoteAction.SESSION_ID.getRequestURI(), null);
		this.serverUrl = settings.getString(Parameters.SERVER_URL.name(), null)
				+ "/" + RemoteAction.WISSL_ENTRY_POINT;
	}

	public Bitmap getArtwork(int albumId) {
		return this.artworkCache.valueAt(albumId);
	}

	public void showArtwork(int albumId, ImageView view) {
		Bitmap artwork = this.getArtwork(albumId);

		if (artwork == null) {
			artwork = this.loadArtworkFromFileCache(albumId);
		}

		if (artwork == null) {
			artwork = this.loadArtworkFormStream(albumId);
		}

		if (artwork != null) {
			this.artworkCache.append(albumId, artwork);
			view.setImageBitmap(artwork);
		}

	}

	private Bitmap loadArtworkFormStream(int albumId) {
		Bitmap artwork = null;
		try {
			// TODO do it asynchronously
			String urlString = this.serverUrl + "/"
					+ RemoteAction.ARTWORK.getRequestURI() + "/" + albumId;
			Log.d(LOGGER_ID, urlString);
			InputStream is = this.fetch(urlString);
			artwork = BitmapFactory.decodeStream(is);

			this.artworkCache.put(albumId, artwork);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return artwork;
	}

	public Bitmap loadArtworkFromFileCache(int albumId) {
		String pathName = CacheStore.BITMAP_URL + "/"
				+ CacheStore.ARTWORK_FILENAME + albumId
				+ CacheStore.ARTWORK_EXT;
		return BitmapFactory.decodeFile(pathName);
	}

	private InputStream fetch(String urlString) throws MalformedURLException,
			IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(urlString);
		request.setHeader(RemoteAction.SESSION_ID.getRequestURI(),
				this.sessionId);
		HttpResponse response = httpClient.execute(request);
		return response.getEntity().getContent();
	}
}
