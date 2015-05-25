package multigear.mginterface.graphics.opengl;

import android.opengl.GLES20;

/**
 * Used to define the drawing blend mode in Drawer.
 * @author user
 *
 */
public enum BlendEquation {
	
	// Conts
	ADD(GLES20.GL_FUNC_ADD),
	SUBTRACT(GLES20.GL_FUNC_SUBTRACT),
	REVERSE_SUBTRACT(GLES20.GL_SRC_COLOR);
	
	// Final Private Variable
	final private int mConst;
	
	/**
	 * Constructor
	 */
	private BlendEquation(final int c) {
		mConst = c;
	}
	
	/**
	 * Get Const
	 */
	final public int getConst() {
		return mConst;
	}
}
