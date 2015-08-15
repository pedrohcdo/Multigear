package com.org.multigear.mginterface.engine;

import com.org.multigear.audio.AudioManager;
import com.org.multigear.cache.CacheManager;
import com.org.multigear.communication.tcp.support.ComManager;
import com.org.multigear.mginterface.graphics.opengl.font.FontManager;
import com.org.multigear.mginterface.scene.DensityParser;
import com.org.multigear.mginterface.scene.Scene;
import com.org.multigear.mginterface.scene.SpaceParser;
import com.org.multigear.services.ServicesManager;

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
	protected Manager(final com.org.multigear.mginterface.engine.Multigear engine) {
		mEngine = engine;
		com.org.multigear.mginterface.scene.Scene mainRoom = null;
		try {
			mainRoom = engine.getConfiguration().getMainRoom().newInstance();
		} catch (Exception e) {
			com.org.multigear.general.utils.KernelUtils.error(engine.getActivity(), e.getMessage(), 0x4);
		}
		mMainRoom = mainRoom;
		mMainRoom.setEngine(mEngine);
		mAudioManager = new com.org.multigear.audio.AudioManager(this);
		mComManager = new com.org.multigear.communication.tcp.support.ComManager(this);
		mSpaceParser = new com.org.multigear.mginterface.scene.SpaceParser(mMainRoom);
		mProportionParser = new com.org.multigear.mginterface.scene.DensityParser(mMainRoom);
		mServicesManager = new com.org.multigear.services.ServicesManager(this);
		mFontManager = new FontManager(this);
		mCacheManager = new CacheManager();
	}
	
	/**
	 * Get Audio Manager
	 * 
	 * @return {@link com.org.multigear.audio.AudioManager}
	 */
	final protected com.org.multigear.audio.AudioManager getAudioManager() {
		return mAudioManager;
	}
	
	/**
	 * Get ComManager
	 * 
	 * @return {@link com.org.multigear.communication.tcp.support.ComManager}
	 */
	final protected com.org.multigear.communication.tcp.support.ComManager getComManager() {
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
	 * @return {@link com.org.multigear.mginterface.scene.SpaceParser}
	 */
	final public com.org.multigear.mginterface.scene.SpaceParser getSpaceParser() {
		return mSpaceParser;
	}
	
	/**
	 * Get Proportion Parser
	 * 
	 * @return {@link com.org.multigear.mginterface.scene.DensityParser}
	 */
	final public com.org.multigear.mginterface.scene.DensityParser getDensityParser() {
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
	 * @return {@link com.org.multigear.services.ServicesManager}}
	 */
	final public com.org.multigear.services.ServicesManager getServicesManager() {
		return mServicesManager;
	}
	
	/*
	 * Retorna a Engine
	 */
	final public com.org.multigear.mginterface.engine.Multigear getEngine() {
		return mEngine;
	}
	
	/*
	 * Retorna a Room principal
	 */
	final public com.org.multigear.mginterface.scene.Scene getMainRoom() {
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
	 * Pause Manager
	 */
	final protected void pause() {
		mComManager.pause();
	}
	
	/**
	 * Resume Manager
	 */
	final protected void resume() {
		mComManager.resume();
	}
	
	/**
	 * Finish Manager
	 */
	final protected void destroy() {
		mAudioManager.finish();
		mComManager.finish();
		mServicesManager.finish();
	}
	
	/**
	 * Unregister All intents of manager
	 */
	final protected void unregisterIntents() {
		mServicesManager.unregisterIntents();
	}
}
