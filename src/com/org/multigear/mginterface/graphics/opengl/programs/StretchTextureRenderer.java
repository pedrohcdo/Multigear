package com.org.multigear.mginterface.graphics.opengl.programs;

import java.nio.FloatBuffer;

import com.org.multigear.general.utils.GeneralUtils;
import com.org.multigear.general.utils.Vector2;
import com.org.multigear.general.utils.buffers.GlobalFloatBuffer;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;

/**
 * Simple Renderer Program
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class StretchTextureRenderer extends BaseProgram {
	
	// Private Variables
	private int mElementVerticesHandle;
	private int mTextureVerticesHandle;
	private int mProjectionMatrixHandle;
	private int mTextureSampleHandle;
	private int mBlendColorHandle;
	
	// Default Buffers
	final private float[] mOrthoMatrix = new float[16];
	final private float[] mProjectionMatrix = new float[16];
	final private float[] mIdentityMatrix = new float[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
	final private FloatBuffer mDefaultElementsBuffer = GeneralUtils.createFloatBuffer(8);
	final private FloatBuffer mDefaultTextureBuffer = GeneralUtils.createFloatBuffer(8);
	
	private FloatBuffer mElementsBuffer;// = GeneralUtils.createFloatBuffer(10000);
	private FloatBuffer mTextureBuffer;// = GeneralUtils.createFloatBuffer(10000);
	
	// Set Buffers
	{
		GeneralUtils.putFloatBuffer(mDefaultElementsBuffer, new float[] { 0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f });
		GeneralUtils.putFloatBuffer(mDefaultTextureBuffer, new float[] { 0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f });
	}
	
	/**
	 * Load Vertex Shader
	 */
	@Override
	final protected String onLoadVertexShader() {
		return "uniform mat4 uProjectionMatrix;" + 
				"attribute vec4 aElementVertices;" +
				"attribute vec2 aTextureVertices;" + 
				"varying vec2 vTextureCoord;" +
				"void main() {" + 
					"gl_Position = uProjectionMatrix * aElementVertices;" +
					"vTextureCoord = aTextureVertices;" + 
				"}";
	}
	
	/**
	 * Load Fragment Shader
	 */
	@Override
	final protected String onLoadFragmentShader() {
		return "precision highp float;" + 
				"varying vec2 vTextureCoord;" +
				"uniform sampler2D uTextureSample;" +
				"uniform vec4 uBlendColor;" +
				"void main() {" +
					"gl_FragColor = texture2D(uTextureSample, vTextureCoord) * uBlendColor;" + 
				"}";
	}
	
	/**
	 * Setup Program
	 */
	@Override
	final protected void onSetup(final Vector2 screenSize) {
		mElementVerticesHandle = GLES20.glGetAttribLocation(getHandle(), "aElementVertices");
		mTextureVerticesHandle = GLES20.glGetAttribLocation(getHandle(), "aTextureVertices");
		mProjectionMatrixHandle = GLES20.glGetUniformLocation(getHandle(), "uProjectionMatrix");
		mTextureSampleHandle = GLES20.glGetUniformLocation(getHandle(), "uTextureSample");
		mBlendColorHandle = GLES20.glGetUniformLocation(getHandle(), "uBlendColor");
		Matrix.orthoM(mOrthoMatrix, 0, 0.0f, (float) screenSize.x, (float) screenSize.y, 0.0f, 0.0f, 1.0f);
		Matrix.multiplyMM(mProjectionMatrix, 0, mOrthoMatrix, 0, mIdentityMatrix, 0);
		
		// Setup default buffers
		GLES20.glVertexAttribPointer(mElementVerticesHandle, 2, GLES20.GL_FLOAT, false, 8, mDefaultElementsBuffer);
		GLES20.glVertexAttribPointer(mTextureVerticesHandle, 2, GLES20.GL_FLOAT, false, 8, mDefaultTextureBuffer);
		
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
	 * Set Attributes Buffers
	 * 
	 * @param elementsBuffer
	 * @param textureBuffer
	 */
	final public void setBuffers(final float[] elementsBuffer, final float[] textureBuffer) {
		// Obtain Buffers
		mElementsBuffer = GlobalFloatBuffer.obtain(elementsBuffer);
		mTextureBuffer = GlobalFloatBuffer.obtain(textureBuffer);
		// Set Attributes
		GLES20.glVertexAttribPointer(mElementVerticesHandle, 2, GLES20.GL_FLOAT, false, 0, mElementsBuffer);
		GLES20.glVertexAttribPointer(mTextureVerticesHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
		// Release Buffers
		GlobalFloatBuffer.release(mElementsBuffer);
		GlobalFloatBuffer.release(mTextureBuffer);
	}
	
	/**
	 * Set Attributes Buffers
	 * 
	 * @param elementsBuffer
	 * @param textureBuffer
	 */
	final public void setBuffers(final FloatBuffer elementsBuffer, final FloatBuffer textureBuffer) {
		// Set Attributes
		GLES20.glVertexAttribPointer(mElementVerticesHandle, 2, GLES20.GL_FLOAT, false, 0, elementsBuffer);
		GLES20.glVertexAttribPointer(mTextureVerticesHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
	}
	
	/**
	 * Set Default Buffers
	 */
	final public void setDefaultBuffers() {
		GLES20.glVertexAttribPointer(mElementVerticesHandle, 2, GLES20.GL_FLOAT, false, 0, mDefaultElementsBuffer);
		GLES20.glVertexAttribPointer(mTextureVerticesHandle, 2, GLES20.GL_FLOAT, false, 0, mDefaultTextureBuffer);
	}
	
	/**
	 * Render with this program.<br>
	 * You will not be possible to render with this program while another is
	 * being used.
	 */
	final public void render() {
		GLES20.glEnableVertexAttribArray(mElementVerticesHandle);
		GLES20.glEnableVertexAttribArray(mTextureVerticesHandle);
		// Set Texture
		GLES20.glUniform1i(mTextureSampleHandle, 0);
		// Draw Triangles
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
		// Disable Attributes
		GLES20.glDisableVertexAttribArray(mElementVerticesHandle);
		GLES20.glDisableVertexAttribArray(mTextureVerticesHandle);
	}
	
	/**
	 * Render with this program.<br>
	 * You will not be possible to render with this program while another is
	 * being used.
	 */
	final public void renderTriangles(int size) {
		// Enable Attributes
		GLES20.glEnableVertexAttribArray(mElementVerticesHandle);
		GLES20.glEnableVertexAttribArray(mTextureVerticesHandle);
		// Initialize Uniform Locations
		GLES20.glUniform1i(mTextureSampleHandle, 0);
		// Draw Triangles
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, size);
		// Disable Attributes
		GLES20.glDisableVertexAttribArray(mElementVerticesHandle);
		GLES20.glDisableVertexAttribArray(mTextureVerticesHandle);
	}
	
	/**
	 * Render with this program.<br>
	 * You will not be possible to render with this program while another is
	 * being used.
	 */
	final public void renderLinear(int size) {
		// Enable Attributes
		GLES20.glEnableVertexAttribArray(mElementVerticesHandle);
		GLES20.glEnableVertexAttribArray(mTextureVerticesHandle);
		// Initialize Uniform Locations
		GLES20.glUniform1i(mTextureSampleHandle, 0);
		// Draw Triangles
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, size);
		// Disable Attributes
		GLES20.glDisableVertexAttribArray(mElementVerticesHandle);
		GLES20.glDisableVertexAttribArray(mTextureVerticesHandle);
	}
	
	/**
	 * Render with this program.<br>
	 * You will not be possible to render with this program while another is
	 * being used.
	 */
	final public void renderTriangleFan(int size) {
		// Enable Attributes
		GLES20.glEnableVertexAttribArray(mElementVerticesHandle);
		GLES20.glEnableVertexAttribArray(mTextureVerticesHandle);
		// Initialize Uniform Locations
		GLES20.glUniform1i(mTextureSampleHandle, 0);
		// Draw Triangles
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, size);
		// Disable Attributes
		GLES20.glDisableVertexAttribArray(mElementVerticesHandle);
		GLES20.glDisableVertexAttribArray(mTextureVerticesHandle);
	}
	
	/**
	 * Unused Program
	 */
	@Override
	protected void onUnused() {
		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mElementVerticesHandle);
		GLES20.glDisableVertexAttribArray(mTextureVerticesHandle);
	}
}
