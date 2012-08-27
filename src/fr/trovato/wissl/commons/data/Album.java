package fr.trovato.wissl.commons.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class representing music album. An album contains several songs, and is
 * contained by one artist.
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class Album extends WisslEntity {

	/** Unique album ID */
	private int id;

	/** Album name */
	private String name;

	/** Unique artist ID */
	private int artist;

	/** Artist name */
	private String artistName;

	/** Album release date */
	private String date;

	/** Musical genre */
	private String genre;

	/** Number of songs */
	private int songs;

	/** Playtime for all songs in seconds */
	private int playtime;

	/** TRUE if the server has an artwork for this album */
	private boolean artwork;

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
	public Album(JSONObject json) throws JSONException {
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
		this.artist = json.getInt("artist");
		this.artistName = json.getString("artist_name");
		this.date = json.getString("date");
		this.genre = json.getString("genre");
		this.songs = json.getInt("songs");
		this.playtime = json.getInt("playtime");
		this.artwork = json.getBoolean("artwork");
	}

	/**
	 * Get the album name.
	 * 
	 * @return Album name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the artist name of the album.
	 * 
	 * @return Artist name
	 */
	public String getArtistName() {
		return this.artistName;
	}

	/**
	 * Get the artist Wissl ID
	 * 
	 * @return Artist Wissl ID
	 */
	public int getArtistId() {
		return this.artist;
	}

	/**
	 * Get the year of the album release.
	 * 
	 * @return Year of the album release
	 */
	public String getYear() {
		return this.date;
	}

	/**
	 * Get the number of songs
	 * 
	 * @return Number of songs
	 */
	public int getNbSongs() {
		return this.songs;
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

	/**
	 * Get genre
	 * 
	 * @return Album genre
	 */
	public String getGenre() {
		return this.genre;
	}

	/**
	 * Has artwork ?
	 * 
	 * @return TRUE if album has an artwork
	 */
	public boolean hasArtwork() {
		return this.artwork;
	}

}
