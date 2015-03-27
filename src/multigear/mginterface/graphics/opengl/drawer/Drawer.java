package multigear.mginterface.graphics.opengl.drawer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import multigear.general.utils.Color;
import multigear.general.utils.GeneralUtils;
import multigear.general.utils.Vector2;
import multigear.general.utils.buffers.GlobalFloatBuffer;
import multigear.mginterface.graphics.drawable.polygon.Polygon;
import multigear.mginterface.graphics.opengl.Renderer;
import multigear.mginterface.graphics.opengl.font.FontMap;
import multigear.mginterface.graphics.opengl.font.FontWrapper;
import multigear.mginterface.graphics.opengl.font.FontWriter;
import multigear.mginterface.graphics.opengl.font.Letter;
import multigear.mginterface.graphics.opengl.font.LetterWrapper;
import multigear.mginterface.graphics.opengl.programs.BaseProgram;
import multigear.mginterface.graphics.opengl.programs.LetterRenderer;
import multigear.mginterface.graphics.opengl.programs.OptimizedEllipseTexturedRenderer;
import multigear.mginterface.graphics.opengl.programs.OptimizedEllipseUniformColorRenderer;
import multigear.mginterface.graphics.opengl.programs.ParticlesTextureRenderer;
import multigear.mginterface.graphics.opengl.programs.StretchTextureRenderer;
import multigear.mginterface.graphics.opengl.programs.UniformColorRenderer;
import multigear.mginterface.graphics.opengl.texture.Texture;
import multigear.mginterface.scene.Scene;
import multigear.mginterface.scene.SceneDrawerState;
import multigear.mginterface.scene.components.receivers.Drawable;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.util.Log;

