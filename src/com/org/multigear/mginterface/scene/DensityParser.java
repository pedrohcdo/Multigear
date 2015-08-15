package com.org.multigear.mginterface.scene;

import com.org.multigear.general.utils.Vector2;


/**
 * DensityParser
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class DensityParser {
	
	// Final Private Variables
	final private com.org.multigear.mginterface.scene.Scene mRoom;
	final private com.org.multigear.mginterface.engine.Configuration.OptimizedKey mOptimizedKeyA, mOptimizedKeyB;
	
	// Private Variables
	private float mBaseDensity;
	private Vector2 mBaseScreen;
	
	/*
	 * Construtor
	 */
	public DensityParser(final com.org.multigear.mginterface.scene.Scene room) {
		mRoom = room;
		mOptimizedKeyA = room.getConfiguration().createOptimizedKey(com.org.multigear.mginterface.engine.Configuration.ATTR_BASE_DENSITY);
		mOptimizedKeyB = room.getConfiguration().createOptimizedKey(com.org.multigear.mginterface.engine.Configuration.ATTR_BASE_SCREEN);
		mBaseDensity = mRoom.getConfiguration().getFloatAttr(com.org.multigear.mginterface.engine.Configuration.ATTR_BASE_DENSITY);
		mBaseScreen = mRoom.getConfiguration().getRef2DAttr(com.org.multigear.mginterface.engine.Configuration.ATTR_BASE_SCREEN);
	}
	
	/*
	 * Update
	 */
	final public void update() {
		mBaseDensity = mOptimizedKeyA.getFloatAttr();
		mBaseScreen = mOptimizedKeyB.getRef2DAttr();
	}
	
	/**
	 * Return Proportional Reference 2D in Base Screen
	 * @param x Static X
	 * @param y Static Y
	 * @return Proportional Reference 2D
	 */
	final public Vector2 smallerRef2D(final float x, final float y) {
		//final float density = mRoom.getDensity();
		final Vector2 screenSize = mRoom.getScreenSize();
		//float baseDensity = mBaseDensity;
		Vector2 baseScreenSize = mBaseScreen;
		
		// If default value, set as default density
		 //if(baseDensity == multigear.mginterface.engine.Configuration.DEFAULT_VALUE)
		//	baseDensity = density;
		// If default value, set as default display
		if(baseScreenSize == com.org.multigear.mginterface.engine.Configuration.DEFAULT_REF2D)
			baseScreenSize = screenSize;
		final Vector2 ref2D = new Vector2(x, y);
		return com.org.multigear.general.utils.GeneralUtils.calculateIndividualRef2DSmaller(ref2D, baseScreenSize, screenSize);
	}
	
	/**
	 * Return Proportional Reference 2D in Base Screen
	 * @param x Static X
	 * @param y Static Y
	 * @return Proportional Reference 2D
	 */
	final public Vector2 biggerRef2D(final float x, final float y) {
		//final float density = mRoom.getDensity();
		final Vector2 screenSize = mRoom.getScreenSize();
		//float baseDensity = mBaseDensity;
		Vector2 baseScreenSize = mBaseScreen;
		
		// If default value, set as default density
		//if(baseDensity == multigear.mginterface.engine.Configuration.DEFAULT_VALUE)
		//	baseDensity = density;
		// If default value, set as default display
		if(baseScreenSize == com.org.multigear.mginterface.engine.Configuration.DEFAULT_REF2D)
			baseScreenSize = screenSize;
		final Vector2 ref2D = new Vector2(x, y);
		return com.org.multigear.general.utils.GeneralUtils.calculateIndividualRef2DBigger(ref2D, baseScreenSize, screenSize);
	}
	
	/**
	 * Return Proportional Value
	 * @param value Value
	 * @return Proportional Value
	 */
	final public float smallerValue(final float value) {
		final Vector2 screenSize = mRoom.getScreenSize();
		Vector2 baseScreenSize = mBaseScreen;
		// If default value, set as default display
		if(baseScreenSize == com.org.multigear.mginterface.engine.Configuration.DEFAULT_REF2D)
			baseScreenSize = screenSize;
		return com.org.multigear.general.utils.GeneralUtils.calculateIndividualValueSmaller(value, baseScreenSize, screenSize);
	}
	
	/**
	 * Return Proportional Value
	 * @param value Value
	 * @return Proportional Value
	 */
	final public float biggerValue(final float value) {
		final Vector2 screenSize = mRoom.getScreenSize();
		Vector2 baseScreenSize = mBaseScreen;
		// If default value, set as default display
		if(baseScreenSize == com.org.multigear.mginterface.engine.Configuration.DEFAULT_REF2D)
			baseScreenSize = screenSize;
		return com.org.multigear.general.utils.GeneralUtils.calculateIndividualValueBigger(value, baseScreenSize, screenSize);
	}
}
