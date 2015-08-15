package com.org.multigear.mginterface.engine;

import android.view.MotionEvent;

/**
 * Multigear Safe
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class MultigearSafe {
	
	// Final Private Variables
	//final private Interface.Engine.Multigear mMultigear;
	final com.org.multigear.mginterface.engine.eventsmanager.EventHandler mEventHandler;
	
	/*
	 * Construtor
	 */
	protected MultigearSafe(final Multigear engine, final com.org.multigear.mginterface.engine.eventsmanager.EventHandler eventHandler) {
		//mMultigear = engine;
		mEventHandler = eventHandler;
	}
	
	
	/*
	 * Repassa evento de touch
	 */
	final public com.org.multigear.mginterface.engine.MultigearSafe touch(final MotionEvent motionEvent) {
		mEventHandler.sendTouch(motionEvent);
		return this;
	}
	
	
	/*
	 * Finalisa a sincronização
	 */
	final public void unsafe() {}
}
