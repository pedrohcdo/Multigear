package multigear.mginterface.graphics.opengl.programs;

import java.nio.FloatBuffer;

import multigear.general.utils.Vector2;
import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * Simple Renderer Program
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class UniformColorRenderer extends BaseProgram {
	
	// Private Variables
	private int mElementVerticesHandle;
	private int mProjectionMatrixHandle;
	private int mColorHandle;
	
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
				"attribute vec4 aElementVertices;" + 
				"void main() {" + 
					"gl_Position = uProjectionMatrix * aElementVertices;" +
				"}";
	}
	
	/**
	 * Load Fragment Shader
	 */
	@Override
	final protected String onLoadFragmentShader() {
		return "precision highp float;" + 
				"uniform vec4 uColor;" +
				"void main() {" +
					"gl_FragColor = uColor;" + 
				"}";
	}
	
	/**
	 * Setup Program
	 */
	@Override
	final protected void onSetup(final Vector2 screenSize) {
		mElementVerticesHandle = GLES20.glGetAttribLocation(getHandle(), "aElementVertices");
		mProjectionMatrixHandle = GLES20.glGetUniformLocation(getHandle(), "uProjectionMatrix");
		mColorHandle = GLES20.glGetUniformLocation(getHandle(), "uColor");
		Matrix.orthoM(mOrthoMatrix, 0, 0.0f, (float) screenSize.x, (float) screenSize.y, 0.0f, 0.0f, 1.0f);
		Matrix.multiplyMM(mProjectionMatrix, 0, mOrthoMatrix, 0, mIdentityMatrix, 0);
	}
	
	/**
	 * Prepare to Draw call
	 */
	@Override
	public void onPrepare(float[] transformMatrix, float[] color) {
		Matrix.multiplyMM(mProjectionMatrix, 0, mOrthoMatrix, 0, transformMatrix, 0);
		GLES20.glUniformMatrix4fv(mProjectionMatrixHandle, 1, false, mProjectionMatrix, 0);
		GLES20.glUniform4f(mColorHandle, color[0], color[1], color[2], color[3]);
	}
	
	/**
	 * Set Attributes Buffers
	 * 
	 * @param elementsBuffer
	 * @param textureBuffer
	 */
	final public void setBuffers(final FloatBuffer elementsBuffer) {
		GLES20.glVertexAttribPointer(mElementVerticesHandle, 2, GLES20.GL_FLOAT, false, 0, elementsBuffer);
	}
	
	/**
	 * Render with this program.<br>
	 * You will not be possible to render with this program while another is
	 * being used.
	 */
	final public void render(int verticesCount) {
		// Enable Attributes
		GLES20.glEnableVertexAttribArray(mElementVerticesHandle);
		// Draw Triangles
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, verticesCount);
		// Disable Attributes
		GLES20.glDisableVertexAttribArray(mElementVerticesHandle);
	}
	
	/**
	 * Unused Program
	 */
	@Override
	protected void onUnused() {
		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mElementVerticesHandle);
	}
}
