package com.org.multigear.mginterface.graphics.opengl;

import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.engine.Configuration;
import com.org.multigear.mginterface.graphics.opengl.programs.BaseProgram;
import com.org.multigear.mginterface.graphics.opengl.programs.ProgramsManager;

import android.graphics.Color;
import android.opengl.GLES20;

/**
 * 
 * Renderer utilisado pela GL Surface.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property SpringBall.
 */
public class Renderer extends BaseRenderer {
	
	// Conts
	final public static int STRETCH_TEXTURE_RENDERER = ProgramsManager.STRETCH_TEXTURE_RENDERER_PROGRAM;
	final public static int REPEAT_TEXTURE_RENDERER = ProgramsManager.REPEAT_TEXTURE_RENDERER_PROGRAM;
	final public static int TRANSITION_TEXTURE_RENDERER = ProgramsManager.TRANSITION_TEXTURE_RENDERER_PROGRAM;
	final public static int POINT_PARTICLES_RENDERER = ProgramsManager.POINT_PARTICLES_RENDERER_PROGRAM;
	final public static int UNIFORM_COLOR_RENDERER = ProgramsManager.UNIFORM_COLOR_RENDERER;
	final public static int OPTIMIZED_ELLIPSE_UNIFORM_COLOR_RENDERER = ProgramsManager.OPTIMIZED_ELLIPSE_UNIFORM_COLOR_RENDERER;
	final public static int OPTIMIZED_ELLIPSE_TEXTURED_RENDERER = ProgramsManager.OPTIMIZED_ELLIPSE_TEXTURED_RENDERER;
	final public static int LETTER_RENDERER = ProgramsManager.LETTER_RENDERER;
	final public static int SPRITE_PARTICLES_RENDERER = ProgramsManager.SPRITE_PARTICLES_RENDERER_PROGRAM;
	final public static int DISCARD_TEXTURE_RENDERER = ProgramsManager.DISCARD_TEXTURE_RENDERER_PROGRAM;
	
	// Final private Variables
	final private com.org.multigear.mginterface.engine.Configuration.OptimizedKey mOptimizedKey;
	final private ProgramsManager mProgramsManager;
	
	/*
	 * Construtor
	 */
	public Renderer(com.org.multigear.mginterface.engine.Multigear engine) {
		super(engine);
		mOptimizedKey = getEngine().getConfiguration().createOptimizedKey(Configuration.ATTR_BACKGROUND_COLOR);
		mProgramsManager = new ProgramsManager();
	}
	
	/*
	 * Preparando a surface
	 * 
	 * @see
	 * OpenGL.GLRendererBase#onCreate(javax.microedition.khronos.opengles.GL10,
	 * int, int, boolean)
	 */
	@Override
	final public void onCreate(final Vector2 size) {
		// Install all programs
		mProgramsManager.installPrograms(size);
	    // Projection Matrix
	    GLES20.glViewport(0, 0, (int)size.x, (int)size.y);
		// Enable properties
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
		//GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		//gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		GLES20.glClearStencil(0);
	}
	
	/*
	 * Preoarando o desenho do frame atual
	 * 
	 * @see
	 * OpenGL.GLRendererBase#onDrawFrame(javax.microedition.khronos.opengles
	 * .GL10, boolean)
	 */
	@Override
	public void onDraw() {
		// If configure key was reconfigured
		if(mOptimizedKey.wasReconfigured()) {
			// Set BG Color
			final int color = (int)getEngine().getConfiguration().getFloatAttr(com.org.multigear.mginterface.engine.Configuration.ATTR_BACKGROUND_COLOR);
			final float red = Color.red(color) / 255f;
			final float green = Color.green(color) / 255f;
			final float blue = Color.blue(color) / 255f;
			final float alpha = Color.alpha(color) / 255f;
			GLES20.glClearColor(red, green, blue, alpha);
		}
		// Clear Screen
		GLES20.glStencilMask(0xFF);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
	}
	
	/**
	 * Use Renderer Program.
	 * <p>
	 * Note: By using this renderer will no longer have the previous effects. 
	 * You can change between renderers using this function but only one at a time.
	 * <p>
	 * @param rendererProgram Program Id
	 */
	final public BaseProgram useRenderer(final int rendererProgram) {
		return mProgramsManager.useProgram(rendererProgram);
	}
}
