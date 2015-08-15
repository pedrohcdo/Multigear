package com.org.multigear.communication.tcp.support;

import com.org.multigear.general.utils.Measure;
import com.org.multigear.general.utils.Vector2;

/**
 * Client Attributes
 * 
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class ParentAttributes {
	
	// Final Public Variables
	final public float Dpi;
	final public int WidthPixels;
	final public int HeightPixels;
	
	/*
	 * COnstrutor
	 */
	protected ParentAttributes(final float dpi, final Vector2 screenPixels) {
		Dpi = dpi;
		WidthPixels = (int)screenPixels.x;
		HeightPixels = (int)screenPixels.y;
	}
	
	/**
	 * Get Dpi
	 * 
	 * @return Return Dpi
	 */
	final public float getDpi() {
		return Dpi;
	}
	
	/**
	 * Get Physical Screen Size
	 * 
	 * @param measure Result Measure
	 * 
	 * @return Measure Value
	 */
	final public Vector2 getPhysicalScreenSize(final Measure measure) {
		final float physicalWidth = Measure.Inch.convertTo(WidthPixels / Dpi, measure);
		final float physicalHeight = Measure.Inch.convertTo(HeightPixels / Dpi, measure);
		return new Vector2(physicalWidth, physicalHeight);
	}
	
	/**
	 * Get Physical Screen Size
	 * <p>
	 * Note: This use Measure.Inch, for other Measures use: {@link ParentAttributes.getPhysicalScreenSize}
	 * 
	 * @param measure Result Measure
	 * 
	 * @return Measure Value
	 */
	final public Vector2 getPhysicalScreenSize() {
		final float physicalWidth = Measure.Inch.convertTo(WidthPixels / Dpi, Measure.Inch);
		final float physicalHeight = Measure.Inch.convertTo(HeightPixels / Dpi, Measure.Inch);
		return new Vector2(physicalWidth, physicalHeight);
	}
	
	/**
	 * Return Screen Size (WidthPixels, HeightPixels)
	 * @return
	 */
	final public Vector2 getScreenSize() {
		return new Vector2(WidthPixels, HeightPixels);
	}
}
