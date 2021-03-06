package com.org.multigear.mginterface.graphics.opengl.font;

import java.lang.annotation.Target;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.org.multigear.general.utils.Color;
import com.org.multigear.general.utils.GeneralUtils;
import com.org.multigear.general.utils.GradientColor;
import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.graphics.opengl.texture.Texture;
import com.org.multigear.mginterface.graphics.opengl.vbo.VertexBufferObject;

/**
 * Letter Drawer
 * 
 * @author user
 * 
 */
final public class LetterDrawer {

	// Conts
	final private static int MODE_SOLID = 0;
	final private static int MODE_GRADIENT = 1;
	
	/**
	 * Script
	 * 
	 * @author user
	 * 
	 */
	final private class Script {

		public FontMap.Layer layer;
		public Color color;
		public GradientColor gradientColor;
		public String text;
		public Vector2 position;
		int mode;
	}
	
	// Private Variables
	private boolean mBegin = false;
	private Color mColor = Color.WHITE;
	private GradientColor mGradientColor = null;
	private int mMode = MODE_SOLID;
	private List<Script> mScripts = new ArrayList<Script>();
	protected FloatBuffer mElements, mColors, mTextures;
	protected int mElementsCount = 0;
	private FontMap mFontMap;
	
	/**
	 * Constructor
	 */
	protected LetterDrawer() {}
	
	/**
	 * Clear
	 */
	final protected void clear() {
		mBegin = false;
		mScripts.clear();
		mElementsCount = 0;
		mElements = null;
		mTextures = null;
	}

	/**
	 * Begin Draw
	 */
	final protected void begin(final FontMap fontMap) {
		mFontMap = fontMap;
		mBegin = true;
		mScripts.clear();
		mElementsCount = 0;
		mElements = null;
		mTextures = null;
	}

	/**
	 * End draw
	 */
	final protected void end() {
		mBegin = false;
		prepareScripts();
	}

	/**
	 * Set Color
	 */
	final public void setColor(final Color color) {
		if (!mBegin)
			throw new RuntimeException("The LetterDrawer can only be used when informed by the listener.");
		mColor = color;
		mMode = MODE_SOLID;
	}
	
	/**
	 * Set Color
	 */
	final public void setColor(final GradientColor gradientColor) {
		if (!mBegin)
			throw new RuntimeException("The LetterDrawer can only be used when informed by the listener.");
		mGradientColor = gradientColor;
		mMode = MODE_GRADIENT;
	}

	/**
	 * Draw Text
	 * 
	 * @param text
	 */
	final public void drawText(final String text, Vector2 position) {
		if (!mBegin)
			throw new RuntimeException("The LetterDrawer can only be used when informed by the listener.");
		Script script = new Script();
		script.color = mColor;
		script.gradientColor = mGradientColor;
		script.text = text;
		script.position = new Vector2(position.x, position.y);
		script.layer = mFontMap.getActiveLayer();
		script.mode = mMode;
		mScripts.add(script);
	}

	/**
	 * Draw Text
	 * 
	 * @param text
	 */
	final public void drawText(final String text) {
		drawText(text, new Vector2(0, 0));
	}
	
