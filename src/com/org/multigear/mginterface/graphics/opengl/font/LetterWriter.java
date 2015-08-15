package com.org.multigear.mginterface.graphics.opengl.font;



/**
 * Letter Writer<br>
 * <b>Note:</b> Unlike FontDrawer, the LetterDrawer is called only when ordered.
 * 
 * @author user
 *
 */
public interface LetterWriter {

	/**
	 * Letter Drawer
	 * @param fontMap FontMap
	 * @param letterDrawer Letter Drawer
	 */
	public void onDraw(final FontMap fontMap, final LetterDrawer letterDrawer);
}
