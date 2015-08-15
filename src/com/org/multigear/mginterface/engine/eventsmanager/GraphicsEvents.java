package com.org.multigear.mginterface.engine.eventsmanager;

import com.org.multigear.general.utils.Vector2;

/**
 * 
 * Controla os eventos relacionados com grafico
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class GraphicsEvents {
	
	// Private Variables
	final private com.org.multigear.mginterface.engine.Manager mManager;
	final private com.org.multigear.mginterface.scene.Scene mMainRoom;
	
	/*
	 * Construtor
	 */
	protected GraphicsEvents(final com.org.multigear.mginterface.engine.Manager manager) {
		mManager = manager;
		mMainRoom = mManager.getMainRoom();
	}
	
	/*
	 * Evento para redimensionamento da tela preparativo
	 */
	public void prepareScreen(final Vector2 screenSize) {
		mMainRoom.prepareScreen(screenSize);
	}
	
	/*
	 * Evento para arquivação preparativo
	 */
	public void prepareCache(final com.org.multigear.mginterface.graphics.opengl.texture.Loader textureLoader) {
		mMainRoom.prepareCache(textureLoader);
	}
	
	/*
	 * Envia um evento para arquivação de texturas
	 */
	final protected void cache() {
		mMainRoom.cache();
	}
	
	/*
	 * Envia um evento para redimensionamento da tela
	 */
	final protected void screen() {
		mMainRoom.screen();
	}
	
	/*
	 * Envia um evento para desenhar o frame atual
	 */
	final protected void draw(final com.org.multigear.mginterface.graphics.opengl.drawer.Drawer drawer) {
		mMainRoom.draw(drawer);
	}
}