/**
 * Utilizado para desenhar uma textura.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class Drawer {
	
	/**
	 * Drawing State
	 * 
	 * @author user
	 *
	 */
	final private class DrawingState {
		
		// Private Variables
		private float opacity = 1.0f;
		private float lastOpacity = 1.0f;
		private Rect snip = null;
		
		/**
		 * Get Opacity
		 * 
		 * @return
		 */
		final private float getOpacity() {
			return opacity * lastOpacity;
		}
	}
	
	// Private Variables
	final private TextureContainer mTextureContainer;
	final private WorldMatrix mMatrixRow;
	final private float[] mTransformMatrix;
	final private Renderer mRenderer;
	final private Scene mMainScene;
	
	// Private Variables
	private SceneDrawerState mSceneDrawerState;
	private Texture mTexture;
	private Color mColor = Color.WHITE;
	private FloatBuffer mElementVertex, mTextureVertex;
	private FloatBuffer mTextureVertexFilled = GeneralUtils.createFloatBuffer(new float[] {0, 0, 1, 0, 1, 1, 0, 1});
	private DrawingState[] mDrawingStates;
	private int mDrawingIndex = 0;
	private int mStencilLevel = 1;
	
	/*
	 * Construtor
	 */
	public Drawer(final Scene room, final Renderer renderer) {
		mMainScene = room;
		mTextureContainer = new TextureContainer(this, mMainScene);
		mMatrixRow = new WorldMatrix(10);
		mDrawingStates = new DrawingState[10];
		for(int i=0; i<10; i++)
			mDrawingStates[i] = new DrawingState();
		mTransformMatrix = new float[16];
		mRenderer = renderer;
	}
	
	/**
	 * Prepare Scene State<br><br>
	 * <b>Note</b>: Do not call this method
	 */
	final public void prepareScene(final SceneDrawerState state) {
		if(state == null)
			throw new IllegalArgumentException("It can not be null.");
		mSceneDrawerState = state;
	}
	
	/**
	 * Get Matrix Row
	 * 
	 * @return
	 */
	final public WorldMatrix getWorldMatrix() {
		return mMatrixRow;
	}
	
	/**
	 * Set Blend Func
	 * 
	 * @param blendFunc
	 */
	final public void setBlendFunc(final BlendFunc blendFunc) {
		GLES20.glBlendFunc(GLES20.GL_ONE, blendFunc.getConst());
	}
	
	/**
	 * Binds the texture for future drawings.
	 * @param texture Texture, If drawing functions is null had used the color to draw.
	 */
	final public void setTexture(final Texture texture) {
		if(texture == null) {
			mTexture = null;
			return;
		}
		mTexture = texture;
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getHandle());
	}
	
	/**
	 * Set Drawer color
	 * @param color Color, if zero is set to white
	 */
	final public void setColor(final Color color) {
		if(color == null) {
			mColor = Color.WHITE;
			return;
		}
		mColor = color;
	}
	
	/**
	 * Set drawer opacity
	 * 
	 * @param opacity
	 */
	final public void setOpacity(final float opacity) {
		mDrawingStates[mDrawingIndex].opacity = opacity;
	}
	
	/**
	 * Set Element Vertex
	 * @param elementVertex Element Vertex
	 */
	final public void setElementVertex(final FloatBuffer elementVertex) {
		mElementVertex = elementVertex;
	}
	
	/**
	 * Set Element Vertex
	 * @param elementVertex Element Vertex
	 */
	final public void setElementVertex(final float[] elementVertex) {
		final FloatBuffer vertex = GeneralUtils.createFloatBuffer(elementVertex.length);
		vertex.put(elementVertex);
		vertex.position(0);
		setElementVertex(vertex);
	}
	
	/**
	 * Set Element Vertex
	 * @param elementVertex Element Vertex
	 */
	final public void setElementVertex(final Vector2[] elementVertex) {
		final FloatBuffer vertex = GeneralUtils.createFloatBuffer(elementVertex.length * 2);
		for(final Vector2 value : elementVertex) {
			vertex.put(value.x);
			vertex.put(value.y);
		}
		vertex.position(0);
		setElementVertex(vertex);
	}
	
	/**
	 * Set Texture Vertex
	 * @param elementVertex Element Vertex
	 */
	final public void setTextureVertex(final FloatBuffer textureVertex) {
		mTextureVertex = textureVertex;
	}
	
	/**
	 * Set Texture Vertex
	 * @param elementVertex Element Vertex
	 */
	final public void setTextureVertex(final float[] textureVertex) {
		final FloatBuffer vertex = GeneralUtils.createFloatBuffer(textureVertex.length);
		vertex.put(textureVertex);
		vertex.position(0);
		setTextureVertex(vertex);
	}
	
	/**
	 * Set Texture Vertex
	 * @param elementVertex Element Vertex
	 */
	final public void setTextureVertex(final Vector2[] textureVertex) {
		final FloatBuffer vertex = GeneralUtils.createFloatBuffer(textureVertex.length * 2);
		for(final Vector2 value : textureVertex) {
			vertex.put(value.x);
			vertex.put(value.y);
		}
		vertex.position(0);
		setTextureVertex(vertex);
	}
	
	/**
	 * Set Filled Texture Vertex, same as:<br> 
	 * {0, 0, 1, 0, 1, 1, 0, 1}
	 */
	final public void setTextureVertexFilled() {
		mTextureVertex = mTextureVertexFilled;
	}
	
	/**
	 * Enable Viewport
	 * @param viewport Viewport, If rect is null it is ignored
	 */
	final public void snip(final Rect rect) {
		if(rect == null) {
			//refreshSnip();
			return;
		}
		Rect snip = mDrawingStates[mDrawingIndex].snip;
		if(snip == null)
			snip = new Rect(rect);
		else {
			if(!snip.equals(rect)) {
				if(!snip.intersect(rect))
					snip.set(0, 0, 0, 0);
			}
		}
		mDrawingStates[mDrawingIndex].snip = snip;
		refreshSnip(snip);
	}
	
	/**
	 * Refresh Snip
	 * @param snip
	 */
	final private void refreshSnip(final Rect snip) {
		if(snip == null) {
			GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
		} else {
			final int screenHeight = (int) mMainScene.getScreenSize().y;
			
			final int bottom = screenHeight - snip.bottom;
			
			GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
			
			GLES20.glScissor(snip.left, bottom, snip.right - snip.left, snip.bottom - snip.top);
		}
	}
	
	/**
	 * Refresh last snip in list
	 */
	final private void refreshSnip() {
		refreshSnip(mDrawingStates[mDrawingIndex].snip);
	}
	
	/**
	 * Disable Viewport
	 */
	final public void clearSnip() {
		GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
	}
	
	/**
	 * Add Stencil
	 */
	final public void drawStencil(final Polygon stencil) {
		
		GLES20.glEnable(GLES20.GL_STENCIL_TEST);
		GLES20.glColorMask(false, false, false, false);
		GLES20.glDepthMask(false);
		
		GLES20.glStencilFunc(GLES20.GL_NEVER, 1, 0xFF);
		GLES20.glStencilOp(GLES20.GL_INCR, GLES20.GL_KEEP, GLES20.GL_KEEP);
		GLES20.glStencilMask(0xFF);
		
		stencil.draw(this);
		
		GLES20.glColorMask(true, true, true, true);
		GLES20.glDepthMask(true);
		GLES20.glStencilMask(0);
		GLES20.glStencilFunc(GLES20.GL_EQUAL, mStencilLevel++, 0xFF);
		
	}
	
	/**
	 * Disable Stencil
	 */
	final public void eraseStencil(final Polygon stencil) {
		
		mStencilLevel--;
		
		GLES20.glColorMask(false, false, false, false);
		GLES20.glDepthMask(false);
		
		GLES20.glStencilFunc(GLES20.GL_NEVER, 1, 0xFF);
		GLES20.glStencilOp(GLES20.GL_DECR, GLES20.GL_KEEP, GLES20.GL_KEEP);
		GLES20.glStencilMask(0xFF);
		
		stencil.draw(this);
		
		GLES20.glColorMask(true, true, true, true);
		GLES20.glDepthMask(true);
		GLES20.glStencilMask(0);
		// Minus 1 because "0" use to clear buffer and
		// in draw stencil jump 1 and in erase subtract 1 to normalize
		GLES20.glStencilFunc(GLES20.GL_EQUAL, mStencilLevel-1, 0xFF);
		
		if(mStencilLevel == 1)
			GLES20.glDisable(GLES20.GL_STENCIL_TEST);
	}
	
	/**
	 * Begin Draw
	 */
	final public void begin() {
		mTexture = null;
		mColor = Color.WHITE;
		mElementVertex = null;
		mTextureVertex = null;
		setBlendFunc(BlendFunc.ONE_MINUS_SRC_ALPHA);
		
		// Set Drawing State
		DrawingState last = mDrawingStates[mDrawingIndex++];
		DrawingState now = mDrawingStates[mDrawingIndex];		
		now.lastOpacity = last.opacity * last.lastOpacity;
		now.opacity = 1.0f;
		if(last.snip == null)
			now.snip = null;
		else
			now.snip = new Rect(last.snip);
	}
	
	/**
	 * End Draw
	 */
	final public void end() {
		mTexture = null;
		mColor = Color.WHITE;
		mElementVertex = null;
		mTextureVertex = null;
		mDrawingIndex--;
		setBlendFunc(BlendFunc.ONE_MINUS_SRC_ALPHA);
		refreshSnip();
	}
	
	/**
	 * Draw Texture
	 * 
	 * @param designedDrawInfo
	 *            Drawer Information for GLES20 draw.
	 */
	public void drawTexture(final Vector2 size) {
		mMatrixRow.swap();
		mMatrixRow.copyValues(mTransformMatrix);
		
		
		// Get real recipient Size
		final float a = mTransformMatrix[0];
		final float b = mTransformMatrix[4];
		final float c = mTransformMatrix[1];
		final float d = mTransformMatrix[5];
		//Vector2 size = new Vector2((float) Math.hypot(a, c), (float) Math.hypot(b, d));
		
		
		// Prepare Map
		mTextureContainer.prepare(mTexture, size);
		
		mTexture.getMapper().onMap(mTextureContainer);
	}
	
	/**
	 * Draw Particles
	 * 
	 * @param particlesCount Amount of particles
	 * @param particleBuffer Information of each particle, with organizational pattern like this:<br>
	 * <i>[x1, y1, opacity1, size1, x2, y2, opacity2, size2, etc ..</i>
	 */
	public void drawParticles(final int particlesCount) {
		// Copy matrix
		mMatrixRow.swap();
		mMatrixRow.copyValues(mTransformMatrix);
		// Get Scale
		final Vector2 scale = mSceneDrawerState.getScale();
		// Swap Buffers
		ParticlesTextureRenderer renderer = (ParticlesTextureRenderer)begin(Renderer.PARTICLES_RENDERER, Color.TRANSPARENT);
		renderer.setScale(Math.min(scale.x, scale.y));
		renderer.setBuffers(mElementVertex);
		renderer.render(particlesCount);
	}
	
	/**
	 * Draw Colored Polygon
	 * @param verticesCount Vertices Count
	 * @param vertex Information of each vertices of polygon, with organizational pattern like this:<br>
	 * <i>[x1, y1, x2, y2, x3, y3, etc ..</i>
	 */
	public void drawPolygon(final int verticesCount) {
		if(mElementVertex == null)
			throw new RuntimeException("Os vertices do elemento não podem ser nulo, é necessario definir as posições do mesmo em 'setElementVertex()'.");
		// Copy matrix
		mMatrixRow.swap();
		mMatrixRow.copyValues(mTransformMatrix);
		// If was textured
		if(mTexture != null) {
			if(mTextureVertex == null) {
				// Uses
				final int position = mElementVertex.position();
				final Vector2 textureSize = mTexture.getSize();
				// Texture Points
				FloatBuffer texture = GlobalFloatBuffer.obtain(verticesCount * 2);
				for(int i=0; i<verticesCount; i++) {
					float x = mElementVertex.get() / textureSize.x;
					float y = mElementVertex.get() / textureSize.y;
					texture.put(x);
					texture.put(y);
				}
				texture.position(0);
				mElementVertex.position(position);
				// Swap Buffers
				StretchTextureRenderer renderer = (StretchTextureRenderer)begin(Renderer.STRETCH_TEXTURE_RENDERER, mColor);
				renderer.setBuffers(mElementVertex, texture);
				renderer.renderTriangleFan(verticesCount);
				// Release
				GlobalFloatBuffer.release(texture);
			} else {
				// Swap Buffers
				StretchTextureRenderer renderer = (StretchTextureRenderer)begin(Renderer.STRETCH_TEXTURE_RENDERER, mColor);
				renderer.setBuffers(mElementVertex, mTextureVertex);
				renderer.renderTriangleFan(verticesCount);
			}
		} else {
			// Swap Buffers
			UniformColorRenderer renderer = (UniformColorRenderer)begin(Renderer.UNIFORM_COLOR_RENDERER, mColor);
			renderer.setBuffers(mElementVertex);
			renderer.render(verticesCount);
		}
	}
	
	/**
	 * Draw Rectangle<br>
	 * <b>Note:</b> This method does not use the element vertex. 
	 * For texturing, will be necessary 4 vertices to texture vertex.
	 * 
	 * @param size Rectangle size
	 */
	public void drawRectangle(final Vector2 size) {
		// Copy matrix
		mMatrixRow.swap();
		mMatrixRow.copyValues(mTransformMatrix);
		// If was textured
		if(mTexture != null) {
			if(mTextureVertex == null) {
				// Uses
				final Vector2 textureSize = mTexture.getSize();
				// Obtain Buffers
				FloatBuffer elementsBuffer = GlobalFloatBuffer.obtain(new float[] {0, 0, size.x, 0, size.x, size.y, 0, size.y});
				FloatBuffer textureBuffer = GlobalFloatBuffer.obtain(new float[] {0, 0, size.x / textureSize.x, 0, size.x / textureSize.x, size.y / textureSize.y, 0, size.y / textureSize.y});
				// Swap Buffers
				StretchTextureRenderer renderer = (StretchTextureRenderer)begin(Renderer.STRETCH_TEXTURE_RENDERER, mColor);
				renderer.setBuffers(elementsBuffer, textureBuffer);
				renderer.renderTriangleFan(4);
				// Release
				GlobalFloatBuffer.release(elementsBuffer);
				GlobalFloatBuffer.release(textureBuffer);
			} else {
				// Obtain buffers
				FloatBuffer buffer = GlobalFloatBuffer.obtain(new float[] {0, 0, size.x, 0, size.x, size.y, 0, size.y});
				// Swap Buffers
				StretchTextureRenderer renderer = (StretchTextureRenderer)begin(Renderer.STRETCH_TEXTURE_RENDERER, mColor);
				renderer.setBuffers(buffer, mTextureVertex);
				renderer.renderTriangleFan(4);
				// Release Buffer
				GlobalFloatBuffer.release(buffer);
			}
		} else {
			// Obtain buffers
			FloatBuffer buffer = GlobalFloatBuffer.obtain(new float[] {0, 0, size.x, 0, size.x, size.y, 0, size.y});
			// Swap Buffers
			UniformColorRenderer renderer = (UniformColorRenderer)begin(Renderer.UNIFORM_COLOR_RENDERER, mColor);
			renderer.setBuffers(buffer);
			renderer.render(4);
			// Release Buffer
			GlobalFloatBuffer.release(buffer);
		}
	}
	
	/**
	 * Draw Square<br>
	 * <b>Note:</b> This method does not use the element vertex. 
	 * For texturing, will be necessary 4 vertices to texture vertex.
	 * 
	 * @param size Rectangle size
	 */
	public void drawSquare(final float sides) {
		drawRectangle(new Vector2(sides, sides));
	}
	
	/**
	 * Draw Circle
	 * @param radius Radius
	 */
	public void drawCircle(final float radius) {
		// Copy matrix
		mMatrixRow.swap();
		mMatrixRow.copyValues(mTransformMatrix);
		// if textured
		if(mTexture != null) {
			// If texture vertex null
			if(mTextureVertex == null) {
				// Get Texture Size
				final Vector2 size = mTexture.getSize();
				final float xw = (radius * 2) / size.x;
				final float yh = (radius * 2) / size.y;
				// Obtain GlobalBuffer
				final FloatBuffer globalBuffer = GlobalFloatBuffer.obtain(new float[] {0, 0, xw, 0, xw, yh, 0, yh});
				// Swap Buffers
				OptimizedEllipseTexturedRenderer renderer = (OptimizedEllipseTexturedRenderer)begin(Renderer.OPTIMIZED_ELLIPSE_TEXTURED_RENDERER, mColor);
				renderer.setRadius(radius, radius);
				renderer.setBuffers(globalBuffer);
				renderer.render();
				// Release GlobalBuffer
				GlobalFloatBuffer.release(globalBuffer);
			} else {
				// Swap Buffers
				OptimizedEllipseTexturedRenderer renderer = (OptimizedEllipseTexturedRenderer)begin(Renderer.OPTIMIZED_ELLIPSE_TEXTURED_RENDERER, mColor);
				renderer.setRadius(radius, radius);
				renderer.setBuffers(mTextureVertex);
				renderer.render();
			}
		} else {
			// Swap Buffers
			OptimizedEllipseUniformColorRenderer renderer = (OptimizedEllipseUniformColorRenderer)begin(Renderer.OPTIMIZED_ELLIPSE_UNIFORM_COLOR_RENDERER, mColor);
			renderer.setRadius(radius, radius);
			renderer.setBuffers();
			renderer.render();
		}
	}
	
	/**
	 * Draw Ellipse
	 * @param radius Radius
	 */
	public void drawEllipse(final Vector2 radius) {
		// Copy matrix
		mMatrixRow.swap();
		mMatrixRow.copyValues(mTransformMatrix);
		// if textured
		if(mTexture != null) {
			// If texture vertex null
			if(mTextureVertex == null) {
				// Obtain GlobalBuffer
				final FloatBuffer globalBuffer = GlobalFloatBuffer.obtain(new float[] {0, 0, radius.x, 0, radius.x, radius.y, 0, radius.y});
				// Swap Buffers
				OptimizedEllipseTexturedRenderer renderer = (OptimizedEllipseTexturedRenderer)begin(Renderer.OPTIMIZED_ELLIPSE_TEXTURED_RENDERER, mColor);
				renderer.setRadius(radius.x, radius.y);
				renderer.setBuffers(globalBuffer);
				renderer.render();
				// Release GlobalBuffer
				GlobalFloatBuffer.release(globalBuffer);
			} else {
				// Swap Buffers
				OptimizedEllipseTexturedRenderer renderer = (OptimizedEllipseTexturedRenderer)begin(Renderer.OPTIMIZED_ELLIPSE_TEXTURED_RENDERER, mColor);
				renderer.setRadius(radius.x, radius.y);
				renderer.setBuffers(mTextureVertex);
				renderer.render();
			}
		} else {
			// Swap Buffers
			OptimizedEllipseUniformColorRenderer renderer = (OptimizedEllipseUniformColorRenderer)begin(Renderer.OPTIMIZED_ELLIPSE_UNIFORM_COLOR_RENDERER, mColor);
			renderer.setRadius(radius.x, radius.y);
			renderer.setBuffers();
			renderer.render();
		}
	}
	
	/**
	 * Draw TileMap
	 * 
	 * @param elements
	 * @param textures
	 */
	public void drawTileMap(final int tileSize) {
		// If null vertex
		if(mElementVertex == null || mTextureVertex == null)
			throw new RuntimeException("The vertices of the elements and vertices texture can not be null.");
		// Copy matrix
		mMatrixRow.swap();
		mMatrixRow.copyValues(mTransformMatrix);
		//
		StretchTextureRenderer renderer = (StretchTextureRenderer)begin(Renderer.STRETCH_TEXTURE_RENDERER, Color.WHITE);
		renderer.setBuffers(mElementVertex, mTextureVertex);
		renderer.renderLinear(tileSize);
	}
	
	/**
	 * Draw Text
	 * 
	 * @param designedDrawInfo
	 *            Drawer Information for GLES20 draw.
	 */
	public void drawText(final FontMap fontMap, final String text, final FontWriter fontWriter) {
		// Copy matrix
		mMatrixRow.swap();
		mMatrixRow.copyValues(mTransformMatrix);
		// Active principal texture
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		// Process Writer
		FontWrapper.processWriter(fontMap, fontWriter, text, mTextureContainer);
	}
	
	/**
	 * Draw Text
	 * 
	 * @param designedDrawInfo
	 *            Drawer Information for GLES20 draw.
	 */
	public void drawText(final FontMap fontMap, final String text) {
		// Copy matrix
		mMatrixRow.swap();
		mMatrixRow.copyValues(mTransformMatrix);
		// Active principal texture
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		// Process Writer
		FontWrapper.processWriter(fontMap, text, mTextureContainer);
	}
	
	/**
	 * Draw Letter<br>
	 * Draw the contents of the letter. 
	 * This method is highly recommended for optimizations.
	 * 
	 * @param letter Letter
	 */
	public void drawLetter(final Letter letter) {
		// Copy matrix
		mMatrixRow.swap();
		mMatrixRow.copyValues(mTransformMatrix);
		// 
		Texture[] textures = letter.getFontMap().getTextures();
		for(int i=0; i<textures.length; i++) {
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i].getHandle());
		}
		
		// Process Drawer
		LetterRenderer renderer = (LetterRenderer)begin(Renderer.LETTER_RENDERER, Color.WHITE);
		LetterWrapper.processDrawer(letter, renderer);
	}
	
	/**
	 * Swap all transformations
	 */
	public void prepare() {
		mMatrixRow.copyValues(mTransformMatrix);
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
		final float opacity = mDrawingStates[mDrawingIndex].getOpacity() * mSceneDrawerState.getOpacity() * color.getAlpha();
		final float[] renderOpacity = new float[] { opacity * color.getRed(), opacity * color.getGreen(), opacity * color.getBlue(), opacity};
		baseProgram.onPrepare(mTransformMatrix, renderOpacity);
		return baseProgram;
	}
}
