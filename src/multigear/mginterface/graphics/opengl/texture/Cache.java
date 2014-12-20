package multigear.mginterface.graphics.opengl.texture;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Cache utilidado para guardar as texturas.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final class Cache {
	
	// Private Varibales
	final private List<multigear.mginterface.graphics.opengl.texture.Texture> mTextureCache;
	
	/*
	 * Construtor
	 */
	public Cache() {
		mTextureCache = new ArrayList<multigear.mginterface.graphics.opengl.texture.Texture>();
	}
	
	/*
	 * Verifica se já existe determinada textura
	 */
	final private boolean hasTexture(final int id) {
		for(multigear.mginterface.graphics.opengl.texture.Texture texture : mTextureCache) {
			if(texture.getID() == id)
				return true;
		}
		return false;
	}
	
	/*
	 * Adiciona uma textura ao pacote
	 */
	final public void addTexture(final multigear.mginterface.graphics.opengl.texture.Texture texture)  {
		if(hasTexture(texture.getID())) {
			multigear.general.utils.KernelUtils.logW("The texture " + texture.getID() + " has already been loaded.");
		}
		mTextureCache.add(0, texture);
	}
	
	/*
	 * Retorna uma textura
	 */
	final public multigear.mginterface.graphics.opengl.texture.Texture getTexture(final int id) {
		for(multigear.mginterface.graphics.opengl.texture.Texture texture : mTextureCache) {
			if(texture.getID() == id)
				return new Texture(texture.getHandle(), texture.getID(), texture.getSize(), texture.getResourceSize(), texture.getUpdate());
		}
		return null;
	}
}