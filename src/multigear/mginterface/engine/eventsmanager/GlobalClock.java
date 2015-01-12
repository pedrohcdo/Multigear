package multigear.mginterface.engine.eventsmanager;

import android.util.Log;

/**
 * Global Clock
 * 
 * @author user
 *
 */
final public class GlobalClock {
	
	// Static Variables
	static private long mVirtualCurrentTime = 0;
	static private long mRealCurrentTime = 0;
	static private boolean mHandled = false;
	
	/**
	 * Constructor
	 */
	private GlobalClock() {
		throw new RuntimeException("It is not possible to instantiate this module.");
	}
	
	/**
	 * Set Clock
	 */
	final static protected void set() {
		mRealCurrentTime = System.currentTimeMillis();
	}
	
	/**
	 * Handle clock
	 */
	final static protected void handle() {
		// prevents reset
		if(!mHandled)
			set();
		mHandled = true;
	}
	
	/**
	 * Uhandle clock
	 */
	final static protected void unhandle() {
		mHandled = false;
	}
	
	/**
	 * Update clock
	 * 
	 * @return
	 */
	final static protected void update(final int frameLimit) {
		if(mHandled) {
			mVirtualCurrentTime += Math.min(System.currentTimeMillis() - mRealCurrentTime, frameLimit);
			mRealCurrentTime = System.currentTimeMillis();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	final static public long currentTimeMillis() {
		return mVirtualCurrentTime;
	}
}
