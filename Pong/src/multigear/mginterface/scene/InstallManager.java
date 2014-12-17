package multigear.mginterface.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.view.MotionEvent;

/**
 * Install Support
 * 
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class InstallManager {
	
	/**
	 * Proc Used for Control Installations
	 * 
	 * @author PedroH, RaphaelB
	 * 
	 *         Property Createlier.
	 */
	final private class Handler {
		
		// Consts
		final private static int INSTALL = 1;
		final private static int UNINSTALL = 2;
		
		// Final Private Variables
		final private int mCode;
		final private multigear.mginterface.scene.Installation mInstallation;
		final private int mExtra;
		
		/**
		 * Constructor
		 * 
		 * @param code
		 */
		private Handler(final int code, final multigear.mginterface.scene.Installation installation, int extra) {
			mCode = code;
			mInstallation = installation;
			mExtra = extra;
		}
		
		/**
		 * Call Proc
		 */
		final private void call() {
			switch (mCode) {
				case INSTALL:
					// Install
					mInstallationList.add(mInstallation);
					prepare(mInstallation, mExtra);
					break;
				case UNINSTALL:
					// Log.d("LogTest", "Size: " + mInstallationList.size());
					// Prevent Uninstalling
					mUninstallingInstances.add(mInstallation);
					if (mInstallationList.remove(mInstallation)) {
						// Finish State
						if (mLifeStep < 4)
							mInstallation.finish();
						// Call Uninstalled
						mInstallation.onUninstalled();
					} else {
					} // multigear.general.utils.KernelUtils.error(mRoom.getEngine().getActivity(),
						// "InstallSupport: An error occurred while removing an object. Informed instance was not previously installed.",
						// 0xF);
						// Remove Prevents
					mUninstallingInstances.remove(mInstallation);
			}
		}
	}
	
	/**
	 * Comparador utilisado para ordenamento de sobreposição para todos Sprites
	 * para fins de Desenho.
	 */
	final private Comparator<Installation> mDrawablesComparatorDraw = new Comparator<Installation>() {
		
		/*
		 * Comparador
		 */
		@Override
		public int compare(Installation lhs, Installation rhs) {
			return lhs.getZ() - rhs.getZ();
		}
	};
	
	// Final Private Variables
	final private multigear.mginterface.scene.Scene mRoom;
	// Lista de instalações utilizada para acompanhar qualquer instalação
	// existente
	final private List<multigear.mginterface.scene.Installation> mInstallationList;
	// Lista de instalações que estao aguardando um determinado tempo
	final private List<multigear.mginterface.scene.DelayIU> mInstallDelay;
	// Lista de desinstalações que estao aguardando um tempo
	final private List<multigear.mginterface.scene.DelayIU> mUninstallDelay;
	// Lista de desinstalações que estaão ocrrendo no momento
	final private List<multigear.mginterface.scene.Installation> mUninstallingInstances;
	// Lista de instalações que estão aguardando para serem instaladas
	final private List<Handler> mProcedures;
	
	// Private Variables
	private long mThisTime;
	private int mLifeStep;
	
	/*
	 * Constutor
	 */
	protected InstallManager(final multigear.mginterface.scene.Scene room) {
		mRoom = room;
		mInstallationList = new ArrayList<multigear.mginterface.scene.Installation>();
		mInstallDelay = new ArrayList<multigear.mginterface.scene.DelayIU>();
		mUninstallDelay = new ArrayList<multigear.mginterface.scene.DelayIU>();
		mUninstallingInstances = new ArrayList<multigear.mginterface.scene.Installation>();
		mProcedures = new ArrayList<InstallManager.Handler>();
		mThisTime = 9;
		mLifeStep = 0;
	}
	
	/**
	 * Prepare to install
	 * 
	 * @param installation
	 */
	final private int firstPrepare(final Installation installation) {
		// Link to Engine
		if (installation instanceof multigear.mginterface.scene.Scene) {
			multigear.mginterface.scene.Scene room = (multigear.mginterface.scene.Scene) installation;
			if (!room.hasEngine())
				((multigear.mginterface.scene.Scene) installation).setEngine(mRoom.getEngine());
		}
		// Pos Install Preparation
		installation.time(mRoom.getThisTime());
		installation.setFather(mRoom);
		// Sync Preparation
		installation.prepareScreen(mRoom.getScreenSize());
		installation.prepareCache(mRoom.getTextureLoader());
		// Installed
		installation.onInstalled();
		// Prepare
		if (mLifeStep >= 1)
			installation.setup();
		if (mLifeStep >= 2)
			installation.screen();
		if (mLifeStep >= 3)
			installation.cache();
		return mLifeStep;
		
	}
	
	/**
	 * Add Procedure
	 * 
	 * @param code
	 * @param installation
	 */
	final private void addProc(final int code, final multigear.mginterface.scene.Installation installation, int extra) {
		mProcedures.add(new InstallManager.Handler(code, installation, extra));
	}
	
	/**
	 * Install Object.
	 * 
	 * @param installation
	 *            Installation
	 */
	final protected void install(final multigear.mginterface.scene.Installation installation) {
		if (mInstallationList.contains(installation))
			multigear.general.utils.KernelUtils.error(mRoom.getEngine().getActivity(), "InstallSupport: An error occurred while installing an object. The same is already installed.", 0xE);
		
		addProc(InstallManager.Handler.INSTALL, installation, firstPrepare(installation));
	}
	
	/**
	 * Uninstall Object.
	 * 
	 * @param installation
	 *            Installation
	 */
	final protected void uninstall(final multigear.mginterface.scene.Installation installation) {
		addProc(InstallManager.Handler.UNINSTALL, installation, 0);
	}
	
	/**
	 * Install with delayed
	 */
	final protected void installDelayed(final multigear.mginterface.scene.Installation installation, final long delay) {
		mInstallDelay.add(new multigear.mginterface.scene.DelayIU(mRoom.getThisTime(), delay, installation));
	}
	
	/**
	 * Uninstall with delayed
	 */
	final protected void uninstallDelayed(final multigear.mginterface.scene.Installation installation, final long delay) {
		mUninstallingInstances.add(installation);
		mUninstallDelay.add(new multigear.mginterface.scene.DelayIU(mRoom.getThisTime(), delay, installation));
	}
	
	/**
	 * Return True if has uninstalling.
	 * 
	 * @param installation
	 *            {@link multigear.mginterface.scene.Installation}
	 * @return True if has uninstalling.
	 */
	final protected boolean hasUninstalling(final multigear.mginterface.scene.Installation installation) {
		return mUninstallingInstances.contains(installation);
	}
	
	/*
	 * Prepare Installation
	 */
	final private void prepare(final multigear.mginterface.scene.Installation installation, int steps) {
		installation.time(mThisTime);
		if (mLifeStep >= 1 && 1 > steps)
			installation.setup();
		if (mLifeStep >= 2 && 2 > steps)
			installation.screen();
		if (mLifeStep >= 3 && 3 > steps)
			installation.cache();
		if (mLifeStep == 4 && 4 > steps)
			installation.finish();
	}
	
	/*
	 * Atualiza o tempo
	 */
	final protected void time(final long time) {
		mThisTime = time;
		for (int index = 0; index < mInstallationList.size(); index++)
			mInstallationList.get(index).time(time);
	}
	
	/*
	 * Configuração dos objetos
	 */
	final protected void prevSetup() {
		mLifeStep = 1;
	}
	
	/*
	 * Configuração dos objetos
	 */
	final protected void setup() {
		mLifeStep = 1;
		for (int index = 0; index < mInstallationList.size(); index++)
			mInstallationList.get(index).setup();
	}
	
	/*
	 * Redimensiona a tela
	 */
	final protected void prevScreen() {
		mLifeStep = 2;
	}
	
	/*
	 * Redimensiona a tela
	 */
	final protected void screen(final multigear.general.utils.Ref2F screenSize) {
		mLifeStep = 2;
		for (int index = 0; index < mInstallationList.size(); index++)
			mInstallationList.get(index).screen();
	}
	
	/*
	 * Arquiva as texturas
	 */
	final protected void prevCache() {
		mLifeStep = 3;
	}
	
	/*
	 * Arquiva as texturas
	 */
	final protected void cache(final multigear.mginterface.graphics.opengl.texture.Loader textureLoader) {
		mLifeStep = 3;
		for (int index = 0; index < mInstallationList.size(); index++)
			mInstallationList.get(index).cache();
	}
	
	/*
	 * Atualiza os objetos
	 */
	final protected void update() {
		Collections.sort(mInstallationList, mDrawablesComparatorDraw);
		for (int index = 0; index < mInstallationList.size(); index++)
			mInstallationList.get(index).update();
	}
	
	/*
	 * Desenha os objetos
	 */
	@SuppressLint("WrongCall")
	final protected void draw(final multigear.mginterface.graphics.opengl.drawer.Drawer drawer) {
		Collections.sort(mInstallationList, mDrawablesComparatorDraw);
		for (int index = 0; index < mInstallationList.size(); index++)
			mInstallationList.get(index).draw(drawer);
	}
	
	/*
	 * Evento de toque
	 */
	final protected void touch(final MotionEvent motionEvent) {
		for (int index = 0; index < mInstallationList.size(); index++)
			mInstallationList.get(index).touch(motionEvent);
	}
	
	/*
	 * Update Root
	 */
	final protected void updateManager() {
		if (mInstallDelay.size() > 0) {
			Iterator<multigear.mginterface.scene.DelayIU> itr = mInstallDelay.iterator();
			while (itr.hasNext()) {
				multigear.mginterface.scene.DelayIU delayIU = itr.next();
				if (delayIU.isTimesUp(mRoom.getThisTime())) {
					install(delayIU.getInstallation());
					itr.remove();
					break;
				}
			}
		}
		if (mUninstallDelay.size() > 0) {
			Iterator<multigear.mginterface.scene.DelayIU> itr = mUninstallDelay.iterator();
			while (itr.hasNext()) {
				multigear.mginterface.scene.DelayIU delayIU = itr.next();
				if (delayIU.isTimesUp(mRoom.getThisTime())) {
					uninstall(delayIU.getInstallation());
					itr.remove();
					break;
				}
			}
		}
		if (mProcedures.size() > 0) {
			// Log.d("LogTest", "Start");
			for (final InstallManager.Handler proc : mProcedures)
				proc.call();
			mProcedures.clear();
			// Log.d("LogTest", "Start");
		}
	}
	
	/*
	 * Evento para finalizar a Engine
	 */
	final protected void prevFinish() {
		mLifeStep = 4;
	}
	
	/*
	 * Evento para finalizar a Engine
	 */
	final protected void finish() {
		mLifeStep = 4;
		for (int index = 0; index < mInstallationList.size(); index++)
			mInstallationList.get(index).finish();
	}
}
