package multigear.mginterface.graphics.animations;



/**
 * 
 * Esta animação cria um efeito de se espremendo.
 * 
 * @author PedroH, RaphaelB
 *
 * Property SpringBall.
 */
public class AnimationZoomOut extends Animation {
	
	// Final Private Variables
	final private float mFactor;
	
	/**
	 * Constructor
	 * @param duration
	 */
	public AnimationZoomOut(final int duration, final float factor) {
		super(duration);
		mFactor = factor;
	}
	
	/**
	 * Constructor
	 * @param duration
	 */
	public AnimationZoomOut(final int duration) {
		super(duration);
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
