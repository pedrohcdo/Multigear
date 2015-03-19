package multigear.mginterface.graphics.drawable.gui.widgets.List;

import android.view.MotionEvent;
import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.opengl.drawer.Drawer;

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
		 * Draw intem
		 * 
		 * @return
		 */
		public void draw(final Drawer drawer, final SelectList.DrawingHolder drawingHolder, final Vector2 cellSize);
		
		/**
		 * Touch
		 * 
		 * @param motionEvent
		 * @return True if consumed
		 */
		public int touch(final MotionEvent motionEvent);
		
		/**
		 * Get Item Height
		 * @return
		 */
		public float getHeight();
	}
	
	/**
	 * Create View
	 */
	public ItemHolder createItem(final int index);
	
	/**
	 * Get Item count
	 * @return
	 */
	public int getCount();
}
