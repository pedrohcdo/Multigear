package multigear.general.utils.animations;

import multigear.mginterface.graphics.animations.Animation;
import multigear.mginterface.graphics.animations.AnimationSet;





/**
 * 
 * Esta animação cria um efeito de explosão.
 * 
 * @author PedroH, RaphaelB
 *
 * Property SpringBall.
 */
public class AnimationExplodeOut implements Animation {
	
	/**
	 * Animate
	 */
	@Override
	final public void onAnimation(AnimationSet animationSet, float delta) {
		final float factor = (float)Math.PI * delta;
		final float cos = (1.0f - (float)Math.abs(Math.cos(factor))) / 8.0f + 1.0f;
		animationSet.setScale(cos, cos);
		animationSet.setOpacity(delta);
	}
}
