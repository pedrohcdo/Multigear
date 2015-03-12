package multigear.mginterface.graphics.drawable.gui.widgets.List;

import multigear.general.utils.Vector2;
import multigear.mginterface.scene.components.receivers.Drawable;

/**
 * Select List Adapter
 * 
 * @author user
 *
 */
public interface SelectListAdapter {

	/**
	 * Item
	 * 
	 * @author user
	 *
	 */
	public interface ItemHolder {
		
		/**
		 * Get Item Drawable
		 * 
		 * @return
		 */
		public Drawable getDrawable();
		
		/**
		 * Get Item Height
		 * @return
		 */
		public float getHeight();
	}
	
	/**
	 * Create View
	 */
	public void createItem(final int index, final float itemWidth);
	
	/**
	 * Get Item count
	 * @return
	 */
	public int getCount();
	
	/**
	 * Get Item
	 * @param index
	 * @return
	 */
	public ItemHolder getItem(final int index);
}
