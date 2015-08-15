package com.org.multigear.mginterface.graphics.drawable.gui.widgets.listview;

import java.util.List;

import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.graphics.opengl.drawer.Drawer;
import com.org.multigear.mginterface.scene.components.receivers.Drawable;

import android.view.MotionEvent;

/**
 * Simple Adapter
 * 
 * @author user
 *
 */
public class ArrayAdapter implements ListViewAdapter {

	/**
	 * Item Adapter
	 * 
	 * @author user
	 *
	 */
	final public static class ItemAdapter implements ListViewAdapter.ItemHolder {

		// Final Private Variables
		final private Drawable mDrawable;
		final private float mHeight;
		
		/**
		 * Constructor
		 * 
		 * @param drawable
		 * @param height
		 */
		public ItemAdapter(final Drawable drawable, final float height) {
			mDrawable = drawable;
			mHeight = height;
		}
		
		/**
		 * Draw Event
		 */
		@Override
		public void draw(final Drawer drawer, final ListView.DrawingHolder drawingHolder, final Vector2 cellSize) {
			mDrawable.draw(drawer);
		}
		
		/**
		 * Touch Event
		 */
		@Override
		public int touch(final MotionEvent motionEvent) {
			return ListView.TOUCH_UNUSE;
		}

		/**
		 * Get Height
		 */
		@Override
		public float getHeight() {
			return mHeight;
		}
		
	}
	// Final Private Variables
	final List<ItemAdapter> mItems;
	
	/**
	 * Constructor
	 * 
	 * @param drawables
	 */
	public ArrayAdapter(final List<ItemAdapter> items) {
		mItems = items;
	}
	
	/* Unused */
	@Override
	public ItemHolder createItem(final int index, final ItemHolder reUse) {
		if(reUse != null)
			return reUse;
		return mItems.get(index);
	}
	
	/**
	 * Count
	 */
	@Override
	public int getCount() {
		return mItems.size();
	}
}
