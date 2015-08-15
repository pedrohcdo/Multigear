package com.org.multigear.mginterface.tools.mgmap;

import com.org.multigear.general.utils.GeneralUtils;
import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.engine.Configuration;
import com.org.multigear.mginterface.scene.Scene;


/**
 * DensityParser
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class GameDensityParser {
	
	// Private Variables
	private Vector2 mBaseScreen;
	private Vector2 mMinorScreen;
	
	/*
	 * Construtor
	 */
	protected GameDensityParser(final Scene scene, final Vector2 minorScreen) {
		mBaseScreen = scene.getConfiguration().getRef2DAttr(Configuration.ATTR_BASE_SCREEN);
		if(mBaseScreen == Configuration.DEFAULT_REF2D)
			mBaseScreen = minorScreen.clone();
		mMinorScreen = minorScreen;
	}
	
	/**
	 * Return Proportional Reference 2D in Base Screen
	 * @param x Static X
	 * @param y Static Y
	 * @return Proportional Reference 2D
	 */
	final public Vector2 smallerRef2D(final float x, final float y) {
		return GeneralUtils.calculateIndividualRef2DSmaller(new Vector2(x, y), mBaseScreen, mMinorScreen);
	}
	
	/**
	 * Return Proportional Reference 2D in Base Screen
	 * @param x Static X
	 * @param y Static Y
	 * @return Proportional Reference 2D
	 */
	final public Vector2 biggerRef2D(final float x, final float y) {
		return GeneralUtils.calculateIndividualRef2DBigger(new Vector2(x, y), mBaseScreen, mMinorScreen);
	}
	
	/**
	 * Return Proportional Value
	 * @param value Value
	 * @return Proportional Value
	 */
	final public float smallerValue(final float value) {
		return GeneralUtils.calculateIndividualValueSmaller(value, mBaseScreen, mMinorScreen);
	}
	
	/**
	 * Return Proportional Value
	 * @param value Value
	 * @return Proportional Value
	 */
	final public float biggerValue(final float value) {
		return GeneralUtils.calculateIndividualValueBigger(value, mBaseScreen, mMinorScreen);
	}
}
