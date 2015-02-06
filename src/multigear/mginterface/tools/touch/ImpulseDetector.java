package multigear.mginterface.tools.touch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import multigear.general.utils.Vector2;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Impulse Detector
 * @author user
 *
 */
final public class ImpulseDetector {

	/**
	 * Pointer
	 * 
	 * @author user
	 *
	 */
	final private class Pointer {
		
		/**
		 * Pointer Impulse
		 * @author user
		 *
		 */
		final private class Impulse {
			
			// Variables
			Vector2 impulse;
			long time;
			long delay;
		}
		
		// Variables
		int id;
		long lastTime;
		long frameTime;
		Vector2 lastPosition;
		Vector2 framePosition;
		List<Impulse> impulses = new ArrayList<Impulse>();
		
		/**
		 * Add Impulse
		 * @param time
		 */
		public void addImpulse() {
			Impulse impulse = new Impulse();
			impulse.impulse = Vector2.sub(framePosition, lastPosition);
			impulse.time = frameTime;
			impulse.delay = frameTime - lastTime;
			impulses.add(impulse);
		}
		
		/**
		 * Update Impulses
		 */
		public void updateImpulses() {
			Iterator<Impulse> itr = impulses.iterator();
			while(itr.hasNext()) {
				Impulse impulse = itr.next();
				if((frameTime - impulse.time) > ACCEPT_IMPULSE_TIME)
					itr.remove();
			}
		}
		
		/**
		 * Get Impulse
		 * @return
		 */
		public Vector2 getImpulse() {
			Vector2 finalImpulse = new Vector2();
			for(final Impulse impulse : impulses) {
				if((frameTime - impulse.time) <= ACCEPT_IMPULSE_TIME) {
					float scaler = impulse.delay / mDensity;
					Vector2 impulsed = Vector2.div(impulse.impulse, scaler);
					finalImpulse = Vector2.div(Vector2.sum(impulsed, finalImpulse), 2);
				}
			}
			return finalImpulse;
		}
	}
	
	// Constants
	final private static int ACCEPT_IMPULSE_TIME = 320;
	final private static float SCROLL_CONTROL = 25.0f;
	
	// Final Private Variables
	final private List<Pointer> mPointers = new ArrayList<Pointer>();
	final private float mDensity;
	
	// Private Variables
	private ImpulseDetectorListener mImpulseDetectorListener;
	
	/**
	 * Default Constructor
	 */
	public ImpulseDetector(final float density) {
		mDensity = density * SCROLL_CONTROL;
	}
	
	/**
	 * Constructor
	 * @param listener Impulse Detector Listener
	 */
	public ImpulseDetector(final float density, final ImpulseDetectorListener listener) {
		mDensity = density * SCROLL_CONTROL;
		mImpulseDetectorListener = listener;
	}
	
	/**
	 * Set Impulse Detector Listener
	 * @param listener Impulse Detector Listener
	 */
	final public void setListener(final ImpulseDetectorListener listener) {
		mImpulseDetectorListener = listener;
	}
	
	/**
	 * Get Impulse Detector Listener
	 * @return
	 */
	final public ImpulseDetectorListener getImpulseDetectorListener() {
		return mImpulseDetectorListener;
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
		removePointer(null, id, 0);
		final Pointer pointer = new Pointer();
		pointer.id = id;
		pointer.lastTime = touch.getEventTime();
		pointer.frameTime = touch.getEventTime();
		pointer.lastPosition = position;
		pointer.framePosition = position;
		mPointers.add(pointer);
	}
	
	/**
	 * Remove Pointer
	 * @param id
	 */
	final private void removePointer(final MotionEvent touch) {
		final int index = MotionEventCompat.getActionIndex(touch);
		final int id = MotionEventCompat.getPointerId(touch, index);
		Pointer found = null;
		for(final Pointer pointer : mPointers) {
			if(pointer.id == id)
				found = pointer;
		}
		removePointer(touch, id, index);
		if(mPointers.size() == 0) {
			if(mImpulseDetectorListener != null) {
				mImpulseDetectorListener.onImpulse(found.getImpulse());
			}
		}
	}
	
	/**
	 * Remove Pointer
	 * @param id
	 */
	final private  void removePointer(final MotionEvent touch, final int id, final int index) {
		final Iterator<Pointer> iterator = mPointers.iterator();
		while(iterator.hasNext()) {
			Pointer pointer = iterator.next();
			if(pointer.id == id) {
				if(touch != null) {
					long eventTime = touch.getEventTime();
					pointer.lastPosition = pointer.framePosition;
					pointer.lastTime = pointer.frameTime;
					pointer.framePosition = new Vector2(touch.getX(index), touch.getY(index));
					pointer.frameTime = eventTime;
					// Add impulse before because use this frame position ^
					pointer.addImpulse();
					pointer.updateImpulses();
				}
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
			final int id = MotionEventCompat.getPointerId(touch, index);
			Pointer found = null;
			for(final Pointer pointer : mPointers) {
				if(pointer.id == id)
					found = pointer;
			}
			if(found == null)
				continue;
			for(int history=0; history<touch.getHistorySize(); history++) {
				long eventTime = touch.getHistoricalEventTime(history);
				found.lastPosition = found.framePosition;
				found.lastTime = found.frameTime;
				found.framePosition = new Vector2(touch.getHistoricalX(index, history), touch.getHistoricalY(index, history));
				found.frameTime = eventTime;
				// Add impulse before because use this frame position ^
				found.addImpulse();
				found.updateImpulses();
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
			break;
		}
	}
}
