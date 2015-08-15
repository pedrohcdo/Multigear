package com.org.multigear.mginterface.graphics.opengl.programs;

import java.nio.FloatBuffer;

import com.org.multigear.general.utils.GeneralUtils;
import com.org.multigear.general.utils.Vector2;

import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * Simple Renderer Program
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class PointParticlesRenderer extends BaseProgram {
	
	// Private Variables
	private int mParticleHandle;
	private int mProjectionMatrixHandle;
	private int mScaleHandle;
	private int mBlendColorHandle;
	private int mTextureSampleHandle;
	
	// Default Buffers
	final private float[] mOrthoMatrix = new float[16];
	final private float[] mProjectionMatrix = new float[16];
	final private float[] mIdentityMatrix = new float[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
	
	/**
	 * Load Vertex Shader
	 */
	@Override
	final protected String onLoadVertexShader() {
		return "uniform mat4 uProjectionMatrix;" + 
				"attribute vec4 aParticle;" + 
				"varying float vOpacity;" +
				"uniform float uScale;" +
				
				"void main() {" + 
					"vOpacity = aParticle.z;" +
					"gl_PointSize = aParticle.w * uScale;" +
					"gl_Position = uProjectionMatrix * vec4(aParticle.xy, 0, 1.0);" +
				"}";
	}
	
	/**
	 * Load Fragment Shader
	 */
	@Override
	final protected String onLoadFragmentShader() {
		return "precision mediump float;" + 
				"varying float vOpacity;" +
				"uniform sampler2D uTextureSample;" +
				"uniform vec4 uBlendColor;" +
				
				"void main() {" +
					"gl_FragColor = texture2D(uTextureSample, gl_PointCoord) * vec4(vOpacity);" + 
				"}";
	}
	
	/**
	 * Setup Program
	 */
	@Override
	final protected void onSetup(final Vector2 screenSize) {
		mParticleHandle = GLES20.glGetAttribLocation(getHandle(), "aParticle");
		mProjectionMatrixHandle = GLES20.glGetUniformLocation(getHandle(), "uProjectionMatrix");
		mScaleHandle = GLES20.glGetUniformLocation(getHandle(), "uScale");
		mBlendColorHandle = GLES20.glGetUniformLocation(getHandle(), "uBlendColor");
		mTextureSampleHandle = GLES20.glGetUniformLocation(getHandle(), "uTextureSample");
		
		Matrix.orthoM(mOrthoMatrix, 0, 0.0f, (float) screenSize.x, (float) screenSize.y, 0.0f, 0.0f, 1.0f);
		Matrix.multiplyMM(mProjectionMatrix, 0, mOrthoMatrix, 0, mIdentityMatrix, 0);
	}
	
	/**
	 * Prepare to Draw call
	 */
	@Override
	public void onPrepare(float[] transformMatrix, float[] blendColor) {
		Matrix.multiplyMM(mProjectionMatrix, 0, mOrthoMatrix, 0, transformMatrix, 0);
		GLES20.glUniformMatrix4fv(mProjectionMatrixHandle, 1, false, mProjectionMatrix, 0);
		GLES20.glUniform4f(mBlendColorHandle, blendColor[0], blendColor[1], blendColor[2], blendColor[3]);
	}
	
	/**
	 * Set particles Scale
	 * 
	 * @param scale
	 */
	final public void setScale(final float scale) {
		GLES20.glUniform1f(mScaleHandle, scale);
	}
	
	/**
	 * Set Attributes Buffers
	 * 
	 * @param elementsBuffer
	 * @param textureBuffer
	 */
	final public void setBuffers(final FloatBuffer particleBuffer) {
		GLES20.glVertexAttribPointer(mParticleHandle, 4, GLES20.GL_FLOAT, false, 0, particleBuffer);
	}
	
	/**
	 * Render with this program.<br>
	 * You will not be possible to render with this program while another is
	 * being used.
	 */
	final public void render(final int count) {
		// Enable Attributes
		GLES20.glEnableVertexAttribArray(mParticleHandle);
		// Initialize Uniform Locations
		GLES20.glUniform1i(mTextureSampleHandle, 0);
		// Draw Triangles
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, count);
		// Disable Attributes
		GLES20.glDisableVertexAttribArray(mParticleHandle);
	}
	
	/**
	 * Unused Program
	 */
	@Override
	protected void onUnused() {
		GLES20.glDisableVertexAttribArray(mParticleHandle);
	}
}
