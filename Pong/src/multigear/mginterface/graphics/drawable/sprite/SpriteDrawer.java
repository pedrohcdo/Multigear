package multigear.mginterface.graphics.drawable.sprite;

import multigear.mginterface.graphics.animations.AnimationStack;
import multigear.mginterface.graphics.drawable.SimpleDrawable;
import android.graphics.Bitmap;


/**
 * 
 * Used to create a floating and flexible texture. Support the positions of and
 * also their mapping vertices thereof.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public abstract class SpriteDrawer extends SimpleDrawable {
	
	// Private Variables
	private multigear.mginterface.graphics.animations.AnimationStack mAnimationStack;
	private multigear.mginterface.graphics.opengl.texture.Texture mTexture;
	
	/**
	 * Constructor
	 */
	public SpriteDrawer(final multigear.mginterface.scene.Scene room) {
		super(room);
		mAnimationStack = new multigear.mginterface.graphics.animations.AnimationStack(room);
	}
	
	/**
	 * Get Animation Stack
	 * @return animationStack {@link multigear.mginterface.graphics.animations.AnimationStack}
	 */
	final public multigear.mginterface.graphics.animations.AnimationStack getAnimationStack() {
		return mAnimationStack;
	}
	
	/**
	 * Return Correct Animation Stack
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
	
	/*
	 * Regenerate Bitmap
	 */
	final protected boolean regenerateBitmap() {
		final Bitmap textureBitmap = onGenerateBitmap();
		if(mTexture == null)
			mTexture = getTextureLoader().create(textureBitmap);
		else
			mTexture.setBitmap(textureBitmap);
		textureBitmap.recycle();
		setSize(mTexture.getSize());
		return true;
	}
	
	/** Generate your Bitmap Texture */
	protected abstract Bitmap onGenerateBitmap();
	
	/** Update your Objects */
	protected abstract void onUpdate();
}
