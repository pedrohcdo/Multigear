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
final public class OptimizedEllipseTexturedRenderer extends BaseProgram {
	
	// Private Variables
	private int mElementVerticesHandle;
	private int mTextureVerticesHandle;
	private int mSidesVerticesHandle;
	private int mTextureSampleHandle;
	private int mProjectionMatrixHandle;
	private int mDiameterHandle;
	private int mColorHandle;
	
	// Default Buffers
	final private float[] mOrthoMatrix = new float[16];
	final private float[] mProjectionMatrix = new float[16];
	final private float[] mIdentityMatrix = new float[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
	
	final private FloatBuffer mVertexBuffer = GeneralUtils.createFloatBuffer(new float[] {0, 0, 1, 0, 1, 1, 0, 1});
	final private FloatBuffer mSidesBuffer = GeneralUtils.createFloatBuffer(new float[] {0, 0, 1, 0, 1, 1, 0, 1});
	
	/**
	 * Load Vertex Shader
	 */
	@Override
	final protected String onLoadVertexShader() {
		return "uniform mat4 uProjectionMatrix;" + 
				"uniform vec2 uDiameter;" +
				"attribute vec4 aElementVertices;" + 
				"attribute vec2 aTextureVertices;" + 
				"attribute vec2 aSidesVertices;" +
				"varying vec2 vPosition;" +
				"varying vec2 vTextureCoord;" +
				"void main() {" + 
					"vec4 newVertice = aElementVertices * vec4(uDiameter.xy, 0, 1.0);" +
					"gl_Position = uProjectionMatrix * newVertice;" +
					"vPosition = aSidesVertices;" + 
					"vTextureCoord = aTextureVertices;" +
				"}";
	}
	
	/**
	 * Load Fragment Shader
	 */
	@Override
	final protected String onLoadFragmentShader() {
		return "precision highp float;" +
				"uniform sampler2D uTextureSample;" +
				"uniform vec4 uColor;" +
				"varying vec2 vPosition;" +
				"varying vec2 vTextureCoord;" +
				"void main() {" +
					"vec2 centered = vPosition - vec2(0.5, 0.5);" +
					"float dx = centered.x;" +
					"float dy = centered.y;" +
					"float d = sqrt(dx * dx + dy * dy);" +
					"if(d < 0.5) {" +
						"gl_FragColor = texture2D(uTextureSample, vTextureCoord) * uColor;" +
					"} else {" +
						"gl_FragColor = vec4(0);" +
					"}" +
				"}";
	}
	
	/**
	 * Setup Program
	 */
	@Override
	final protected void onSetup(final Vector2 screenSize) {
		mElementVerticesHandle = GLES20.glGetAttribLocation(getHandle(), "aElementVertices");
		mTextureVerticesHandle = GLES20.glGetAttribLocation(getHandle(), "aTextureVertices");
		mSidesVerticesHandle = GLES20.glGetAttribLocation(getHandle(), "aSidesVertices");
		mTextureSampleHandle = GLES20.glGetUniformLocation(getHandle(), "uTextureSample");
		mProjectionMatrixHandle = GLES20.glGetUniformLocation(getHandle(), "uProjectionMatrix");
		mDiameterHandle = GLES20.glGetUniformLocation(getHandle(), "uDiameter");
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
	final public void setBuffers(final FloatBuffer textureVertex) {
		GLES20.glVertexAttribPointer(mElementVerticesHandle, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
		GLES20.glVertexAttribPointer(mTextureVerticesHandle, 2, GLES20.GL_FLOAT, false, 0, textureVertex);
		GLES20.glVertexAttribPointer(mSidesVerticesHandle, 2, GLES20.GL_FLOAT, false, 0, mSidesBuffer);
	}
	
	/**
	 * Set Circle Radius
	 * @param radius Radius
	 */
	final public void setRadius(final float radiusX, final float radiusY) {
		GLES20.glUniform2f(mDiameterHandle, radiusX * 2, radiusY * 2);
	}
	
	/**
	 * Render with this program.<br>
	 * You will not be possible to render with this program while another is
	 * being used.
	 */
	final public void render() {
		// Enable Attributes
		GLES20.glEnableVertexAttribArray(mElementVerticesHandle);
		GLES20.glEnableVertexAttribArray(mTextureVerticesHandle);
		GLES20.glEnableVertexAttribArray(mSidesVerticesHandle);
		// Set Texture
		GLES20.glUniform1i(mTextureSampleHandle, 0);
		// Draw Triangles
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
		// Disable Attributes
		GLES20.glDisableVertexAttribArray(mElementVerticesHandle);
		GLES20.glDisableVertexAttribArray(mTextureVerticesHandle);
		GLES20.glDisableVertexAttribArray(mSidesVerticesHandle);
	}
	
	/**
	 * Unused Program
	 */
	@Override
	protected void onUnused() {
		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mElementVerticesHandle);
		GLES20.glDisableVertexAttribArray(mTextureVerticesHandle);
		GLES20.glDisableVertexAttribArray(mSidesVerticesHandle);
	}
}
