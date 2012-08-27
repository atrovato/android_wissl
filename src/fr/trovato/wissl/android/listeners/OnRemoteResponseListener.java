package fr.trovato.wissl.android.listeners;

import org.json.JSONArray;

import fr.trovato.wissl.android.remote.RemoteAction;

/**
 * Interface used by remote tasks to handler remote responses.
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public interface OnRemoteResponseListener {

	/**
	 * Action to execute after remote response.
	 * 
	 * @param action
	 *            Received remote action
	 * @param object
	 *            JSON array to decode
	 * @param statusCode
	 *            Request status code
	 * @param errorMessage
	 *            Error message
	 */
	public void onPostExecute(RemoteAction action, JSONArray object,
			int statusCode, String errorMessage);

}
