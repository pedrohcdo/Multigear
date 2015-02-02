package multigear.mginterface.tools.touch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import multigear.general.utils.GeneralUtils;
import multigear.general.utils.Vector2;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Focus Detector
 * @author user
 *
 */
final public class FocusDetector {

	/**
	 * Pointer
	 * 
	 * @author user
	 *
	 */
	final private class Pointer {
		
		int id;
		boolean firstInteraction;
		Vector2 lastPosition;
		Vector2 framePosition;
	}
	
	// Final Private Variables
	final private List<Pointer> mPointers = new ArrayList<Pointer>();
	
	// Private Variables
	private FocusDetectorListener mFocusDetectorListener;
	
	/**
	 * Default Constructor
	 */
	public FocusDetector() {}
	
	/**
	 * Constructor
	 * @param listener Focus Detector Listener
	 */
	public FocusDetector(final FocusDetectorListener listener) {
		mFocusDetectorListener = listener;
	}
	
	/**
	 * Set Focus Detector Listener
	 * @param listener Focus Detector Listener
	 */
	final public void setListener(final FocusDetectorListener listener) {
		mFocusDetectorListener = listener;
	}
	
	/**
	 * Get Focus Detector Listener
	 * @return
	 */
	final public FocusDetectorListener getFocusDetectorListener() {
		return mFocusDetectorListener;
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
		final Vector2 position = new Vector2(MotionEventCompat.getX(touch, index), MotionEventCompat.getY(touch, index));
		removePointer(id);
		final Pointer pointer = new Pointer();
		pointer.id = id;
		pointer.lastPosition = position;
		pointer.framePosition = position;
		pointer.firstInteraction = true;
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
	 * Move Pointers
	 * @param touch
	 */
	final private void movePointers(final MotionEvent touch) {
		for(int index=0; index<MotionEventCompat.getPointerCount(touch); index++) {
			final Vector2 position = new Vector2(MotionEventCompat.getX(touch, index), MotionEventCompat.getY(touch, index));
			final int id = MotionEventCompat.getPointerId(touch, index);
			for(final Pointer pointer : mPointers) {
				if(pointer.id == id) {
					pointer.lastPosition = pointer.framePosition;
					pointer.framePosition = position;
					break;
				}
			}
		}
	}
	
	/**
	 * Update Move
	 */
	final private void updateMove() {
		if(mPointers.size() >= 2) {
			final Pointer a = mPointers.get(0);
			final Pointer b = mPointers.get(1);
			final Vector2 aLastPosition = a.lastPosition;
			final Vector2 bLastPosition = b.lastPosition;
			if(mFocusDetectorListener != null) {
				//if(a.firstInteraction || b.firstInteraction) {
					a.firstInteraction = false;
					b.firstInteraction = false;
					mFocusDetectorListener.onFocus(aLastPosition.position(bLastPosition, 0.5f));
				//}
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
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			removePointer(touch);
			break;
		case MotionEvent.ACTION_CANCEL:
			mPointers.clear();
			break;
		case MotionEvent.ACTION_MOVE:
			movePointers(touch);
			updateMove();
			break;
		}
	}
}
