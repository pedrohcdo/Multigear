package multigear.mginterface.graphics.opengl.texture;

import java.util.Locale;

import multigear.general.utils.KernelUtils;
import multigear.general.utils.Vector2;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * 
 * Utilisado para cerragamento das texturas.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class Loader {
	
	// Final Constants (Do not change)
	final static private String ERROR_RESOURCE_NOT_FOUND = "An error occurred loading a certain resource %d. This resource does not exist in the package.";
	final static protected String ERROR_GLTEXTURE_LOAD_ERROR = "GL Texture Load Error: %s";
	
	final static private int ERROR_RESOURCE_NOT_FOUND_CODE = 0x1;
	final static protected int ERROR_GLTEXTURE_LOAD_ERROR_CODE = 0x2;
	
	// Private Variables
	final private multigear.mginterface.engine.Multigear mEngine;
	final private multigear.mginterface.graphics.opengl.texture.Cache mCache;
	final private Resources mResources;
	final private Vector2 mScreenSize;
	
	/*
	 * Construtor
	 */
	public Loader(final multigear.mginterface.engine.Multigear engine, final Vector2 screenSize) {
		mEngine = engine;
		mResources = mEngine.getActivity().getResources();
		mCache = new Cache();
		mScreenSize = screenSize;
	}
	
	/**
	 * Loads an existing resource in the package.
	 * 
	 * @param resourceId
	 *            Id of the resource, it must be stated in R.
	 */
	final public multigear.mginterface.graphics.opengl.texture.Texture load(final int resourceId) {
		// If pre loaded texture
		multigear.mginterface.graphics.opengl.texture.Texture texture = mCache.getTexture(resourceId);
		if (texture != null) {
			texture.stretch(correctSize(texture.getResourceSize()));
			return texture;
		}
		// Disable Pre Scale to get Bounds
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inScaled = false;
		option.inJustDecodeBounds = true;
		// Decode Bitmap
		BitmapFactory.decodeResource(mResources, resourceId, option);
		// Get Real Size
		Vector2 size = new Vector2(option.outWidth, option.outHeight);
		// Get Bitmap
		option = new BitmapFactory.Options();
		option.inScaled = false;
		Bitmap bitmap = BitmapFactory.decodeResource(mResources, resourceId, option);
		// If correct load Bitmap
		if (bitmap == null) {
			final String message = String.format(Locale.US, ERROR_RESOURCE_NOT_FOUND, resourceId);
			multigear.general.utils.KernelUtils.error(mEngine.getActivity(), message, ERROR_RESOURCE_NOT_FOUND_CODE);
		}
		texture = this.load(bitmap, resourceId, size);
		bitmap.recycle();
		mCache.addTexture(texture);
		return texture;
	}
	
	/**
	 * Create a empty Texture.
	 * 
	 * @param id
	 *            Id used by texture.
	 */
	final public Texture create(final Bitmap bitmap) {
		Texture texture = this.createTexture(bitmap);
		// mCache.addTexture(texture);
		return texture;
	}
	
	/*
	 * Correct Texture Size
	 */
	final private Vector2 correctSize(final Vector2 textureSize) {
		// If Texture proportion Enabled
		if (mEngine.getConfiguration().hasFunc(multigear.mginterface.engine.Configuration.FUNC_TEXTURE_PROPORTION)) {
			// Get Attributes
			final float selfDensity = mResources.getDisplayMetrics().density;
			final Vector2 selfScreenSize = mScreenSize;
			// Get Base Attributes
			float baseDensity = mEngine.getConfiguration().getFloatAttr(multigear.mginterface.engine.Configuration.ATTR_BASE_DENSITY);
			Vector2 baseScreenSize = mEngine.getConfiguration().getRef2DAttr(multigear.mginterface.engine.Configuration.ATTR_BASE_SCREEN);
			// If not set, set as default display
			if (baseScreenSize == multigear.mginterface.engine.Configuration.DEFAULT_REF2D)
				baseScreenSize = selfScreenSize;
			// If not set, set as default density
			if (baseDensity == multigear.mginterface.engine.Configuration.DEFAULT_VALUE)
				baseDensity = selfDensity;
			// Get mode
			final int from = (int) mEngine.getConfiguration().getFloatAttr(multigear.mginterface.engine.Configuration.ATTR_PROPORTION_FROM);
			final int mode = (int) mEngine.getConfiguration().getFloatAttr(multigear.mginterface.engine.Configuration.ATTR_PROPORTION_MODE);
			// Check From
			switch (from) {
				case multigear.mginterface.engine.Configuration.PROPORTION_FROM_INDIVIDUAL:
					// Check Func
					switch (mode) {
					// If Mode Smaller
						case multigear.mginterface.engine.Configuration.PROPORTION_MODE_SMALLER:
							return multigear.general.utils.GeneralUtils.calculateIndividualTextureSizeSmaller(textureSize, baseScreenSize, selfScreenSize);
							// If Mode Bigger
						case multigear.mginterface.engine.Configuration.PROPORTION_MODE_BIGGER:
							return multigear.general.utils.GeneralUtils.calculateIndividualTextureSizeBigger(textureSize, baseScreenSize, selfScreenSize);
							// If Mode Diagonal
						case multigear.mginterface.engine.Configuration.PROPORTION_MODE_DIAGONAL:
							return multigear.general.utils.GeneralUtils.calculateIndividualTextureSizeDiagonal(textureSize, baseScreenSize, selfScreenSize);
							// If Default
						default:
						case multigear.mginterface.engine.Configuration.DEFAULT_VALUE:
						case multigear.mginterface.engine.Configuration.PROPORTION_MODE_UNSPECT:
							return multigear.general.utils.GeneralUtils.calculateIndividualTextureSizeUnspect(textureSize, baseScreenSize, selfScreenSize);
					}
					// If default or General
				default:
				case multigear.mginterface.engine.Configuration.DEFAULT_VALUE:
				case multigear.mginterface.engine.Configuration.PROPORTION_FROM_GENERAL:
					// Check Mode
					switch (mode) {
					// If Mode Smaller
						case multigear.mginterface.engine.Configuration.PROPORTION_MODE_SMALLER:
							return multigear.general.utils.GeneralUtils.calculateGeneralTextureSizeSmaller(textureSize, baseScreenSize, baseDensity, selfScreenSize, selfDensity);
							// If Mode Bigger
						case multigear.mginterface.engine.Configuration.PROPORTION_MODE_BIGGER:
							return multigear.general.utils.GeneralUtils.calculateGeneralTextureSizeBigger(textureSize, baseScreenSize, baseDensity, selfScreenSize, selfDensity);
							// If Mode Diagonal
						case multigear.mginterface.engine.Configuration.PROPORTION_MODE_DIAGONAL:
							return multigear.general.utils.GeneralUtils.calculateGeneralTextureSizeDiagonal(textureSize, baseScreenSize, baseDensity, selfScreenSize, selfDensity);
							// If Default
						default:
						case multigear.mginterface.engine.Configuration.DEFAULT_VALUE:
						case multigear.mginterface.engine.Configuration.PROPORTION_MODE_UNSPECT:
							return multigear.general.utils.GeneralUtils.calculateGeneralTextureSizeUnspect(textureSize, baseScreenSize, baseDensity, selfScreenSize, selfDensity);
					}
			}
		}
		return textureSize;
	}
	
	/*
	 * Carrega um Bitmap diretamente no OGL
	 */
	final private multigear.mginterface.graphics.opengl.texture.Texture load(final Bitmap bitmap, final int id, final Vector2 size) {
		// Create Handle for texture
		int[] handleVec = new int[1];
		GLES20.glGenTextures(1, handleVec, 0);
		int mTextureHandle = handleVec[0];
		// Parametrize TExture
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		// Load texture
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		// If Hardware not suporte NPOT Textures, scale texture for POT and
		// re-load
		if (GLES20.glGetError() != GLES20.GL_NO_ERROR) {
			// Get POT dimensions
			int targetWidth = multigear.general.utils.GeneralUtils.calculateUpperPowerOfTwo(bitmap.getWidth());
			int targetHeight = multigear.general.utils.GeneralUtils.calculateUpperPowerOfTwo(bitmap.getHeight());
			// Scale Bitmap, load and recycle unused scaled bitmap
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, scaledBitmap, 0);
			scaledBitmap.recycle();
			/*
			 * // Set Rect for new scaled texture int[] mTextureRect = new
			 * int[4]; mTextureRect[0] = 0; mTextureRect[1] = targetHeight;
			 * mTextureRect[2] = targetWidth; mTextureRect[3] = -targetHeight;
			 * // Rect ((GL11) mGLES20).glTexParameteriv(GLES20.GL_TEXTURE_2D,
			 * GL11Ext.GL_TEXTURE_CROP_RECT_OES, mTextureRect, 0);
			 */
		}
		
		// If any error, log
		int error = GLES20.glGetError();
		if (error != GLES20.GL_NO_ERROR)
			multigear.general.utils.KernelUtils.error(mEngine.getActivity(), String.format(Locale.US, ERROR_GLTEXTURE_LOAD_ERROR), ERROR_GLTEXTURE_LOAD_ERROR_CODE);
		// Create Updater
		final Updater updater = new Updater(mEngine);
		// Create Texture
		final Texture texture = new Texture(mTextureHandle, id, correctSize(size), size, updater);
		// Return Texture
		return texture;
	}
	
	/*
	 * Cria uma textura vazia
	 */
	final private multigear.mginterface.graphics.opengl.texture.Texture createTexture(final Bitmap bitmap) {
		// Create Handle for texture
		int[] handleVec = new int[1];
		GLES20.glGenTextures(1, handleVec, 0);
		int mTextureHandle = handleVec[0];
		
		// Parametrize TExture
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);
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
			int targetWidth = multigear.general.utils.GeneralUtils.calculateUpperPowerOfTwo(bitmap.getWidth());
			int targetHeight = multigear.general.utils.GeneralUtils.calculateUpperPowerOfTwo(bitmap.getHeight());
			// Scale Bitmap, load and recycle unused scaled bitmap
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, scaledBitmap, 0);
			scaledBitmap.recycle();
			/*
			 * // Set Rect for new scaled texture int[] mTextureRect = new
			 * int[4]; mTextureRect[0] = 0; mTextureRect[1] = targetHeight;
			 * mTextureRect[2] = targetWidth; mTextureRect[3] = -targetHeight;
			 * // Rect ((GL11) mGLES20).glTexParameteriv(GLES20.GL_TEXTURE_2D,
			 * GL11Ext.GL_TEXTURE_CROP_RECT_OES, mTextureRect, 0);
			 */
		}
		
		// If any error, log
		int error = GLES20.glGetError();
		if (error != GLES20.GL_NO_ERROR)
			multigear.general.utils.KernelUtils.error(mEngine.getActivity(), String.format(Locale.US, ERROR_GLTEXTURE_LOAD_ERROR), ERROR_GLTEXTURE_LOAD_ERROR_CODE);
		
		// References
		final Vector2 size = new Vector2(bitmap.getWidth(), bitmap.getHeight());
		final Updater updater = new Updater(mEngine);
		final Texture texture = new Texture(mTextureHandle, -1, size, size, updater);
		
		// Return Texture
		return texture;
	}
}