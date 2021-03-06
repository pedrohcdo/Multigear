package com.org.multigear.mginterface.scene.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.org.multigear.mginterface.graphics.opengl.drawer.Drawer;
import com.org.multigear.mginterface.scene.Component;
import com.org.multigear.mginterface.scene.Scene;
import com.org.multigear.mginterface.scene.components.receivers.Drawable;
import com.org.multigear.mginterface.scene.components.receivers.Touchable;
import com.org.multigear.mginterface.scene.components.receivers.Updatable;

import android.view.MotionEvent;

/**
 * Components Batch
 * 
 * @author user
 *
 */
public class ComponentBatch {
	
	/**
	 * Components comparator.
	 */
	final private Comparator<Component> mComponentsComparatorDraw = new Comparator<Component>() {
		
		/**
		 * Compare
		 * 
		 * @param lhs
		 * @param rhs
		 * @return
		 */
		@Override
		public int compare(final Component lhs, final Component rhs) {
			return lhs.getZ() - rhs.getZ();
		}
	};
	
	/**
	 * Updatable Handler
	 * 
	 * @author user
	 *
	 */
	final private class UpdatableHandler implements UpdatableListener {

		/**
		 * On Update
		 */
		@Override
		public void onUpdate(Scene scene) {
			Collections.sort(mComponents, mComponentsComparatorDraw);
			for(int i=0; i<mComponents.size(); i++) {
				final Component component = mComponents.get(i);
				if(component instanceof Updatable) {
					Updatable updatable = (Updatable) component;
					updatable.update();
				}
			}
		}
	}
	
	/**
	 * Drawable Handler
	 * 
	 * @author user
	 */
	final private class DrawableHandler implements DrawableListener {

		/**
		 * On Draw
		 */
		@Override
		public void onDraw(Scene scene, Drawer drawer) {
			Collections.sort(mComponents, mComponentsComparatorDraw);
			for(int i=0; i<mComponents.size(); i++) {
				final Component component = mComponents.get(i);
				if(component instanceof Drawable) {
					Drawable drawable = (Drawable) component;
					drawable.draw(drawer);
				}
			}
		}
	}
	
	/**
	 * Touchable Handler
	 * 
	 * @author user
	 */
	final private class TouchableHandler implements TouchableListener {

		/**
		 * On Touch
		 * 
		 * @param motionEvent
		 */
		@Override
		public boolean onTouch(Scene scene, MotionEvent motionEvent) {
			for(int i=0; i<mComponents.size(); i++) {
				final int j = mComponents.size() - i - 1;
				Component component = mComponents.get(j);
				if(component instanceof Touchable) {
					Touchable touchable = (Touchable) component;
					if(touchable.touch(motionEvent))
						return true;
				}
			}
			return false;
		}
	}
	
	// Final private Variables
	final private List<Component> mComponents = new ArrayList<Component>();
	final private UpdatableListener mUpdatableHandler = new UpdatableHandler();
	final private DrawableHandler mDrawableHandler = new DrawableHandler();
	final private TouchableListener mTouchableHandler = new TouchableHandler();
	
	// Private Variables
	private Scene mAttachedScene;
	
	/**
	 * Constructor.<br>
	 * Attach to the scene.
	 */
	public ComponentBatch() {}
	
	/**
	 * Attach to scene
	 * @param scene
	 */
	final public void attach(final Scene scene) {
		if(mAttachedScene != null)
			throw new RuntimeException("This object is already attached to another scene.");
		mAttachedScene = scene;
		mAttachedScene.addUpdatableListener(mUpdatableHandler);
		mAttachedScene.addDrawableListener(mDrawableHandler);
		mAttachedScene.addTouchableListener(mTouchableHandler);
	}
	
	/**
	 * Detach this Object from the scene
	 */
	final public void detach() {
		if(mAttachedScene == null)
			throw new RuntimeException("This object is not attached to any scene.");
		mAttachedScene.removeUpdatableListener(mUpdatableHandler);
		mAttachedScene.removeDrawableListener(mDrawableHandler);
		mAttachedScene.removeTouchableListener(mTouchableHandler);
		mAttachedScene = null;
	}
	
	/**
	 * If you are not attached to any scene returns null.
	 * @return Attached Scene or Null if it is not attached to any scene
	 */
	final public Scene getAttachedScene() {
		return mAttachedScene;
	}
	
	/**
	 * Add Component
	 * 
	 * @param component
	 */
	final public void addComponent(final Component component) {
		mComponents.add(component);
	}
	
	/**
	 * Remove Component
	 * 
	 * @param component
	 */
	final public void removeComponent(final Component component) {
		mComponents.remove(component);
	}
	
	/**
	 * Get Components count
	 */
	final public int getCount() {
		return mComponents.size();
	}
}
