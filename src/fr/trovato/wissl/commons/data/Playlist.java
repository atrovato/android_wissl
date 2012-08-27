package fr.trovato.wissl.commons.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class representing a playlist. A playlist is a list of songs created by an
 * user.
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class Playlist extends WisslEntity {

	/** Unique playlist ID */
	private int id;

	/** Playlist name */
	private String name;

	/** Unique user id, playlist owner */
	private int user;

	/** Number of songs */
	private int songs;

	/** Total duration of all songs in seconds */
	private int playtime;

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
	public Playlist(JSONObject json) throws JSONException {
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
		this.name = json.getString("name");
		this.user = json.getInt("user");
		this.songs = json.getInt("songs");
		this.playtime = json.getInt("playtime");
	}

	/**
	 * Get Wissl ID
	 * 
	 * @return Wissl ID
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Get playlist name
	 * 
	 * @return Playlist name
	 */
	public String getPlaylistName() {
		return this.name;
	}

	/**
	 * Get user Wissl ID
	 * 
	 * @return User Wissl ID
	 */
	public int getUserId() {
		return this.user;
	}

	/**
	 * Get global playing duration
	 * 
	 * @return Duration
	 */
	public int getDuration() {
		return this.playtime;
	}

	/**
	 * Get the number of songs
	 * 
	 * @return Number of songs
	 */
	public int getNbSongs() {
		return this.songs;
	}

}
