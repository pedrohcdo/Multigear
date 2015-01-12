package multigear.mginterface.scene;

import multigear.general.utils.Vector2;

/**
 * Scene Drawer State
 * 
 * @author user
 *
 */
final public class SceneDrawerState {

	// Final Private Variables
	private float mOpacity;
	private Vector2 mScale;
	
	/**
	 * Constructor
	 */
	protected SceneDrawerState() {}
	
	/**
	 * Get opacity state
	 * @return Opacity State
	 */
	final public float getOpacity() {
		return mOpacity;
	}
	
	/**
	 * Get scale state
	 * @return Opacity State
	 */
	final public Vector2 getScale() {
		return mScale;
	}
	
	/**
	 * Set opacity state
	 * @param opacity [in] State Opacity
	 */
	final protected void setOpacity(final float opacity) {
		mOpacity = opacity;
	}
	
	/**
	 * Set scale state
	 * @param opacity [in] State Opacity
	 */
	final protected void setScale(final Vector2 scale) {
		mScale = scale;
	}
}
