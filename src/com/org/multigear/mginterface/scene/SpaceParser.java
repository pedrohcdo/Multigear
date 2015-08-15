package com.org.multigear.mginterface.scene;

import com.org.multigear.general.utils.Vector2;




/**
 * Base Plane Support
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class SpaceParser {
	
	// Final Private Variables
	final private com.org.multigear.mginterface.scene.Scene mRoom;
	final private com.org.multigear.mginterface.engine.Configuration.OptimizedKey mOptimizedKey;
	
	/*
	 * Construtor
	 */
	public SpaceParser(final com.org.multigear.mginterface.scene.Scene room) {
		mRoom = room;
		mOptimizedKey = room.getConfiguration().createOptimizedKey(com.org.multigear.mginterface.engine.Configuration.ATTR_BASE_DPI);
	}
	
	/**
	 * Return Scale Factor
	 * 
	 * @return
	 */
	final public float getScaleFactor() {
		// Scale Factor
		float scaleFactor;
		// Get Base Dpi
		final float baseDensity = mOptimizedKey.getFloatAttr();
		// Default Scale
		if(baseDensity == -1) {
			scaleFactor = 1;
		// Calculare Scale Factor
		}else
			scaleFactor = mRoom.getDPI() / baseDensity;
		// Return Scale Factor
		return scaleFactor;
	}
	
	/**
	 * Return Inverse Scale Factor
	 * 
	 * @return
	 */
	final public float getInverseScaleFactor() {
		// Scale Factor
		float scaleFactor;
		// Get Base Dpi
		final float baseDensity = mOptimizedKey.getFloatAttr();
		// Default Scale
		if(baseDensity == -1)
			scaleFactor = 1;
		// Calculare Scale Factor
		else
			scaleFactor = baseDensity / mRoom.getDPI();
		// Return Scale Factor
		return scaleFactor;
	}
	
	/*
	 * Retorna o tamanho da tela
	 */
	final public Vector2 getScreenSize() {
		final Vector2 size = new Vector2(0, 0);
		final float scaleFactor = getScaleFactor();
		size.x = mRoom.getScreenSize().x / scaleFactor;
		size.y = mRoom.getScreenSize().y / scaleFactor;
		return size;
	}
	
	/*
	 * Passa um valor para a base
	 */
	final public float parseToBase(final float value) {
		return value * getScaleFactor();
	}
	
	/**
	 * Parse to inverse of Base
	 * 
	 * @param value
	 * @return
	 */
	final public double parseToInverseBase(final double value) {
		return value / getScaleFactor();
	}
	
	/*
	 * Passa uma referencia2d para a base
	 */
	final public Vector2 parseToBase(final Vector2 ref2d) {
		final float scaleFactor = getScaleFactor();
		return new Vector2(ref2d.x * scaleFactor, ref2d.y * scaleFactor);
	}
}
