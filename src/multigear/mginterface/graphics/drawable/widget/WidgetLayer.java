package multigear.mginterface.graphics.drawable.widget;

import multigear.mginterface.graphics.opengl.drawer.Drawer;

/**
 * WidgetLayer
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public interface WidgetLayer {

	
	/**
	 * Set Z.
	 * 
	 * @param z
	 *            Int Z
	 */
	public void setZ(final int z);
	
	/**
	 * Set ID.
	 * 
	 * @param id
	 *            Int ID
	 */
	public void setId(final int id);
	
	/**
	 * Get Z.
	 * 
	 * @return Int Z
	 */
	public int getZ();
	
	/**
	 * Get ID.
	 * 
	 * @return Int ID
	 */
	public int getId();
	
	/**
	 * Set Matrix Transformations for this Layer
	 * <p>
	 * 
	 * @param matrixRow
	 *            MatrixRow
	 * @return True if need Draw
	 */
	public void draw(final float preOpacity, final Drawer drawer);
}
