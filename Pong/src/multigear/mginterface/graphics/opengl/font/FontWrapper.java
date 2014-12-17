package multigear.mginterface.graphics.opengl.font;

import multigear.mginterface.graphics.opengl.drawer.TextureContainer;
import android.annotation.SuppressLint;

/**
 * Text Extender
 * 
 * @author user
 * 
 */
public class FontWrapper {

	/**
	 * Process Writer to Draw
	 * 
	 * @param text
	 *            Text to draw
	 */
	@SuppressLint("WrongCall")
	final static public void processWriter(final FontMap fontMap, final FontWriter fontWriter, final String text, final TextureContainer container) {
		if (fontWriter != null) {
			fontWriter.onDraw(fontMap.beginFontDrawer(container), text);
			fontMap.endFontDrawer();
		}
	}
}
