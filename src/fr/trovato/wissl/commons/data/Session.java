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
	 * Build an entity directly from JSON object
	 * 
	 * @param json
	 *            JSON object
	 * @throws JSONException
	 *             JSON error
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
	 * Get user Wissl ID
	 * 
	 * @return User Wissl ID
	 */
	public int getUserId() {
		return this.userId;
	}

	/**
	 * Get user name
	 * 
	 * @return User name
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Get user last activity, milliseconds since last server activity
	 * 
	 * @return User last activity
	 */
	public int getLastActivity() {
		return this.lastActivity;
	}

	/**
	 * Get user session creation, milliseconds since session creation
	 * 
	 * @return User session creation
	 */
	public int getCreation() {
		return this.createdAt;
	}

	/**
	 * Get client IP address : ADMIN ONLY
	 * 
	 * @return Client IP address : ADMIN ONLY
	 */
	public String getOrigin() {
		return this.origin;
	}

	/**
	 * Get last played song, or empty
	 * 
	 * @return Last played song, or empty
	 */
	public Song getLastPlayedSong() {
		return this.lastPlayedSong;
	}
}
