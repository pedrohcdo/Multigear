package multigear.mginterface.graphics.animations;

/**
 * 
 * Base para animação.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property SpringBall.
 */
final public class ParallelAnimation extends Animation {
	
	// Final Private Variables
	final private Animation[] mAnimations;
	
	/**
	 * Constructor
	 * @param duration
	 */
	public ParallelAnimation(int duration, final Animation... args) {
		super(duration);
		mAnimations = args;
	}

	/**
	 * Animate
	 */
	@Override
	public void onAnimation(AnimationSet animationSet, float delta) {
		for(final Animation animation : mAnimations)
			animation.animate(animationSet, delta);
	}
}
