package multigear.mginterface.graphics.opengl.programs;

import java.nio.FloatBuffer;

import multigear.general.utils.GeneralUtils;
import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.opengl.texture.Texture;
import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * Simple Renderer Program
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class TransitionTextureRenderer extends BaseProgram {
	
	// Private Variables
	private int mElementVerticesHandle;
	private int mTextureVerticesHandle;
	private int mProjectionMatrixHandle;
	private int mTextureSampleBegin;
	private int mTextureSampleTransition;
	private int mTextureSampleEnd;
	private int mBlendColorHandle;
	private int mTimeControlHandle;
	
	// Default Buffers
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
		return "uniform mat4 uProjectionMatrix;" + "attribute vec4 aElementVertices;" + "attribute vec2 aTextureVertices;" + "varying vec2 vTextureCoord;" + "void main() {" + "gl_Position = uProjectionMatrix * aElementVertices;" + "vTextureCoord = aTextureVertices;" + "}";
	}
	
	/**
	 * Load Fragment Shader
	 */
	@Override
	final protected String onLoadFragmentShader() {
		return "precision mediump float;" + "varying vec2 vTextureCoord;" + "uniform vec4 uBlendColor;" + "uniform float uTimeControl;" + "uniform sampler2D uTextureBegin;" + "uniform sampler2D uTextureTransition;" + "uniform sampler2D uTextureEnd;" + "void main() {" + "vec4 transitionColor = texture2D(uTextureTransition, vTextureCoord);" + "vec4 beginColor = texture2D(uTextureBegin, vTextureCoord);" + "vec4 endColor = texture2D(uTextureEnd, vTextureCoord);" + "float transitionValue = transitionColor.x * transitionColor.y * transitionColor.z * transitionColor.w;" + "if(uTimeControl > transitionValue) {" + "gl_FragColor = endColor * uBlendColor;" + "} else {" + "float delta = uTimeControl / transitionValue;" + "if(delta > 0.8) {" + "float alpha = (delta - 0.8) / 0.2;" + "vec4 mixColor = beginColor * (1.0 - alpha) + endColor * alpha;" + "gl_FragColor = mixColor * uBlendColor;" + "} else {" + "gl_FragColor = beginColor * uBlendColor;" + "}" + "}" + "}";
	}
	
	/**
	 * Setup Program
	 */
	@Override
	final protected void onSetup(final Vector2 screenSize) {
		mElementVerticesHandle = GLES20.glGetAttribLocation(getHandle(), "aElementVertices");
		mTextureVerticesHandle = GLES20.glGetAttribLocation(getHandle(), "aTextureVertices");
		mProjectionMatrixHandle = GLES20.glGetUniformLocation(getHandle(), "uProjectionMatrix");
		mTextureSampleBegin = GLES20.glGetUniformLocation(getHandle(), "uTextureBegin");
		mTextureSampleTransition = GLES20.glGetUniformLocation(getHandle(), "uTextureTransition");
		mTextureSampleEnd = GLES20.glGetUniformLocation(getHandle(), "uTextureEnd");
		mBlendColorHandle = GLES20.glGetUniformLocation(getHandle(), "uBlendColor");
		mTimeControlHandle = GLES20.glGetUniformLocation(getHandle(), "uTimeControl");
		Matrix.orthoM(mOrthoMatrix, 0, 0.0f, (float) screenSize.x, (float) screenSize.y, 0.0f, 0.0f, 1.0f);
		Matrix.multiplyMM(mProjectionMatrix, 0, mOrthoMatrix, 0, mIdentityMatrix, 0);
		// Enable Attributes
		GLES20.glEnableVertexAttribArray(mElementVerticesHandle);
		GLES20.glEnableVertexAttribArray(mTextureVerticesHandle);
		// Initialize Uniform Locations
		GLES20.glUniform1i(mTextureSampleBegin, 0);
		GLES20.glUniform1i(mTextureSampleTransition, 1);
		GLES20.glUniform1i(mTextureSampleEnd, 2);
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
	 * Set used Textues
	 * 
	 * @param transitionTexture
	 * @param finalTexture
	 */
	final public void setTextures(final Texture transitionTexture, final Texture finalTexture) {
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, transitionTexture.getHandle());
		GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, finalTexture.getHandle());
	}
	
	/**
	 * Render with this program.<br>
	 * You will not be possible to render with this program while another is
	 * being used.
	 */
	final public void render(final float time) {
		// Enable Attributes
		GLES20.glEnableVertexAttribArray(mElementVerticesHandle);
		GLES20.glEnableVertexAttribArray(mTextureVerticesHandle);
		// Initialize Uniform Control
		GLES20.glUniform1i(mTextureSampleBegin, 0);
		GLES20.glUniform1i(mTextureSampleTransition, 1);
		GLES20.glUniform1i(mTextureSampleEnd, 2);
		GLES20.glUniform1f(mTimeControlHandle, time);
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
