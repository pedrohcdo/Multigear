package com.org.multigear.mginterface.graphics.opengl;

import android.opengl.GLES20;

/**
 * Used to define the drawing blend mode in Drawer.
 * @author user
 *
 */
public enum BlendFunc {
	
	// Conts
	ZERO(GLES20.GL_ZERO),
	ONE(GLES20.GL_ONE),
	SRC_COLOR(GLES20.GL_SRC_COLOR),
	ONE_MINUS_SRC_COLOR(GLES20.GL_ONE_MINUS_SRC_COLOR),
	DST_COLOR(GLES20.GL_DST_COLOR),
	ONE_MINUS_DST_COLOR(GLES20.GL_ONE_MINUS_DST_COLOR),
	SRC_ALPHA(GLES20.GL_SRC_ALPHA),
	ONE_MINUS_SRC_ALPHA(GLES20.GL_ONE_MINUS_SRC_ALPHA),
	DST_ALPHA(GLES20.GL_DST_ALPHA),
	ONE_MINUS_DST_ALPHA(GLES20.GL_ONE_MINUS_DST_ALPHA),
	CONSTANT_COLOR(GLES20.GL_CONSTANT_COLOR),
	ONE_MINUS_CONSTANT_COLOR(GLES20.GL_ONE_MINUS_CONSTANT_COLOR),
	CONSTANT_ALPHA(GLES20.GL_CONSTANT_ALPHA),
	ONE_MINUS_CONSTANT_ALPHA(GLES20.GL_ONE_MINUS_CONSTANT_ALPHA),
	SRC_ALPHA_SATURATE(GLES20.GL_SRC_ALPHA_SATURATE);
	
	// Final Private Variable
	final private int mConst;
	
	/**
	 * Constructor
	 */
	private BlendFunc(final int c) {
		mConst = c;
	}
	
	/**
	 * Get Const
	 */
	final public int getConst() {
		return mConst;
	}
}
