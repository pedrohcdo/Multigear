package multigear.general.utils;

import android.util.Log;

/**
 * Concurrency Logger
 * 
 * Used to write a log indented with thread concurrency.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class ConcurrencyLogger {
	
	// Private Variables
	private static volatile int mLoggerIdentifier = -1;
	
	/**
	 * Set Logger Identifier
	 * 
	 * @param identifier
	 */
	final static private void setIdentifier(final int identifier, final String tag) {
		if(mLoggerIdentifier != identifier) {
			mLoggerIdentifier = identifier;
			Log.d(tag, "Logger " + identifier + ": ");
		}
	}
	
	/**
	 * Log Debug message
	 */
	final static public void log(final int identifier, final String tag, final String msg) {
		synchronized (ConcurrencyLogger.class) {
			setIdentifier(identifier, tag);
			Log.d(tag, "  " + msg);
		}
	}
}
