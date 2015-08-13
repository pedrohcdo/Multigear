package multigear.general.utils;

import multigear.mginterface.engine.Configuration;
import multigear.mginterface.scene.Scene;

/**
 * Egine Config Collection
 * 
 * @author user
 *
 */
final public class EngineConfigCollection {
	
	// Private Constructor
	private EngineConfigCollection() {};
	
	/**
	 * Get Simple Configuration with Shared Resources
	 * @return
	 */
	final static public Configuration sharedResources(final Class<? extends Scene> mainScene, final Vector2 baseScreenSize) {
		// Instance
		Configuration config = new Configuration();
		
		// Set default base Dpi
		config.setAttr(Configuration.ATTR_BASE_DPI, Configuration.DEFAULT_VALUE);
		
		// Set texture function 
		config.enable(Configuration.FUNC_TEXTURE_PROPORTION);
		config.setAttr(Configuration.ATTR_BASE_SCREEN, baseScreenSize);
		config.setAttr(Configuration.ATTR_PROPORTION_FROM, Configuration.PROPORTION_FROM_INDIVIDUAL);
		config.setAttr(Configuration.ATTR_PROPORTION_MODE, Configuration.PROPORTION_MODE_BIGGER);
		
		// Set Restorer Function
		config.disable(Configuration.FUNC_RESTORER_SERVICE);
		
		// Set Background Color
		config.setAttr(Configuration.ATTR_BACKGROUND_COLOR, 0xFF000000);
		
		// Set Main Room
		config.setMainRoom(mainScene);
		
		// Return configuration
		return config;
	}
}
