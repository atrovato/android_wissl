package fr.trovato.wissl.commons.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class representing a Wissl session. When an user is connected, a Session is
 * bound to it. There can be only one session per user at a time.
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class Session extends WisslEntity {

	/** Unique user ID */
	private int userId;

	/** User name */
	private String username;

	/** Milliseconds since last server activity */
	private int lastActivity;

	/** Milliseconds since session creation */
	private int createdAt;

	/** ADMIN ONLY: client IP address */
	private String origin;

	/** last played song, or empty */
	private Song lastPlayedSong;

	/**
	 * {@inheritDoc}
	 * 
	 * @see WisslEntity#WisslEntity(JSONObject)
	 */
	public Session(JSONObject json) throws JSONException {
		super(json);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see WisslEntity#fromJSON(JSONObject)
	 */
	@Override
	protected void fromJSON(JSONObject json) throws JSONException {
		this.userId = json.getInt("user_id");
		this.username = json.getString("username");
		this.lastActivity = json.getInt("last_activity");
		this.createdAt = json.getInt("created_at");
		this.origin = json.getString("origin");
		this.lastPlayedSong = new Song(json.getJSONObject("last_played_song"));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see WisslEntity#toJSON()
	 */
	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.accumulate("user_id", this.userId);
		json.accumulate("username", this.username);
		json.accumulate("last_activity", this.lastActivity);
		json.accumulate("created_at", this.createdAt);
		json.accumulate("origin", this.origin);
		json.accumulate("lastPlayedSong", this.lastPlayedSong.toJSON());

		return json;
	}
}
