package multigear.mginterface.graphics.drawable.gui.widgets.List;

import java.util.List;

import android.view.MotionEvent;

import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.scene.components.receivers.Drawable;

/**
 * Simple Adapter
 * 
 * @author user
 *
 */
public class ArrayAdapter implements SelectListAdapter {

	/**
	 * Item Adapter
	 * 
	 * @author user
	 *
	 */
	final public static class ItemAdapter implements SelectListAdapter.ItemHolder {

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
		public void draw(final Drawer drawer, final SelectList.DrawingHolder drawingHolder, final Vector2 cellSize) {
			mDrawable.draw(drawer);
		}
		
		/**
		 * Touch Event
		 */
		@Override
		public int touch(final MotionEvent motionEvent) {
			return SelectList.TOUCH_UNUSE;
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
	public ItemHolder createItem(final int index) {
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
