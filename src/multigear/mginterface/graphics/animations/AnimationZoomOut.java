package multigear.mginterface.graphics.animations;



/**
 * 
 * Esta anima��o cria um efeito de se espremendo.
 * 
 * @author PedroH, RaphaelB
 *
 * Property SpringBall.
 */
public class AnimationZoomOut implements Animation {
	
	// Final Private Variables
	final private float mFactor;
	
	/**
	 * Constructor
	 * @param duration
	 */
	public AnimationZoomOut(final float factor) {
		mFactor = factor;
	}
	
	/**
	 * Constructor
	 * @param duration
	 */
	public AnimationZoomOut() {
		mFactor = 0.12f;
	}
	
	/**
	 * Animate
	 */
	@Override
	final public void onAnimation(AnimationSet animationSet, float delta) {
		final float f = 1 - (mFactor * (1-delta));
		animationSet.setScale(f, f);
	}
}
