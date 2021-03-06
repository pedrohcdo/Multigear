package com.org.multigear.mginterface.tools.touch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.org.multigear.general.utils.GeneralUtils;
import com.org.multigear.general.utils.Vector2;

import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Drag Detector
 * @author user
 *
 */
final public class DragDetector {

	/**
	 * Pointer
	 * 
	 * @author user
	 *
	 */
	final private class Pointer {
		
		int id;
		Vector2 lastPosition;
		Vector2 framePosition;
	}
	
	// Final Private Variables
	final private List<Pointer> mPointers = new ArrayList<Pointer>();
	
	// Private Variables
	private DragDetectorListener mDragDetectorListener;
	
	/**
	 * Default Constructor
	 */
	public DragDetector() {}
	
	/**
	 * Constructor
	 * @param listener Drag Detector Listener
	 */
	public DragDetector(final DragDetectorListener listener) {
		mDragDetectorListener = listener;
	}
	
	/**
	 * Set Drag Detector Listener
	 * @param listener Drag Detector Listener
	 */
	final public void setListener(final DragDetectorListener listener) {
		mDragDetectorListener = listener;
	}
	
	/**
	 * Get Drag Detector Listener
	 * @return
	 */
	final public DragDetectorListener getDragDetectorListener() {
		return mDragDetectorListener;
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
		if(mPointers.size() == 1) {
			final Pointer a = mPointers.get(0);
			final Vector2 aLastPosition = a.lastPosition;
			final Vector2 aFramePosition = a.framePosition;
			final Vector2 aDraged = Vector2.sub(aFramePosition, aLastPosition);
			if(mDragDetectorListener != null && aDraged.length() != 0)
				mDragDetectorListener.onDrag(aDraged);
		} else if(mPointers.size() >= 2) {
			final Pointer a = mPointers.get(mPointers.size()-1);
			final Pointer b = mPointers.get(mPointers.size()-2);
			final Vector2 aLastPosition = a.lastPosition;
			final Vector2 bLastPosition = b.lastPosition;
			final Vector2 aFramePosition = a.framePosition;
			final Vector2 bFramePosition = b.framePosition;
			final Vector2 aDraged = Vector2.sub(aFramePosition, aLastPosition);
			final Vector2 bDraged = Vector2.sub(bFramePosition, bLastPosition);
			final Vector2 finalDraged = Vector2.div(Vector2.sum(aDraged, bDraged), 2);
			if(mDragDetectorListener != null && finalDraged.length() != 0)
				mDragDetectorListener.onDrag(finalDraged);
		}
		for(final Pointer pointer : mPointers)
			pointer.lastPosition = pointer.framePosition;
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
	
	/**
	 * Reset Detector
	 */
	final public void reset() {
		mPointers.clear();
	}
}
