package multigear.mginterface.scene.listeners;

import multigear.mginterface.scene.Component;
import android.view.MotionEvent;

/**
 * Listener utilizado pelo Sprite.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public interface TouchListener extends BaseListener  {
	
	/** On Touch Event */
	public void onTouch(final Component drawable, final MotionEvent motionEvent);
}
