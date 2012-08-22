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

	/** Password parameter */
	PASSWORD,

	/** Session ID parameter, with request */
	SESSION_ID("sessionId"),

	/** Preferences parameter */
	PREFS_NAME;

	/** Parameter used for remote */
	private String requestParam;

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
