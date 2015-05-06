package multigear.mginterface.engine.eventsmanager;

import android.view.MotionEvent;

final public class TouchEvents {
	
	// Private Variables
	final private multigear.mginterface.engine.Manager mManager;
	final private multigear.mginterface.scene.Scene mMainRoom;
	
	/*
	 * Construtor
	 */
	protected TouchEvents(final multigear.mginterface.engine.Manager manager) {
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
