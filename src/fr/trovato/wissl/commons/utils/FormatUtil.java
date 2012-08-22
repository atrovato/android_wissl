package fr.trovato.wissl.commons.utils;

/**
 * Utils to format data to human string.
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public class FormatUtil {

	/**
	 * Get an integer representing a time in seconds and format it into a
	 * [h]h:mm:ss string.
	 * 
	 * @param duration
	 *            Duration in seconds
	 * @param defaultValue
	 *            Default value
	 * @return The formated string, or <code>defaultValue</code> if duration is
	 *         under 0
	 */
	public static String formatDuration(int duration, String defaultValue) {
		if (duration >= 0) {
			// Hours
			int hours = duration / 3600;
			// Minutes
			int minutes = (duration % 3600) / 60;
			// Secondes
			int secondes = duration % 60;

			// Format to [h]h:mm:ss
			return hours + ":" + String.format("%02d", minutes) + ":"
					+ String.format("%02d", secondes);
		} else {
			return defaultValue;
		}
	}
}
