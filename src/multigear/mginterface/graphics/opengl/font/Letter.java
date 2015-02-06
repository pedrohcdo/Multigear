package multigear.mginterface.graphics.opengl.font;

import android.annotation.SuppressLint;

/**
 * Letter is perfect to optimizations, 
 * as this letter had not suffered changes so not raising 
 * the processing consumption.<br>
 * 
 * @author user
 *
 */

final public class Letter {

	// Final Private Variables
	final LetterDrawer mLetterDrawer;
	
	// Private Variables
	private FontMap mFontMap;
	
	/**
	 * Constructor
	 * 
	 * @param fontMap
	 * @param drawer
	 */
	public Letter() {
		mLetterDrawer = new LetterDrawer(this);
	}
	
	/**
	 * Set FontMap<br>
	 * By setting a font, the Letter will be cleared and
	 * it will be necessary to call the method to write().
	 */
	final public void setFontMap(final FontMap fontMap) {
		mFontMap = fontMap;
		mLetterDrawer.clear();
	}
	
	/**
	 * Get Font Map
	 * @return FontMap
	 */
	final public FontMap getFontMap() {
		return mFontMap;
	}
	
	/**
	 * Calls the writer, not recommended repetitive call this method, 
	 * because the letter was meant to be crafted as static, calling 
	 * can often have loss of optimization.
	 */
	@SuppressLint("WrongCall") 
	final public void write(final LetterWriter writer) {
		if(mFontMap == null)
			return;
		if(writer == null)
			return;
		mLetterDrawer.begin(mFontMap);
		writer.onDraw(mFontMap, mLetterDrawer);
		mLetterDrawer.end();
	}
}
