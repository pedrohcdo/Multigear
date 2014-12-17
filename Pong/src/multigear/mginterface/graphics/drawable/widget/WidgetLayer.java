package multigear.mginterface.graphics.drawable.widget;

import multigear.mginterface.graphics.opengl.drawer.Drawer;

/**
 * WidgetLayer
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class WidgetLayer {
	
	// Private Variables
	private int mZ = 0;
	private int mID = 0;
	
	/**
	 * Set Z.
	 * 
	 * @param z
	 *            Int Z
	 */
	final public void setZ(final int z) {
		mZ = z;
	}
	
	/**
	 * Set ID.
	 * 
	 * @param id
	 *            Int ID
	 */
	final public void setID(final int id) {
		mID = id;
	}
	/**
	 * Get Z.
	 * 
	 * @return Int Z
	 */
	final public int getZ() {
		return mZ;
	}
	
	/**
	 * Get ID.
	 * 
	 * @return Int ID
	 */
	final public int getID() {
		return mID;
	}
	
	/**
	 * Set Matrix Transformations for this Layer
	 * <p>
	 * 
	 * @param matrixRow
	 *            MatrixRow
	 * @return True if need Draw
	 */
	protected boolean beginDraw(final float preOpacity, final Drawer drawer) {
		return false;
	}
	
	/*
	 * Atualiza e Desenha
	 */
	protected void endDraw(final Drawer drawer) {
	}
}
