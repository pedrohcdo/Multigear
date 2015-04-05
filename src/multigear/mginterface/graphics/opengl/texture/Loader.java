package multigear.mginterface.graphics.opengl.texture;

import java.io.InputStream;
import java.util.Locale;

import multigear.general.utils.GeneralUtils;
import multigear.general.utils.Vector2;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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
	final private AssetManager mAssetManager;
	
	/*
	 * Construtor
	 */
	public Loader(final multigear.mginterface.engine.Multigear engine) {
		mEngine = engine;
		mResources = mEngine.getActivity().getResources();
		mAssetManager = mEngine.getActivity().getAssets();
		mCache = new Cache();
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
			texture.stretch(GeneralUtils.calculateEquivalentSizeOption(texture.getResourceSize(), mEngine));
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
	 * Loads an existing resource in the package. Textures read from the asset have original size.
	 * 
	 * @param resourceId
	 *            Id of the resource, it must be stated in R.
	 */
	final public Texture loadTilesetFromAsset(final String filename, final Vector2 tileSize, final int grid, final int margin) {
		// Open file
		InputStream is = null;
		try {
			is = mAssetManager.open(filename);
		} catch (Exception e) {
			throw new RuntimeException("Could not load the image.");
		}
		// Get Bitmap
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inScaled = false;
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, option);
		// If correct load Bitmap
		if (bitmap == null) {
			final String message = String.format(Locale.US, ERROR_RESOURCE_NOT_FOUND, 0);
			multigear.general.utils.KernelUtils.error(mEngine.getActivity(), message, ERROR_RESOURCE_NOT_FOUND_CODE);
		}
		// Draw tileset
		final int tileWidth = (int)tileSize.x;
		final int tileHeight = (int)tileSize.y;
		final int startX = Math.min(margin, option.outWidth / 2);
		final int startY = Math.min(margin, option.outWidth / 2);
		final int maxW = Math.max(option.outWidth - margin, option.outWidth / 2);
		final int maxH = Math.max(option.outHeight - margin, option.outHeight / 2);
		
		
		final int tilesQW = (int)Math.floor((maxW - startX + grid) / ((tileWidth + grid) * 1.0f));
		final int tilesQH = (int)Math.floor((maxH - startY + grid) / ((tileHeight + grid) * 1.0f));
		
		final int newWidth = tilesQW * (tileWidth + 2) - 2;
		final int newHeight = tilesQH * (tileHeight + 2) - 2;
		
		Bitmap tileset = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
		tileset.eraseColor(0x00000000);
		
		Canvas canvas = new Canvas(tileset);
		Paint clear = new Paint();
		Paint paint = new Paint();
		clear.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		
		Rect src = new Rect();
		Rect dst = new Rect();
		
		int dx = 0;
		int dy = 0;
		int sx = startX;
		int sy = startY;
		
		
		for(int y=0; y<tilesQH; y++) {
			for(int x=0; x<tilesQW; x++) {
				
				src.set(Math.min(sx, maxW), Math.min(sy, maxH), Math.min(sx + tileWidth, maxW), Math.min(sy + tileHeight, maxH));
				
				dst.set(dx-1, dy-1, dx + tileWidth+1, dy + tileHeight+1);
				canvas.drawBitmap(bitmap, src, dst, paint);

				
				dst.set(dx, dy, dx + tileWidth, dy + tileHeight);
				canvas.drawRect(dst, clear);
				
				dst.set(dx, dy, dx + tileWidth, dy + tileHeight);
				canvas.drawBitmap(bitmap, src, dst, paint);
				
				dx += tileWidth + 2;
				sx += tileWidth + grid;
			}
			dx = 0;
			sx = startX;
			dy += tileHeight + 2;
			sy += tileHeight + grid;
		}
		
		Vector2 size = new Vector2(newWidth, newHeight);
		// Load Texture
		Texture texture = this.load(tileset, 0, size);
		// Recycle unused bitmaps
		bitmap.recycle();
		tileset.recycle();
		// Correct size, textures read from the asset have original size
		texture.stretch(size);
		// Add texture to pack
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
	 * Carrega um Bitmap diretamente no OGL
	 */
	final public multigear.mginterface.graphics.opengl.texture.Texture load(final Bitmap bitmap, final int id, final Vector2 size) {
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
		final Texture texture = new Texture(mTextureHandle, id, GeneralUtils.calculateEquivalentSizeOption(size, mEngine), size, updater);
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
		
		// References
		final Vector2 size = new Vector2(bitmap.getWidth(), bitmap.getHeight());
		final Updater updater = new Updater(mEngine);
		final Texture texture = new Texture(mTextureHandle, -1, size, size, updater);
		
		// Return Texture
		return texture;
	}
}