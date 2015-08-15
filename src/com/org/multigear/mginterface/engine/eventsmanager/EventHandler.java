package com.org.multigear.mginterface.engine.eventsmanager;

import com.org.multigear.general.utils.Vector2;

import android.view.MotionEvent;

/**
 * 
 * Gerencia os eventos.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class EventHandler {
	
	// Private Variables
	final private com.org.multigear.mginterface.engine.Manager mManager;
	final private com.org.multigear.mginterface.engine.eventsmanager.TouchEvents mTouchEvents;
	final private com.org.multigear.mginterface.engine.eventsmanager.GraphicsEvents mGraphicsEvents;
	final private com.org.multigear.mginterface.engine.eventsmanager.GeneralEvents mGeneralEvents;
	
	/*
	 * Construtor
	 */
	public EventHandler(final com.org.multigear.mginterface.engine.Manager manager) {
		mManager = manager;
		mTouchEvents = new com.org.multigear.mginterface.engine.eventsmanager.TouchEvents(mManager);
		mGraphicsEvents = new com.org.multigear.mginterface.engine.eventsmanager.GraphicsEvents(mManager);
		mGeneralEvents = new com.org.multigear.mginterface.engine.eventsmanager.GeneralEvents(mManager);
	}
	
	/*
	 * Envia um evento para atualização do tempo
	 */
	final public void sendTime() {
		mGeneralEvents.time();
	}
	
	/*
	 * Evento para redimensionamento da tela preparativo
	 */
	public void prepareScreen(final Vector2 screenSize) {
		mGraphicsEvents.prepareScreen(screenSize);
	}
	
	/*
	 * Evento para arquivação preparativo
	 */
	public void prepareCache(final com.org.multigear.mginterface.graphics.opengl.texture.Loader textureLoader) {
		mGraphicsEvents.prepareCache(textureLoader);
	}
	
	/*
	 * Envia um evento de setup
	 */
	public void sendSetup() {
		mGeneralEvents.setup();
	}
	
	/*
	 * Envia um evento para arquivação de texturas
	 */
	public void sendCache() {
		mGraphicsEvents.cache();
	}
	
	/*
	 * Evento para redimensionamento da tela
	 */
	public void sendScreen() {
		mGraphicsEvents.screen();
	}
	
	/*
	 * Evento para atualização dos objetos
	 */
	final public void sendUpdate() {
		mGeneralEvents.update();
	}
	
	/*
	 * Eevento para desenhar o frame atual
	 */
	final public void sendDraw(final com.org.multigear.mginterface.graphics.opengl.drawer.Drawer drawer) {
		mGraphicsEvents.draw(drawer);
	}
	
	/*
	 * Envia um evento de tocuh
	 */
	public void sendTouch(final MotionEvent motionEvent) {
		mTouchEvents.onTouch(motionEvent);
	}
	
	/**
	 * Send Back Pressed
	 */
	final public void sendBackPress() {
		mTouchEvents.onBackPressed();
	}
	
	/*
	 * Envia um evento para liberar manuseamento dos objetos
	 */
	final public void sendHandle() {
		mGeneralEvents.handle();
	}
	
	/*
	 * Envia um evento para bloquear manuseamento dos objetos
	 */
	final public void sendUnhandle() {
		mGeneralEvents.unhandle();
	}
}
