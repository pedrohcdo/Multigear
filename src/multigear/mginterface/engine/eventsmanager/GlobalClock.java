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
	static private long mElapsedTime = 0;
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
		mElapsedTime = 0;
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
			mElapsedTime = Math.min(System.currentTimeMillis() - mRealCurrentTime, frameLimit);
			mVirtualCurrentTime +=	mElapsedTime;
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
	
	/**
	 * Get Elapsed Time
	 * @return
	 */
	final static public float elapsedFramedTime() {
		return mElapsedTime / 17.0f;
	}
}
