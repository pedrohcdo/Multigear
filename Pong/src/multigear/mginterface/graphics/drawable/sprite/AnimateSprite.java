package multigear.mginterface.graphics.drawable.sprite;

import java.util.ArrayList;
import java.util.List;

import multigear.mginterface.graphics.animations.AnimationStack;
import multigear.mginterface.graphics.drawable.SimpleDrawable;
import multigear.mginterface.graphics.opengl.texture.Texture;

/**
 * 
 * Used to create a floating and flexible texture. Support the positions of and
 * also their mapping vertices thereof.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class AnimateSprite extends SimpleDrawable {
	
	// Private Variables
	private multigear.mginterface.graphics.animations.AnimationStack mAnimationStack;
	private List<Texture> mTextures = new ArrayList<Texture>();
	private boolean mAnimate = false;
	private int mAnimationDuration;
	private long mAnimationStartedTime;
	
	/**
	 * Constructor
	 */
	public AnimateSprite(final multigear.mginterface.scene.Scene room) {
		super(room);
		mAnimationStack = new multigear.mginterface.graphics.animations.AnimationStack(room);
	}
	
	/**
	 * Set Texture
	 * 
	 * @param texture
	 *            {@link multigear.mginterface.graphics.opengl.texture.Texture}
	 */
	final public void addTextureFrame(final Texture texture) {
		mTextures.add(texture);
	}
	
	/**
	 * Clear frame stack. This method stop animation.
	 */
	final public void clearFrames() {
		mTextures.clear();
		mAnimate = false;
	}
	
	/**
	 * Start Animation
	 */
	final public void startAnimation(final int duration) {
		mAnimate = true;
		mAnimationDuration = duration;
		mAnimationStartedTime = getAttachedRoom().getThisTime();
	}
	
	/**
	 * Stop Animation
	 */
	final public void stopAnimation() {
		mAnimate = false;
	}
	
	/**
	 * Get Animation Stack
	 * 
	 * @return animationStack
	 *         {@link multigear.mginterface.graphics.animations.AnimationStack}
	 */
	final public multigear.mginterface.graphics.animations.AnimationStack getAnimationStack() {
		return mAnimationStack;
	}
	
	/*
	 * Return Correct Animation Stack
	 * 
	 * @see Interface.Graphics.Drawable.BaseDrawable#getImplAnimationStack()
	 */
	@Override
	protected AnimationStack getImplAnimationStack() {
		return mAnimationStack;
	}
	
	/**
	 * Update Sprite. obs(If it is created in a ROM, it will update
	 * altomatically on onUpdate() event.)
	 */
	@Override
	final public void updateAndDraw(final multigear.mginterface.graphics.opengl.drawer.Drawer drawer, final float preOpacity) {
		if (mTextures.size() > 0) {
			if (mAnimate) {
				int time = (int) ((getAttachedRoom().getThisTime() - mAnimationStartedTime) % mAnimationDuration);
				int frame = (int) Math.min(time * ((mTextures.size() * 1.0f)) / (mAnimationDuration - 1), mTextures.size() - 1);
				updateAndDraw(mTextures.get(frame), drawer, preOpacity);
			} else
				updateAndDraw(mTextures.get(0), drawer, preOpacity);
		}
		onUpdate();
	}
	
	/** Update your Objects */
	public void onUpdate() {
	};
}
