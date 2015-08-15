package com.org.multigear.mginterface.graphics.animations;

/**
 * 
 * Esta animação cria um efeito de se espremendo.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property SpringBall.
 */
public class AnimationInvisibleWait implements Animation {
	
	/**
	 * Animate
	 */
	@Override
	final public void onAnimation(AnimationSet animationSet, float delta) {
		animationSet.setOpacity(0);
	}
}
