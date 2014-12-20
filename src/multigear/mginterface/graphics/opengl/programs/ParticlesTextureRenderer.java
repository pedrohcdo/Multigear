package multigear.mginterface.graphics.opengl.programs;

import java.nio.FloatBuffer;

import multigear.general.utils.GeneralUtils;
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
final public class ParticlesTextureRenderer extends BaseProgram {
	
	// Private Variables
	private int mVertexHandle;
	private int mOpacityHandle;
	private int mScaleHandle;
	
	private int mProjectionMatrixHandle;
	
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
				"attribute vec4 aVertex;" + 
				"attribute float aOpacity;" + 
				"attribute float aScale;" + 
				
				"varying float vOpacity;" +
				
				"void main() {" + 
					"gl_PointSize = aScale;" +
					"vOpacity = aOpacity;" +
					"gl_Position = uProjectionMatrix * aVertex;" +
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
				
				"void main() {" +
					"gl_FragColor = texture2D(uTextureSample, gl_PointCoord) * vec4(vOpacity);" + 
				"}";
	}
	
	/**
	 * Setup Program
	 */
	@Override
	final protected void onSetup(final Vector2 screenSize) {
		mVertexHandle = GLES20.glGetAttribLocation(getHandle(), "aVertex");
		mOpacityHandle = GLES20.glGetAttribLocation(getHandle(), "aOpacity");
		mScaleHandle = GLES20.glGetAttribLocation(getHandle(), "aScale");
		
		mProjectionMatrixHandle = GLES20.glGetUniformLocation(getHandle(), "uProjectionMatrix");
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
		GLES20.glUniformMatrix4fv(mProjectionMatrixHandle, 1, false, mOrthoMatrix, 0);
	}
	
	/**
	 * Set Attributes Buffers
	 * 
	 * @param elementsBuffer
	 * @param textureBuffer
	 */
	final public void setBuffers(final FloatBuffer vertexBuffer, final FloatBuffer opacityBuffer, final FloatBuffer scaleBuffer) {
		GLES20.glVertexAttribPointer(mVertexHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
		GLES20.glVertexAttribPointer(mOpacityHandle, 1, GLES20.GL_FLOAT, false, 0, opacityBuffer);
		GLES20.glVertexAttribPointer(mScaleHandle, 1, GLES20.GL_FLOAT, false, 0, scaleBuffer);
	}
	
	/**
	 * Render with this program.<br>
	 * You will not be possible to render with this program while another is
	 * being used.
	 */
	final public void render(final int count) {
		// Enable Attributes
		GLES20.glEnableVertexAttribArray(mVertexHandle);
		GLES20.glEnableVertexAttribArray(mOpacityHandle);
		GLES20.glEnableVertexAttribArray(mScaleHandle);
		// Initialize Uniform Locations
		GLES20.glUniform1i(mTextureSampleHandle, 0);
		// Draw Triangles
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, count);
		// Disable Attributes
		GLES20.glDisableVertexAttribArray(mVertexHandle);
		GLES20.glDisableVertexAttribArray(mOpacityHandle);
		GLES20.glDisableVertexAttribArray(mScaleHandle);
	}
	
	/**
	 * Unused Program
	 */
	@Override
	protected void onUnused() {
		GLES20.glDisableVertexAttribArray(mVertexHandle);
		GLES20.glDisableVertexAttribArray(mOpacityHandle);
		GLES20.glDisableVertexAttribArray(mScaleHandle);
	}
}
