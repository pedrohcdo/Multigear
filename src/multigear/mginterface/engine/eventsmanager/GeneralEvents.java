package multigear.mginterface.engine.eventsmanager;


/**
 * Controla os eventos gerais.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class GeneralEvents {
	
	/**
	 * Synchronized Clock
	 * 
	 * @author PedroH, RaphaelB
	 *
	 * Property Createlier.
	 */
	final public class SyncClock {
		
		// Final Private Variables
		private long mClockTime;
		
		/**
		 * Constructor
		 */
		private SyncClock() {
			mClockTime = mLastTimeMillis;
		}
		
		/**
		 * Set Clock to Engine current time
		 */
		final public void set() {
			mClockTime = mLastTimeMillis;
		}
		
		/**
		 * Return Elapsed Time
		 * @return
		 */
		final public long elapseTime() {
			return System.currentTimeMillis() - mClockTime;
		}
	}
	
	// Private Variables
	final private multigear.mginterface.engine.Manager mManager;
	final private multigear.mginterface.scene.Scene mMainRoom;
	
	// Private Variables
	private boolean mHandled;
	private long mClockTimeMillis;
	private long mLastTimeMillis;
	
	/*
	 * Construtor
	 */
	protected GeneralEvents(final multigear.mginterface.engine.Manager manager) {
		mManager = manager;
		mMainRoom = mManager.getMainRoom();
		mHandled = true;
		mClockTimeMillis = 0;
		mLastTimeMillis = System.currentTimeMillis();
	}
	
	/*
	 * Evento para atualização do tempo
	 */
	final protected void time() {
		if(!mHandled)
			return;
		mClockTimeMillis += Math.min(System.currentTimeMillis() - mLastTimeMillis, 25);
		mManager.setEngineCurrentTime(mClockTimeMillis);
		mMainRoom.time(mClockTimeMillis);
		mLastTimeMillis = System.currentTimeMillis();
	}
	
	/**
	 * Creates a clock synchronized with the engine.
	 * @return
	 */
	final protected SyncClock createSyncClock() {
		return new SyncClock();
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
		mLastTimeMillis = System.currentTimeMillis();
	}
	
	/*
	 * Evento para bloquear manuseamento dos objetos
	 */
	final protected void unhandle() {
		mHandled = false;
	}
	
	/*
	 * Evento para finalizar a Engine
	 */
	final protected void finish() {
		mMainRoom.finish();
	}
}
