package multigear.mginterface.touch;

import multigear.general.utils.Vector2;


/**
 * Untouch Detector Listener
 * 
 * @author user
 *
 */
public interface TouchEventsDetectorListener {
	
	/**
	 * On Touch<br>
	 * <b>Note:</b> The event is not called more than once while the
	 *  situation is the same.
	 * @param scale
	 */
	public void onTouch(final int pointerCount);
	
	/**
	 * On Untouch<br>
	 * <b>Note:</b> The event is not called more than once while the
	 *  situation is the same.
	 * @param scale
	 */
	public void onUntouch(final int pointerCount);
}
