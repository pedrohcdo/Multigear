package multigear.mginterface.graphics.opengl.font;

import java.util.List;

import multigear.cache.CacheComponent;
import multigear.general.exceptions.MultigearException;
import multigear.general.utils.GeneralUtils;
import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.opengl.drawer.TextureContainer;
import multigear.mginterface.graphics.opengl.texture.Loader;
import multigear.mginterface.graphics.opengl.texture.Texture;
import multigear.mginterface.scene.Scene;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.opengl.GLES20;

/**
 * Font Map
 * 
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class FontMap extends CacheComponent {

	/**
	 * FontMap Metrics
	 * 
	 * @author PedroH, RaphaelB
	 * 
	 *         Property Createlier.
	 */
	final public class FontMapMetrics {

		final private float mAscent;
		final private float mBottom;
		final private float mDescent;
		final private float mLeading;
		final private float mTop;

		/**
		 * Constructor
		 * 
		 * @param fontMetrics
		 */
		private FontMapMetrics(FontMetrics fontMetrics) {
			mAscent = fontMetrics.ascent;
			mBottom = fontMetrics.bottom;
			mDescent = fontMetrics.descent;
			mLeading = fontMetrics.leading;
			mTop = fontMetrics.top;
		}

		/**
		 * Get Ascent
		 * 
		 * @return
		 */
		public float getAscent() {
			return mAscent;
		}

		/**
		 * Get Bottom
		 * 
		 * @return
		 */
		public float getBottom() {
			return mBottom;
		}

		/**
		 * Get Descent
		 * 
		 * @return
		 */
		public float getDescent() {
			return mDescent;
		}

		/**
		 * Get Leading
		 * 
		 * @return
		 */
		public float getLeading() {
			return mLeading;
		}

		/**
		 * Get Top
		 * 
		 * @return
		 */
		public float getTop() {
			return mTop;
		}
	}

	/**
	 * CharMapRegister Register
	 * 
	 * @author PedroH, RaphaelB
	 * 
	 *         Property Createlier.
	 */
	final public static class CharMapRegister {

		// Private Variables
		private boolean mCharacters[] = new boolean[0xFFFF];

		/**
		 * Register range of characters
		 * 
		 * @param start
		 * @param end
		 */
		final public void register(int start, int end) {
			for (int i = start; i <= end; i++)
				mCharacters[i] = true;
		}

		/**
		 * Register text
		 * 
		 * @param start
		 * @param end
		 */
		final public void register(final String text) {
			final char chars[] = new char[text.length()];
			text.getChars(0, text.length(), chars, 0);
			for (char c : chars)
				mCharacters[c] = true;
		}

		/**
		 * Register character
		 * 
		 * @param start
		 * @param end
		 */
		final public void register(final char character) {
			mCharacters[character] = true;
		}

		/**
		 * Register list of characters
		 * 
		 * @param list
		 */
		final public void register(List<Character> list) {
			for (char c : list)
				mCharacters[(int) c] = true;
		}

		/**
		 * Register list of characters
		 * 
		 * @param list
		 */
		final public void register(char list[]) {
			for (char c : list)
				mCharacters[(int) c] = true;
		}

		/**
		 * Pack CharMap
		 * 
		 * @return
		 */
		final public CharMap pack() {
			int count = 0;
			for (boolean c : mCharacters) {
				if (c)
					count++;
			}
			char map[] = new char[count];
			int indexes[] = new int[0xFFFF];

			count = 0;
			for (int i = 0; i < mCharacters.length; i++) {
				if (mCharacters[i]) {
					indexes[i] = count;
					map[count++] = (char) i;
				}
			}
			return new CharMap(mCharacters, map, indexes);
		}
	}

	/**
	 * CharMap
	 * 
	 * @author PedroH, RaphaelB
	 * 
	 *         Property Createlier.
	 */
	final public static class CharMap {

		final protected boolean mCharacters[];
		final protected char mCharactersPack[];
		final protected int mCharactersIndexes[];

		/**
		 * Constructor
		 * 
		 * @param characters
		 */
		private CharMap(final boolean characters[],
				final char charactersPack[], int indexes[]) {
			mCharacters = characters;
			mCharactersPack = charactersPack;
			mCharactersIndexes = indexes;
		}

		/**
		 * Has Character
		 * 
		 * @param character
		 * @return Return true if has character in registered map
		 */
		final public boolean hasCharacter(final char character) {
			return mCharacters[character];
		}
	}
	
	/**
	 * Font Layer
	 * 
	 * @author user
	 *
	 */
	final protected static class Layer {
		
		// Private Variables
		protected FontMapMetrics mFontMetrics;
		protected Texture mTextureFont;
		protected float[] mCharactersWidths;
		protected Vector2[] mCharactersBounds;
		protected int mMaxWidth;
		protected int mMaxHeight;
		protected int mMaxBoundedWidth;
		protected int mMaxBoundedHeight;
		protected float mScale;
		protected int mId;
	}
	
	/**
	 * Styles
	 * 
	 * @author user
	 *
	 */
	public enum Style {
		
		Normal, Italic, Bold, BoldItalic;
		
		/**
		 * Create typeface
		 */
		final private Typeface create(final Typeface fontFamily) {
			switch(this) {
			default:
			case Normal:
				return Typeface.create(fontFamily, Typeface.NORMAL);
			case Bold:
				return Typeface.create(fontFamily, Typeface.BOLD);
			case BoldItalic:
				return Typeface.create(fontFamily, Typeface.BOLD_ITALIC);
			case Italic:
				return Typeface.create(fontFamily, Typeface.ITALIC);
			}
		}
	}
	
	// Constants
	final static public CharMap CharMapBasic;
	static {
		final CharMapRegister register = new CharMapRegister();
		register.register(32, 255);
		CharMapBasic = register.pack();
	}

	// Font Private Variables
	final private FontDrawer mFontDrawer = new FontDrawer(this);
	final private Layer[] mLayers = new Layer[4];
	
	// Private Variables
	protected Style mStyle = Style.Normal;
	protected CharMap mCharMap;
	protected FontAttributes mAttributes = new FontAttributes(new Vector2(0, 0), true, false);
	protected float mFontSize;
	
	/**
	 * Metrics
	 * 
	 * @param metrics
	 * @return
	 */
	final private FontMapMetrics getNewMetricsInstance(FontMetrics metrics) {
		return new FontMapMetrics(metrics);
	}

	/**
	 * Returns the maximum size of a font vertically. 
	 * It is important to reveal the horizontal size, because 
	 * it can be less.
	 * @return
	 */
	final private static int getMaxVerticalFontSizeSupport(final Typeface typefaceFamily, final Style style, CharMap map) {
		// Create styled typeface
		Typeface typeface = style.create(typefaceFamily);
		// Create Paint
		Paint paint = new Paint();
		paint.setColor(0xFFFFFFFF);
		paint.setAntiAlias(true);
		paint.setTypeface(typeface);
		// Compatibility font bold
		if((style == Style.Bold || style == Style.BoldItalic) && !typefaceFamily.isBold())
			paint.setFakeBoldText(true);
		// Compatibility font italic
		if((style == Style.Italic || style == Style.BoldItalic) && !typefaceFamily.isItalic())
			paint.setTextSkewX(-0.25f);
		// Get base Metrics
		paint.setTextSize(100);
		Paint.FontMetrics baseMetrics = paint.getFontMetrics();
		// Get minimums Limits
		float baseFontAscent = Math.abs(baseMetrics.ascent);
		float baseFontDescent = Math.abs(baseMetrics.descent);
		int baseFontHeight = (int) Math.ceil(baseFontAscent + baseFontDescent);
		// Get the maximum texture size that the device supports
		final int maxTextureSizeBuff[] = new int[1];
		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSizeBuff, 0);
		final int maxTextureSize = maxTextureSizeBuff[0];
		// Get squared characters pack
		int square = (int) Math.round(Math.sqrt(map.mCharactersPack.length));
		// Get cell size
		float cellSize = maxTextureSize / (square * 1.0f);
		// Get max Font size
		float maxFontSize = (100 * cellSize) / baseFontHeight;
		// return Max Font Size
		return (int)Math.floor(maxFontSize) - 1;
	}
	
	/**
	 * Returns the maximum size of a font horizontally. 
	 * It is important to reveal the vertical size, 
	 * because it can be less.
	 * 
	 * @return
	 */
	final private static int getMaxHorizontalFontSizeSupport(final Typeface typefaceFamily, final Style style, final CharMap map) {
		// Create styled typeface
		Typeface typeface = style.create(typefaceFamily);
		// Create Paint
		Paint paint = new Paint();
		paint.setColor(0xFFFFFFFF);
		paint.setAntiAlias(true);
		paint.setTypeface(typeface);
		// Compatibility font bold
		if((style == Style.Bold || style == Style.BoldItalic) && !typefaceFamily.isBold())
			paint.setFakeBoldText(true);
		// Compatibility font italic
		if((style == Style.Italic || style == Style.BoldItalic) && !typefaceFamily.isItalic())
			paint.setTextSkewX(-0.25f);
		// Set base size
		paint.setTextSize(100);
		// Max Width
		Rect rect = new Rect();
		int maxBoundedWidth = 0;
		for (int i = 0; i < map.mCharactersPack.length; i++) {
			final char char_ = map.mCharactersPack[i];
			paint.getTextBounds(char_ + "", 0, 1, rect);
			maxBoundedWidth = Math.max(maxBoundedWidth, rect.width());
		}
		maxBoundedWidth += 2;
		// Get the maximum texture size that the device supports
		final int maxTextureSizeBuff[] = new int[1];
		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSizeBuff, 0);
		final int maxTextureSize = maxTextureSizeBuff[0];
		// Get squared characters pack
		int square = (int) Math.round(Math.sqrt(map.mCharactersPack.length));
		// Get cell size
		float cellSize = maxTextureSize / (square * 1.0f);
		// Get max Font size
		final float maxFontSize = (100 * cellSize) / maxBoundedWidth;
		// return Max Font Size
		return (int)Math.floor(maxFontSize) - 1;
	}

	/**
	 * Returns the largest font size that the device supports.
	 * <br>
	 * <b>Note</b>: All sources that are created with the above supported
	 *  sizes used compatibility of methods, such as automatic zoom. 
	 *  It is not necessary to be very careful with this detail because 
	 *  everything will be adapted automatically.
	 */
	final static public int getMaxFontSizeSupport(final Typeface typefaceFamily, final Style style, CharMap map) {
		// Get Max Sizes
		final int maxVerticalSize = getMaxVerticalFontSizeSupport(typefaceFamily, style, map);
		final int maxHorizontalSize = getMaxHorizontalFontSizeSupport(typefaceFamily, style, map);
		// Return Max Size
		return Math.min(maxVerticalSize, maxHorizontalSize);
	}
	
	/**
	 * Create FontMap
	 * 
	 * @param scene
	 * @param assetManager
	 * @param loader
	 * @param fontPath
	 * @param fontSize
	 * @param map
	 * @return
	 */
	final static protected FontMap create(final Scene scene, final Loader loader, final Typeface typeface, final int fontSize, final CharMap map) {
		// Not support size smaller than 1
		if(fontSize < 0)
			throw new IllegalArgumentException("Font Size can not be less than 1");
				
		// Create FontMap
		FontMap fontMap = new FontMap();
				
		// Get Asset Manager
		AssetManager assetManager = scene.getActivity().getAssets();
				
		// Set CharMap and Font Size
		fontMap.mCharMap = map;
		fontMap.mFontSize = fontSize;
				
		// Id counter
		int id = 0;
				
		// Create All Layers
		for(final Style styleLayer : Style.values()) {
					
			// Load typeface
			Typeface typefaceFamily = typeface;
					
			// Create Layer
			Layer layer = new Layer();
			layer.mId = id++;
					
			// get Compatibility Font Size
			final float compatibilityFontSize = Math.min(fontSize, getMaxFontSizeSupport(typefaceFamily, styleLayer, map));
			final float compatibilityScaleCorrection = fontSize / compatibilityFontSize;
					
			// Set font scale
			layer.mScale = compatibilityScaleCorrection;
					
			// Create Paint
			Paint paint = new Paint();
			paint.setColor(0xFFFFFFFF);
			paint.setAntiAlias(true);
			paint.setTypeface(styleLayer.create(typeface));
			paint.setTextSize(compatibilityFontSize);
					
			// Compatibility font bold
			if((styleLayer == Style.Bold || styleLayer == Style.BoldItalic) && !typefaceFamily.isBold())
				paint.setFakeBoldText(true);
					
			// Compatibility font italic
			if((styleLayer == Style.Italic || styleLayer == Style.BoldItalic) && !typefaceFamily.isItalic())
				paint.setTextSkewX(-0.25f);
					
			// Get font metrics
			Paint.FontMetrics metrics = paint.getFontMetrics();
					
			// Set FontMapMetrics
			layer.mFontMetrics = fontMap.getNewMetricsInstance(metrics);
					
			// Get minimums Limits
			float fontAscent = Math.abs(metrics.ascent);
			float fontDescent = Math.abs(metrics.descent);
			int fontHeight = (int) Math.ceil(fontAscent + fontDescent);
					
			// Set font map max height
			layer.mMaxHeight = (int) Math.ceil(Math.ceil(fontHeight) * layer.mScale);
			layer.mMaxBoundedHeight = (int) Math.ceil(fontHeight);
					
			// Get CharMap map widths
			float widths[] = new float[map.mCharactersPack.length];
			int maxWidth = Integer.MIN_VALUE;
			paint.getTextWidths(map.mCharactersPack, 0, map.mCharactersPack.length, widths);
					
			// Set font map characters widths
			layer.mCharactersWidths = widths;
					
			// Get max width to create linear text map
			for (int i=0; i<widths.length; i++) {
				widths[i] *= layer.mScale;
				maxWidth = (int) Math.ceil(Math.max(maxWidth, widths[i]));
			}
			maxWidth = (int) Math.ceil(maxWidth);
					
			// Set font map max width
			layer.mMaxWidth = (int) maxWidth;
					
			// Get Bounds
			Rect rect = new Rect();
			Vector2 bounds[] = new Vector2[map.mCharactersPack.length];
			int maxBoundedWidth = 0;
			for (int i = 0; i < map.mCharactersPack.length; i++) {
				final char char_ = map.mCharactersPack[i];
				paint.getTextBounds(char_ + "", 0, 1, rect);
				bounds[i] = new Vector2(rect.left, rect.right);
				maxBoundedWidth = Math.max(maxBoundedWidth, rect.width());
			}
			maxBoundedWidth += 2;
					
			// Set font bounds and max bounded size
			layer.mCharactersBounds = bounds;
			layer.mMaxBoundedWidth = maxBoundedWidth;
					
			// Get squared texture size
			int square = (int) Math.round(Math.sqrt(map.mCharactersPack.length));
			int textureWidth = GeneralUtils.calculateUpperPowerOfTwo((int) (maxBoundedWidth * square));
			int textureHeight = GeneralUtils.calculateUpperPowerOfTwo((int) (fontHeight * square));
					
			// Get max texture size
			final int maxTextureSize[] = new int[1];
			GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
					
			// OpenGL does not support textures with size above 2048
			if (textureWidth > maxTextureSize[0] || textureHeight > maxTextureSize[0])
				throw new MultigearException(0x15, "It was not possible to create a font, size exceeded the allowed.");
					
			// Create Bitmap with alpha chanel
			Bitmap bitmap = Bitmap.createBitmap(textureWidth, textureHeight, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			bitmap.eraseColor(0x00000000);
					
			// Draw map
			float maxWidth2 = maxBoundedWidth / 2.0f;
			float dx = maxWidth2;
			float dy = fontAscent;
			for (int i = 0; i < map.mCharactersPack.length; i++) {
				final char c = map.mCharactersPack[i];
				final Vector2 bound = bounds[i];
				final int width = (int) (bound.y - bound.x);
				canvas.drawText(c + "", (dx - bound.x) - (width / 2.0f), dy, paint);
				dx += maxBoundedWidth;
				if (dx + maxWidth2 >= textureWidth) {
					dy += fontHeight;
					dx = maxWidth2;
				}
			}
					
			// Load texture
			layer.mTextureFont = loader.create(bitmap);
					
			// Set FontMap layer
			fontMap.mLayers[styleLayer.ordinal()] = layer;
		}
				
		// Return FontMap
		return fontMap;
	}
	
	/**
	 * Create FontMap
	 * 
	 * @param scene
	 * @param assetManager
	 * @param loader
	 * @param fontPath
	 * @param fontSize
	 * @param map
	 * @return
	 */
	final static protected FontMap create(final Scene scene, final Loader loader, final String fontPath, final int fontSize, final CharMap map) {
		// Not support size smaller than 1
		if(fontSize < 0)
			throw new IllegalArgumentException("Font Size can not be less than 1");
		
		// Create FontMap
		FontMap fontMap = new FontMap();
		
		// Get Asset Manager
		AssetManager assetManager = scene.getActivity().getAssets();
		
		// Set CharMap and Font Size
		fontMap.mCharMap = map;
		fontMap.mFontSize = fontSize;
		
		// Id counter
		int id = 0;
		
		// Create All Layers
		for(final Style styleLayer : Style.values()) {
			
			// Load typeface
			Typeface typefaceFamily = Typeface.createFromAsset(assetManager, fontPath);
			Typeface typeface = styleLayer.create(typefaceFamily);
			
			// Create Layer
			Layer layer = new Layer();
			layer.mId = id++;
			
			// get Compatibility Font Size
			final float compatibilityFontSize = Math.min(fontSize, getMaxFontSizeSupport(typefaceFamily, styleLayer, map));
			final float compatibilityScaleCorrection = fontSize / compatibilityFontSize;
			
			// Set font scale
			layer.mScale = compatibilityScaleCorrection;
			
			// Create Paint
			Paint paint = new Paint();
			paint.setColor(0xFFFFFFFF);
			paint.setAntiAlias(true);
			paint.setTypeface(typeface);
			paint.setTextSize(compatibilityFontSize);
			
			// Compatibility font bold
			if((styleLayer == Style.Bold || styleLayer == Style.BoldItalic) && !typefaceFamily.isBold())
				paint.setFakeBoldText(true);
			
			// Compatibility font italic
			if((styleLayer == Style.Italic || styleLayer == Style.BoldItalic) && !typefaceFamily.isItalic())
				paint.setTextSkewX(-0.25f);
			
			// Get font metrics
			Paint.FontMetrics metrics = paint.getFontMetrics();
			
			// Set FontMapMetrics
			layer.mFontMetrics = fontMap.getNewMetricsInstance(metrics);
			
			// Get minimums Limits
			float fontAscent = Math.abs(metrics.ascent);
			float fontDescent = Math.abs(metrics.descent);
			int fontHeight = (int) Math.ceil(fontAscent + fontDescent);
			
			// Set font map max height
			layer.mMaxHeight = (int) Math.ceil(Math.ceil(fontHeight) * layer.mScale);
			layer.mMaxBoundedHeight = (int) Math.ceil(fontHeight);
			
			// Get CharMap map widths
			float widths[] = new float[map.mCharactersPack.length];
			int maxWidth = Integer.MIN_VALUE;
			paint.getTextWidths(map.mCharactersPack, 0, map.mCharactersPack.length, widths);
			
			// Set font map characters widths
			layer.mCharactersWidths = widths;
			
			// Get max width to create linear text map
			for (int i=0; i<widths.length; i++) {
				widths[i] *= layer.mScale;
				maxWidth = (int) Math.ceil(Math.max(maxWidth, widths[i]));
			}
			maxWidth = (int) Math.ceil(maxWidth);
			
			// Set font map max width
			layer.mMaxWidth = (int) maxWidth;
			
			// Get Bounds
			Rect rect = new Rect();
			Vector2 bounds[] = new Vector2[map.mCharactersPack.length];
			int maxBoundedWidth = 0;
			for (int i = 0; i < map.mCharactersPack.length; i++) {
				final char char_ = map.mCharactersPack[i];
				paint.getTextBounds(char_ + "", 0, 1, rect);
				bounds[i] = new Vector2(rect.left, rect.right);
				maxBoundedWidth = Math.max(maxBoundedWidth, rect.width());
			}
			maxBoundedWidth += 2;
			
			// Set font bounds and max bounded size
			layer.mCharactersBounds = bounds;
			layer.mMaxBoundedWidth = maxBoundedWidth;
			
			// Get squared texture size
			int square = (int) Math.round(Math.sqrt(map.mCharactersPack.length));
			int textureWidth = GeneralUtils.calculateUpperPowerOfTwo((int) (maxBoundedWidth * square));
			int textureHeight = GeneralUtils.calculateUpperPowerOfTwo((int) (fontHeight * square));
			
			// Get max texture size
			final int maxTextureSize[] = new int[1];
			GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
			
			// OpenGL does not support textures with size above 2048
			if (textureWidth > maxTextureSize[0] || textureHeight > maxTextureSize[0])
				throw new MultigearException(0x15, "It was not possible to create a font, size exceeded the allowed.");
			
			// Create Bitmap with alpha chanel
			Bitmap bitmap = Bitmap.createBitmap(textureWidth, textureHeight, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			bitmap.eraseColor(0x00000000);
			
			// Draw map
			float maxWidth2 = maxBoundedWidth / 2.0f;
			float dx = maxWidth2;
			float dy = fontAscent;
			for (int i = 0; i < map.mCharactersPack.length; i++) {
				final char c = map.mCharactersPack[i];
				final Vector2 bound = bounds[i];
				final int width = (int) (bound.y - bound.x);
				canvas.drawText(c + "", (dx - bound.x) - (width / 2.0f), dy, paint);
				dx += maxBoundedWidth;
				if (dx + maxWidth2 >= textureWidth) {
					dy += fontHeight;
					dx = maxWidth2;
				}
			}
			
			// Load texture
			layer.mTextureFont = loader.create(bitmap);
			
			// Set FontMap layer
			fontMap.mLayers[styleLayer.ordinal()] = layer;
		}
		
		// Return FontMap
		return fontMap;
	}
	
	/**
	 * Return active layer by Style
	 * @return
	 */
	final protected Layer getActiveLayer() {
		return mLayers[mStyle.ordinal()];
	}
	
	/**
	 * Get Font Size
	 * @return Font Size
	 */
	final public float getFontSize() {
		return mFontSize;
	}
	
	/**
	 * Return Text Size
	 * 
	 * @return
	 */
	final public Vector2 getTextSize(final String text, final int start, final int size) {
		// Check range
		if (start < 0 || start + size > text.length())
			throw new IndexOutOfBoundsException();
		// Get Layer
		final Layer layer = getActiveLayer();
		// Get Padd distance
		Vector2 padd = mAttributes.getPadd();
		// If empty
		if(text.length() == 0)
			return new Vector2(0, layer.mMaxHeight + padd.y * 2);
		// Measure text
		if (mAttributes.isLinear()) {
			final float textWidth = layer.mMaxWidth * size + padd.x * (size + 1);
			final float textHeight = layer.mMaxHeight + padd.y * 2;
			return new Vector2(textWidth, textHeight);
		} else {
			float textWidth = padd.x * (size + 1);
			float textHeight = layer.mMaxHeight + padd.y * 2;
			char chars[] = new char[size];
			text.getChars(start, start + size, chars, 0);
			for (char c : chars) {
				if (mCharMap.mCharacters[c]) {
					int index = mCharMap.mCharactersIndexes[c];
					textWidth += layer.mCharactersWidths[index];
				}
			}
			return new Vector2(textWidth, textHeight);
		}
	}

	/**
	 * Return Text Size
	 * 
	 * @return
	 */
	final public Vector2 getTextSize(final String text) {
		return getTextSize(text, 0, text.length());
	}

	/**
	 * Return Text Size
	 * 
	 * @return
	 */
	final public Vector2 getTextSize(final char[] text, final int start, final int size) {
		if (start < 0 || start + size > text.length)
			throw new IndexOutOfBoundsException();
		// Get Layer
		final Layer layer = getActiveLayer();
		// Get padd distance		
		Vector2 padd = mAttributes.getPadd();
		// If empty
		if(text.length == 0)
			return new Vector2(0, layer.mMaxHeight + padd.y * 2);
		// Measure text
		if (mAttributes.isLinear()) {
			final float textWidth = layer.mMaxWidth * size + padd.x * (size + 1);
			final float textHeight = layer.mMaxHeight + padd.y * 2;
			return new Vector2(textWidth, textHeight);
		} else {
			float textWidth = padd.x * (size + 1);
			float textHeight = layer.mMaxHeight + padd.y * 2;
			for (int i = start; i < (start + size); i++) {
				char c = text[i];
				if (mCharMap.mCharacters[c]) {
					int index = mCharMap.mCharactersIndexes[c];
					textWidth += layer.mCharactersWidths[index];
				}
			}
			return new Vector2(textWidth, textHeight);
		}
	}

	/**
	 * Return Text Size
	 * 
	 * @return
	 */
	final public Vector2 getTextSize(final char[] text) {
		return getTextSize(text, 0, text.length);
	}

	/**
	 * Return Characters widths<br>
	 * <br>
	 * <b>Obs: If a word is not included on the map, it will be set as linear
	 * size.</b>
	 * 
	 * @param text
	 *            Text to read
	 * @param index
	 *            Index of first char
	 * @param count
	 *            Characters count
	 * @param out
	 *            Widths out
	 * @param offset
	 *            Out vector offset
	 * @return
	 */
	final public void getTextWidths(final String text, final int index, final int count, final float out[], int offset) {
		// Check text range
		if (index < 0 || index + count > text.length())
			throw new IndexOutOfBoundsException();
		// Check array range
		if (offset < 0 || offset + count > out.length || count < 0)
			throw new ArrayIndexOutOfBoundsException();
		// Get Layer
		final Layer layer = getActiveLayer();
		// Measure text widths
		char chars[] = new char[text.length()];
		text.getChars(0, text.length(), chars, 0);
		for (int i = index; i < (index + count); i++) {
			char c = chars[i];
			if (mCharMap.mCharacters[c]) {
				int charIndex = mCharMap.mCharactersIndexes[c];
				out[offset++] = layer.mCharactersWidths[charIndex];
			} else
				out[offset++] = layer.mMaxWidth;
		}
	}

	/**
	 * Return Characters widths<br>
	 * <br>
	 * <b>Obs: If a word is not included on the map, it will be set as linear
	 * size.</b>
	 * 
	 * @param text
	 *            Text to read
	 * @param index
	 *            Index of first char
	 * @param count
	 *            Characters count
	 * @param out
	 *            Widths out
	 * @param offset
	 *            Out vector offset
	 * @return
	 */
	final public void getTextWidths(final char[] text, final int index, final int count, final float out[], int offset) {
		// Check text range
		if (index < 0 || index + count > text.length)
			throw  new IndexOutOfBoundsException();
		// Check array range
		if (offset < 0 || offset + count > out.length || count < 0)
			throw new ArrayIndexOutOfBoundsException();
		// Get Layer
		final Layer layer = getActiveLayer();
		// Measure text widths
		for (int i = index; i < (index + count); i++) {
			char c = text[i];
			if (mCharMap.mCharacters[c]) {
				int charIndex = mCharMap.mCharactersIndexes[c];
				out[offset++] = layer.mCharactersWidths[charIndex];
			} else
				out[offset++] = layer.mMaxWidth;
		}
	}

	/**
	 * Set Font Attributes
	 * 
	 * @return
	 */
	final public void setAttributes(FontAttributes attributes) {
		// Set attributes
		mAttributes = attributes;
	}
	
	/**
	 * Set Font Style
	 * 
	 * @param style
	 */
	final public void setStyle(final Style style) {
		mStyle = style;
	}

	/**
	 * Get Texture
	 * 
	 * @return
	 */
	final public Texture getTexture() {
		return getActiveLayer().mTextureFont;
	}

	/**
	 * Get Textures
	 * 
	 * @return
	 */
	final public Texture[] getTextures() {
		Texture[] textures = new Texture[mLayers.length];
		for(int i=0; i<mLayers.length; i++) {
			textures[i] = mLayers[i].mTextureFont;
		}
		return textures;
	}

	
	/**
	 * Get Font Attributes
	 * 
	 * @return
	 */
	final public FontAttributes getAttributes() {
		return mAttributes;
	}

	/**
	 * Get Font Style
	 * 
	 * @param style
	 */
	final public Style getStyle() {
		return mStyle;
	}
	
	/**
	 * Get Font Metrics
	 * 
	 * @return
	 */
	final public FontMapMetrics getMetrics() {
		return getActiveLayer().mFontMetrics;
	}

	/**
	 * Get registered characters map
	 * 
	 * @return
	 */
	final public CharMap getCharMap() {
		return mCharMap;
	}

	/**
	 * Get Linear Character Size
	 * 
	 * @return
	 */
	final public Vector2 getLinearCharSize() {
		// Get Layer
		final Layer layer = getActiveLayer();
		// Return linear layer size
		return new Vector2(layer.mMaxWidth, layer.mMaxHeight);
	}

	/**
	 * Begin and get FontDrawer
	 * 
	 * @return
	 */
	final protected FontDrawer beginFontDrawer(final TextureContainer container) {
		mFontDrawer.setTextureContainer(container);
		mFontDrawer.begin();
		return mFontDrawer;
	}

	/**
	 * End Font Drawer
	 */
	final protected void endFontDrawer() {
		mFontDrawer.end();
	}
}
