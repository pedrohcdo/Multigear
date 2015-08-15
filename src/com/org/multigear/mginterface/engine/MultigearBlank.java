package com.org.multigear.mginterface.engine;

import com.org.multigear.general.utils.Vector2;

import android.widget.LinearLayout;

/**
 * Multigear Sync
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class MultigearBlank {
	
	// Final Private Variables
	final private com.org.multigear.mginterface.engine.Multigear mMultigear;
	final com.org.multigear.mginterface.engine.eventsmanager.EventHandler mEventHandler;
	
	/*
	 * Construtor
	 */
	protected MultigearBlank(final Multigear engine, final com.org.multigear.mginterface.engine.eventsmanager.EventHandler eventHandler) {
		mMultigear = engine;
		mEventHandler = eventHandler;
	}
	
	/*
	 * Configura a ativade corretamente
	 */
	final public com.org.multigear.mginterface.engine.MultigearBlank setupActivity() {
		if(mMultigear.isFinished())
			return this;
		mMultigear.setupActivity();
		return this;
	}
	
	/*
	 * Injeta o contaudo para Atividade
	 */
	final public com.org.multigear.mginterface.engine.MultigearBlank fillActivityContentView() {
		if(mMultigear.isFinished())
			return this;
		mMultigear.getSurface().fillActivityContentView();
		return this;
	}
	
	/*
	 * Injeta o contaudo para um Layout
	 */
	final public com.org.multigear.mginterface.engine.MultigearBlank addToLayout(final LinearLayout layout) {
		if(mMultigear.isFinished())
			return this;
		mMultigear.getSurface().addToLayout(layout);
		return this;
	}
	
	/*
	 * Atualiza o tempo atual
	 */
	final public MultigearBlank time() {
		if(mMultigear.isFinished())
			return this;
		mEventHandler.sendTime();
		return this;
	}
	
	/*
	 * Redimensionamento da tela preparativo
	 */
	final public com.org.multigear.mginterface.engine.MultigearBlank prepareScreen(final Vector2 screenSize) {
		if(mMultigear.isFinished())
			return this;
		mEventHandler.prepareScreen(screenSize);
		return this;
	}
	
	/*
	 * Arquivação de texturas
	 */
	final public com.org.multigear.mginterface.engine.MultigearBlank prepareCache(final com.org.multigear.mginterface.graphics.opengl.texture.Loader textureLoader) {
		if(mMultigear.isFinished())
			return this;
		mEventHandler.prepareCache(textureLoader);
		return this;
	}
	
	/*
	 * Configuração dos objetos
	 */
	final public com.org.multigear.mginterface.engine.MultigearBlank setup() {
		if(mMultigear.isFinished())
			return this;
		mEventHandler.sendSetup();
		return this;
	}
	
	/*
	 * Arquivação de texturas
	 */
	final public com.org.multigear.mginterface.engine.MultigearBlank cache() {
		if(mMultigear.isFinished())
			return this;
		mEventHandler.sendCache();
		return this;
	}
	
	/*
	 * Redimensionamento da tela
	 */
	final public com.org.multigear.mginterface.engine.MultigearBlank screen() {
		if(mMultigear.isFinished())
			return this;
		mEventHandler.sendScreen();
		return this;
	}
	
	/*
	 * Atualiza os objetos
	 */
	final public com.org.multigear.mginterface.engine.MultigearBlank update() {
		if(mMultigear.isFinished())
			return this;
		mMultigear.update();
		mEventHandler.sendUpdate();
		return this;
	}
	
	/*
	 * Desenha os objetos
	 */
	final public com.org.multigear.mginterface.engine.MultigearBlank draw(final com.org.multigear.mginterface.graphics.opengl.drawer.Drawer drawer) {
		if(mMultigear.isFinished())
			return this;
		mEventHandler.sendDraw(drawer);
		return this;
	}
}
