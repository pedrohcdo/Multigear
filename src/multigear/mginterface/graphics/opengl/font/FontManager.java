package multigear.mginterface.graphics.opengl.font;

import android.graphics.Typeface;
import multigear.mginterface.engine.Manager;

/**
 * Font Manager
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class FontManager {
	
	// Final Private Variables
	final private Manager mManager;
	
	/**
	 * Constructor
	 * 
	 * @param manager
	 */
	public FontManager(final Manager manager) {
		mManager = manager;
	}
	
	/**
	 * Create FontMap with ascii range [32, 126]
	 * 
	 * @param fontPath Font path
	 * @param fontSize Font size
	 * @return
	 */
	final public FontMap create(String fontPath, int fontSize, boolean optimized) {
		FontMap fontMap = FontMap.create(mManager.getMainRoom(), mManager.getMainRoom().getTextureLoader(), fontPath, fontSize, FontMap.CharMapBasic, optimized);
		return fontMap;
	}
	
	/**
	 * Create FontMap with ascii range [32, 126]
	 * 
	 * @param fontPath Font path
	 * @param fontSize Font size
	 * @return
	 */
	final public FontMap create(String fontPath, int fontSize) {
		FontMap fontMap = FontMap.create(mManager.getMainRoom(), mManager.getMainRoom().getTextureLoader(), fontPath, fontSize, FontMap.CharMapBasic, true);
		return fontMap;
	}
	
	/**
	 * Create FontMap with ascii range [32, 126]
	 * 
	 * @param typeface Typeface
	 * @param fontSize Font size
	 * @return
	 */
	final public FontMap create(Typeface typeface, int fontSize, boolean optimized) {
		FontMap fontMap = FontMap.create(mManager.getMainRoom(), mManager.getMainRoom().getTextureLoader(), typeface, fontSize, FontMap.CharMapBasic, optimized);
		return fontMap;
	}
	
	/**
	 * Create FontMap with ascii range [32, 126]
	 * 
	 * @param typeface Typeface
	 * @param fontSize Font size
	 * @return
	 */
	final public FontMap create(Typeface typeface, int fontSize) {
		FontMap fontMap = FontMap.create(mManager.getMainRoom(), mManager.getMainRoom().getTextureLoader(), typeface, fontSize, FontMap.CharMapBasic, true);
		return fontMap;
	}
}
