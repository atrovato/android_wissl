package fr.trovato.wissl.android;

import org.json.JSONArray;

/**
 * Interface used by remote tasks to handler remote songs.
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public interface OnReceiveRemoteSongListener {

	/**
	 * Action to execute after remote response.
	 * 
	 * @param object
	 *            JSON array to decode
	 * @param statusCode
	 *            Request status code
	 * @param errorMessage
	 *            Error message
	 */
	public void onReceiveSongs(JSONArray object, int statusCode,
			String errorMessage);

}
