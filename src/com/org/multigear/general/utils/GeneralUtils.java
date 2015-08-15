package com.org.multigear.general.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.org.multigear.mginterface.engine.Multigear;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * 
 * Utilidades gerais.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class GeneralUtils {
	
	// Constants
	final static public float INCH = 2.54f;
	final static private double D2R = Math.PI / 180.0f;
	final static private double R2D = 180.0f / Math.PI;
	
	/* Privando construtor */
	private GeneralUtils() {
	}
	
	/**
	 * Convert degrees to radians
	 * 
	 * @param degree
	 * @return
	 */
	final static public double degreeToRad(final double degree) {
		return degree * D2R;
	}
	
	/**
	 * Convert radians to degrees
	 * 
	 * @param degree
	 * @return
	 */
	final static public double radToDegree(final double degree) {
		return degree * R2D;
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Baseado em um
	 * tamanho já pre-calculado em uma certa base o tamanho sera corrido em
	 * outras bases. Sendo importante manter uma escala de textura por
	 * densidade. Ex:
	 * 
	 * Texturas em disco com tamanhos definos abaixo Base Text: 100 x 100 3dpi
	 * xxhdpi Self Text: 66 x 66 2dpi xhdpi
	 * 
	 * Como é mostrado no exemplo, a proporção se mantem, sendo possivel o
	 * funcionamento correto da função abaixo.
	 */
	final static public Vector2 calculateGeneralTextureSizeBigger(final Vector2 textureSize, final Vector2 baseScreenSize, final float baseDensity, final Vector2 selfScreenSize, final float selfDensity) {
		// Get Scale Density
		final float scaleDensity = baseDensity / selfDensity;
		// Get Base Size
		final float baseWidth = textureSize.x * scaleDensity;
		final float baseHeight = textureSize.y * scaleDensity;
		// Scale Factor
		float xScaleFactor = 0f;
		float yScaleFactor = 0f;
		// Get Scale Factor for Minor Screen  value
		if (baseScreenSize.x > baseScreenSize.y) {
			xScaleFactor = baseWidth / baseScreenSize.x;
			yScaleFactor = baseHeight / baseScreenSize.x;
		} else {
			xScaleFactor = baseWidth / baseScreenSize.y;
			yScaleFactor = baseHeight / baseScreenSize.y;
		}
		// Correct Texture Size
		float textureWidth = 0;
		float textureHeight = 0;
		// Get Corrected Texture Size for Minor Screen  Value
		if (selfScreenSize.x > selfScreenSize.y) {
			textureWidth = xScaleFactor * selfScreenSize.x;
			textureHeight = yScaleFactor * selfScreenSize.x;
		} else {
			textureWidth = xScaleFactor * selfScreenSize.y;
			textureHeight = yScaleFactor * selfScreenSize.y;
		}
		// Return correct Texture size
		return new Vector2(textureWidth, textureHeight);
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Baseado em um
	 * tamanho já pre-calculado em uma certa base o tamanho sera corrido em
	 * outras bases. Sendo importante manter uma escala de textura por
	 * densidade. Ex:
	 * 
	 * Texturas em disco com tamanhos definos abaixo Base Text: 100 x 100 3dpi
	 * xxhdpi Self Text: 66 x 66 2dpi xhdpi
	 * 
	 * Como é mostrado no exemplo, a proporção se mantem, sendo possivel o
	 * funcionamento correto da função abaixo.
	 */
	final static public Vector2 calculateGeneralTextureSizeSmaller(final Vector2 textureSize, final Vector2 baseScreenSize, final float baseDensity, final Vector2 selfScreenSize, final float selfDensity) {
		// Get Scale Density
		final float scaleDensity = baseDensity / selfDensity;
		// Get Base Size
		final float baseWidth = textureSize.x * scaleDensity;
		final float baseHeight = textureSize.y * scaleDensity;
		// Scale Factor
		float xScaleFactor = 0f;
		float yScaleFactor = 0f;
		// Get Scale Factor for Minor Screen  value
		if (baseScreenSize.x < baseScreenSize.y) {
			xScaleFactor = baseWidth / baseScreenSize.x;
			yScaleFactor = baseHeight / baseScreenSize.x;
		} else {
			xScaleFactor = baseWidth / baseScreenSize.y;
			yScaleFactor = baseHeight / baseScreenSize.y;
		}
		// Correct Texture Size
		float textureWidth = 0;
		float textureHeight = 0;
		// Get Corrected Texture Size for Minor Screen  Value
		if (selfScreenSize.x < selfScreenSize.y) {
			textureWidth = xScaleFactor * selfScreenSize.x;
			textureHeight = yScaleFactor * selfScreenSize.x;
		} else {
			textureWidth = xScaleFactor * selfScreenSize.y;
			textureHeight = yScaleFactor * selfScreenSize.y;
		}
		// Return correct Texture size
		return new Vector2(textureWidth, textureHeight);
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Baseado em um
	 * tamanho já pre-calculado em uma certa base o tamanho sera corrido em
	 * outras bases. Sendo importante manter uma escala de textura por
	 * densidade. Ex:
	 * 
	 * Texturas em disco com tamanhos definos abaixo Base Text: 100 x 100 3dpi
	 * xxhdpi Self Text: 66 x 66 2dpi xhdpi
	 * 
	 * Como é mostrado no exemplo, a proporção se mantem, sendo possivel o
	 * funcionamento correto da função abaixo.
	 */
	final static public Vector2 calculateGeneralTextureSizeDiagonal(final Vector2 textureSize, final Vector2 baseScreenSize, final float baseDensity, final Vector2 selfScreenSize, final float selfDensity) {
		// Get Scale Density
		final float scaleDensity = baseDensity / selfDensity;
		// Get Base Size
		final float baseWidth = textureSize.x * scaleDensity;
		final float baseHeight = textureSize.y * scaleDensity;
		// Scale Factor
		float xScaleFactor = selfScreenSize.x / baseScreenSize.x;
		float yScaleFactor = selfScreenSize.y / baseScreenSize.y;
		// Get Diagonal
		float xyScaleFactor = (float)Math.hypot(xScaleFactor, yScaleFactor);
		// Return correct Texture size
		return new Vector2(xyScaleFactor * baseWidth, xyScaleFactor * baseHeight);
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Baseado em um
	 * tamanho já pre-calculado em uma certa base o tamanho sera corrido em
	 * outras bases. Sendo importante manter uma escala de textura por
	 * densidade. Ex:
	 * 
	 * Texturas em disco com tamanhos definos abaixo Base Text: 100 x 100 3dpi
	 * xxhdpi Self Text: 66 x 66 2dpi xhdpi
	 * 
	 * Como é mostrado no exemplo, a proporção se mantem, sendo possivel o
	 * funcionamento correto da função abaixo.
	 */
	final static public Vector2 calculateGeneralTextureSizeUnspect(final Vector2 textureSize, final Vector2 baseScreenSize, final float baseDensity, final Vector2 selfScreenSize, final float selfDensity) {
		// Get Scale Density
		final float scaleDensity = baseDensity / selfDensity;
		// Get Base Size
		final float baseWidth = textureSize.x * scaleDensity;
		final float baseHeight = textureSize.y * scaleDensity;
		// Scale Factor
		float xScaleFactor = selfScreenSize.x / baseScreenSize.x;
		float yScaleFactor = selfScreenSize.y / baseScreenSize.y;
		// Return correct Texture size
		return new Vector2(xScaleFactor * baseWidth, yScaleFactor * baseHeight);
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Diferente do
	 * metodo "calculateGeneralTextureSize", este calcula o tamanho se baseando
	 * apenas na proporção. Então é importante informar o tamanho realmente
	 * exibido em uma tela base. Obs( Para evitar problemas com tamanhos
	 * incorretos, coloque as texturas desejadas somente na pasta base e
	 * desabilite o redimensionamento para outras densidades)
	 */
	final static public Vector2 calculateIndividualTextureSizeBigger(final Vector2 textureSize, final Vector2 baseScreenSize, final Vector2 selfScreenSize) {
		// Scale Factor
		float xScaleFactor = 0f;
		float yScaleFactor = 0f;
		// Get Scale Factor for Minor Screen  value
		if (baseScreenSize.x > baseScreenSize.y) {
			xScaleFactor = textureSize.x / baseScreenSize.x;
			yScaleFactor = textureSize.y / baseScreenSize.x;
		} else {
			xScaleFactor = textureSize.x / baseScreenSize.y;
			yScaleFactor = textureSize.y / baseScreenSize.y;
		}
		// Correct Texture Size
		float correctTextureWidth = 0;
		float correctTextureHeight = 0;
		// Get Corrected Texture Size for Minor Screen  Value
		if (selfScreenSize.x > selfScreenSize.y) {
			correctTextureWidth = xScaleFactor * selfScreenSize.x;
			correctTextureHeight = yScaleFactor * selfScreenSize.x;
		} else {
			correctTextureWidth = xScaleFactor * selfScreenSize.y;
			correctTextureHeight = yScaleFactor * selfScreenSize.y;
		}
		// Return correct Texture size
		return new Vector2(correctTextureWidth, correctTextureHeight);
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Diferente do
	 * metodo "calculateGeneralTextureSize", este calcula o tamanho se baseando
	 * apenas na proporção. Então é importante informar o tamanho realmente
	 * exibido em uma tela base. Obs( Para evitar problemas com tamanhos
	 * incorretos, coloque as texturas desejadas somente na pasta base e
	 * desabilite o redimensionamento para outras densidades)
	 */
	final static public Vector2 calculateIndividualTextureSizeDiagonal(final Vector2 textureSize, final Vector2 baseScreenSize, final Vector2 selfScreenSize) {
		// Scale Factor
		float xScaleFactor = selfScreenSize.x / baseScreenSize.x;
		float yScaleFactor = selfScreenSize.y / baseScreenSize.y;
		// Get Diagonal
		float xyScaleFactor = (float)Math.hypot(xScaleFactor, yScaleFactor);
		// Return correct Texture size
		return Vector2.scale(textureSize, xyScaleFactor);
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Diferente do
	 * metodo "calculateGeneralTextureSize", este calcula o tamanho se baseando
	 * apenas na proporção. Então é importante informar o tamanho realmente
	 * exibido em uma tela base. Obs( Para evitar problemas com tamanhos
	 * incorretos, coloque as texturas desejadas somente na pasta base e
	 * desabilite o redimensionamento para outras densidades)
	 */
	final static public Vector2 calculateIndividualTextureSizeUnspect(final Vector2 textureSize, final Vector2 baseScreenSize, final Vector2 selfScreenSize) {
		// Scale Factor
		float xScaleFactor = selfScreenSize.x / baseScreenSize.x;
		float yScaleFactor = selfScreenSize.y / baseScreenSize.y;
		// Return correct Texture size
		return Vector2.scale(textureSize, xScaleFactor, yScaleFactor);
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Diferente do
	 * metodo "calculateGeneralTextureSize", este calcula o tamanho se baseando
	 * apenas na proporção. Então é importante informar o tamanho realmente
	 * exibido em uma tela base. Obs( Para evitar problemas com tamanhos
	 * incorretos, coloque as texturas desejadas somente na pasta base e
	 * desabilite o redimensionamento para outras densidades)
	 */
	final static public Vector2 calculateIndividualTextureSizeSmaller(final Vector2 textureSize, final Vector2 baseScreenSize, final Vector2 selfScreenSize) {
		// Scale Factor
		float xScaleFactor = 0f;
		float yScaleFactor = 0f;
		// Get Scale Factor for Minor Screen  value
		if (baseScreenSize.x < baseScreenSize.y) {
			xScaleFactor = textureSize.x / baseScreenSize.x;
			yScaleFactor = textureSize.y / baseScreenSize.x;
		} else {
			xScaleFactor = textureSize.x / baseScreenSize.y;
			yScaleFactor = textureSize.y / baseScreenSize.y;
		}
		// Correct Texture Size
		float correctTextureWidth = 0;
		float correctTextureHeight = 0;
		// Get Corrected Texture Size for Minor Screen  Value
		if (selfScreenSize.x < selfScreenSize.y) {
			correctTextureWidth = xScaleFactor * selfScreenSize.x;
			correctTextureHeight = yScaleFactor * selfScreenSize.x;
		} else {
			correctTextureWidth = xScaleFactor * selfScreenSize.y;
			correctTextureHeight = yScaleFactor * selfScreenSize.y;
		}
		// Return correct Texture size
		return new Vector2(correctTextureWidth, correctTextureHeight);
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Diferente do
	 * metodo "calculateGeneralTextureSize", este calcula o tamanho se baseando
	 * apenas na proporção. Então é importante informar o tamanho realmente
	 * exibido em uma tela base. Obs( Para evitar problemas com tamanhos
	 * incorretos, coloque as texturas desejadas somente na pasta base e
	 * desabilite o redimensionamento para outras densidades)
	 */
	final static public float calculateIndividualValueBigger(final float value, final Vector2 baseScreenSize, final Vector2 selfScreenSize) {
		// If same Display
		if((baseScreenSize.y - selfScreenSize.y) <= 0.01)
			return value;
		
		// Scale Factor
		float scaleFactor = 0f;
		// Get Scale Factor for Minor Screen  value
		if (baseScreenSize.x > baseScreenSize.y)
			scaleFactor = value / baseScreenSize.x;
		else
			scaleFactor = value / baseScreenSize.y;
		// Correct Texture Size
		float correctValue = 0;
		// Get Corrected Texture Size for Minor Screen  Value
		if (selfScreenSize.x > selfScreenSize.y)
			correctValue = scaleFactor * selfScreenSize.x;
		else
			correctValue = scaleFactor * selfScreenSize.y;
		// Return correct Texture size
		return correctValue;
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Diferente do
	 * metodo "calculateGeneralTextureSize", este calcula o tamanho se baseando
	 * apenas na proporção. Então é importante informar o tamanho realmente
	 * exibido em uma tela base. Obs( Para evitar problemas com tamanhos
	 * incorretos, coloque as texturas desejadas somente na pasta base e
	 * desabilite o redimensionamento para outras densidades)
	 */
	final static public float calculateIndividualValueSmaller(final float value, final Vector2 baseScreenSize, final Vector2 selfScreenSize) {
		// If same Display
		if((baseScreenSize.x - selfScreenSize.x) <= 0.01)
			return value;
		
		// Scale Factor
		float scaleFactor = 0f;
		// Get Scale Factor for Minor Screen  value
		if (baseScreenSize.x < baseScreenSize.y)
			scaleFactor = value / baseScreenSize.x;
		else
			scaleFactor = value / baseScreenSize.y;
		// Correct Texture Size
		float correctValue = 0;
		// Get Corrected Texture Size for Minor Screen  Value
		if (selfScreenSize.x < selfScreenSize.y)
			correctValue = scaleFactor * selfScreenSize.x;
		else
			correctValue = scaleFactor * selfScreenSize.y;
		// Return correct Texture size
		return correctValue;
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Diferente do
	 * metodo "calculateGeneralTextureSize", este calcula o tamanho se baseando
	 * apenas na proporção. Então é importante informar o tamanho realmente
	 * exibido em uma tela base. Obs( Para evitar problemas com tamanhos
	 * incorretos, coloque as texturas desejadas somente na pasta base e
	 * desabilite o redimensionamento para outras densidades)
	 */
	final static public Vector2 calculateIndividualRef2DBigger(final Vector2 ref2D, final Vector2 baseScreenSize, final Vector2 selfScreenSize) {
		// Scale Factor
		float xScaleFactor = 0f;
		float yScaleFactor = 0f;
		// Get Scale Factor for Minor Screen  value
		if (baseScreenSize.x > baseScreenSize.y) {
			xScaleFactor = ref2D.x / baseScreenSize.x;
			yScaleFactor = ref2D.y / baseScreenSize.x;
		} else {
			xScaleFactor = ref2D.x / baseScreenSize.y;
			yScaleFactor = ref2D.y / baseScreenSize.y;
		}
		// Correct Texture Size
		float correctTextureWidth = 0;
		float correctTextureHeight = 0;
		// Get Corrected Texture Size for Minor Screen  Value
		if (selfScreenSize.x > selfScreenSize.y) {
			correctTextureWidth = xScaleFactor * selfScreenSize.x;
			correctTextureHeight = yScaleFactor * selfScreenSize.x;
		} else {
			correctTextureWidth = xScaleFactor * selfScreenSize.y;
			correctTextureHeight = yScaleFactor * selfScreenSize.y;
		}
		// Return correct Texture size
		return new Vector2(correctTextureWidth, correctTextureHeight);
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Diferente do
	 * metodo "calculateGeneralTextureSize", este calcula o tamanho se baseando
	 * apenas na proporção. Então é importante informar o tamanho realmente
	 * exibido em uma tela base. Obs( Para evitar problemas com tamanhos
	 * incorretos, coloque as texturas desejadas somente na pasta base e
	 * desabilite o redimensionamento para outras densidades)
	 */
	final static public Vector2 calculateIndividualRef2DSmaller(final Vector2 ref2D, final Vector2 baseScreenSize, final Vector2 selfScreenSize) {
		// Scale Factor
		float xScaleFactor = 0f;
		float yScaleFactor = 0f;
		// Get Scale Factor for Minor Screen  value
		if (baseScreenSize.x < baseScreenSize.y) {
			xScaleFactor = ref2D.x / baseScreenSize.x;
			yScaleFactor = ref2D.y / baseScreenSize.x;
		} else {
			xScaleFactor = ref2D.x / baseScreenSize.y;
			yScaleFactor = ref2D.y / baseScreenSize.y;
		}
		// Correct Texture Size
		float correctTextureWidth = 0;
		float correctTextureHeight = 0;
		// Get Corrected Texture Size for Minor Screen  Value
		if (selfScreenSize.x < selfScreenSize.y) {
			correctTextureWidth = xScaleFactor * selfScreenSize.x;
			correctTextureHeight = yScaleFactor * selfScreenSize.x;
		} else {
			correctTextureWidth = xScaleFactor * selfScreenSize.y;
			correctTextureHeight = yScaleFactor * selfScreenSize.y;
		}
		// Return correct Texture size
		return new Vector2(correctTextureWidth, correctTextureHeight);
	}
	
	/*
	 * Retorna um valor na base 2^x-POT acima do valor informado caso o mesmo
	 * não seja.
	 */
	final static public int calculateUpperPowerOfTwo(int v) {
		v--;
		v |= v >>> 1;
		v |= v >>> 2;
		v |= v >>> 4;
		v |= v >>> 8;
		v |= v >>> 16;
		v++;
		return v;
	}
	
	/*
	 * Retorna o tamanho real da tela
	 */
	@SuppressLint("NewApi")
	final static public Vector2 getRealScreenSize(Display display, Vector2 defaultScreenSize) {
		Vector2 finalScreenSize = new Vector2(0, 0);
		if (Build.VERSION.SDK_INT >= 19) {
			Point outPoint = new Point();
			DisplayMetrics metrics = new DisplayMetrics();
			display.getRealMetrics(metrics);
			outPoint.x = metrics.widthPixels;
			outPoint.y = metrics.heightPixels;
			finalScreenSize = new Vector2(outPoint.x, outPoint.y);
		}
		if (finalScreenSize.x < defaultScreenSize.x && finalScreenSize.y < defaultScreenSize.y) {
			return defaultScreenSize;
		}
		return finalScreenSize;
	}
	
	/**
	 * Create FloatBuffer, and set position 0
	 * <p>
	 * @param bufferLenght
	 * @return
	 */
	final static public FloatBuffer createFloatBuffer(final int bufferLenght) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufferLenght * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		return byteBuffer.asFloatBuffer();
	}
	
	/**
	 * Create IntBuffer, and set position 0
	 * <p>
	 * @param bufferLenght
	 * @return
	 */
	final static public IntBuffer createIntBuffer(final int bufferLength) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufferLength* 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		return byteBuffer.asIntBuffer();
	}
	
	/**
	 * Create FloatBuffer from float values, and set position 0
	 * <p>
	 * @param bufferLenght
	 * @return
	 */
	final static public FloatBuffer createFloatBuffer(final float[] values) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(values.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = byteBuffer.asFloatBuffer();
		buffer.put(values);
		buffer.position(0);
		return buffer;
	}
	
	/**
	 * Create IntBuffer from int values, and set position 0
	 * <p>
	 * @param bufferLenght
	 * @return
	 */
	final static public IntBuffer createIntBuffer(final int[] values) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(values.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		IntBuffer buffer = byteBuffer.asIntBuffer();
		buffer.put(values);
		buffer.position(0);
		return buffer;
	}
	
	/**
	 * Put float values to FloatBuffer.
	 * @param floatBuffer FloatBuffer
	 * @param buffer Float Values
	 */
	final static public void putFloatBuffer(final FloatBuffer floatBuffer, final float[] buffer) {
		floatBuffer.put(buffer);
		floatBuffer.position(0);
	}
	
	/**
	 * Put float values to FloatBuffer.
	 * @param floatBuffer FloatBuffer
	 * @param buffer Float Values
	 */
	final static public void putFloatBuffer(final FloatBuffer floatBuffer, final float[][] buffers) {
		for(float[] buffer : buffers) 
			floatBuffer.put(buffer);
		floatBuffer.position(0);
	}
	
	/**
	 * Map Rect reference to float reference.
	 * The 0 represents side reference and 1 represents the max reference of the opposite side.
	 * The float[] model is:<br>
	 * (0,0) -> (1, 0) -> (1, 1) -> (0, 1)
	 * @return
	 */
	final static public float[] mapRectToFloat(final Rect ref, final Rect measure) {
		final float width = measure.width();
		final float height = measure.height();
		final float left = ref.left / width;
		final float top = ref.top / height;
		final float right = ref.right / width;
		final float bottom = ref.bottom / height;
		return new float[] {left, top, right, top, right, bottom, left, bottom};
	}
	
	/**
	 * Map Rect reference to float reference.
	 * The 0 represents side reference and 1 represents the max reference of the opposite side.
	 * The float[] model is:<br>
	 * (0,0) -> (1, 0) -> (1, 1) -> (0, 1)
	 * @return
	 */
	final static public float[] mapRectToFloat(final Rect ref, final Vector2 measure) {
		final float width = (float)measure.x;
		final float height = (float)measure.y;
		final float left = ref.left / width;
		final float top = ref.top / height;
		final float right = ref.right / width;
		final float bottom = ref.bottom / height;
		return new float[] {left, top, right, top, right, bottom, left, bottom};
	}
	
	/**
	 * Map Rect reference to float reference.
	 * The 0 represents side reference and 1 represents the max reference of the opposite side.
	 * The float[] model is:<br>
	 * (0,0) -> (1, 0) -> (1, 1) -> (0, 1)
	 * @return
	 */
	final static public float[] mapRectToFloat(final RectF ref, final Vector2 measure) {
		final float width = (float)measure.x;
		final float height = (float)measure.y;
		final float left = ref.left / width;
		final float top = ref.top / height;
		final float right = ref.right / width;
		final float bottom = ref.bottom / height;
		return new float[] {left, top, right, top, right, bottom, left, bottom};
	}
	
	/**
	 * Map Rect reference to float reference.
	 * The 0 represents side reference and 1 represents the max reference of the opposite side.
	 * The float[] model is:<br>
	 * (0,0) -> (1, 0) -> (1, 1) -> (0, 1)
	 * @return
	 */
	final static public void mapRectToFloat(final RectF ref, final Vector2 measure, float[] out, int offset) {
		final float width = (float)measure.x;
		final float height = (float)measure.y;
		final float left = ref.left / width;
		final float top = ref.top / height;
		final float right = ref.right / width;
		final float bottom = ref.bottom / height;
		out[offset] = left;
		out[offset+1] = top;
		out[offset+2] = right;
		out[offset+3] = top;
		out[offset+4] = right;
		out[offset+5] = bottom;
		out[offset+6] = left;
		out[offset+7] = bottom;
	}
	
	/**
	 * Map Rect reference to float reference.
	 * The min represents side reference and max represents the max reference of the opposite side.
	 * The float[] model is:<br>
	 * (0,0) -> (1, 0) -> (1, 1) -> (0, 1)
	 * @return
	 */
	final static public void mapRectToFloat(final RectF ref,  float[] out, int offset) {
		final float left = ref.left;
		final float top = ref.top;
		final float right = ref.right;
		final float bottom = ref.bottom;
		out[offset] = left;
		out[offset+1] = top;
		out[offset+2] = right;
		out[offset+3] = top;
		out[offset+4] = right;
		out[offset+5] = bottom;
		out[offset+6] = left;
		out[offset+7] = bottom;
	}
	
	/**
	 * Get Float element Bounds.
	 * @param element Float Element with 8
	 * @param out Out of bounds
	 */
	final static public void getFloatBounds(final float[] element, final float[] out) {
		out[0] = element[0];
		out[1] = element[1];
		out[2] = element[2];
		out[3] = element[5];
	}
	
	/**
	 * Divide RectF per Vector2
	 * @param rect
	 * @param size
	 * @return
	 */
	final static public RectF divideRect(final RectF rect, final Vector2 size) {
		return new RectF(rect.left / size.x, rect.top / size.y, rect.right / size.x, rect.bottom / size.y);
	}
	
	/**
	 * Returns the equivalent size in this device, 
	 * the values are calculated around the set options in the engine.
	 * @param size Size
	 * @param engine Engine
	 * @return Calculed Size
	 */
	final static public Vector2 calculateEquivalentSizeOption(final Vector2 size, final Multigear engine) {
		
		//
		Vector2 screenSize = engine.getMainRoom().getScreenSize();
		Resources res = engine.getActivity().getResources();
		
		// If Texture proportion Enabled
		if (engine.getConfiguration().hasFunc(com.org.multigear.mginterface.engine.Configuration.FUNC_TEXTURE_PROPORTION)) {
			// Get Attributes
			final float selfDensity = res.getDisplayMetrics().density;
			final Vector2 selfScreenSize = screenSize;
			// Get Base Attributes
			float baseDensity = engine.getConfiguration().getFloatAttr(com.org.multigear.mginterface.engine.Configuration.ATTR_BASE_DENSITY);
			Vector2 baseScreenSize = engine.getConfiguration().getRef2DAttr(com.org.multigear.mginterface.engine.Configuration.ATTR_BASE_SCREEN);
			// If not set, set as default display
			if (baseScreenSize == com.org.multigear.mginterface.engine.Configuration.DEFAULT_REF2D)
				baseScreenSize = selfScreenSize;
			// If not set, set as default density
			if (baseDensity == com.org.multigear.mginterface.engine.Configuration.DEFAULT_VALUE)
				baseDensity = selfDensity;
			// Get mode
			final int from = (int) engine.getConfiguration().getFloatAttr(com.org.multigear.mginterface.engine.Configuration.ATTR_PROPORTION_FROM);
			final int mode = (int) engine.getConfiguration().getFloatAttr(com.org.multigear.mginterface.engine.Configuration.ATTR_PROPORTION_MODE);
			// Check From
			switch (from) {
				case com.org.multigear.mginterface.engine.Configuration.PROPORTION_FROM_INDIVIDUAL:
					// Check Func
					switch (mode) {
					// If Mode Smaller
						case com.org.multigear.mginterface.engine.Configuration.PROPORTION_MODE_SMALLER:
							return com.org.multigear.general.utils.GeneralUtils.calculateIndividualTextureSizeSmaller(size, baseScreenSize, selfScreenSize);
							// If Mode Bigger
						case com.org.multigear.mginterface.engine.Configuration.PROPORTION_MODE_BIGGER:
							return com.org.multigear.general.utils.GeneralUtils.calculateIndividualTextureSizeBigger(size, baseScreenSize, selfScreenSize);
							// If Mode Diagonal
						case com.org.multigear.mginterface.engine.Configuration.PROPORTION_MODE_DIAGONAL:
							return com.org.multigear.general.utils.GeneralUtils.calculateIndividualTextureSizeDiagonal(size, baseScreenSize, selfScreenSize);
							// If Default
						default:
						case com.org.multigear.mginterface.engine.Configuration.DEFAULT_VALUE:
						case com.org.multigear.mginterface.engine.Configuration.PROPORTION_MODE_UNSPECT:
							return com.org.multigear.general.utils.GeneralUtils.calculateIndividualTextureSizeUnspect(size, baseScreenSize, selfScreenSize);
					}
					// If default or General
				default:
				case com.org.multigear.mginterface.engine.Configuration.DEFAULT_VALUE:
				case com.org.multigear.mginterface.engine.Configuration.PROPORTION_FROM_GENERAL:
					// Check Mode
					switch (mode) {
					// If Mode Smaller
						case com.org.multigear.mginterface.engine.Configuration.PROPORTION_MODE_SMALLER:
							return com.org.multigear.general.utils.GeneralUtils.calculateGeneralTextureSizeSmaller(size, baseScreenSize, baseDensity, selfScreenSize, selfDensity);
							// If Mode Bigger
						case com.org.multigear.mginterface.engine.Configuration.PROPORTION_MODE_BIGGER:
							return com.org.multigear.general.utils.GeneralUtils.calculateGeneralTextureSizeBigger(size, baseScreenSize, baseDensity, selfScreenSize, selfDensity);
							// If Mode Diagonal
						case com.org.multigear.mginterface.engine.Configuration.PROPORTION_MODE_DIAGONAL:
							return com.org.multigear.general.utils.GeneralUtils.calculateGeneralTextureSizeDiagonal(size, baseScreenSize, baseDensity, selfScreenSize, selfDensity);
							// If Default
						default:
						case com.org.multigear.mginterface.engine.Configuration.DEFAULT_VALUE:
						case com.org.multigear.mginterface.engine.Configuration.PROPORTION_MODE_UNSPECT:
							return com.org.multigear.general.utils.GeneralUtils.calculateGeneralTextureSizeUnspect(size, baseScreenSize, baseDensity, selfScreenSize, selfDensity);
					}
			}
		}
		return size;
	}
	
	final public static Color mix(final Color colorA, final Color colorB, float control) {
		final float iControl = (1.0f - control);
		return Color.rgba(colorB.getRed() * control + colorA.getRed() * iControl,
						  colorB.getGreen() * control + colorA.getGreen() * iControl,
						  colorB.getBlue() * control + colorA.getBlue() * iControl,
						  colorB.getAlpha() * control + colorA.getAlpha() * iControl);
	}
}
