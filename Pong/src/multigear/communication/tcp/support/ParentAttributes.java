package multigear.communication.tcp.support;

import multigear.general.utils.Measure;
import multigear.general.utils.Ref2F;

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
	final Ref2F mScreenAjust;
	
	/*
	 * COnstrutor
	 */
	protected ParentAttributes(final float dpi, final multigear.general.utils.Ref2F screenPixels, final Ref2F thisScreenSize) {
		Dpi = dpi;
		WidthPixels = (int)screenPixels.XAxis;
		HeightPixels = (int)screenPixels.YAxis;
		mScreenAjust = new Ref2F(WidthPixels / thisScreenSize.XAxis, HeightPixels / thisScreenSize.YAxis);
	}
	
	/**
	 * Get Physical Screen Size
	 * 
	 * @param measure Result Measure
	 * 
	 * @return Measure Value
	 */
	final public Ref2F getPhysicalScreenSize(final multigear.general.utils.Measure measure) {
		final float physicalWidth = Measure.Inch.convertTo(WidthPixels / Dpi, measure);
		final float physicalHeight = Measure.Inch.convertTo(HeightPixels / Dpi, measure);
		return new Ref2F(physicalWidth, physicalHeight);
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
	final public Ref2F getPhysicalScreenSize() {
		final float physicalWidth = Measure.Inch.convertTo(WidthPixels / Dpi, Measure.Inch);
		final float physicalHeight = Measure.Inch.convertTo(HeightPixels / Dpi, Measure.Inch);
		return new Ref2F(physicalWidth, physicalHeight);
	}
	
	/**
	 * Return Screen Size (WidthPixels, HeightPixels)
	 * @return
	 */
	final public Ref2F getScreenSize() {
		return new Ref2F(WidthPixels, HeightPixels);
	}
	
	/**
	 * Return Screen Ajust Factor
	 * @return
	 */
	final public Ref2F getScreenAjust() {
		return mScreenAjust.clone();
	}
}
