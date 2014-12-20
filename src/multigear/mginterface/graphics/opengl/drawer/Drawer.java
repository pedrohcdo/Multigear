package multigear.mginterface.graphics.opengl.drawer;

import java.nio.FloatBuffer;

import multigear.general.utils.Color;
import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.opengl.Renderer;
import multigear.mginterface.graphics.opengl.font.FontMap;
import multigear.mginterface.graphics.opengl.font.FontWrapper;
import multigear.mginterface.graphics.opengl.font.FontWriter;
import multigear.mginterface.graphics.opengl.programs.BaseProgram;
import multigear.mginterface.graphics.opengl.programs.ParticlesTextureRenderer;
import multigear.mginterface.graphics.opengl.texture.Texture;
import multigear.mginterface.scene.Scene;
import android.opengl.GLES20;

/**
 * Utilizado para desenhar uma textura.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class Drawer {
	
	// Private Variables
	final private TextureContainer mTextureContainer;
	final private MatrixRow mMatrixRow;
	final private TransformMatrix mTransformationMatrix;
	final private float[] mTransformMatrix;
	final private Renderer mRenderer;
	final private Scene mMainRoom;
	
	// Private Variables
	private float mFinalOpacity;
	
	/*
	 * Construtor
	 */
	public Drawer(final Scene room, final Renderer renderer) {
		mMainRoom = room;
		mTextureContainer = new TextureContainer(this);
		mMatrixRow = new MatrixRow(10);
		mTransformationMatrix = new TransformMatrix(mMatrixRow);
		mTransformMatrix = new float[16];
		mFinalOpacity = 1;
		mRenderer = renderer;
	}
	
	/**
	 * Get Screen Size
	 * 
	 * @return
	 */
	final protected Scene getMainRom() {
		return mMainRoom;
	}
	
	/**
	 * Get Matrix Row
	 * 
	 * @return
	 */
	final public MatrixRow getMatrixRow() {
		return mMatrixRow;
	}
	
	/**
	 * Create Transform Matrix.
	 * 
	 * @return Return Transform Matrix
	 */
	final public TransformMatrix getTransformMatrix() {
		return mTransformationMatrix;
	}
	
	/**
	 * Draw Texture
	 * 
	 * @param designedDrawInfo
	 *            Drawer Information for GLES20 draw.
	 */
	public void drawTexture(final multigear.mginterface.graphics.opengl.texture.Texture texture, final Vector2 recipientSize, final float finalOpacity) {
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getHandle());
		mMatrixRow.copyValues(mTransformMatrix);
		
		// Get real recipient Size
		final float a = mTransformMatrix[0];
		final float b = mTransformMatrix[4];
		final float c = mTransformMatrix[1];
		final float d = mTransformMatrix[5];
		Vector2 size = new Vector2((float) Math.hypot(a, c), (float) Math.hypot(b, d));
		// Prepare Map
		mFinalOpacity = finalOpacity;
		mTextureContainer.prepare(texture, size);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		texture.getMapper().onMap(mTextureContainer);
	}
	
	/**
	 * Draw Particles
	 * 
	 * @param designedDrawInfo
	 *            Drawer Information for GLES20 draw.
	 */
	public void drawParticles(final Texture texture, final int particlesCount, final FloatBuffer vertexBuffer, final FloatBuffer opacityBuffer, final FloatBuffer scaleBuffer) {
		// Prepare Texture
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getHandle());
		
		
		// Set Blend Func
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	
		// Swap Buffers
		ParticlesTextureRenderer renderer = (ParticlesTextureRenderer)begin(Renderer.PARTICLES_RENDERER, Color.TRANSPARENT);
		renderer.setBuffers(vertexBuffer, opacityBuffer, scaleBuffer);
		renderer.render(particlesCount);
	}
	
	/**
	 * Draw Text
	 * 
	 * @param designedDrawInfo
	 *            Drawer Information for GLES20 draw.
	 */
	public void drawText(final FontMap fontMap, final FontWriter fontWriter, final String text, final float finalOpacity) {
		// Active principal texture
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		// 
		mMatrixRow.copyValues(mTransformMatrix);

		// Prepare Map
		mFinalOpacity = finalOpacity;
		
		// Set Blend Func
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		// Process Writer
		FontWrapper.processWriter(fontMap, fontWriter, text, mTextureContainer);
	}
	
	/**
	 * Begin Drawer
	 * <p>
	 * 
	 * @param rendererMode
	 *            Renderer Mode
	 */
	final public BaseProgram begin(final int rendererProgram, final Color color) {
		final BaseProgram baseProgram = mRenderer.useRenderer(rendererProgram);
		final float colorAlpha = color.getAlpha();
		final float[] renderOpacity = new float[] { mFinalOpacity * color.getRed() * colorAlpha, mFinalOpacity * color.getGreen() * colorAlpha, mFinalOpacity * color.getBlue() * colorAlpha, mFinalOpacity * colorAlpha};
		baseProgram.onPrepare(mTransformMatrix, renderOpacity);
		return baseProgram;
	}
}
