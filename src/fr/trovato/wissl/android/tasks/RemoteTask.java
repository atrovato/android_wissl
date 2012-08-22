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
import fr.trovato.wissl.android.IActivity;

public class RemoteTask extends AsyncTask<HttpRequestBase, Void, JSONArray> {

	/** Exception causes application error */
	private Exception exception;

	private int status;
	private IActivity activity;

	public RemoteTask(IActivity activity) {
		this.activity = activity;
	}

	protected RemoteTask() {
		super();
	}

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

				this.status = response.getStatusLine().getStatusCode();

				Log.d(this.getClass().getSimpleName(), "Response status "
						+ response.getStatusLine().toString());

				switch (this.status) {
				case 200:
					break;
				default:
					this.setException(new Exception(json.getString("message")));
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
		if (this.exception == null) {
			this.exception = e;
		}
	}

	/**
	 * Get the exception causing the defect
	 * 
	 * @return The exception or null
	 */
	public Exception getException() {
		return this.exception;
	}

	@Override
	public void onPostExecute(JSONArray object) {
		this.activity.onPostExecute(object);
	}

	public int getStatusCode() {
		return this.status;
	}

}
