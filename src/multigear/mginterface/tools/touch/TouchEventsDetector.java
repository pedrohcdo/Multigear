package multigear.mginterface.tools.touch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

/**
 * Untouch Detector
 * @author user
 *
 */
final public class TouchEventsDetector {

	/**
	 * Pointer
	 * 
	 * @author user
	 *
	 */
	final private class Pointer {
		
		int id;
	}
	
	// Final Private Variables
	final private List<Pointer> mPointers = new ArrayList<Pointer>();
	
	// Private Variables
	private TouchEventsDetectorListener mTouchEventsDetectorListener;
	
	/**
	 * Default Constructor
	 */
	public TouchEventsDetector() {}
	
	/**
	 * Constructor
	 * @param listener TouchEvents Detector Listener
	 */
	public TouchEventsDetector(final TouchEventsDetectorListener listener) {
		mTouchEventsDetectorListener = listener;
	}
	
	/**
	 * Set Untouch Detector Listener
	 * @param listener Untouch Detector Listener
	 */
	final public void setListener(final TouchEventsDetectorListener listener) {
		mTouchEventsDetectorListener = listener;
	}
	
	/**
	 * Get Untouch Detector Listener
	 * @return
	 */
	final public TouchEventsDetectorListener getUntouchDetectorListener() {
		return mTouchEventsDetectorListener;
	}
	
	/**
	 * Add pointer
	 * 
	 * @param id
	 * @param touch
	 */
	final private void addPointer(final MotionEvent touch) {
		final int index = MotionEventCompat.getActionIndex(touch);
		final int id = MotionEventCompat.getPointerId(touch, index);
		removePointer(id);
		final Pointer pointer = new Pointer();
		pointer.id = id;
		mPointers.add(pointer);
	}
	
	/**
	 * Remove Pointer
	 * @param id
	 */
	final private void removePointer(final MotionEvent touch) {
		final int index = MotionEventCompat.getActionIndex(touch);
		final int id = MotionEventCompat.getPointerId(touch, index);
		removePointer(id);
	}
	
	/**
	 * Remove Pointer
	 * @param id
	 */
	final private  void removePointer(final int id) {
		final Iterator<Pointer> iterator = mPointers.iterator();
		while(iterator.hasNext()) {
			Pointer pointer = iterator.next();
			if(pointer.id == id) {
				iterator.remove();
			}
		}
	}
	
	/**
	 * Process touch event
	 */
	final public void touch(final MotionEvent touch) {
		switch(MotionEventCompat.getActionMasked(touch)) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			addPointer(touch);
			if(mTouchEventsDetectorListener != null)
				mTouchEventsDetectorListener.onTouch(touch.getPointerCount());
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			removePointer(touch);
			if(mTouchEventsDetectorListener != null)
				mTouchEventsDetectorListener.onUntouch(touch.getPointerCount() - 1);
			break;
		case MotionEvent.ACTION_CANCEL:
			mPointers.clear();
			if(mTouchEventsDetectorListener != null)
				mTouchEventsDetectorListener.onUntouch(0);
			break;
		}
	}
}
