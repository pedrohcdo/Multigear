package multigear.mginterface.graphics.animations;

/**
 * 
 * Base para animação.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property SpringBall.
 */
final public class ParallelAnimation implements Animation {
	
	// Final Private Variables
	final private Animation[] mAnimations;
	
	/**
	 * Constructor
	 * @param duration
	 */
	public ParallelAnimation(final Animation... args) {
		mAnimations = args;
	}

	/**
	 * Animate
	 */
	@Override
	public void onAnimation(AnimationSet animationSet, float delta) {
		for(final Animation animation : mAnimations)
			animation.onAnimation(animationSet, delta);
	}
}
