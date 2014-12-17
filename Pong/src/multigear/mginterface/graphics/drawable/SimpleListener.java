package multigear.mginterface.graphics.drawable;


/**
 * Listener utilizado pelo Sprite.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public interface SimpleListener extends multigear.mginterface.graphics.drawable.BaseListener {
	
	/** On Press Event */
	public void onPress(final multigear.mginterface.graphics.drawable.BaseDrawable drawable);
	
	/** On Move Event */
	public void onMove(final multigear.mginterface.graphics.drawable.BaseDrawable drawable, final multigear.general.utils.Ref2F moved);
	
	/** On Release Event */
	public void onRelease(final multigear.mginterface.graphics.drawable.BaseDrawable drawable);
	
}
