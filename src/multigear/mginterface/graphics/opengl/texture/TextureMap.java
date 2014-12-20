package multigear.mginterface.graphics.opengl.texture;

import multigear.mginterface.graphics.opengl.drawer.TextureContainer;


/**
 * Texture Map used by Texture.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public interface TextureMap {
	
	/**
	 * Draw your Texture.
	 */
	public void onMap(final TextureContainer container);
}
