package com.org.multigear.mginterface.graphics.opengl.texture;

import java.util.Locale;

import com.org.multigear.general.utils.Vector2;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * 
 * Utilisado para recarregamento das texturas.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class Updater {
	
	// Private Variables
	final private com.org.multigear.mginterface.engine.Multigear mEngine;
	
	/*
	 * Construtor
	 */
	protected Updater(final com.org.multigear.mginterface.engine.Multigear engine) {
		mEngine = engine;
	}
	
	/*
	 * Atualiza uma textura
	 */
	final protected Vector2 update(final int handle, final Bitmap bitmap) {
		// Parametrize TExture
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, handle);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		// Load texture
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		// If Hardware not suporte NPOT Textures, scale texture for POT and
		// re-load
		if (GLES20.glGetError() != GLES20.GL_NO_ERROR) {
			// Get POT dimensions
			int targetWidth = com.org.multigear.general.utils.GeneralUtils.calculateUpperPowerOfTwo(bitmap.getWidth());
			int targetHeight = com.org.multigear.general.utils.GeneralUtils.calculateUpperPowerOfTwo(bitmap.getHeight());
			// Scale Bitmap, load and recycle unused scaled bitmap
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, scaledBitmap, 0);
			scaledBitmap.recycle();
			/*
			// Set Rect for new scaled texture
			int[] mTextureRect = new int[4];
			mTextureRect[0] = 0;
			mTextureRect[1] = targetHeight;
			mTextureRect[2] = targetWidth;
			mTextureRect[3] = -targetHeight;
			// Rect
			((GL11) GLES20).glTexParameteriv(GLES20.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, mTextureRect, 0);
			*/
		}
		
		// If any error, log
		int error = GLES20.glGetError();
		if (error != GLES20.GL_NO_ERROR)
			com.org.multigear.general.utils.KernelUtils.error(mEngine.getActivity(), String.format(Locale.US, Loader.ERROR_GLTEXTURE_LOAD_ERROR, error), Loader.ERROR_GLTEXTURE_LOAD_ERROR_CODE);
	
		// Return new Texture Size
		return new Vector2(bitmap.getWidth(), bitmap.getHeight());
	}
}