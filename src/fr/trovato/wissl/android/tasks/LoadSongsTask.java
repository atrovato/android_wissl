package fr.trovato.wissl.android.tasks;

import org.json.JSONArray;

import fr.trovato.wissl.android.OnReceiveRemoteSongListener;

/**
 * Class to provide asynchronous request to load songs from Wissl server.
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class LoadSongsTask extends RemoteTask {

	/** Source activity */
	private OnReceiveRemoteSongListener activity;

	/**
	 * Constructor.
	 * 
	 * @param activity
	 *            Source activity
	 */
	public LoadSongsTask(OnReceiveRemoteSongListener activity) {
		super();
		this.activity = activity;
	}

	/**
	 * Call the <code>onReceiveSongs</code> method form the
	 * <code>OnReceiveRemoteSongListener</code>
	 * 
	 * @see OnReceiveRemoteSongListener#onReceiveSongs(JSONArray, int, String)
	 */
	@Override
	public void onPostExecute(JSONArray object) {
		this.activity.onReceiveSongs(object, this.getStatusCode(),
				this.getMessage());
	}

}
