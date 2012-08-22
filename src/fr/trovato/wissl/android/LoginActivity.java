package fr.trovato.wissl.android;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import fr.trovato.wissl.android.activities.ArtistListActivity;
import fr.trovato.wissl.android.tasks.RemoteTask;
import fr.trovato.wissl.commons.Parameters;

/**
 * Android activity to log on the application.
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class LoginActivity extends Activity implements OnRemoteResponseListener,
		OnClickListener {

	/** Activity settings */
	private SharedPreferences preferences;

	/**
	 * Create all needs of the application.<br>
	 * - Restore preferences<br>
	 * - Load session<br>
	 * - Load layout<br>
	 * - Load form values<br>
	 * 
	 * @see Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// Restore preferences
		this.preferences = this.getSharedPreferences(
				Parameters.PREFS_NAME.name(), Context.MODE_PRIVATE);

		// Restore session ID or null
		String sessionId = this.getSharedPreferences().getString(
				Parameters.SESSION_ID.name(), null);

		if (sessionId != null) {
			this.authenticate(sessionId);
		}

		// Load up the layout
		this.setContentView(R.layout.login);

		// Submit form on <code>login</code> button click
		Button login = (Button) this.findViewById(R.id.login_button);
		login.setOnClickListener(this);

		// Restore form input values
		EditText formEditText = (EditText) this.findViewById(R.id.txt_server);
		formEditText.setText(this.getSharedPreferences().getString(
				Parameters.SERVER_URL.name(), null));

		formEditText = (EditText) this.findViewById(R.id.txt_username);
		formEditText.setText(this.getSharedPreferences().getString(
				Parameters.USERNAME.name(), null));

		formEditText = (EditText) this.findViewById(R.id.txt_password);
		formEditText.setText(this.getSharedPreferences().getString(
				Parameters.PASSWORD.name(), null));
	}

	/**
	 * On form submit, send request to Wissl server to authenticate.
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// Get server URL
		EditText formEditText = (EditText) this.findViewById(R.id.txt_server);
		String serverUrlValue = formEditText.getText().toString();

		// Get username
		formEditText = (EditText) this.findViewById(R.id.txt_username);
		String usernameValue = formEditText.getText().toString();

		// Get password
		formEditText = (EditText) this.findViewById(R.id.txt_password);
		String passwordValue = formEditText.getText().toString();

		// No empty value
		if (serverUrlValue.trim().length() > 0
				&& usernameValue.trim().length() > 0
				&& passwordValue.trim().length() > 0) {
			Map<String, String> params = new HashMap<String, String>();
			params.put(Parameters.USERNAME.getRequestParam(), usernameValue);
			params.put(Parameters.PASSWORD.getRequestParam(), passwordValue);

			this.post(serverUrlValue + "/" + Parameters.WISSL_ENTRY_POINT + "/"
					+ Parameters.LOGIN.getRequestParam(), params);

			SharedPreferences.Editor editor = this.getSharedPreferences()
					.edit();
			editor.putString(Parameters.SERVER_URL.name(), serverUrlValue);
			editor.putString(Parameters.USERNAME.name(), usernameValue);
			editor.putString(Parameters.PASSWORD.name(), passwordValue);
			editor.commit();
		} else {
			this.showErrorDialog(this.getString(R.string.no_empty_field));
		}
	}

	/**
	 * Decode received response and enter the application
	 */
	@Override
	public void onPostExecute(JSONArray object, int statusCode,
			String errorMessage) {
		if (statusCode != 200) {
			this.showErrorDialog(errorMessage);
		}

		try {
			JSONObject json = object.getJSONObject(0);
			String sessionId = json.getString(Parameters.SESSION_ID
					.getRequestParam());

			this.authenticate(sessionId);
		} catch (JSONException e) {
			this.showErrorDialog(e.getMessage());
		}
	}

	/**
	 * Save session ID and go to main menu of the application.
	 * 
	 * @param sessionId
	 *            Wissl server session OD
	 */
	private void authenticate(String sessionId) {
		if (sessionId != null) {
			SharedPreferences.Editor editor = this.getSharedPreferences()
					.edit();
			editor.putString(Parameters.SESSION_ID.name(), sessionId);
			editor.commit();

			Intent intent = new Intent(this, ArtistListActivity.class);
			this.startActivityIfNeeded(intent, 0);
		}
	}

	/**
	 * Get application preferences.
	 * 
	 * @return Application preferences
	 */
	private SharedPreferences getSharedPreferences() {
		return this.preferences;
	}

	/**
	 * Send remote request
	 * 
	 * @param request
	 *            Request to send
	 */
	private void connect(HttpRequestBase request) {
		new RemoteTask(this).execute(request);
	}

	/**
	 * Send POST request to Wissl server
	 * 
	 * @param uri
	 *            Request URI
	 * @param params
	 *            Parameters to send
	 */
	private void post(String uri, Map<String, String> params) {
		try {
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();

			if (params != null) {
				for (Entry<String, String> line : params.entrySet()) {
					paramList.add(new BasicNameValuePair(line.getKey(), line
							.getValue()));
				}
			}

			HttpEntity entity = new UrlEncodedFormEntity(paramList);
			HttpPost request = new HttpPost(uri);
			request.setEntity(entity);

			this.connect(request);
		} catch (UnsupportedEncodingException e) {
			this.showErrorDialog(e.getMessage());
		}
	}

	/**
	 * Show alert dialog.
	 * 
	 * @param message
	 *            Text to show
	 */
	private void showErrorDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setNeutralButton("Ok",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}

				});
		builder.create();
		builder.show();
	}

}