package com.org.multigear.mginterface.scene.components;

import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.scene.Component;
import com.org.multigear.mginterface.scene.components.receivers.Touchable;
import com.org.multigear.mginterface.scene.listeners.BaseListener;
import com.org.multigear.mginterface.scene.listeners.ClickListener;
import com.org.multigear.mginterface.scene.listeners.SimpleListener;
import com.org.multigear.mginterface.scene.listeners.TouchListener;

import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

/**
 * Touchable Area
 * 
 * @author user
 * 
 */
public class TouchableArea implements Touchable, Component {

	// Private Variables
	private BaseListener mListener;
	private boolean mTouchHandled;
	private int mTouchHandledId;
	private Vector2 mHandledPosition;

	// Public Variables
	protected int mZ = 0;
	protected int mId = 0;
	final protected RectF mRect = new RectF(0, 0, 0, 0);
	protected boolean mTouchable = true;

	/**
	 * Constructor
	 */
	public TouchableArea() {
		mListener = null;
		mTouchHandled = false;
		mHandledPosition = new Vector2(0, 0);
	}

	/**
	 * Set a listener. Listener used for send Touch Events.
	 * 
	 * @param listener
	 *            Used Listener.
	 */
	final public void setListener(final BaseListener listener) {
		mListener = listener;
	}

	/*
	 * Retorna o listener
	 */
	final protected BaseListener getListener() {
		return mListener;
	}

	/**
	 * Set Area
	 * 
	 * @param size
	 *            Draw texture dest Size
	 */
	final public void setArea(final float left, final float top,
			final float right, final float bottom) {
		mRect.set(left, top, right, bottom);
	}
	
	/**
	 * Set Area
	 * 
	 * @param size
	 *            Draw texture dest Size
	 */
	final public void setArea(final RectF area) {
		mRect.set(area);
	}

	/**
	 * Set depth
	 * 
	 * @param z
	 *            Depth
	 */
	public void setZ(final int z) {
		mZ = z;
	}

	/**
	 * Set identifier
	 * 
	 * @param z
	 *            Depth
	 */
	public void setId(final int id) {
		mId = id;
	}

	/**
	 * Set Touchable.
	 * 
	 * @param touchable
	 *            Boolean Touchable
	 */
	final public void setTouchable(final boolean touchable) {
		mTouchable = touchable;
	}

	/**
	 * Return RectF Area
	 * 
	 * @return {@link RectF} Area
	 */
	final public RectF getSize() {
		return new RectF(mRect);
	}

	/**
	 * Get depth
	 * 
	 * @return Return Depth
	 */
	public int getZ() {
		return mZ;
	}

	/**
	 * Get indentifier
	 */
	@Override
	public int getId() {
		return mId;
	}

	/**
	 * Get Touchable.
	 * 
	 * @return Boolean Touchable
	 */
	final public boolean getTouchable() {
		return mTouchable;
	}

	/**
	 * Check if point is over Sprite.
	 * 
	 * @param point
	 *            Point used for check.
	 * @return Return true if point over Sprite.
	 */
	final public boolean pointOver(final Vector2 point) {
		return (point.x >= mRect.left && point.x < mRect.right
				&& point.y >= mRect.top && point.y < mRect.bottom);
	}

	/**
	 * Get Touch Event.
	 * 
	 * @param motionEvent
	 *            MotionEvent used for touch.
	 * @return Return true if handled.
	 */
	@Override
	public boolean touch(final MotionEvent motionEvent) {
		if (!mTouchable) {
			mTouchHandled = false;
			return false;
		}
		Vector2 point = null;
		int id = 0;
		int index = 0;
		switch (motionEvent.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			if(!mTouchHandled) {
				index = MotionEventCompat.getActionIndex(motionEvent);
				point = new Vector2(MotionEventCompat.getX(motionEvent, index), MotionEventCompat.getY(motionEvent, index));
				if (pointOver(point)) {
					mTouchHandled = true;
					mTouchHandledId = MotionEventCompat.getPointerId(motionEvent, index);
					if (mListener != null && mListener instanceof SimpleListener)
						((SimpleListener) mListener).onPress(this);
					mHandledPosition = point;
					if (mListener != null && mListener instanceof TouchListener)
						((TouchListener) mListener).onTouch(this, motionEvent);
					return true;
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			if (mTouchHandled) {
				mTouchHandled = false;
				if (mListener != null && mListener instanceof TouchListener)
					((TouchListener) mListener).onTouch(this, motionEvent);
				
			}
			// For all consume
			return false;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			if (mTouchHandled) {
				index = MotionEventCompat.getActionIndex(motionEvent);
				id = MotionEventCompat.getPointerId(motionEvent, index);
				if(id == mTouchHandledId) {
					mTouchHandled = false;
					
					if (mListener != null && mListener instanceof SimpleListener)
						((SimpleListener) mListener).onRelease(this);
					point = new Vector2(MotionEventCompat.getX(motionEvent, index), MotionEventCompat.getY(motionEvent, index));
					if (pointOver(point) && mListener != null
							&& mListener instanceof ClickListener)
						((ClickListener) mListener).onClick(this);
					if (mListener != null && mListener instanceof TouchListener)
						((TouchListener) mListener).onTouch(this, motionEvent);
					return true;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			for(int c=0; c<MotionEventCompat.getPointerCount(motionEvent); c++) {
				id = MotionEventCompat.getPointerId(motionEvent, c);
				if(id == mTouchHandledId) {
					point = new Vector2(MotionEventCompat.getX(motionEvent, c), MotionEventCompat.getY(motionEvent, c));
					final float diffX = point.x - mHandledPosition.x;
					final float diffY = point.y - mHandledPosition.y;
					final float scaleFactor = 1;// getBaseScaleFacor();
					final Vector2 moved = new Vector2(diffX / scaleFactor, diffY / scaleFactor);
					if (mTouchHandled) {
						if (mListener != null && mListener instanceof SimpleListener)
							((SimpleListener) mListener).onMove(this, moved);
						mHandledPosition = point;
						if (mListener != null && mListener instanceof TouchListener)
							((TouchListener) mListener).onTouch(this, motionEvent);
					}
				}
			}
			break;
		}
		// For all consume
		return false;
	}

	/**
	 * Return Sprite pressed state.
	 * 
	 * @return Return true if Sprite pressed state.
	 */
	final public boolean isPressed() {
		return mTouchHandled && mTouchable;
	}
}
