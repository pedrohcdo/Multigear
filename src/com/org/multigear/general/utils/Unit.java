package com.org.multigear.general.utils;

/**
 * Unit Enum
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public enum Unit {
	
	/** Millimeter 1x10<sup>-3</sup> */
	Millimeter (-3),
	/** Centimeter 1x10<sup>-2</sup> */
	Centimeter (-2),
	/** Decimeter 1x10<sup>-1</sup> */
	Decimeter (-1),
	/** Meter 1x10<sup>0</sup> */
	Meter (0),
	/** Decameter 1x10<sup>1</sup> */
	Decameter (1),
	/** Hectometer 1x10<sup>2</sup> */
	Hectometer (2),
	/** Kilometer 1x10<sup>3</sup> */
	Kilometer (3);
	
	// Private Variable
	final private double mDecimalValue;
	
	/**
	 * Constructor
	 * 
	 * @param decimalValue Decimal Value
	 */
	Unit(final float cap) {
		mDecimalValue = Math.pow(10, cap);
	}
	
	/**
	 * Converts a value to another unit.
	 */
	final public double convertTo(final double value, final Unit unit) {
		return value * (mDecimalValue / unit.mDecimalValue);
	}
	
	/**
	 * Converts a value to another unit.
	 */
	final public float convertTo(final float value, final Unit unit) {
		return (float)(value * (mDecimalValue / unit.mDecimalValue));
	}
}
