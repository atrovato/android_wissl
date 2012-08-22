package fr.trovato.wissl.commons.utils;

/**
 * Enumeration of all player steps.
 * 
 * @author alexandre.trovato@gmail.com
 * 
 */
public enum Player {

	/** Start playing */
	PLAY,

	/** Pause */
	PAUSE,

	/** Stop playing */
	STOP,

	/** Seeker position */
	SEEK,

	/** Queue state */
	QUEUE,

	/** Error state */
	ERROR;

}
