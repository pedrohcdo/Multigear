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
final public class SpriteParticlesRenderer extends BaseProgram {
	
	// Private Variables
	private int mParticleHandleA, mParticleHandleB;
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
				"attribute vec4 aParticleA;" + 
				"attribute vec4 aParticleB;" + 
				"varying float vOpacity;" +
				"uniform float uScale;" +
				"varying vec2 vTextureCoord;" +
				
				"void main() {" + 
					"float x = aParticleA.x * uScale;" +
					"float y = aParticleA.y * uScale;" +
					"float r = radians(aParticleB.w);" +
					"float c = cos(r);" +
					"float s = sin(r);" +
					"float new_x = (x * c - y * s) + aParticleA.z;" +
					"float new_y = (x * s + y * c) + aParticleA.w;" +
					"gl_Position = uProjectionMatrix * vec4(new_x, new_y, 0.0, 1.0);" +
					"vTextureCoord = vec2(aParticleB.x, aParticleB.y);" +
					"vOpacity = aParticleB.z;" +
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
				"varying vec2 vTextureCoord;" +
				
				"void main() {" +
					"gl_FragColor = texture2D(uTextureSample, vTextureCoord) * vec4(vOpacity, vOpacity, vOpacity, vOpacity) * uBlendColor;" + 
				"}";
	}
	
	/**
	 * Setup Program
	 */
	@Override
	final protected void onSetup(final Vector2 screenSize) {
		mParticleHandleA = GLES20.glGetAttribLocation(getHandle(), "aParticleA");
		mParticleHandleB = GLES20.glGetAttribLocation(getHandle(), "aParticleB");
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
		int lastPos = particleBuffer.position();
		GLES20.glVertexAttribPointer(mParticleHandleA, 4, GLES20.GL_FLOAT, false, 8*4, particleBuffer);
		particleBuffer.position(4);
		GLES20.glVertexAttribPointer(mParticleHandleB, 4, GLES20.GL_FLOAT, false, 8*4, particleBuffer);
		particleBuffer.position(lastPos);
	}
	
	/**
	 * Render with this program.<br>
	 * You will not be possible to render with this program while another is
	 * being used.
	 */
	final public void render(final int count) {
		// Enable Attributes
		GLES20.glEnableVertexAttribArray(mParticleHandleA);
		GLES20.glEnableVertexAttribArray(mParticleHandleB);
		// Initialize Uniform Locations
		GLES20.glUniform1i(mTextureSampleHandle, 0);
		// Draw Triangles
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, count);
		// Disable Attributes
		GLES20.glDisableVertexAttribArray(mParticleHandleA);
		GLES20.glDisableVertexAttribArray(mParticleHandleB);
	}
	
	/**
	 * Unused Program
	 */
	@Override
	protected void onUnused() {
		GLES20.glDisableVertexAttribArray(mParticleHandleA);
		GLES20.glDisableVertexAttribArray(mParticleHandleB);
	}
}
