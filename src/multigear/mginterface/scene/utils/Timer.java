package multigear.mginterface.scene.utils;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.scene.Installation;

/**
 * Updater
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public abstract class Timer extends Installation {
	
	// Private Variables
	private long mLastTime;
	boolean mRepeat;
	int mDelay;
	
	/**
	 * Constructor
	 * @param timerInMillis
	 * @param repeat
	 */
	public Timer(final int timerInMillis, boolean repeat) {
		mDelay = timerInMillis;
		mRepeat = repeat;
	}
	
	/** Unused */
	@Override
	protected void time(long thisTime) {}
			
	/** Unused */
	@Override
	final public void setup() {
		mLastTime = getFatherRoom().getThisTime();
	}
	
	/** Unused */
	@Override
	public void cache() {}
	
	/** Unused */
	@Override
	public void screen() {}
	
	/**
	 * Update
	 */
	@Override
	final public void update() {
		long time = getFatherRoom().getThisTime();
		if((time - mLastTime) > mDelay) {
			onTime();
			if(!mRepeat)
				requestUninstall();
			else {
				mLastTime = time;
			}
		}
	}
	
	/**
	 * Set Timer
	 * @param timerInMillis
	 */
	final public void setTimer(final int timerInMillis) {
		mDelay = timerInMillis;
	}
	
	/**
	 * Set Repeat
	 * @param repeat
	 */
	final public void setRepeat(final boolean repeat) {
		mRepeat = repeat;
	}
	
	/**
	 * Get Timer
	 * @param timerInMillis
	 */
	final public long getTimer() {
		return mDelay;
	}
	
	/**
	 * Get Repeat
	 * @param repeat
	 */
	final public boolean getRepeat() {
		return mRepeat;
	}
	
	/** Unused */
	@Override
	@SuppressLint("WrongCall")
	public void draw(Drawer drawer) {}
	
	/** Unused */
	@Override
	public void touch(MotionEvent motionEvent) {}
	
	/** Unused */
	@Override
	public void finish() {}
	
	/**
	 * Updater
	 */
	public abstract void onTime();
}
