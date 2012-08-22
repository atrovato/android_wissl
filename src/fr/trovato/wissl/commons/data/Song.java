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
	 * {@inheritDoc}
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
	 * {@inheritDoc}
	 * 
	 * @see WisslEntity#toJSON()
	 */
	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.accumulate("id", this.id);
		json.accumulate("title", this.title);
		json.accumulate("position", this.position);
		json.accumulate("disc_no", this.discNumber);
		json.accumulate("duration", this.duration);
		json.accumulate("format", this.format);
		json.accumulate("album_id", this.albumId);
		json.accumulate("album_name", this.albumName);
		json.accumulate("artist_id", this.artistId);
		json.accumulate("artist_name", this.artistName);

		return json;
	}

	@Override
	public String toString() {
		return this.title;
	}

	public int getId() {
		return this.id;
	}

	public String getTitle() {
		return this.title;
	}

	public String getAlbumName() {
		return this.albumName;
	}

	public String getArtistName() {
		return this.artistName;
	}

}