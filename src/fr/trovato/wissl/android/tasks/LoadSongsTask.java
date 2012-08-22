package fr.trovato.wissl.android.tasks;

import org.json.JSONArray;

import fr.trovato.wissl.android.IRemoteActivity;

public class LoadSongsTask extends RemoteTask {

	private IRemoteActivity activity;

	public LoadSongsTask(IRemoteActivity activity) {
		super();
		this.activity = activity;
	}

	@Override
	public void onPostExecute(JSONArray object) {
		this.activity.receiveSongs(object);
	}

}
