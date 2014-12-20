package multigear.mginterface.graphics.opengl.font;

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
	final public FontMap create(String fontPath, int fontSize) {
		FontMap fontMap = FontMap.create(mManager.getMainRoom(), mManager.getMainRoom().getTextureLoader(), fontPath, fontSize, FontMap.CharMapBasic);
		return fontMap;
	}
}
