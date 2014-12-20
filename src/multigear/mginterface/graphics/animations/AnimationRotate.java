package multigear.mginterface.graphics.animations;


/**
 * 
 * Esta animação cria um efeito de se espremendo.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property SpringBall.
 */
public class AnimationRotate extends Animation {
	
	// Final Private Variables
	final private float mStart, mEnd;
	
	/**
	 * Constructor
	 * 
	 * @param duration
	 */
	public AnimationRotate(final int duration, final float start, final float end) {
		super(duration);
		mStart = start;
		mEnd = end;
	}
	
	/**
	 * Animate
	 */
	@Override
	final public void onAnimation(AnimationSet animationSet, float delta) {
		animationSet.setRotation(mStart + (mEnd - mStart) * delta);
	}
}
