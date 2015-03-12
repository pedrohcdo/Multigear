package multigear.mginterface.scene.listeners;

import multigear.general.utils.Vector2;
import multigear.mginterface.scene.components.receivers.Component;


/**
 * Listener utilizado pelo Sprite.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public interface SimpleListener extends BaseListener {
	
	/** On Press Event */
	public void onPress(final Component drawable);
	
	/** On Move Event */
	public void onMove(final Component drawable, final Vector2 moved);
	
	/** On Release Event */
	public void onRelease(final Component drawable);
	
}
