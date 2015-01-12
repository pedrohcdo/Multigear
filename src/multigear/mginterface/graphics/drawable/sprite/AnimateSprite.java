package multigear.mginterface.graphics.drawable.sprite;

import java.util.ArrayList;
import java.util.List;

import multigear.mginterface.engine.eventsmanager.GlobalClock;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
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
public class AnimateSprite extends Sprite {
	
	// Private Variables
	private List<Texture> mTextures = new ArrayList<Texture>();
	private boolean mAnimate = false;
	private int mAnimationDuration;
	private long mAnimationStartedTime;
	
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
		mAnimationStartedTime = GlobalClock.currentTimeMillis();
	}
	
	/**
	 * Stop Animation
	 */
	final public void stopAnimation() {
		mAnimate = false;
	}
	
	/**
	 * Update Sprite. obs(If it is created in a ROM, it will update
	 * altomatically on onUpdate() event.)
	 */
	@Override
	public void draw(final Drawer drawer) {
		if (mTextures.size() > 0) {
			if (mAnimate) {
				int time = (int) ((GlobalClock.currentTimeMillis() - mAnimationStartedTime) % mAnimationDuration);
				int frame = (int) Math.min(time * ((mTextures.size() * 1.0f)) / (mAnimationDuration - 1), mTextures.size() - 1);
				setTexture(mTextures.get(frame));
			} else
				setTexture(mTextures.get(0));
		}
		super.draw(drawer);
	}
}
