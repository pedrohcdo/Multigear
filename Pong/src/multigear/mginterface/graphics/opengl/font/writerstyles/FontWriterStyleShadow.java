package multigear.mginterface.graphics.opengl.font.writerstyles;

import multigear.general.utils.Color;
import multigear.general.utils.GeneralUtils;
import multigear.general.utils.Ref2F;
import multigear.mginterface.graphics.opengl.font.FontDrawer;
import multigear.mginterface.graphics.opengl.font.FontWriter;


/**
 * Font Style Shadow
 * 
 * @author user
 *
 */
public class FontWriterStyleShadow implements FontWriter {
	
	// Final Private Variables
	final private Ref2F mProjection;
	final private Color mShadowColor;
	
	/**
	 * Constructor
	 * 
	 * @param angle Angle of Shadow
	 * @param distance Distance from the text until the shadow
	 */
	public FontWriterStyleShadow(final float angle, final float distance) {
		final double rad = GeneralUtils.degreeToRad(angle);
		final float x = (float)Math.cos(rad) * distance;
		final float y = (float)Math.sin(rad) * distance;
		mProjection = new Ref2F(x, y);
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
		final double rad = GeneralUtils.degreeToRad(angle);
		final float x = (float)Math.cos(rad) * distance;
		final float y = (float)Math.sin(rad) * distance;
		mProjection = new Ref2F(x, y);
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
		fontDrawer.drawText(text, new Ref2F(0, 0));
	}
}
