package fr.trovato.wissl.android.remote;

/**
 * Parameters used to rely application and server side.
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public enum RemoteAction {

	/** Login parameter, with request */
	LOGIN("login"),

	/** Username parameter, with request */
	USERNAME("username"),

	/** Password parameter */
	PASSWORD("password"),

	/** Session ID parameter, with request */
	SESSION_ID("sessionId"),

	/** Error message */
	ERROR("message"),

	/** Album ID */
	ALBUM_ID("albumId"),

	/** Artist ID */
	ARTIST_ID("artistId"),
	
	/** Artwork */
	ARTWORK("art"),

	/** Playlist ID */
	PLAYLIST_ID("playlistId"),

	/** Albums */
	ALBUMS("albums"),

	/** Artists */
	ARTISTS("artists"),

	/** Artist */
	ARTIST("artist"),

	/** Songs */
	SONGS("songs"),

	/** Playlist */
	PLAYLIST("playlist"),

	/** Random */
	RANDOM("playlist/random"),

	/** Load playlists action */
	PLAYLISTS("playlists"),

	/** Load playlists action */
	LOAD_PLAYLISTS("playlists");

	/** URI used for remote */
	private String requestURI;

	/** Wissl server enter point */
	public static final String WISSL_ENTRY_POINT = "wissl";

	/**
	 * Constructor
	 * 
	 * @param requestURI
	 *            URI used for remote
	 */
	private RemoteAction(String requestURI) {
		this.requestURI = requestURI;
	}

	/**
	 * Get the request URI
	 * 
	 * @return URI used for remote
	 */
	public String getRequestURI() {
		return this.requestURI;
	}
}
