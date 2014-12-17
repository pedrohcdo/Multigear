package multigear.mginterface.graphics.animations;


/**
 * 
 * Esta anima��o cria um efeito de se espremendo.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property SpringBall.
 */
public class AnimationOpacity extends Animation {
	
	// Final Private Variables
	final private float mStart, mEnd;
	
	/**
	 * Constructor
	 * 
	 * @param duration
	 */
	public AnimationOpacity(final int duration, final float start, final float end) {
		super(duration);
		mStart = start;
		mEnd = end;
	}
	
	/**
	 * Animate
	 */
	@Override
	final public void onAnimation(AnimationSet animationSet, float delta) {
		animationSet.setOpacity(mStart + (mEnd - mStart) * delta);
	}
}
