package multigear.mginterface.engine;

import multigear.general.utils.Vector2;
import android.widget.LinearLayout;

/**
 * Multigear Sync
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class MultigearSync {
	
	// Final Private Variables
	final private multigear.mginterface.engine.Multigear mMultigear;
	final multigear.mginterface.engine.eventsmanager.EventHandler mEventHandler;
	
	/*
	 * Construtor
	 */
	protected MultigearSync(final Multigear engine, final multigear.mginterface.engine.eventsmanager.EventHandler eventHandler) {
		mMultigear = engine;
		mEventHandler = eventHandler;
	}
	
	/*
	 * Configura a ativade corretamente
	 */
	final public multigear.mginterface.engine.MultigearSync setupActivity() {
		if(mMultigear.isFinished())
			return this;
		mMultigear.setupActivity();
		return this;
	}
	
	/*
	 * Injeta o contaudo para Atividade
	 */
	final public multigear.mginterface.engine.MultigearSync fillActivityContentView() {
		if(mMultigear.isFinished())
			return this;
		mMultigear.getSurface().fillActivityContentView();
		return this;
	}
	
	/*
	 * Injeta o contaudo para um Layout
	 */
	final public multigear.mginterface.engine.MultigearSync addToLayout(final LinearLayout layout) {
		if(mMultigear.isFinished())
			return this;
		mMultigear.getSurface().addToLayout(layout);
		return this;
	}
	
	/*
	 * Atualiza o tempo atual
	 */
	final public multigear.mginterface.engine.MultigearSync time() {
		if(mMultigear.isFinished())
			return this;
		mEventHandler.sendTime();
		return this;
	}
	
	/*
	 * Redimensionamento da tela preparativo
	 */
	final public multigear.mginterface.engine.MultigearSync prepareScreen(final Vector2 screenSize) {
		if(mMultigear.isFinished())
			return this;
		mEventHandler.prepareScreen(screenSize);
		return this;
	}
	
	/*
	 * Arquivação de texturas
	 */
	final public multigear.mginterface.engine.MultigearSync prepareCache(final multigear.mginterface.graphics.opengl.texture.Loader textureLoader) {
		if(mMultigear.isFinished())
			return this;
		mEventHandler.prepareCache(textureLoader);
		return this;
	}
	
	/*
	 * Configuração dos objetos
	 */
	final public multigear.mginterface.engine.MultigearSync setup() {
		if(mMultigear.isFinished())
			return this;
		mEventHandler.sendSetup();
		return this;
	}
	
	/*
	 * Arquivação de texturas
	 */
	final public multigear.mginterface.engine.MultigearSync cache() {
		if(mMultigear.isFinished())
			return this;
		mEventHandler.sendCache();
		return this;
	}
	
	/*
	 * Redimensionamento da tela
	 */
	final public multigear.mginterface.engine.MultigearSync screen() {
		if(mMultigear.isFinished())
			return this;
		mEventHandler.sendScreen();
		return this;
	}
	
	/*
	 * Atualiza os objetos
	 */
	final public multigear.mginterface.engine.MultigearSync update() {
		if(mMultigear.isFinished())
			return this;
		mMultigear.update();
		mEventHandler.sendUpdate();
		return this;
	}
	
	/*
	 * Desenha os objetos
	 */
	final public multigear.mginterface.engine.MultigearSync draw(final multigear.mginterface.graphics.opengl.drawer.Drawer drawer) {
		if(mMultigear.isFinished())
			return this;
		mEventHandler.sendDraw(drawer);
		return this;
	}

	
	/**
	 * Resume Engine
	 */
	final public multigear.mginterface.engine.MultigearSync resume() {
		if(mMultigear.isFinished())
			return this;
		mMultigear.resume();
		mEventHandler.sendHandle();
		return this;
	}
	
	/**
	 * Pause Engine
	 */
	final public multigear.mginterface.engine.MultigearSync pause() {
		if(mMultigear.isFinished())
			return this;
		mMultigear.pause();
		mEventHandler.sendUnhandle();
		return this;
	}
	
	/**
	 * Destroy Engine. 
	 * <p>
	 * Note: After this call end with '.unsync()' and set the instance to null.
	 */
	final public multigear.mginterface.engine.MultigearSync destroy() {
		if(mMultigear.isFinished())
			return this;
		mMultigear.getManager().destroy();
		mMultigear.destroy();
		mEventHandler.sendFinish();
		return this;
	}
	
	/*
	 * Finalisa a sincronização
	 */
	final public void unsync() {
		mMultigear.unsync();
	}
}
