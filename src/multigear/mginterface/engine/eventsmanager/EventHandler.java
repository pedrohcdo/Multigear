package multigear.mginterface.engine.eventsmanager;

import multigear.general.utils.Vector2;
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
	final private multigear.mginterface.engine.Manager mManager;
	final private multigear.mginterface.engine.eventsmanager.TouchEvents mTouchEvents;
	final private multigear.mginterface.engine.eventsmanager.GraphicsEvents mGraphicsEvents;
	final private multigear.mginterface.engine.eventsmanager.GeneralEvents mGeneralEvents;
	
	/*
	 * Construtor
	 */
	public EventHandler(final multigear.mginterface.engine.Manager manager) {
		mManager = manager;
		mTouchEvents = new multigear.mginterface.engine.eventsmanager.TouchEvents(mManager);
		mGraphicsEvents = new multigear.mginterface.engine.eventsmanager.GraphicsEvents(mManager);
		mGeneralEvents = new multigear.mginterface.engine.eventsmanager.GeneralEvents(mManager);
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
	public void prepareCache(final multigear.mginterface.graphics.opengl.texture.Loader textureLoader) {
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
	final public void sendDraw(final multigear.mginterface.graphics.opengl.drawer.Drawer drawer) {
		mGraphicsEvents.draw(drawer);
	}
	
	/*
	 * Envia um evento de tocuh
	 */
	public void sendTouch(final MotionEvent motionEvent) {
		mTouchEvents.onTouch(motionEvent);
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
	
	/*
	 * Envia um evento para finalizar a Engine
	 */
	final public void sendFinish() {
		mGeneralEvents.finish();
	}
}
