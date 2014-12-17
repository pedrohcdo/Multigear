package multigear.mginterface.graphics.opengl.texture;

import multigear.cache.CacheComponent;
import multigear.general.utils.Ref2F;
import multigear.mginterface.graphics.opengl.drawer.TextureContainer;
import android.graphics.Bitmap;


/**
 * Classe utilisada para arquivar informações de uma textura
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class Texture extends CacheComponent {
	
	/**
	 * Default Texture Map 
	 */
	final private static TextureMap DefaultTextureMap = new TextureMap() {
		
		@Override
		public void onMap(TextureContainer container) {
			container.fill();
		}
	};
	
	// Variables
	final private int mHandle;
	final private int mID;
	final private multigear.general.utils.Ref2F mSize;
	final private multigear.mginterface.graphics.opengl.texture.Updater mUpdater;
	final private Ref2F mResourceSize;
	
	// Private Variables
	private TextureMap mTextureMap = DefaultTextureMap;
	
	/*
	 * Construtor
	 */
	protected Texture(final int handle, final int id, final Ref2F size, final Ref2F resourceSize, final Updater updater) {
		mHandle = handle;
		mID = id;
		mSize = size.clone();
		mUpdater = updater;
		mResourceSize = resourceSize.clone();
	}
	
	/*
	 * Retorna o manipulador
	 */
	public int getHandle() {
		return mHandle;
	}
	
	/*
	 * Retorna o ID da textura. Normalmente atribuido pela id da respectiva
	 * Resource.
	 */
	public int getID() {
		return mID;
	}
	
	/*
	 * Atualiza o conteudo da textura
	 */
	final public multigear.mginterface.graphics.opengl.texture.Texture setBitmap(final Bitmap bitmap) {
		final multigear.general.utils.Ref2F size = mUpdater.update(mHandle, bitmap);
		mSize.XAxis = size.XAxis;
		mSize.YAxis = size.YAxis;
		return this;
	}
	
	/**
	 * Return Texture Size
	 * @return
	 */
	final public Ref2F getSize() {
		return mSize.clone();
	}
	
	/**
	 * Return Resource Size without proportion.
	 * @return
	 */
	final public Ref2F getResourceSize() {
		return mResourceSize.clone();
	}
	
	/**
	 * Return Texture Updater
	 * @return
	 */
	final public Updater getUpdate() {
		return mUpdater;
	}
	
	/*
	 * Atualiza o conteudo da textura
	 */
	final public multigear.mginterface.graphics.opengl.texture.Texture setBitmap(final Bitmap bitmap, final boolean recycle) {
		final multigear.general.utils.Ref2F size = mUpdater.update(mHandle, bitmap);
		if(recycle)
			bitmap.recycle();
		mSize.XAxis = size.XAxis;
		mSize.YAxis = size.YAxis;
		return this;
	}
	
	/**
	 * Stretch Texture.
	 * @param size Size
	 */
	final public void stretch(final multigear.general.utils.Ref2F size) {
		mSize.XAxis = size.XAxis;
		mSize.YAxis = size.YAxis;
	}
	
	/**
	 * Set Texture Mapper used by this instance. This mapping is used by all 
	 * containers that carry is texture. The initial mapping this set to draw 
	 * any texture in the container. 
	 * <p>
	 * @param textureMap {@link TextureMap}
	 */
	final public void setMapper(final TextureMap textureMap) {
		mTextureMap = textureMap;
	}
	
	/**
	 * Get Texture Map;
	 * @return
	 */
	final public TextureMap getMapper() {
		return mTextureMap;
	}
}