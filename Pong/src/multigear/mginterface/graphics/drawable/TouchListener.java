package multigear.mginterface.graphics.drawable;

import android.view.MotionEvent;

/**
 * Listener utilizado pelo Sprite.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public interface TouchListener extends multigear.mginterface.graphics.drawable.BaseListener  {
	
	/** On Touch Event */
	public void onTouch(final multigear.mginterface.graphics.drawable.BaseDrawable drawable, final MotionEvent motionEvent);
}
