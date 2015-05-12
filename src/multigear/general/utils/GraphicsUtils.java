package multigear.general.utils;

import multigear.mginterface.graphics.drawable.polygon.Polygon;
import multigear.mginterface.graphics.drawable.sprite.FrameHolder;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.texture.Texture;
import multigear.mginterface.scene.components.receivers.Drawable;


/**
 * Graphics Utils
 * 
 * @author user
 *
 */
final public class GraphicsUtils {

	/** 
	 * Private Constructor
	 */
	private GraphicsUtils() {}
	
	/**
	 * Create Border of Simple Polygon
	 * @return
	 */
	final static public FrameHolder createBorderOfPolygon(final Polygon polygon, final float borderSize, final Color borderColor) {
		
		Vector2 frameSize = polygon.getFramedSize();
		Vector2 eraseSize = Vector2.sub(frameSize, new Vector2(borderSize * 2, borderSize * 2));
		Vector2 eraseScale = Vector2.div(eraseSize, frameSize);
		
		
		final Polygon border = new Polygon(polygon);
		final Polygon erase = new Polygon(polygon);
		
		border.setColor(borderColor);
		
		erase.setScale(eraseScale);
		erase.setPosition(new Vector2(borderSize, borderSize));
		
		
		Drawable adapter = new Drawable() {
			
			/**
			 * Implement
			 */
			@Override
			public void draw(Drawer drawer) {
				drawer.drawStencil(border);
				drawer.drawStencil(erase);
				drawer.setStencilLevel(drawer.getStencilMaxLevel() - 1);
				
				border.draw(drawer);
				
				drawer.restoreStencilLevel();
				drawer.eraseStencil(erase);
				drawer.eraseStencil(border);
			}
		};
		
		return new FrameHolder(adapter, frameSize);
	}
}
