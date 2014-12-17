package multigear.mginterface.graphics.opengl.programs;

import java.nio.FloatBuffer;

import multigear.general.utils.GeneralUtils;
import multigear.general.utils.Ref2F;
import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * Simple Renderer Program
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class RepeatTextureRenderer extends BaseProgram {
	
	// Private Variables
	private int mElementVerticesHandle;
	private int mTextureVerticesHandle;
	private int mProjectionMatrixHandle;
	private int mTextureSampleHandle;
	private int mStepHandle;
	private int mBoundsHandle;
	private int mBlendColorHandle;
	private float[] mTransformationComponents;
	private Ref2F mTransformationSize;
	
	// Final Private Variables
	final private float[] mOrthoMatrix = new float[16];
	final private float[] mProjectionMatrix = new float[16];
	final private float[] mIdentityMatrix = new float[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
	final private FloatBuffer mElementsBuffer = GeneralUtils.createFloatBuffer(8);
	final private FloatBuffer mTextureBuffer = GeneralUtils.createFloatBuffer(8);
	final private FloatBuffer mDefaultElementsBuffer = GeneralUtils.createFloatBuffer(8);
	final private FloatBuffer mDefaultTextureBuffer = GeneralUtils.createFloatBuffer(8);
	
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
			   "uniform vec2 uStep;" +
			   "uniform vec4 uBounds;" + 
			   "uniform vec4 uBlendColor;" +
			   "uniform bvec3 uAxis;" + 
			   
			   "void main() {" + 
			   		"vec2 limits = uBounds.zw - uBounds.xy;" + 
					"vec2 roll = (vTextureCoord - uBounds.xy) * uStep;" + 
					"vec2 normalized = mod(roll, limits) + uBounds.xy;" + 
					"vec4 texColor = texture2D(uTextureSample, normalized);" + 
					"gl_FragColor = texColor * uBlendColor;" +
	   			"}";
	}
	
	/**
	 * Setup Program
	 */
	@Override
	final protected void onSetup(final Ref2F screenSize) {
		// Get Handles
		mElementVerticesHandle = GLES20.glGetAttribLocation(getHandle(), "aElementVertices");
		mTextureVerticesHandle = GLES20.glGetAttribLocation(getHandle(), "aTextureVertices");
		mProjectionMatrixHandle = GLES20.glGetUniformLocation(getHandle(), "uProjectionMatrix");
		mTextureSampleHandle = GLES20.glGetUniformLocation(getHandle(), "uTextureSample");
		mStepHandle = GLES20.glGetUniformLocation(getHandle(), "uStep");
		mBoundsHandle = GLES20.glGetUniformLocation(getHandle(), "uBounds");
		mBlendColorHandle = GLES20.glGetUniformLocation(getHandle(), "uBlendColor");
		// Set Projection Matrix
		Matrix.orthoM(mOrthoMatrix, 0, 0.0f, (float) screenSize.XAxis, (float) screenSize.YAxis, 0.0f, 0.0f, 1.0f);
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
		// Project Matrix Transformation
		Matrix.multiplyMM(mProjectionMatrix, 0, mOrthoMatrix, 0, transformMatrix, 0);
		// Get Step
		final float a = transformMatrix[0];
		final float b = transformMatrix[4];
		final float c = transformMatrix[1];
		final float d = transformMatrix[5];
		mTransformationComponents = new float[] {a, c, b, d};
		mTransformationSize = new Ref2F((float)Math.hypot(a, c), (float)Math.hypot(b, d));
		// Pas Attributes
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
		// Get Transformation Components and Element Size
		final float a = mTransformationComponents[0];
		final float c = mTransformationComponents[1];
		final float b = mTransformationComponents[2];
		final float d = mTransformationComponents[3];
		final float w = elementsBuffer[4] - elementsBuffer[0];
		final float h = elementsBuffer[5] - elementsBuffer[1];
		// Set Transformation Size
		mTransformationSize = new Ref2F((float)Math.hypot(a * w, c * w), (float)Math.hypot(b * h, d * h));
		// Set Buffers
		GeneralUtils.putFloatBuffer(mElementsBuffer, elementsBuffer);
		GeneralUtils.putFloatBuffer(mTextureBuffer, textureBuffer);
		// Set Attributes
		GLES20.glVertexAttribPointer(mElementVerticesHandle, 2, GLES20.GL_FLOAT, false, 0, mElementsBuffer);
		GLES20.glVertexAttribPointer(mTextureVerticesHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
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
	final public void render(final float[] repeatBounds, final Ref2F repeatSize, final boolean horizontalRepeat, final boolean verticalRepeat) {
		// Enable Attributes
		GLES20.glEnableVertexAttribArray(mElementVerticesHandle);
		GLES20.glEnableVertexAttribArray(mTextureVerticesHandle);
		// Set Repeat Step
		final float step[] = new float[] {1, 1};
		if(horizontalRepeat)
			step[0] = (float) (mTransformationSize.XAxis / repeatSize.XAxis);
		if(verticalRepeat)
			step[1] = (float) (mTransformationSize.YAxis / repeatSize.YAxis);
		// Initialize Uniform Locations
		GLES20.glUniform1i(mTextureSampleHandle, 0);
		// Initialize Uniform Locations
		GLES20.glUniform2f(mStepHandle, step[0], step[1]);
		GLES20.glUniform4f(mBoundsHandle, repeatBounds[0], repeatBounds[1], repeatBounds[2], repeatBounds[3]);
		// Draw Triangles
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
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
