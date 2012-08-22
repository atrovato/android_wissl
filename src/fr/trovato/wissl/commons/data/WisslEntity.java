package fr.trovato.wissl.commons.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Abstract class used to unify used Wissl objects
 * 
 * @author Alexandre.trovato@gmail.com
 * 
 */
public abstract class WisslEntity {

	/**
	 * Build an entity directly from JSON object
	 * 
	 * @param json
	 *            JSON object
	 * @throws JSONException
	 *             JSON error
	 * @see WisslEntity#fromJSON(JSONObject)
	 */
	public WisslEntity(JSONObject json) throws JSONException {
		this.fromJSON(json);
	}

	/**
	 * Build a Wissl entity from a JSON object
	 * 
	 * @param json
	 *            JSON object
	 * @throws JSONException
	 *             JSON error
	 */
	protected abstract void fromJSON(JSONObject json) throws JSONException;

	/**
	 * Build a JSON object from the current entity
	 * 
	 * @return The current entity in JSON format
	 * @throws JSONException
	 *             JSON error
	 */
	public abstract JSONObject toJSON() throws JSONException;
}
