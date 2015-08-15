package com.org.multigear.mginterface.graphics.opengl.font.writerstyles;

import com.org.multigear.general.utils.Color;
import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.graphics.opengl.font.FontDrawer;
import com.org.multigear.mginterface.graphics.opengl.font.FontWriter;


/**
 * Font Style Shadow
 * 
 * @author user
 *
 */
public class FontWriterStyleShadow implements FontWriter {
	
	// Final Private Variables
	final private Vector2 mProjection;
	final private Color mShadowColor;
	
	/**
	 * Constructor
	 * 
	 * @param angle Angle of Shadow
	 * @param distance Distance from the text until the shadow
	 */
	public FontWriterStyleShadow(final float angle, final float distance) {
		mProjection = Vector2.rotate(new Vector2(distance, distance), angle);
		mShadowColor = Color.BLACK;
	}
	
	/**
	 * Constructor
	 * 
	 * @param angle Angle of Shadow
	 * @param distance Distance from the text until the shadow
	 * @param shadowColor Shadow Color
	 */
	public FontWriterStyleShadow(final float angle, final float distance, final Color shadowColor) {
		mProjection = Vector2.rotate(new Vector2(distance, distance), angle);
		mShadowColor = shadowColor;
	}
	
	/**
	 * Draw
	 */
	@Override
	public void onDraw(FontDrawer fontDrawer, String text) {
		fontDrawer.setColor(mShadowColor);
		fontDrawer.drawText(text, mProjection);
		fontDrawer.setColor(Color.WHITE);
		fontDrawer.drawText(text, new Vector2(0, 0));
	}
}
