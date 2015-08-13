package multigear.general.utils.animations;

import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.animations.Animation;
import multigear.mginterface.graphics.animations.AnimationSet;

/**
 * 
 * Esta animação cria um efeito de se espremendo.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property SpringBall.
 */
public class AnimationPosition implements Animation {
	
	// Final Private Variables
	final private Vector2 mStart, mEnd;
	
	/**
	 * Constructor
	 * 
	 * @param duration
	 */
	public AnimationPosition(final Vector2 start, final Vector2 end) {
		mStart = start.clone();
		mEnd = end.clone();
	}
	
	/**
	 * Animate
	 */
	@Override
	final public void onAnimation(AnimationSet animationSet, float delta) {
		animationSet.setPosition(Vector2.sum(mStart, Vector2.scale( Vector2.sub(mEnd, mStart), delta)));
	}
}
