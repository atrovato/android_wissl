package fr.trovato.wissl.android;

import org.json.JSONArray;

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
	 * @param object
	 *            JSON array to decode
	 * @param statusCode
	 *            Request status code
	 * @param errorMessage
	 *            Error message
	 */
	public void onPostExecute(JSONArray object, int statusCode,
			String errorMessage);

}
