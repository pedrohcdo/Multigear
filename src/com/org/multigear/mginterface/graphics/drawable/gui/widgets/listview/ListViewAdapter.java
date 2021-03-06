package com.org.multigear.mginterface.graphics.drawable.gui.widgets.listview;

import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.graphics.opengl.drawer.Drawer;

import android.view.MotionEvent;

/**
 * Select List Adapter
 * 
 * @author user
 *
 */
public interface ListViewAdapter {

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
		public void draw(final Drawer drawer, final ListView.DrawingHolder drawingHolder, final Vector2 cellSize);
		
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
	public ItemHolder createItem(final int index, final ItemHolder reUse);
	
	/**
	 * Get Item count
	 * @return
	 */
	public int getCount();
}
