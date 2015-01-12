package multigear.mginterface.engine;

import multigear.audio.AudioManager;
import multigear.cache.CacheManager;
import multigear.communication.tcp.support.ComManager;
import multigear.mginterface.graphics.opengl.font.FontManager;
import multigear.mginterface.scene.DensityParser;
import multigear.mginterface.scene.Scene;
import multigear.mginterface.scene.SpaceParser;
import multigear.services.ServicesManager;
import android.app.Activity;



/**
 * 
 * Gerenciador utilisado pela Multigear
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class Manager {
	
	// Private Variables
	final private Multigear mEngine;
	final private Scene mMainRoom;
	final private AudioManager mAudioManager;
	final private ComManager mComManager;
	final private SpaceParser mSpaceParser;
	final private DensityParser mProportionParser;
	final private ServicesManager mServicesManager;
	final private FontManager mFontManager;
	final private CacheManager mCacheManager;
	
	/*
	 * Construtor
	 */
	protected Manager(final multigear.mginterface.engine.Multigear engine) {
		mEngine = engine;
		multigear.mginterface.scene.Scene mainRoom = null;
		try {
			mainRoom = engine.getConfiguration().getMainRoom().newInstance();
		} catch (Exception e) {
			multigear.general.utils.KernelUtils.error(engine.getActivity(), e.getMessage(), 0x4);
		}
		mMainRoom = mainRoom;
		mMainRoom.setEngine(mEngine);
		mAudioManager = new multigear.audio.AudioManager(this);
		mComManager = new multigear.communication.tcp.support.ComManager(this);
		mSpaceParser = new multigear.mginterface.scene.SpaceParser(mMainRoom);
		mProportionParser = new multigear.mginterface.scene.DensityParser(mMainRoom);
		mServicesManager = new multigear.services.ServicesManager(this);
		mFontManager = new FontManager(this);
		mCacheManager = new CacheManager();
	}
	
	/**
	 * Get Audio Manager
	 * 
	 * @return {@link multigear.audio.AudioManager}
	 */
	final protected multigear.audio.AudioManager getAudioManager() {
		return mAudioManager;
	}
	
	/**
	 * Get ComManager
	 * 
	 * @return {@link multigear.communication.tcp.support.ComManager}
	 */
	final protected multigear.communication.tcp.support.ComManager getComManager() {
		return mComManager;
	}
	
	/**
	 * Get Cache Manager
	 * 
	 * @return {@link CacheManager}
	 */
	final protected CacheManager getCacheManager() {
		return mCacheManager;
	}
	
	/**
	 * Get Space Parser
	 * 
	 * @return {@link multigear.mginterface.scene.SpaceParser}
	 */
	final public multigear.mginterface.scene.SpaceParser getSpaceParser() {
		return mSpaceParser;
	}
	
	/**
	 * Get Proportion Parser
	 * 
	 * @return {@link multigear.mginterface.scene.DensityParser}
	 */
	final public multigear.mginterface.scene.DensityParser getDensityParser() {
		return mProportionParser;
	}
	
	/**
	 * Get Font Manager
	 * 
	 * @return {@link FontManager}
	 */
	final public FontManager getFontManager() {
		return mFontManager;
	}
	
	/**
	 * Get Services Manager
	 * 
	 * @return {@link multigear.services.ServicesManager}}
	 */
	final public multigear.services.ServicesManager getServicesManager() {
		return mServicesManager;
	}
	
	/*
	 * Retorna a Engine
	 */
	final public multigear.mginterface.engine.Multigear getEngine() {
		return mEngine;
	}
	
	/*
	 * Retorna a Room principal
	 */
	final public multigear.mginterface.scene.Scene getMainRoom() {
		return mMainRoom;
	}
	
	/**
	 * Get Activity
	 * 
	 * @return
	 */
	final public Activity getActivity() {
		return mEngine.getActivity();
	}
	
	/**
	 * Update Manager
	 */
	final protected void update() {
		mComManager.update();
		mProportionParser.update();
		mServicesManager.update();
	}
	
	/**
	 * Finish Manager
	 */
	final protected void destroy() {
		mServicesManager.finish();
		mComManager.finish();
	}
}
