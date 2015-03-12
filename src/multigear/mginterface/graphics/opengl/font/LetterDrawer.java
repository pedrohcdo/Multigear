package multigear.mginterface.graphics.opengl.font;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import android.graphics.RectF;
import android.util.Log;

import multigear.general.utils.Color;
import multigear.general.utils.Vector2;
import multigear.general.utils.buffers.GlobalFloatBuffer;
import multigear.general.utils.buffers.GlobalIntBuffer;
import multigear.mginterface.graphics.opengl.texture.Texture;
import multigear.mginterface.graphics.opengl.vbo.VertexBufferObject;
import multigear.mginterface.graphics.opengl.vbo.VertexBufferObject.Target;
import multigear.mginterface.graphics.opengl.vbo.VertexBufferObject.Usage;

/**
 * Letter Drawer
 * 
 * @author user
 * 
 */
final public class LetterDrawer {

	/**
	 * Script
	 * 
	 * @author user
	 * 
	 */
	final private class Script {

		public FontMap.Layer layer;
		public Color color;
		public String text;
		public Vector2 position;
	}
	
	// Private Variables
	private boolean mBegin = false;
	private Color mColor = Color.WHITE;
	private List<Script> mScripts = new ArrayList<Script>();
	protected VertexBufferObject mElementsVBO, mColorsVBO, mTexturesVBO, mIndicesIBO;
	protected boolean mPrepared = false;
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
		if(mPrepared) {
			mElementsVBO.destroy();
			mColorsVBO.destroy();
			mTexturesVBO.destroy();
			mIndicesIBO.destroy();
			mPrepared = false;
		}
		mElementsCount = 0;
		mElementsVBO = null;
		mTexturesVBO = null;
		mIndicesIBO = null;
	}

	/**
	 * Begin Draw
	 */
	final protected void begin(final FontMap fontMap) {
		mFontMap = fontMap;
		mBegin = true;
		mScripts.clear();
		if(mPrepared) {
			mElementsVBO.destroy();
			mColorsVBO.destroy();
			mTexturesVBO.destroy();
			mIndicesIBO.destroy();
			mPrepared = false;
		}
		mElementsCount = 0;
		mElementsVBO = null;
		mTexturesVBO = null;
		mIndicesIBO = null;
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
		script.text = text;
		script.position = new Vector2(position.x, position.y);
		script.layer = mFontMap.getActiveLayer();
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
			vertexElementsLength += textLength * 16;
			vertexColorsLength += textLength * 16;
			vertexTextureLength += textLength * 4;
			indicesLength += textLength * 6;
		}
		
		mElementsCount = indicesLength;
		
		FloatBuffer elementsBuffer = GlobalFloatBuffer.obtain(vertexElementsLength);
		FloatBuffer colorsBuffer = GlobalFloatBuffer.obtain(vertexColorsLength);
		FloatBuffer textureBuffer = GlobalFloatBuffer.obtain(vertexTextureLength);
		IntBuffer indicesBuffer = GlobalIntBuffer.obtain(indicesLength);
		
		// Add all Scripts
		int indicesCount = 0;
		
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
					elementsBuffer.put(elementLeft);
					elementsBuffer.put(elementTop);
					elementsBuffer.put(textureLeft);
					elementsBuffer.put(textureTop);
					
					/*
					 * .   [.]
					 * 
					 * .    .
					 */
					elementsBuffer.put(elementRight);
					elementsBuffer.put(elementTop);
					elementsBuffer.put(textureRight);
					elementsBuffer.put(textureTop);
					
					/*
					 * .    .
					 * 
					 * .   [.]
					 */
					elementsBuffer.put(elementRight);
					elementsBuffer.put(elementBottom);
					elementsBuffer.put(textureRight);
					elementsBuffer.put(textureBottom);
					
					/*
					 * .    .
					 * 
					 *[.]   .
					 */
					elementsBuffer.put(elementLeft);
					elementsBuffer.put(elementBottom);
					elementsBuffer.put(textureLeft);
					elementsBuffer.put(textureBottom);
					
					// Colors
					for(int i=0; i<4; i++) {
						colorsBuffer.put(color.getRed());
						colorsBuffer.put(color.getGreen());
						colorsBuffer.put(color.getBlue());
						colorsBuffer.put(color.getAlpha());
					}
					
					// Texture Handle
					textureBuffer.put(layer.mId);
					textureBuffer.put(layer.mId);
					textureBuffer.put(layer.mId);
					textureBuffer.put(layer.mId);

					// Add Indices
					indicesBuffer.put(indicesCount);
					indicesBuffer.put(indicesCount+1);
					indicesBuffer.put(indicesCount+2);
					indicesBuffer.put(indicesCount);
					indicesBuffer.put(indicesCount+3);
					indicesBuffer.put(indicesCount+2);
					
					if(mFontMap.mAttributes.isLinear())
						x += layer.mMaxWidth;
					else
						x += layer.mCharactersWidths[index];
					
					x += padd.x;
				} else {
					// Colors
					for(int i=0; i<4; i++) {
						elementsBuffer.put(0);
						elementsBuffer.put(0);
						elementsBuffer.put(0);
						elementsBuffer.put(0);
						colorsBuffer.put(0);
						colorsBuffer.put(0);
						colorsBuffer.put(0);
						colorsBuffer.put(0);
						textureBuffer.put(0);
					}
					
					// Add Indices
					indicesBuffer.put(indicesCount);
					indicesBuffer.put(indicesCount+1);
					indicesBuffer.put(indicesCount+2);
					indicesBuffer.put(indicesCount);
					indicesBuffer.put(indicesCount+3);
					indicesBuffer.put(indicesCount+2);
				}
				
				// Move Indices
				indicesCount += 4;
			}
		}
		
		// Finish
		elementsBuffer.position(0);
		colorsBuffer.position(0);
		textureBuffer.position(0);
		indicesBuffer.position(0);
		// Create VBOs
		VertexBufferObject evbo = VertexBufferObject.create(Target.ARRAY_BUFFER, Usage.STATIC_DRAW);
		VertexBufferObject cvbo = VertexBufferObject.create(Target.ARRAY_BUFFER, Usage.STATIC_DRAW);
		VertexBufferObject tvbo = VertexBufferObject.create(Target.ARRAY_BUFFER, Usage.STATIC_DRAW);
		VertexBufferObject ibo = VertexBufferObject.create(Target.ELEMENT_ARRAY_BUFFER, Usage.STATIC_DRAW);
		// Set VBOs Buffers
		evbo.setBuffer(elementsBuffer, vertexElementsLength);
		cvbo.setBuffer(colorsBuffer, vertexColorsLength);
		tvbo.setBuffer(textureBuffer, vertexTextureLength);
		ibo.setBuffer(indicesBuffer, indicesLength);
		// Release Buffers
		GlobalFloatBuffer.release(elementsBuffer);
		GlobalFloatBuffer.release(colorsBuffer);
		GlobalFloatBuffer.release(textureBuffer);
		GlobalIntBuffer.release(indicesBuffer);
		// Set VBO
		mElementsVBO = evbo;
		mColorsVBO = cvbo;
		mTexturesVBO = tvbo;
		mIndicesIBO = ibo;
		// Prepared
		mPrepared = true;
	}
}
