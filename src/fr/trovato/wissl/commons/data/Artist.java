package fr.trovato.wissl.commons.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class representing music artist. An artist contains several albums.
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class Artist extends WisslEntity {

	/** Unique artist ID */
	private int id;

	/** Artist name */
	private String name;

	/** Number of albums */
	private int albums;

	/** Number of songs */
	private int songs;

	/** Playtime for all songs in seconds */
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
	public Artist(JSONObject json) throws JSONException {
		super(json);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fromJSON(JSONObject json) throws JSONException {
		this.id = json.getInt("id");
		this.name = json.getString("name");
		this.albums = json.getInt("albums");
		this.songs = json.getInt("songs");
		this.playtime = json.getInt("playtime");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.accumulate("id", this.id);
		json.accumulate("name", this.name);
		json.accumulate("albums", this.albums);
		json.accumulate("songs", this.songs);
		json.accumulate("playtime", this.playtime);

		return json;
	}

	/**
	 * Get the artist name.
	 * 
	 * @return Artist name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the number of albums
	 * 
	 * @return Number of albums
	 */
	public String getNbAlbums() {
		return Integer.toString(this.albums);
	}

	/**
	 * Get the number of songs
	 * 
	 * @return Number of songs
	 */
	public String getNbSongs() {
		return Integer.toString(this.songs);
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
	 * Get Wissl ID
	 * 
	 * @return Wissl ID
	 */
	public int getId() {
		return this.id;
	}

}
