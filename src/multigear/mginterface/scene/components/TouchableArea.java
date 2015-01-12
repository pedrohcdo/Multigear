package multigear.mginterface.scene.components;

import multigear.general.utils.Vector2;
import multigear.mginterface.scene.components.receivers.Touchable;
import multigear.mginterface.scene.listeners.BaseListener;
import android.view.MotionEvent;

/**
 * Touchable Area
 * 
 * @author user
 *
 */
public class TouchableArea implements Touchable {
	
	// Private Variables
	private BaseListener mListener;
	private boolean mTouchHandled;
	private Vector2 mHandledPosition;
	
	// Public Variables
	protected int mZ = 0;
	protected int mId = 0;
	final protected Vector2 mPosition = new Vector2(0, 0);
	final protected Vector2 mSize = new Vector2(32, 32);
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
	final public void setListener(
			final multigear.mginterface.scene.listeners.BaseListener listener) {
		mListener = listener;
	}

	/*
	 * Retorna o listener
	 */
	final protected BaseListener getListener() {
		return mListener;
	}

	/**
	 * Set Sprite Position
	 * 
	 * @param position
	 *            {@link Vector2} Position
	 */
	final public void setPosition(final Vector2 position) {
		mPosition.set(position);
	}

	/**
	 * Set draw dest texture size.
	 * 
	 * @param size
	 *            Draw texture dest Size
	 */
	final public void setSize(final Vector2 size) {
		mSize.set(size);
	}

	/**
	 * Set depth
	 * 
	 * @param z Depth
	 */
	public void setZ(final int z) {
		mZ = z;
	}

	/**
	 * Set identifier
	 * 
	 * @param z Depth
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
	 * Return Position
	 * 
	 * @return {@link Vector2} Position
	 */
	final public Vector2 getPosition() {
		return mPosition.clone();
	}

	
	/**
	 * Return draw dest Texture size.
	 * 
	 * @return {@link Vector2} Size
	 */
	final public Vector2 getSize() {
		return mSize.clone();
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
		// Get Edges
		final float left = mPosition.x;
		final float top = mPosition.y;
		final float right = mPosition.x + mSize.x;
		final float bottom = mPosition.y + mSize.y;
		// Return result
		return (point.x >= left && point.x < right && point.y >= top && point.y < bottom);
	}

	
	/**
	 * Get Touch Event.
	 * 
	 * @param motionEvent
	 *            MotionEvent used for touch.
	 * @return Return true if handled.
	 */
	@Override
	public void touch(final MotionEvent motionEvent) {
		if(motionEvent.getPointerCount() > 1) {
			//mTouchHandled = false;
			//return;
		}
		if (!mTouchable) {
			mTouchHandled = false;
			return;
		}
		if (mListener != null) {
			Vector2 point = null;
			switch (motionEvent.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				point = new Vector2(motionEvent.getX(), motionEvent.getY());
				if (pointOver(point)) {
					mTouchHandled = true;
					if (mListener instanceof multigear.mginterface.scene.listeners.SimpleListener)
						((multigear.mginterface.scene.listeners.SimpleListener) mListener).onPress(this);
					mHandledPosition = point;
					if (mListener instanceof multigear.mginterface.scene.listeners.TouchListener)
						((multigear.mginterface.scene.listeners.TouchListener) mListener).onTouch(this, motionEvent);
					return;
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				if (mTouchHandled) {
					mTouchHandled = false;
					if (mListener instanceof multigear.mginterface.scene.listeners.TouchListener)
						((multigear.mginterface.scene.listeners.TouchListener) mListener)
								.onTouch(this, motionEvent);
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mTouchHandled) {
					if (mListener instanceof multigear.mginterface.scene.listeners.SimpleListener)
						((multigear.mginterface.scene.listeners.SimpleListener) mListener)
								.onRelease(this);
					mTouchHandled = false;
					point = new Vector2(motionEvent.getX(), motionEvent.getY());
					if (pointOver(point)
							&& mListener instanceof multigear.mginterface.scene.listeners.ClickListener)
						((multigear.mginterface.scene.listeners.ClickListener) mListener)
								.onClick(this);
					if (mListener instanceof multigear.mginterface.scene.listeners.TouchListener)
						((multigear.mginterface.scene.listeners.TouchListener) mListener)
								.onTouch(this, motionEvent);
					return;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				point = new Vector2(motionEvent.getX(), motionEvent.getY());
				final float diffX = point.x - mHandledPosition.x;
				final float diffY = point.y - mHandledPosition.y;
				final float scaleFactor = 1;//getBaseScaleFacor();
				final Vector2 moved = new Vector2(diffX / scaleFactor, diffY
						/ scaleFactor);
				if (mTouchHandled) {
					if (mListener instanceof multigear.mginterface.scene.listeners.SimpleListener)
						((multigear.mginterface.scene.listeners.SimpleListener) mListener)
								.onMove(this, moved);
					mHandledPosition = point;
					if (mListener instanceof multigear.mginterface.scene.listeners.TouchListener)
						((multigear.mginterface.scene.listeners.TouchListener) mListener)
								.onTouch(this, motionEvent);
					return;
				}
				break;
			}
		}
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
