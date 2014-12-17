package multigear.general.utils;

/**
 * Color
 * @author user
 *
 */
final public class Color {
	
	// Constants
	final static public Color RED = new Color(1, 0, 0);
	final static public Color GREEN = new Color(0, 1, 0);
	final static public Color BLUE = new Color(0, 0, 1);
	final static public Color BLACK = new Color(0, 0, 0);
	final static public Color WHITE = new Color(1, 1, 1);
	final static public Color TRANSPARENT = new Color(0, 0, 0, 0);
	
	// Private Variables
	private float mColor[] = {1, 1, 1, 1};
	
	/**
	 * Constructor
	 * 
	 * @param red Red Component
	 * @param green Green Component
	 * @param blue Blue Component
	 * @param alpha Alpha Component
	 */
	public Color(final float red, final float green, final float blue, final float alpha) {
		mColor = new float[] {red, green, blue, alpha};
	}
	
	/**
	 * Constructor
	 * 
	 * @param red Red Component
	 * @param green Green Component
	 * @param blue Blue Component
	 * @param alpha Alpha Component
	 */
	public Color(final float red, final float green, final float blue) {
		mColor = new float[] {red, green, blue, 1};
	}
	
	/**
	 * Constructor
	 * 
	 * @param red Red Component
	 * @param green Green Component
	 * @param blue Blue Component
	 * @param alpha Alpha Component
	 */
	public Color(final Color color) {
		mColor = new float[] {color.mColor[0], color.mColor[1], color.mColor[2], color.mColor[3]};
	}
	
	/**
	 * Return instance of color
	 * 
	 * @param red Red Component
	 * @param green Green Component
	 * @param blue Blue Component
	 * @param alpha Alpha Component
	 */
	final static public Color rgba(final float red, final float green, final float blue, final float alpha) {
		return new Color(red, green, blue, alpha);
	}
	
	/**
	 * Return instance of color
	 * 
	 * @param red Red Component
	 * @param green Green Component
	 * @param blue Blue Component
	 */
	final static public Color rgb(final float red, final float green, final float blue) {
		return new Color(red, green, blue);
	}
	
	/**
	 * Get Red Component
	 * 
	 * @return Red Component
	 */
	final public float getRed() {
		return mColor[0];
	}
	
	/**
	 * Get Green Component
	 * 
	 * @return Green Component
	 */
	final public float getGreen() {
		return mColor[1];
	}
	
	/**
	 * Get Blue Component
	 * 
	 * @return Blue Component
	 */
	final public float getBlue() {
		return mColor[2];
	}
	
	/**
	 * Get Alpha Component
	 * 
	 * @return Alpha Component
	 */
	final public float getAlpha() {
		return mColor[3];
	}
}
