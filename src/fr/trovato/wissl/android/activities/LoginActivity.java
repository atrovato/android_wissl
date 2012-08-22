package fr.trovato.wissl.android.activities;

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
import fr.trovato.wissl.android.AbstractListActivity;
import fr.trovato.wissl.android.IActivity;
import fr.trovato.wissl.android.R;
import fr.trovato.wissl.android.tasks.RemoteTask;
import fr.trovato.wissl.commons.Parameters;

public class LoginActivity extends Activity implements IActivity,
		OnClickListener {

	/** URL of the server */
	private String SERVER_URI = "192.168.1.4:8080";

	/** Activity settings */
	private SharedPreferences settings;

	private RemoteTask remoteTask;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// Restore preferences
		this.settings = this.getSharedPreferences(Parameters.PREFS_NAME.name(),
				Context.MODE_PRIVATE);

		String sessionId = this.getSettings().getString(
				Parameters.SESSION_ID.name(), null);
		if (sessionId != null) {
			this.authenticate(sessionId);
		}

		// load up the layout
		this.setContentView(R.layout.login);

		// get the button resource in the xml file and assign it to a local
		// variable of type Button

		Button login = (Button) this.findViewById(R.id.login_button);
		login.setOnClickListener(this);

		this.setUserNameText(this.getSettings().getString("Login", ""));

		this.setPasswordText(this.getSettings().getString("Password", ""));
	}

	public void setUserNameText(String $username) {
		EditText usernameEditText = (EditText) this
				.findViewById(R.id.txt_username);
		usernameEditText.setText($username);
	}

	public void setPasswordText(String $username) {
		EditText passwordEditText = (EditText) this
				.findViewById(R.id.txt_password);
		passwordEditText.setText($username);

	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */

	@Override
	public void onClick(View v) {

		// this gets the resources in the xml file

		// and assigns it to a local variable of type EditText

		EditText usernameEditText = (EditText) this
				.findViewById(R.id.txt_username);

		EditText passwordEditText = (EditText) this
				.findViewById(R.id.txt_password);

		// the getText() gets the current value of the text box

		// the toString() converts the value to String data type

		// then assigns it to a variable of type String

		String sUserName = usernameEditText.getText().toString();

		String sPassword = passwordEditText.getText().toString();

		// call the backend using Get parameters (discouraged but works good for
		// this exampl <img
		// src="http://www.instropy.com/wp-includes/images/smilies/icon_wink.gif"
		// alt=";)" class="wp-smiley"> )

		if (usernameEditText == null || passwordEditText == null) {
			// show some warning
		} else {
			// display the username and the password in string format
			this.showBusyCursor(true);

			Map<String, String> params = new HashMap<String, String>();
			params.put(Parameters.LOGIN.name(), sUserName);
			params.put(Parameters.PASSWORD.name(), sPassword);
			this.post(Parameters.LOGIN.getRequestParam(), params);

			SharedPreferences.Editor editor = this.getSettings().edit();
			editor.putString(Parameters.LOGIN.name(), sUserName);
			editor.putString(Parameters.PASSWORD.name(), sPassword);
			editor.commit();

			this.showBusyCursor(false);
		}// end else

	}// end OnClick

	private void showBusyCursor(Boolean show) {
		this.setProgressBarIndeterminateVisibility(show);
	}

	@Override
	public void onPostExecute(JSONArray object) {
		try {
			JSONObject json = object.getJSONObject(0);
			String sessionId = json.getString(Parameters.SESSION_ID
					.getRequestParam());

			this.authenticate(sessionId);
		} catch (JSONException e) {
			this.showErrorDialog(e.getMessage());
		}

	}

	public void authenticate(String sessionId) {
		if (sessionId != null) {
			SharedPreferences.Editor editor = this.getSettings().edit();
			editor.putString(Parameters.SESSION_ID.name(), sessionId);
			editor.commit();

			Intent intent = new Intent(this, ArtistListActivity.class);
			intent.putExtra(AbstractListActivity.ALBUM_ID, 3);
			this.startActivity(intent);
		}
	}

	public SharedPreferences getSettings() {
		return this.settings;
	}

	/**
	 * Get the Wissl server URL
	 * 
	 * @returnThe server URL
	 */
	protected String getServerUrl() {
		return "http://" + this.SERVER_URI + "/wissl";
	}

	private void connect(HttpRequestBase request) {
		if (this.remoteTask == null) {
			this.remoteTask = new RemoteTask(this);
		}

		this.remoteTask.execute(request);
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
			HttpPost request = new HttpPost(this.getServerUrl() + "/" + uri);
			request.setEntity(entity);

			this.connect(request);
		} catch (UnsupportedEncodingException e) {
			this.showErrorDialog(e.getMessage());
		}
	}

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