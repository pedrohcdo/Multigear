package multigear.mginterface.scene.listeners;

import multigear.mginterface.scene.components.Component;


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
