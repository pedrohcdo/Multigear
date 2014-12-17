package multigear.general.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.annotation.SuppressLint;
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
	final static public multigear.general.utils.Ref2F calculateGeneralTextureSizeBigger(final multigear.general.utils.Ref2F textureSize, final multigear.general.utils.Ref2F baseScreenSize, final float baseDensity, final multigear.general.utils.Ref2F selfScreenSize, final float selfDensity) {
		// Get Scale Density
		final float scaleDensity = baseDensity / selfDensity;
		// Get Base Size
		final float baseWidth = textureSize.XAxis * scaleDensity;
		final float baseHeight = textureSize.YAxis * scaleDensity;
		// Scale Factor
		float xScaleFactor = 0f;
		float yScaleFactor = 0f;
		// Get Scale Factor for Minor Screen Axis value
		if (baseScreenSize.XAxis > baseScreenSize.YAxis) {
			xScaleFactor = baseWidth / baseScreenSize.XAxis;
			yScaleFactor = baseHeight / baseScreenSize.XAxis;
		} else {
			xScaleFactor = baseWidth / baseScreenSize.YAxis;
			yScaleFactor = baseHeight / baseScreenSize.YAxis;
		}
		// Correct Texture Size
		float textureWidth = 0;
		float textureHeight = 0;
		// Get Corrected Texture Size for Minor Screen Axis Value
		if (selfScreenSize.XAxis > selfScreenSize.YAxis) {
			textureWidth = xScaleFactor * selfScreenSize.XAxis;
			textureHeight = yScaleFactor * selfScreenSize.XAxis;
		} else {
			textureWidth = xScaleFactor * selfScreenSize.YAxis;
			textureHeight = yScaleFactor * selfScreenSize.YAxis;
		}
		// Return correct Texture size
		return multigear.general.utils.KernelUtils.ref2d(textureWidth, textureHeight);
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
	final static public multigear.general.utils.Ref2F calculateGeneralTextureSizeSmaller(final multigear.general.utils.Ref2F textureSize, final multigear.general.utils.Ref2F baseScreenSize, final float baseDensity, final multigear.general.utils.Ref2F selfScreenSize, final float selfDensity) {
		// Get Scale Density
		final float scaleDensity = baseDensity / selfDensity;
		// Get Base Size
		final float baseWidth = textureSize.XAxis * scaleDensity;
		final float baseHeight = textureSize.YAxis * scaleDensity;
		// Scale Factor
		float xScaleFactor = 0f;
		float yScaleFactor = 0f;
		// Get Scale Factor for Minor Screen Axis value
		if (baseScreenSize.XAxis < baseScreenSize.YAxis) {
			xScaleFactor = baseWidth / baseScreenSize.XAxis;
			yScaleFactor = baseHeight / baseScreenSize.XAxis;
		} else {
			xScaleFactor = baseWidth / baseScreenSize.YAxis;
			yScaleFactor = baseHeight / baseScreenSize.YAxis;
		}
		// Correct Texture Size
		float textureWidth = 0;
		float textureHeight = 0;
		// Get Corrected Texture Size for Minor Screen Axis Value
		if (selfScreenSize.XAxis < selfScreenSize.YAxis) {
			textureWidth = xScaleFactor * selfScreenSize.XAxis;
			textureHeight = yScaleFactor * selfScreenSize.XAxis;
		} else {
			textureWidth = xScaleFactor * selfScreenSize.YAxis;
			textureHeight = yScaleFactor * selfScreenSize.YAxis;
		}
		// Return correct Texture size
		return multigear.general.utils.KernelUtils.ref2d(textureWidth, textureHeight);
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
	final static public multigear.general.utils.Ref2F calculateGeneralTextureSizeDiagonal(final multigear.general.utils.Ref2F textureSize, final multigear.general.utils.Ref2F baseScreenSize, final float baseDensity, final multigear.general.utils.Ref2F selfScreenSize, final float selfDensity) {
		// Get Scale Density
		final float scaleDensity = baseDensity / selfDensity;
		// Get Base Size
		final float baseWidth = textureSize.XAxis * scaleDensity;
		final float baseHeight = textureSize.YAxis * scaleDensity;
		// Scale Factor
		float xScaleFactor = selfScreenSize.XAxis / baseScreenSize.XAxis;
		float yScaleFactor = selfScreenSize.YAxis / baseScreenSize.YAxis;
		// Get Diagonal
		float xyScaleFactor = (float)Math.hypot(xScaleFactor, yScaleFactor);
		// Return correct Texture size
		return new Ref2F(xyScaleFactor * baseWidth, xyScaleFactor * baseHeight);
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
	final static public multigear.general.utils.Ref2F calculateGeneralTextureSizeUnspect(final multigear.general.utils.Ref2F textureSize, final multigear.general.utils.Ref2F baseScreenSize, final float baseDensity, final multigear.general.utils.Ref2F selfScreenSize, final float selfDensity) {
		// Get Scale Density
		final float scaleDensity = baseDensity / selfDensity;
		// Get Base Size
		final float baseWidth = textureSize.XAxis * scaleDensity;
		final float baseHeight = textureSize.YAxis * scaleDensity;
		// Scale Factor
		float xScaleFactor = selfScreenSize.XAxis / baseScreenSize.XAxis;
		float yScaleFactor = selfScreenSize.YAxis / baseScreenSize.YAxis;
		// Return correct Texture size
		return new Ref2F(xScaleFactor * baseWidth, yScaleFactor * baseHeight);
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Diferente do
	 * metodo "calculateGeneralTextureSize", este calcula o tamanho se baseando
	 * apenas na proporção. Então é importante informar o tamanho realmente
	 * exibido em uma tela base. Obs( Para evitar problemas com tamanhos
	 * incorretos, coloque as texturas desejadas somente na pasta base e
	 * desabilite o redimensionamento para outras densidades)
	 */
	final static public multigear.general.utils.Ref2F calculateIndividualTextureSizeBigger(final multigear.general.utils.Ref2F textureSize, final multigear.general.utils.Ref2F baseScreenSize, final multigear.general.utils.Ref2F selfScreenSize) {
		// Scale Factor
		float xScaleFactor = 0f;
		float yScaleFactor = 0f;
		// Get Scale Factor for Minor Screen Axis value
		if (baseScreenSize.XAxis > baseScreenSize.YAxis) {
			xScaleFactor = textureSize.XAxis / baseScreenSize.XAxis;
			yScaleFactor = textureSize.YAxis / baseScreenSize.XAxis;
		} else {
			xScaleFactor = textureSize.XAxis / baseScreenSize.YAxis;
			yScaleFactor = textureSize.YAxis / baseScreenSize.YAxis;
		}
		// Correct Texture Size
		float correctTextureWidth = 0;
		float correctTextureHeight = 0;
		// Get Corrected Texture Size for Minor Screen Axis Value
		if (selfScreenSize.XAxis > selfScreenSize.YAxis) {
			correctTextureWidth = xScaleFactor * selfScreenSize.XAxis;
			correctTextureHeight = yScaleFactor * selfScreenSize.XAxis;
		} else {
			correctTextureWidth = xScaleFactor * selfScreenSize.YAxis;
			correctTextureHeight = yScaleFactor * selfScreenSize.YAxis;
		}
		// Return correct Texture size
		return multigear.general.utils.KernelUtils.ref2d(correctTextureWidth, correctTextureHeight);
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Diferente do
	 * metodo "calculateGeneralTextureSize", este calcula o tamanho se baseando
	 * apenas na proporção. Então é importante informar o tamanho realmente
	 * exibido em uma tela base. Obs( Para evitar problemas com tamanhos
	 * incorretos, coloque as texturas desejadas somente na pasta base e
	 * desabilite o redimensionamento para outras densidades)
	 */
	final static public multigear.general.utils.Ref2F calculateIndividualTextureSizeDiagonal(final multigear.general.utils.Ref2F textureSize, final multigear.general.utils.Ref2F baseScreenSize, final multigear.general.utils.Ref2F selfScreenSize) {
		// Scale Factor
		float xScaleFactor = selfScreenSize.XAxis / baseScreenSize.XAxis;
		float yScaleFactor = selfScreenSize.YAxis / baseScreenSize.YAxis;
		// Get Diagonal
		float xyScaleFactor = (float)Math.hypot(xScaleFactor, yScaleFactor);
		// Return correct Texture size
		return textureSize.clone().mul(new Ref2F(xyScaleFactor, xyScaleFactor));
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Diferente do
	 * metodo "calculateGeneralTextureSize", este calcula o tamanho se baseando
	 * apenas na proporção. Então é importante informar o tamanho realmente
	 * exibido em uma tela base. Obs( Para evitar problemas com tamanhos
	 * incorretos, coloque as texturas desejadas somente na pasta base e
	 * desabilite o redimensionamento para outras densidades)
	 */
	final static public multigear.general.utils.Ref2F calculateIndividualTextureSizeUnspect(final multigear.general.utils.Ref2F textureSize, final multigear.general.utils.Ref2F baseScreenSize, final multigear.general.utils.Ref2F selfScreenSize) {
		// Scale Factor
		float xScaleFactor = selfScreenSize.XAxis / baseScreenSize.XAxis;
		float yScaleFactor = selfScreenSize.YAxis / baseScreenSize.YAxis;
		// Return correct Texture size
		return textureSize.clone().mul(new Ref2F(xScaleFactor, yScaleFactor));
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Diferente do
	 * metodo "calculateGeneralTextureSize", este calcula o tamanho se baseando
	 * apenas na proporção. Então é importante informar o tamanho realmente
	 * exibido em uma tela base. Obs( Para evitar problemas com tamanhos
	 * incorretos, coloque as texturas desejadas somente na pasta base e
	 * desabilite o redimensionamento para outras densidades)
	 */
	final static public multigear.general.utils.Ref2F calculateIndividualTextureSizeSmaller(final multigear.general.utils.Ref2F textureSize, final multigear.general.utils.Ref2F baseScreenSize, final multigear.general.utils.Ref2F selfScreenSize) {
		// Scale Factor
		float xScaleFactor = 0f;
		float yScaleFactor = 0f;
		// Get Scale Factor for Minor Screen Axis value
		if (baseScreenSize.XAxis < baseScreenSize.YAxis) {
			xScaleFactor = textureSize.XAxis / baseScreenSize.XAxis;
			yScaleFactor = textureSize.YAxis / baseScreenSize.XAxis;
		} else {
			xScaleFactor = textureSize.XAxis / baseScreenSize.YAxis;
			yScaleFactor = textureSize.YAxis / baseScreenSize.YAxis;
		}
		// Correct Texture Size
		float correctTextureWidth = 0;
		float correctTextureHeight = 0;
		// Get Corrected Texture Size for Minor Screen Axis Value
		if (selfScreenSize.XAxis < selfScreenSize.YAxis) {
			correctTextureWidth = xScaleFactor * selfScreenSize.XAxis;
			correctTextureHeight = yScaleFactor * selfScreenSize.XAxis;
		} else {
			correctTextureWidth = xScaleFactor * selfScreenSize.YAxis;
			correctTextureHeight = yScaleFactor * selfScreenSize.YAxis;
		}
		// Return correct Texture size
		return multigear.general.utils.KernelUtils.ref2d(correctTextureWidth, correctTextureHeight);
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Diferente do
	 * metodo "calculateGeneralTextureSize", este calcula o tamanho se baseando
	 * apenas na proporção. Então é importante informar o tamanho realmente
	 * exibido em uma tela base. Obs( Para evitar problemas com tamanhos
	 * incorretos, coloque as texturas desejadas somente na pasta base e
	 * desabilite o redimensionamento para outras densidades)
	 */
	final static public float calculateIndividualValueBigger(final float value, final multigear.general.utils.Ref2F baseScreenSize, final multigear.general.utils.Ref2F selfScreenSize) {
		// If same Display
		if((baseScreenSize.YAxis - selfScreenSize.YAxis) <= 0.01)
			return value;
		
		// Scale Factor
		float scaleFactor = 0f;
		// Get Scale Factor for Minor Screen Axis value
		if (baseScreenSize.XAxis > baseScreenSize.YAxis)
			scaleFactor = value / baseScreenSize.XAxis;
		else
			scaleFactor = value / baseScreenSize.YAxis;
		// Correct Texture Size
		float correctValue = 0;
		// Get Corrected Texture Size for Minor Screen Axis Value
		if (selfScreenSize.XAxis > selfScreenSize.YAxis)
			correctValue = scaleFactor * selfScreenSize.XAxis;
		else
			correctValue = scaleFactor * selfScreenSize.YAxis;
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
	final static public float calculateIndividualValueSmaller(final float value, final multigear.general.utils.Ref2F baseScreenSize, final multigear.general.utils.Ref2F selfScreenSize) {
		// If same Display
		if((baseScreenSize.XAxis - selfScreenSize.XAxis) <= 0.01)
			return value;
		
		// Scale Factor
		float scaleFactor = 0f;
		// Get Scale Factor for Minor Screen Axis value
		if (baseScreenSize.XAxis < baseScreenSize.YAxis)
			scaleFactor = value / baseScreenSize.XAxis;
		else
			scaleFactor = value / baseScreenSize.YAxis;
		// Correct Texture Size
		float correctValue = 0;
		// Get Corrected Texture Size for Minor Screen Axis Value
		if (selfScreenSize.XAxis < selfScreenSize.YAxis)
			correctValue = scaleFactor * selfScreenSize.XAxis;
		else
			correctValue = scaleFactor * selfScreenSize.YAxis;
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
	final static public multigear.general.utils.Ref2F calculateIndividualRef2DBigger(final multigear.general.utils.Ref2F ref2D, final multigear.general.utils.Ref2F baseScreenSize, final multigear.general.utils.Ref2F selfScreenSize) {
		// Scale Factor
		float xScaleFactor = 0f;
		float yScaleFactor = 0f;
		// Get Scale Factor for Minor Screen Axis value
		if (baseScreenSize.XAxis > baseScreenSize.YAxis) {
			xScaleFactor = ref2D.XAxis / baseScreenSize.XAxis;
			yScaleFactor = ref2D.YAxis / baseScreenSize.XAxis;
		} else {
			xScaleFactor = ref2D.XAxis / baseScreenSize.YAxis;
			yScaleFactor = ref2D.YAxis / baseScreenSize.YAxis;
		}
		// Correct Texture Size
		float correctTextureWidth = 0;
		float correctTextureHeight = 0;
		// Get Corrected Texture Size for Minor Screen Axis Value
		if (selfScreenSize.XAxis > selfScreenSize.YAxis) {
			correctTextureWidth = xScaleFactor * selfScreenSize.XAxis;
			correctTextureHeight = yScaleFactor * selfScreenSize.XAxis;
		} else {
			correctTextureWidth = xScaleFactor * selfScreenSize.YAxis;
			correctTextureHeight = yScaleFactor * selfScreenSize.YAxis;
		}
		// Return correct Texture size
		return multigear.general.utils.KernelUtils.ref2d(correctTextureWidth, correctTextureHeight);
	}
	
	/*
	 * Calcula o tamanho correto em que uma textura deveria ter. Diferente do
	 * metodo "calculateGeneralTextureSize", este calcula o tamanho se baseando
	 * apenas na proporção. Então é importante informar o tamanho realmente
	 * exibido em uma tela base. Obs( Para evitar problemas com tamanhos
	 * incorretos, coloque as texturas desejadas somente na pasta base e
	 * desabilite o redimensionamento para outras densidades)
	 */
	final static public multigear.general.utils.Ref2F calculateIndividualRef2DSmaller(final multigear.general.utils.Ref2F ref2D, final multigear.general.utils.Ref2F baseScreenSize, final multigear.general.utils.Ref2F selfScreenSize) {
		// Scale Factor
		float xScaleFactor = 0f;
		float yScaleFactor = 0f;
		// Get Scale Factor for Minor Screen Axis value
		if (baseScreenSize.XAxis < baseScreenSize.YAxis) {
			xScaleFactor = ref2D.XAxis / baseScreenSize.XAxis;
			yScaleFactor = ref2D.YAxis / baseScreenSize.XAxis;
		} else {
			xScaleFactor = ref2D.XAxis / baseScreenSize.YAxis;
			yScaleFactor = ref2D.YAxis / baseScreenSize.YAxis;
		}
		// Correct Texture Size
		float correctTextureWidth = 0;
		float correctTextureHeight = 0;
		// Get Corrected Texture Size for Minor Screen Axis Value
		if (selfScreenSize.XAxis < selfScreenSize.YAxis) {
			correctTextureWidth = xScaleFactor * selfScreenSize.XAxis;
			correctTextureHeight = yScaleFactor * selfScreenSize.XAxis;
		} else {
			correctTextureWidth = xScaleFactor * selfScreenSize.YAxis;
			correctTextureHeight = yScaleFactor * selfScreenSize.YAxis;
		}
		// Return correct Texture size
		return multigear.general.utils.KernelUtils.ref2d(correctTextureWidth, correctTextureHeight);
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
	final static public multigear.general.utils.Ref2F getRealScreenSize(Display display, multigear.general.utils.Ref2F defaultScreenSize) {
		multigear.general.utils.Ref2F finalScreenSize = multigear.general.utils.KernelUtils.ref2d(0, 0);
		if (Build.VERSION.SDK_INT >= 19) {
			Point outPoint = new Point();
			DisplayMetrics metrics = new DisplayMetrics();
			display.getRealMetrics(metrics);
			outPoint.x = metrics.widthPixels;
			outPoint.y = metrics.heightPixels;
			finalScreenSize = multigear.general.utils.KernelUtils.ref2d(outPoint.x, outPoint.y);
		}
		if (finalScreenSize.XAxis < defaultScreenSize.XAxis && finalScreenSize.YAxis < defaultScreenSize.YAxis) {
			return defaultScreenSize;
		}
		return finalScreenSize;
	}
	
	/**
	 * Create FloatBuffer
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
	final static public float[] mapRectToFloat(final Rect ref, final Ref2F measure) {
		final float width = (float)measure.XAxis;
		final float height = (float)measure.YAxis;
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
	final static public float[] mapRectToFloat(final RectF ref, final Ref2F measure) {
		final float width = (float)measure.XAxis;
		final float height = (float)measure.YAxis;
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
	final static public void mapRectToFloat(final RectF ref, final Ref2F measure, float[] out, int offset) {
		final float width = (float)measure.XAxis;
		final float height = (float)measure.YAxis;
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
}
