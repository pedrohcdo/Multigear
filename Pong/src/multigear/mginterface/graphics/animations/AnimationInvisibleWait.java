package multigear.mginterface.graphics.animations;

/**
 * 
 * Esta animação cria um efeito de se espremendo.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property SpringBall.
 */
public class AnimationInvisibleWait extends Animation {
	
	/**
	 * Constructor
	 * 
	 * @param duration
	 */
	public AnimationInvisibleWait(final int duration) {
		super(duration);
	}
	
	/**
	 * Animate
	 */
	@Override
	final public void onAnimation(AnimationSet animationSet, float delta) {
		animationSet.setOpacity(0);
	}
}
