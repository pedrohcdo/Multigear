package com.org.multigear.mginterface.scene.components;

import com.org.multigear.mginterface.scene.Scene;

import android.view.MotionEvent;

/**
 * Base of a drawable.
 * 
 * @author user
 *
 */
public interface TouchableListener {
	
	/**
	 * Evento de toque
	 * @param motionEvent
	 */
	public boolean onTouch(final Scene scene, final MotionEvent motionEvent);
}
