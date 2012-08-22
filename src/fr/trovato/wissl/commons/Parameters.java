package fr.trovato.wissl.commons;

/**
 * Parameters used to rely application and server side.
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public enum Parameters {

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

	/** Server URL */
	SERVER_URL,

	/** Preferences parameter */
	PREFS_NAME;

	/** Parameter used for remote */
	private String requestParam;

	/** Wissl server enter point */
	public static final String WISSL_ENTRY_POINT = "wissl";

	/**
	 * Constructor
	 * 
	 * @param requestParam
	 *            Parameter used for remote
	 */
	private Parameters(String requestParam) {
		this.requestParam = requestParam;
	}

	/**
	 * Empty constructor
	 */
	private Parameters() {
	}

	/**
	 * Get the request parameter name
	 * 
	 * @return Parameter used for remote
	 */
	public String getRequestParam() {
		return this.requestParam;
	}
}
