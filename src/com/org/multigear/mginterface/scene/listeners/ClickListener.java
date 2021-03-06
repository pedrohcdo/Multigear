package com.org.multigear.mginterface.scene.listeners;

import com.org.multigear.mginterface.scene.Component;


/**
 * Listener utilizado pelo Sprite.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public interface ClickListener extends BaseListener {
	
	/** On Click Event */
	public void onClick(final Component drawable);
}
