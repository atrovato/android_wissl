package fr.trovato.wissl.commons.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class representing music song. A song is contained by one album and one
 * artist. Each song can be added to multiple playlists.
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class Song extends WisslEntity {

	/** Unique song ID */
	private int id;

	/** Song title */
	private String title;

	/** Song position in album */
	private int position;

	/** Disc number when multiple volumes */
	private int discNumber;

	/** Song duration in seconds */
	private int duration;

	/** Audio mimetype, ie 'audio/mp3' */
	private String format;

	/** Unique album ID */
	private int albumId;

	/** Album name */
	private String albumName;

	/** Unique artist ID */
	private int artistId;

	/** Artist name */
	private String artistName;

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
	public Song(JSONObject json) throws JSONException {
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
		this.title = json.getString("title");
		this.position = json.getInt("position");
		this.discNumber = json.getInt("disc_no");
		this.duration = json.getInt("duration");
		this.format = json.getString("format");
		this.albumId = json.getInt("album_id");
		this.albumName = json.getString("album_name");
		this.artistId = json.getInt("artist_id");
		this.artistName = json.getString("artist_name");
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
	 * Get the song title.
	 * 
	 * @return Song title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Get the album name of the song.
	 * 
	 * @return Album name
	 */
	public String getAlbumName() {
		return this.albumName;
	}

	/**
	 * Get the artist name of the song.
	 * 
	 * @return Artist name
	 */
	public String getArtistName() {
		return this.artistName;
	}

	/**
	 * Get the song position into its album
	 * 
	 * @return Song position
	 */
	public int getPosition() {
		return this.position;
	}

	/**
	 * Get disc number when multiple volumes
	 * 
	 * @return Disc number
	 */
	public int getDiscNumber() {
		return this.discNumber;
	}

	/**
	 * Get song duration in seconds
	 * 
	 * @return Song duration in seconds
	 */
	public int getDuration() {
		return this.duration;
	}

	/**
	 * Get audio mimetype, ie 'audio/mp3'
	 * 
	 * @return Audio mimetype
	 */
	public String getFormat() {
		return this.format;
	}

	/**
	 * Get the album Wissl ID.
	 * 
	 * @return Album Wissl ID
	 */
	public int getAlbumId() {
		return this.albumId;
	}

	/**
	 * Get the artist Wissl ID.
	 * 
	 * @return Artist Wissl ID
	 */
	public int getArtistId() {
		return this.artistId;
	}

}