package fr.trovato.wissl.commons.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class representing a Wissl user. Each registered account is represented by an
 * user.
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class User extends WisslEntity {

	/** Unique user ID */
	private int id;

	/** User name */
	private String username;

	/** 1: admin; 2: regular user */
	private int auth;

	/** Total number of bytes downloaded */
	private int dowloaded;

	/**
	 * Build an entity directly from JSON object
	 * 
	 * @param json
	 *            JSON object
	 * @throws JSONException
	 *             JSON error
	 * 
	 * @see WisslEntity#WisslEntity(JSONObject)
	 */
	public User(JSONObject json) throws JSONException {
		super(json);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see WisslEntity#fromJSON(JSONObject)
	 */
	@Override
	protected void fromJSON(JSONObject json) throws JSONException {
		this.id = json.getInt("id");
		this.username = json.getString("username");
		this.auth = json.getInt("auth");
		this.dowloaded = json.getInt("downloaded");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see WisslEntity#toJSON()
	 */
	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.accumulate("id", this.id);
		json.accumulate("username", this.username);
		json.accumulate("auth", this.auth);
		json.accumulate("dowloaded", this.dowloaded);

		return json;
	}

}