	/**
	 * Prepare Scripts
	 */
	final private void prepareScripts() {
		// x, y, tex_x, tex_y, cr, cg, cb, ca
		// text_id
		
		// Create Buffers
		int vertexElementsLength = 0;
		int vertexColorsLength = 0;
		int vertexTextureLength = 0;
		int indicesLength = 0;
		
		for (final Script script : mScripts) {
			final int textLength = script.text.length();
			vertexElementsLength += textLength * 24;
			vertexColorsLength += textLength * 24;
			vertexTextureLength += textLength * 6;
			indicesLength += textLength * 6;
		}
		
		mElementsCount = indicesLength;
		
		mElements = GeneralUtils.createFloatBuffer(vertexElementsLength);
		mColors = GeneralUtils.createFloatBuffer(vertexColorsLength);
		mTextures = GeneralUtils.createFloatBuffer(vertexTextureLength);
		//IntBuffer indicesBuffer = GeneralUtils.createIntBuffer(indicesLength);
		
		// Add all Scripts
		for (final Script script : mScripts) {
			
			// Uses
			final FontMap.Layer layer = script.layer;
			final String text = script.text;
			final Vector2 position = script.position;
			final Texture texture = layer.mTextureFont;
			final Vector2 textureSize = texture.getSize();
			final Color color = script.color;
			
			char chars[] = new char[text.length()];
			text.getChars(0, text.length(), chars, 0);
			
			Vector2 padd = mFontMap.getAttributes().getPadd();
			float scale = layer.mScale;
			
			float x = padd.x + position.x;
			float y = padd.y + position.y;
			
			if(mFontMap.getAttributes().isUseMetrics())
				y += mFontMap.getMetrics().getAscent() * scale;
			
			int maxTextX = (int)textureSize.x / (int)layer.mMaxBoundedWidth;
			float maxBoundedWidth2 = (layer.mMaxBoundedWidth / 2.0f);
			
			// If use Gradient
			GradientColor.InterpolatedBlock[] interpolatedBlock = null;
			if(script.mode == MODE_GRADIENT) {
				// Optimized
				if(script.gradientColor.getGuide() == GradientColor.GUIDE_VERTICAL)
					interpolatedBlock = script.gradientColor.interpolate(1);
				else
					interpolatedBlock = script.gradientColor.interpolate(chars.length);
			}
			
			int characterIndex = 0;
			for(char c : chars) {
				if(mFontMap.mCharMap.mCharacters[c]) {
					int index = mFontMap.mCharMap.mCharactersIndexes[c];
					float tX = (index % maxTextX) * layer.mMaxBoundedWidth + maxBoundedWidth2; // centered;
					int tY = (index / maxTextX) * layer.mMaxBoundedHeight;
					
					final Vector2 bounds = layer.mCharactersBounds[index];
					final float center = (int)(bounds.y - bounds.x) / 2.0f;
					
					float textureLeft = ((tX - center) - 1) / textureSize.x;
					float textureTop = tY / textureSize.y;
					float textureRight = (tX + center + 1) / textureSize.x;
					float textureBottom = (tY + layer.mMaxBoundedHeight) / textureSize.y;

					float elementLeft = x + bounds.x * scale;
					float elementTop = y;
					float elementRight = x + bounds.y * scale;
					float elementBottom = y + layer.mMaxHeight;
					
					
					/*
					 *[.]   .
					 * 
					 * .    .
					 */
					mElements.put(elementLeft);
					mElements.put(elementTop);
					mElements.put(textureLeft);
					mElements.put(textureTop);
					
					/*
					 * .   [.]
					 * 
					 * .    .
					 */
					mElements.put(elementRight);
					mElements.put(elementTop);
					mElements.put(textureRight);
					mElements.put(textureTop);
					
					/*
					 * .    .
					 * 
					 * .   [.]
					 */
					mElements.put(elementRight);
					mElements.put(elementBottom);
					mElements.put(textureRight);
					mElements.put(textureBottom);
					
					/*
					 *[.]   .
					 * 
					 * .    .
					 */
					mElements.put(elementLeft);
					mElements.put(elementTop);
					mElements.put(textureLeft);
					mElements.put(textureTop);
					
					/*
					 * .    .
					 * 
					 * [.]  .
					 */
					mElements.put(elementLeft);
					mElements.put(elementBottom);
					mElements.put(textureLeft);
					mElements.put(textureBottom);
					
					/*
					 * .    .
					 * 
					 * .   [.]
					 */
					mElements.put(elementRight);
					mElements.put(elementBottom);
					mElements.put(textureRight);
					mElements.put(textureBottom);
				
					
					// Colors
					if(script.mode == MODE_SOLID) {
						for(int i=0; i<6; i++) {
							mColors.put(color.getRed());
							mColors.put(color.getGreen());
							mColors.put(color.getBlue());
							mColors.put(color.getAlpha());
						}
					} else {
						
						GradientColor.InterpolatedBlock useBloc;
						
						// Optimized
						if(script.gradientColor.getGuide() == GradientColor.GUIDE_VERTICAL)
							useBloc = interpolatedBlock[0];
						else
							useBloc = interpolatedBlock[characterIndex];
						
						Color lt = useBloc.getColor(0);
						Color rt = useBloc.getColor(1);
						Color rb = useBloc.getColor(2);
						Color lb = useBloc.getColor(3);
							
						mColors.put(lt.getRed());
						mColors.put(lt.getGreen());
						mColors.put(lt.getBlue());
						mColors.put(lt.getAlpha());
							
						mColors.put(rt.getRed());
						mColors.put(rt.getGreen());
						mColors.put(rt.getBlue());
						mColors.put(rt.getAlpha());
							
						mColors.put(rb.getRed());
						mColors.put(rb.getGreen());
						mColors.put(rb.getBlue());
						mColors.put(rb.getAlpha());
							
						mColors.put(lt.getRed());
						mColors.put(lt.getGreen());
						mColors.put(lt.getBlue());
						mColors.put(lt.getAlpha());
							
						mColors.put(lb.getRed());
						mColors.put(lb.getGreen());
						mColors.put(lb.getBlue());
						mColors.put(lb.getAlpha());
							
						mColors.put(rb.getRed());
						mColors.put(rb.getGreen());
						mColors.put(rb.getBlue());
						mColors.put(rb.getAlpha());
					}
					
					// Texture Handle
					mTextures.put(layer.mId);
					mTextures.put(layer.mId);
					mTextures.put(layer.mId);
					mTextures.put(layer.mId);
					mTextures.put(layer.mId);
					mTextures.put(layer.mId);
					
					if(mFontMap.mAttributes.isLinear())
						x += layer.mMaxWidth;
					else
						x += layer.mCharactersWidths[index];
					
					x += padd.x;
				} else {
					
					// Colors
					for(int i=0; i<4; i++) {
						mElements.put(0);
						mElements.put(0);
						mElements.put(0);
						mElements.put(0);
						mColors.put(0);
						mColors.put(0);
						mColors.put(0);
						mColors.put(0);
						mTextures.put(0);
					}
				}
				
				characterIndex++;
			}
		}
		
		// Finish
		mElements.position(0);
		mColors.position(0);
		mTextures.position(0);
	}
}
