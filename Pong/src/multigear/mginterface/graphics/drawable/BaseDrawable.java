package multigear.mginterface.graphics.drawable;

import multigear.mginterface.scene.Scene;
import android.view.MotionEvent;

public abstract class BaseDrawable {
	
	// Final Private Variables
	final private multigear.mginterface.scene.Scene mRoom;
	
	// Protected Variables
	private float mOpacity = 1;
	private int mZ = 0;
	private int mID = 0;
	
	/**
	 * Constructor
	 */
	public BaseDrawable(final multigear.mginterface.scene.Scene room) {
		mRoom = room;
		mRoom.addDrawable(this);
	}
	
	/**
	 * Set Opacity
	 * 
	 * @param Int
	 *            Opacity {0-255}
	 */
	final public void setOpacity(final float opacity) {
		mOpacity = Math.min(Math.max(opacity, 0), 1.0f);
	}
	
	/**
	 * Set Z.
	 * 
	 * @param z
	 *            Int Z
	 */
	final public void setZ(final int z) {
		mZ = z;
	}
	
	/**
	 * Set ID.
	 * 
	 * @param id
	 *            Int ID
	 */
	final public void setID(final int id) {
		mID = id;
	}
	
	/**
	 * Return used Room.
	 * 
	 * @return Used Room.
	 */
	final public Scene getAttachedRoom() {
		return mRoom;
	}
	
	/**
	 * Get Opacity
	 */
	final public float getOpacity() {
		return mOpacity;
	}
	
	/**
	 * Get Z.
	 * 
	 * @return Int Z
	 */
	final public int getZ() {
		return mZ;
	}
	
	/**
	 * Get ID.
	 * 
	 * @return Int ID
	 */
	final public int getID() {
		return mID;
	}
	
	
	/*
	 * Prepara para desenho. Utiliza AnimationStack.
	 */
	public void updateAndDraw(final multigear.mginterface.graphics.opengl.drawer.Drawer drawer, final float preOpacity) {
	}
	
	/**
	 * Get Touch Event.
	 * 
	 * @param motionEvent
	 *            MotionEvent used for touch.
	 * @return Return true if handled.
	 */
	public void touch(final MotionEvent motionEvent) {
	}
}
