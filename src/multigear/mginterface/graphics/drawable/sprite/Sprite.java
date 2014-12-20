package multigear.mginterface.graphics.drawable.sprite;

import multigear.mginterface.graphics.animations.AnimationStack;
import multigear.mginterface.graphics.drawable.SimpleDrawable;


/**
 * 
 * Used to create a floating and flexible texture. Support the positions of and
 * also their mapping vertices thereof.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class Sprite extends SimpleDrawable {
	
	// Private Variables
	private multigear.mginterface.graphics.animations.AnimationStack mAnimationStack;
	private multigear.mginterface.graphics.opengl.texture.Texture mTexture;
	
	/**
	 * Constructor
	 */
	public Sprite(final multigear.mginterface.scene.Scene room) {
		super(room);
		mAnimationStack = new multigear.mginterface.graphics.animations.AnimationStack(room);
		mTexture = null;
	}
	
	/**
	 * Set Texture
	 * 
	 * @param texture {@link multigear.mginterface.graphics.opengl.texture.Texture}
	 */
	final public void setTexture(final multigear.mginterface.graphics.opengl.texture.Texture texture) {
		mTexture = texture;
		this.setSize(mTexture.getSize());
	}
	
	/**
	 * Get Animation Stack
	 * @return animationStack {@link multigear.mginterface.graphics.animations.AnimationStack}
	 */
	final public multigear.mginterface.graphics.animations.AnimationStack getAnimationStack() {
		return mAnimationStack;
	}
	
	/**
	 * Get Texture
	 * 
	 * @return texture {@link multigear.mginterface.graphics.opengl.texture.Texture}
	 */
	final public multigear.mginterface.graphics.opengl.texture.Texture getTexture() {
		return mTexture;
	}
	
	/*
	 * Return Correct Animation Stack
	 * @see Interface.Graphics.Drawable.BaseDrawable#getImplAnimationStack()
	 */
	@Override
	protected AnimationStack getImplAnimationStack() {
		return mAnimationStack;
	}
	
	/**
	 * Update Sprite. 
	 * obs(If it is created in a ROM, it will update altomatically on onUpdate() event.)
	 */
	@Override
	final public void updateAndDraw(final multigear.mginterface.graphics.opengl.drawer.Drawer drawer, final float preOpacity) {
		updateAndDraw(mTexture, drawer, preOpacity);
		onUpdate();
	}
	
	/** Update your Objects */
	public void onUpdate() {};
}
