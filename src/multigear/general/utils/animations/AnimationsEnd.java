package multigear.general.utils.animations;

import multigear.mginterface.graphics.animations.Animation;
import multigear.mginterface.graphics.animations.AnimationListener;

/**
 * 
 * Listener utilisado para gerenciamento das animações
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Booclass.
 */
public abstract class AnimationsEnd implements AnimationListener {

	/**
	 * Animation Start
	 */
	@Override
	final public void onAnimationStart(Animation animation) {}

	/**
	 * Animation End
	 */
	@Override
	final public void onAnimationEnd(Animation animation) {}

	/**
	 * Animations Start
	 */
	@Override
	final public void onAnimationsStart() {}

	/**
	 * Animations End
	 */
	@Override
	final public void onAnimationsEnd() {
		onEnd();
	}
	
	/** End Animations */
	public abstract void onEnd();
}