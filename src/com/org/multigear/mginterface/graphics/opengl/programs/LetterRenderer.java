package com.org.multigear.mginterface.graphics.opengl.programs;

import java.nio.FloatBuffer;

import com.org.multigear.general.utils.GeneralUtils;
import com.org.multigear.general.utils.Vector2;
import com.org.multigear.general.utils.buffers.GlobalFloatBuffer;
import com.org.multigear.mginterface.graphics.opengl.vbo.VertexBufferObject;

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

/**
 * Simple Renderer Program
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class LetterRenderer extends BaseProgram {
	
	// Private Variables
	private int mElementVerticesHandle;
	private int mColorsVerticesHandle;
	private int mTextureVerticesHandle;
	
	private int mProjectionMatrixHandle;
	private int mTextureSampleHandle1;
	private int mTextureSampleHandle2;
	private int mTextureSampleHandle3;
	private int mTextureSampleHandle4;
	private int mBlendColorHandle;
	
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
				"attribute vec4 aElementVertex;" +
				"attribute vec4 aColorsVertex;" +
				"attribute float aTexturesVertex;" +
				"varying vec2 vTextureCoord;" +
				"varying float vTextureId;" +
				"varying vec4 vColor;" +
				"void main() {" + 
					"gl_Position = uProjectionMatrix * vec4(aElementVertex.xy, 0, 1);" +
					"vTextureCoord = aElementVertex.zw;" +
					"vTextureId = aTexturesVertex * 100.0;" +
					"vColor = aColorsVertex;" +
				"}";
	}
	
	/**
	 * Load Fragment Shader
	 */
	@Override
	final protected String onLoadFragmentShader() {
		return "precision highp float;" + 
				"uniform sampler2D uTextureSample1;" +
				"uniform sampler2D uTextureSample2;" +
				"uniform sampler2D uTextureSample3;" +
				"uniform sampler2D uTextureSample4;" +
				"uniform vec4 uBlendColor;" +
				"varying vec2 vTextureCoord;" +
				"varying float vTextureId;" +
				"varying vec4 vColor;" +
				"void main() {" +
					"int id = int(vTextureId);" +
					"if(id <= 50) {" +
						"gl_FragColor = texture2D(uTextureSample1, vTextureCoord) * uBlendColor * vColor;" +
					"} else if(id > 50 && id <= 150) {" +
						"gl_FragColor = texture2D(uTextureSample2, vTextureCoord) * uBlendColor * vColor;" +
					"} else if(id > 150 && id <= 250) {" +
						"gl_FragColor = texture2D(uTextureSample3, vTextureCoord) * uBlendColor * vColor;" +
					"} else {" +
						"gl_FragColor = texture2D(uTextureSample4, vTextureCoord) * uBlendColor * vColor;" + 
					"}" +
				"}";
	}
	
	/**
	 * Setup Program
	 */
	@Override
	final protected void onSetup(final Vector2 screenSize) {
		mElementVerticesHandle = GLES20.glGetAttribLocation(getHandle(), "aElementVertex");
		mColorsVerticesHandle = GLES20.glGetAttribLocation(getHandle(), "aColorsVertex");
		mTextureVerticesHandle = GLES20.glGetAttribLocation(getHandle(), "aTexturesVertex");
		
		mProjectionMatrixHandle = GLES20.glGetUniformLocation(getHandle(), "uProjectionMatrix");
		mTextureSampleHandle1 = GLES20.glGetUniformLocation(getHandle(), "uTextureSample1");
		mTextureSampleHandle2 = GLES20.glGetUniformLocation(getHandle(), "uTextureSample2");
		mTextureSampleHandle3 = GLES20.glGetUniformLocation(getHandle(), "uTextureSample3");
		mTextureSampleHandle4 = GLES20.glGetUniformLocation(getHandle(), "uTextureSample4");
		mBlendColorHandle = GLES20.glGetUniformLocation(getHandle(), "uBlendColor");
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
	 * Set Attributes Buffers
	 * 
	 * @param elementsBuffer
	 * @param textureBuffer
	 */
	final public void setBuffers(final FloatBuffer elements, final FloatBuffer colors, final FloatBuffer textures) {
		GLES20.glVertexAttribPointer(mElementVerticesHandle, 4, GLES20.GL_FLOAT, false, 0, elements);
		GLES20.glVertexAttribPointer(mColorsVerticesHandle, 4, GLES20.GL_FLOAT, false, 0, colors);
		GLES20.glVertexAttribPointer(mTextureVerticesHandle, 1, GLES20.GL_FLOAT, false, 0, textures);
	}
	
	/**
	 * Render with this program.<br>
	 * You will not be possible to render with this program while another is
	 * being used.
	 */
	@SuppressLint("NewApi")
	final public void render(final int count) {
		// Enable Vertexes
		GLES20.glEnableVertexAttribArray(mElementVerticesHandle);
		GLES20.glEnableVertexAttribArray(mColorsVerticesHandle);
		GLES20.glEnableVertexAttribArray(mTextureVerticesHandle);
		
		// Set Texture
		GLES20.glUniform1i(mTextureSampleHandle1, 0);
		GLES20.glUniform1i(mTextureSampleHandle2, 1);
		GLES20.glUniform1i(mTextureSampleHandle3, 2);
		GLES20.glUniform1i(mTextureSampleHandle4, 3);
		
		// Draw Triangles
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, count);
		
		// Disable Attributes
		GLES20.glDisableVertexAttribArray(mElementVerticesHandle);
		GLES20.glDisableVertexAttribArray(mColorsVerticesHandle);
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
