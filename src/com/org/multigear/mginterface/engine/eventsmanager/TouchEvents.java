package com.org.multigear.mginterface.engine.eventsmanager;

import android.view.MotionEvent;

final public class TouchEvents {
	
	// Private Variables
	final private com.org.multigear.mginterface.engine.Manager mManager;
	final private com.org.multigear.mginterface.scene.Scene mMainRoom;
	
	/*
	 * Construtor
	 */
	protected TouchEvents(final com.org.multigear.mginterface.engine.Manager manager) {
		mManager = manager;
		mMainRoom = mManager.getMainRoom();
	}
	
	/*
	 * Evento de toque
	 */
	final protected void onTouch(final MotionEvent motionEvent) {
		mMainRoom.touchImpl(motionEvent);
	}
	
	/**
	 * Back pressed
	 */
	final protected void onBackPressed() {
		mMainRoom.backImpl();
	}
}
