package multigear.mginterface.touch;

import multigear.general.utils.Vector2;
import android.view.MotionEvent;

/**
 * Scale Detector
 * @author user
 *
 */
final public class ScaleDetector {

	// Private Variables
	private ScaleDetectorListener mScaleDetectorListener;
	private Vector2[] mLastPositions = {new Vector2(), new Vector2()};
	
	/**
	 * Set Scale Detector Listener
	 * @param listener Scale Detector Listener
	 */
	final public void setListener(final ScaleDetectorListener listener) {
		mScaleDetectorListener = listener;
	}
	
	/**
	 * Get Scale Detector Listener
	 * @return
	 */
	final public ScaleDetectorListener getScaleDetectorListener() {
		return mScaleDetectorListener;
	}

	/**
	 * Process touch event
	 */
	final public void touch(final MotionEvent touch) {
		
	}
}
