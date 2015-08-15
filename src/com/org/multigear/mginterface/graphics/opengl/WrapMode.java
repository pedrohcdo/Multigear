package com.org.multigear.mginterface.graphics.opengl;

import android.opengl.GLES20;

/**
 * Wrap Mode
 * 
 * @author user
 *
 */
public enum WrapMode {
	
	// Conts
	CLAMP_TO_EDGE(GLES20.GL_CLAMP_TO_EDGE), 
	MIRRORED_REPEAT(GLES20.GL_MIRRORED_REPEAT),
	REPEAT(GLES20.GL_REPEAT);


	// Final Private Variable
	final private int mConst;

	/**
	 * Constructor
	 */
	private WrapMode(final int c) {
		mConst = c;
	}

	/**
	 * Get Const
	 */
	final public int getConst() {
		return mConst;
	}
}
