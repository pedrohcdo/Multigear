package multigear.mginterface.engine.eventsmanager;

import android.util.Log;


/**
 * Controla os eventos gerais.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class GeneralEvents {
	
	// Private Variables
	final private multigear.mginterface.engine.Manager mManager;
	final private multigear.mginterface.scene.Scene mMainRoom;
	
	// Private Variables
	private boolean mHandled;
	
	/*
	 * Construtor
	 */
	protected GeneralEvents(final multigear.mginterface.engine.Manager manager) {
		mManager = manager;
		mMainRoom = mManager.getMainRoom();
		mHandled = false;
		GlobalClock.set();
	}
	
	/*
	 * Evento para atualização do tempo
	 */
	final protected void time() {
		if(!mHandled)
			return;
		
		// Update global clock
		GlobalClock.update(25);
		
		// Time MainRoom
		mMainRoom.time(GlobalClock.currentTimeMillis());
	}
	
	/*
	 * Evento de inicialização dos objetos
	 */
	final protected void setup() {
		mMainRoom.setup();
	}
	
	/*
	 * Evento para atualização dos objetos
	 */
	final protected void update() {
		if(mHandled)
			mMainRoom.update();
	}
	
	/*
	 * Evento para liberar manuseamento dos objetos
	 */
	final protected void handle() {
		if(mHandled)
			return;
		mHandled = true;
		GlobalClock.handle();
	}
	
	/*
	 * Evento para bloquear manuseamento dos objetos
	 */
	final protected void unhandle() {
		mHandled = false;
		GlobalClock.unhandle();
	}
	
	/*
	 * Evento para finalizar a Engine
	 */
	final protected void finish() {
		mMainRoom.finish();
	}
}
