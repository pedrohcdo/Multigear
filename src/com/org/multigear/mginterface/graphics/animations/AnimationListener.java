package com.org.multigear.mginterface.graphics.animations;

/**
 * 
 * Listener utilisado para gerenciamento das anima��es
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Booclass.
 */
public interface AnimationListener {
	
	/* Anima��o iniciada */
	public void onAnimationStart(final Animation animation);
	
	/* Anima��o terminada */
	public void onAnimationEnd(final Animation animation);
	
	/* Anima��es iniciadas */
	public void onAnimationsStart();
	
	/* Anima��es terminadas */
	public void onAnimationsEnd();
	
}