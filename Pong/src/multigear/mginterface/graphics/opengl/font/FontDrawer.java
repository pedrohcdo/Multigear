package multigear.mginterface.graphics.opengl.font;

import multigear.general.utils.Color;
import multigear.general.utils.Ref2F;
import multigear.mginterface.graphics.opengl.drawer.TextureContainer;
import multigear.mginterface.graphics.opengl.drawer.TextureContainer.BltGroup;
import multigear.mginterface.graphics.opengl.texture.Texture;
import android.graphics.RectF;
import android.opengl.GLES20;

final public class FontDrawer {
	
	// Final Private Variables
	final private FontMap mFontMap;

	// Private Variables
	private BltGroup mBltGorup;
	private TextureContainer mTextureContainer;
	
	private Texture mActivedTexture = null;
	private FontMap.Style mActivedStyle = null;
	
	/**
	 * Constructor
	 */
	protected FontDrawer(final FontMap fontMap) {
		mFontMap = fontMap;
		mBltGorup = new BltGroup();
	}
	
	/**
	 * Set Texture Container
	 * @param textureContainer
	 */
	protected void setTextureContainer(final TextureContainer textureContainer) {
		mTextureContainer = textureContainer;
	}

	/**
	 * Get Font Map
	 * @return
	 */
	final public FontMap getFontMap() {
		return mFontMap;
	}
	
	/**
	 * Begin Draw
	 */
	final protected void begin() {
		prepareSource();
		mBltGorup.clear();
	}
	
	/**
	 * Prepare Source
	 */
	final protected void prepareSource() {
		mActivedStyle = mFontMap.mStyle;
		mActivedTexture = mFontMap.getTexture();
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mActivedTexture.getHandle());
	}
	
	/**
	 * End draw
	 */
	final protected void end() {
		mTextureContainer.bltUnsized(mBltGorup, mActivedTexture.getSize());
		mBltGorup.clear();
	}
	
	/**
	 * Set Color
	 */
	final public void setColor(final Color color) {
		end();
		mTextureContainer.setBlendColor(color);
	}
	
	/**
	 * Draw Text
	 * @param text
	 */
	final public void drawText(final String text, Ref2F position) {
		
		// Swap buffers if switched style
		if(mActivedStyle != mFontMap.mStyle) {
			end();
			prepareSource();
		}
		
		//
		final FontMap.Layer layer = mFontMap.getActiveLayer();
		
		
		char chars[] = new char[text.length()];
		text.getChars(0, text.length(), chars, 0);
		
		Ref2F padd = mFontMap.getAttributes().getPadd();
		float scale = layer.mScale;
		
		float x = padd.XAxis + position.XAxis;
		float y = padd.YAxis + position.YAxis;
		
		if(mFontMap.getAttributes().isUseMetrics())
			y += mFontMap.getMetrics().getAscent() * scale;
			
		Ref2F textureSize = mActivedTexture.getSize();
		int maxTextX = (int)textureSize.XAxis / (int)layer.mMaxBoundedWidth;
		float maxBoundedWidth2 = (layer.mMaxBoundedWidth / 2.0f);
		
		for(char c : chars) {
			if(mFontMap.mCharMap.mCharacters[c]) {
				int index = mFontMap.mCharMap.mCharactersIndexes[c];
				float tX = (index % maxTextX) * layer.mMaxBoundedWidth + maxBoundedWidth2; // centered;
				int tY = (index / maxTextX) * layer.mMaxBoundedHeight;
				
				final Ref2F bounds = layer.mCharactersBounds[index];
				final float center = (int)(bounds.YAxis - bounds.XAxis) / 2.0f;
				
				RectF src = new RectF((tX - center) - 1, tY, tX + center + 1, tY + layer.mMaxBoundedHeight);
				RectF dst = new RectF(x + bounds.XAxis * scale, y, x + bounds.YAxis * scale, y + layer.mMaxHeight);
				
				mBltGorup.blt(src, dst);
				
				if(mFontMap.mAttributes.isLinear())
					x += layer.mMaxWidth;
				else
					x += layer.mCharactersWidths[index];
				
				x += padd.XAxis;
			}
		}
	}
}
