package com.org.multigear.mginterface.graphics.animations;

/**
 * 
 * Listener utilisado para gerenciamento das animações
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Booclass.
 */
public interface AnimationListener {
	
	/* Animação iniciada */
	public void onAnimationStart(final Animation animation);
	
	/* Animação terminada */
	public void onAnimationEnd(final Animation animation);
	
	/* Animações iniciadas */
	public void onAnimationsStart();
	
	/* Animações terminadas */
	public void onAnimationsEnd();
	
}