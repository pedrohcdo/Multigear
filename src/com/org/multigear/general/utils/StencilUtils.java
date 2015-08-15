package com.org.multigear.general.utils;

import com.org.multigear.mginterface.graphics.drawable.polygon.Polygon;
import com.org.multigear.mginterface.graphics.opengl.drawer.Drawer;
import com.org.multigear.mginterface.scene.Component;
import com.org.multigear.mginterface.scene.components.receivers.Drawable;

/**
 * Stencil Utils
 * 
 * @author user
 *
 */
final public class StencilUtils {

	/**
	 * Used to use as Drawable
	 * @author user
	 *
	 */
	final static public class StencilDrawableOpen implements Drawable, Component {

		// Final Private Variables
		final private Polygon mForm;
		
		// Private Variables
		private int mZ = 0;
		
		/**
		 * Constructor
		 * 
		 * @param form
		 */
		public StencilDrawableOpen(final Polygon form) {
			mForm = form;
		}
		
		/**
		 * Draw
		 */
		@Override
		public void draw(Drawer drawer) {
			drawer.drawStencil(mForm);
		}

		/**
		 * Set Z
		 */
		public void setZ(final int z) {
			mZ = z;
		}
		
		/**
		 * Get Z
		 */
		@Override
		public int getZ() {
			return mZ;
		}

		/**
		 * Get Id
		 * 
		 * @return
		 */
		@Override
		public int getId() {
			return 0;
		}
	}
	
	/**
	 * Used to use as Drawable
	 * @author user
	 *
	 */
	final static public class StencilDrawableClose implements Drawable, Component {

		// Final Private Variables
		final private Polygon mForm;
		
		// Private Variables
		private int mZ = 0;
		
		/**
		 * Constructor
		 * 
		 * @param form
		 */
		public StencilDrawableClose(final Polygon form) {
			mForm = form;
		}
		
		/**
		 * Draw
		 */
		@Override
		public void draw(Drawer drawer) {
			drawer.eraseStencil(mForm);
		}
		
		/**
		 * Set Z
		 */
		public void setZ(final int z) {
			mZ = z;
		}
		
		/**
		 * Get Z
		 */
		@Override
		public int getZ() {
			return mZ;
		}

		/**
		 * Get Id
		 * 
		 * @return
		 */
		@Override
		public int getId() {
			return 0;
		}
	}
	
	/** Private Constructor */
	private StencilUtils() {};
}
