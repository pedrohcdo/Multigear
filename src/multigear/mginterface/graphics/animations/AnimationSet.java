package multigear.mginterface.graphics.animations;

import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.opengl.texture.Texture;

/**
 * Animation Set
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class AnimationSet {
	
	// Private Variables
	final private Vector2 mPosition;
	final private Vector2 mScale;
	private float mRotation;
	private float mOpacity;
	private Texture mTexture;
	
	/**
	 * Constructor
	 */
	public AnimationSet() {
		mPosition = new Vector2(0, 0);
		mScale = new Vector2(1, 1);
		mTexture = null;
		reset();
	}
	
	/**
	 * Reset Animations Attributes
	 */
	final public void reset() {
		mPosition.set(0, 0);
		mScale.set(1, 1);
		mRotation = 0.0f;
		mOpacity = 1.0f;
	}
	
	/**
	 * Set Animation Position
	 * @param arg
	 */
	final public void setPosition(final Vector2 position) {
		mPosition.sum(position);
	}
	
	/**
	 * Set Animation Position
	 * @param arg
	 */
	final public void setPosition(final float positionX, float positionY) {
		mPosition.sum(positionX, positionY);
	}
	
	/**
	 * Set Scale
	 * @param scale
	 */
	final public void setScale(final Vector2 scale) {
		mScale.set(scale);
	}
	
	/**
	 * Set Scale
	 * @param scale
	 */
	final public void setScale(final float scaleX, final float scaleY) {
		mScale.set(scaleX, scaleY);
	}
	
	/**
	 * Set Animation Rotation
	 * @param ang
	 */
	final public void setRotation(final float rotation) {
		mRotation = rotation;
	}
	
	/**
	 * Set Animation Opacity
	 * @param opacity
	 */
	final public void setOpacity(final float opacity) {
		mOpacity = opacity;
	}
	
	/**
	 * Set Animation Texture
	 * @param texture
	 */
	final public void setTexture(final Texture texture) {
		mTexture = texture;
	}
	
	/**
	 * Get Animation Position
	 * @param arg
	 */
	final public Vector2 getPosition() {
		return mPosition.clone();
	}
	
	/**
	 * Get Scale
	 * @param scale
	 */
	final public Vector2 getScale() {
		return mScale.clone();
	}
	
	/**
	 * Get Animation Rotation
	 * @param ang
	 */
	final public float getRotation() {
		return mRotation;
	}
	
	/**
	 * Get Animation Opacity
	 * @param opacity
	 */
	final public float getOpacity() {
		return mOpacity;
	}
	
	/**
	 * Get Animation Texture
	 * @param texture
	 */
	final public Texture getTexture() {
		return mTexture;
	}
}
