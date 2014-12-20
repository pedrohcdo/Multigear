package multigear.general.utils;

/**
 * Measure Enum
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public enum Measure {
	
	/** Inch Measure in Centimeter Unit */
	Inch (2.54f, Unit.Centimeter),
	/** Real Measure in Centimeter Unit */
	Real (1, Unit.Centimeter);
	
	// Final Private Variables
	final private float mMeasure;
	final private Unit mUnit;
	
	/**
	 * Constructor
	 */
	Measure (final float measure, final Unit unit) {
		mMeasure = measure;
		mUnit = unit;
	}
	
	/**
	 * Get Measure Value
	 * 
	 * @return Float Value
	 */
	final public float getValue() {
		return mMeasure;
	}
	
	/**
	 * Get Measure Unit
	 * 
	 * @return Unit Value
	 */
	final public Unit getUnit() {
		return mUnit;
	}
	
	/**
	 * Converts a measure value to another unit.
	 * <p>
	 * @param value Measure Value
	 * @param measure Measure
	 * @param unit Convert Unit
	 * @return Converted float value
	 */
	final public float convertTo(final float value, final Measure measure) {
		return mUnit.convertTo((value * mMeasure), measure.mUnit) / measure.mMeasure;
	}
	
	/**
	 * Converts a measure value to another unit.
	 * <p>
	 * @param value Measure Value
	 * @param measure Measure
	 * @param unit Convert Unit
	 * @return Converted double value
	 */
	final public double convertTo(final double value, final Measure measure) {
		return mUnit.convertTo((value * mMeasure), measure.mUnit) / measure.mMeasure;
	}
}
