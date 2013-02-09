package fr.trovato.wissl.android.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import fr.trovato.wissl.android.listeners.OnRemoteResponseListener;
import fr.trovato.wissl.android.remote.RemoteAction;

/**
 * Class to provide asynchronous request to Wissl server.
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class RemoteTask extends AsyncTask<HttpRequestBase, Void, JSONArray> {

	/** MEssage exception causes application error */
	private String message;

	/** Request status code */
	private int statusCode;

	/** Source activity */
	private OnRemoteResponseListener activity;

	/** Remote action */
	private RemoteAction remoteAction;

	/**
	 * Constructor.
	 * 
	 * @param action
	 *            Remote action to apply
	 * @param activity
	 *            Source activity
	 */
	public RemoteTask(RemoteAction action, OnRemoteResponseListener activity) {
		this.remoteAction = action;
		this.activity = activity;
	}

	/**
	 * Constructor used to override this task.
	 */
	protected RemoteTask() {
		super();
	}

	/**
	 * Send request to the remote server and get result as a JSON array of JSON
	 * objet
	 */
	@Override
	protected JSONArray doInBackground(HttpRequestBase... params) {
		int reqSize = params.length;

		JSONArray resultArray = new JSONArray();

		for (int i = 0; i < reqSize; i++) {
			HttpRequestBase request = params[i];
			Log.d(this.getClass().getSimpleName(),
					"Requesting " + request.getURI());

			HttpClient httpclient = new DefaultHttpClient();

			// Execute the request
			HttpResponse response;

			JSONObject json = new JSONObject();

			try {
				response = httpclient.execute(request);

				HttpEntity entity = response.getEntity();

				if (entity != null) {

					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
					String result = this.convertStreamToString(instream);

					json = new JSONObject(result);

					Log.d(this.getClass().getSimpleName(),
							"Response : " + json.toString());

					instream.close();
				}

				this.statusCode = response.getStatusLine().getStatusCode();

				Log.d(this.getClass().getSimpleName(), "Response status "
						+ response.getStatusLine().toString());

				switch (this.statusCode) {
					case 200:
						break;
					default:
						this.setException(new Exception(
								json.getString(RemoteAction.ERROR
										.getRequestURI())));
				}
			} catch (ClientProtocolException e) {
				this.setException(e);
			} catch (IOException e) {
				this.setException(e);
			} catch (JSONException e) {
				this.setException(e);
			}

			resultArray.put(json);
		}

		return resultArray;
	}

	/**
	 * Convert a stream to a string
	 * 
	 * @param is
	 *            The stream
	 * @return The resulting string
	 */
	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	/**
	 * Set the cause exception
	 * 
	 * @param e
	 *            The exception
	 */
	protected void setException(Exception e) {
		if (this.message == null) {
			this.message = e.getMessage();
		}
	}

	/**
	 * Get the exception message causing the defect
	 * 
	 * @return The exception message or null
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Call the <code>onPostExecute</code> method form the
	 * <code>OnRemoteResponseListener</code>
	 * 
	 * @see OnRemoteResponseListener#onPostExecute(RemoteAction, JSONArray, int,
	 *      String)
	 */
	@Override
	public void onPostExecute(JSONArray object) {
		this.activity.onPostExecute(this.remoteAction, object,
				this.getStatusCode(), this.message);
	}

	/**
	 * Get the request status code
	 * 
	 * @return Status code
	 */
	protected int getStatusCode() {
		return this.statusCode;
	}

}
