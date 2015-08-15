package com.org.multigear.mginterface.graphics.opengl.texture;

import com.org.multigear.cache.CacheComponent;
import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.graphics.opengl.BlendFunc;
import com.org.multigear.mginterface.graphics.opengl.WrapMode;
import com.org.multigear.mginterface.graphics.opengl.drawer.TextureContainer;

import android.graphics.Bitmap;
import android.opengl.GLES20;


/**
 * Classe utilisada para arquivar informações de uma textura
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class Texture implements CacheComponent {
	
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
	final private Vector2 mSize;
	final private com.org.multigear.mginterface.graphics.opengl.texture.Updater mUpdater;
	final private Vector2 mResourceSize;
	
	// Private Variables
	private TextureMap mTextureMap = DefaultTextureMap;
	private WrapMode[] mWrapMode = new WrapMode[] {WrapMode.CLAMP_TO_EDGE, WrapMode.CLAMP_TO_EDGE};
	
	/*
	 * Construtor
	 */
	protected Texture(final int handle, final int id, final Vector2 size, final Vector2 resourceSize, final Updater updater) {
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
	
	/**
	 * Set Texture Wrap Mode
	 */
	final public void setWrapMode(final WrapMode xWrapMode, final WrapMode yWrapMode) {
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mHandle);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, xWrapMode.getConst());
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, yWrapMode.getConst());
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	}
	
	/*
	 * Atualiza o conteudo da textura
	 */
	final public com.org.multigear.mginterface.graphics.opengl.texture.Texture setBitmap(final Bitmap bitmap) {
		final Vector2 size = mUpdater.update(mHandle, bitmap);
		mSize.x = size.x;
		mSize.y = size.y;
		return this;
	}
	
	/**
	 * Return Texture Size
	 * @return
	 */
	final public Vector2 getSize() {
		return mSize.clone();
	}
	
	/**
	 * Return Resource Size without proportion.
	 * @return
	 */
	final public Vector2 getResourceSize() {
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
	final public com.org.multigear.mginterface.graphics.opengl.texture.Texture setBitmap(final Bitmap bitmap, final boolean recycle) {
		final Vector2 size = mUpdater.update(mHandle, bitmap);
		if(recycle)
			bitmap.recycle();
		mSize.x = size.x;
		mSize.y = size.y;
		return this;
	}
	
	/**
	 * Stretch Texture.
	 * @param size Size
	 */
	final public void stretch(final Vector2 size) {
		mSize.x = size.x;
		mSize.y = size.y;
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
	
	/**
	 * Clone Texture
	 */
	final public Texture clone() {
		return new Texture(mHandle, -1, mSize.clone(), mResourceSize.clone(), mUpdater);
	}
}