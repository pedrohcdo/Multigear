package multigear.mginterface.graphics.opengl;

import multigear.mginterface.engine.Configuration;
import multigear.mginterface.graphics.opengl.programs.BaseProgram;
import multigear.mginterface.graphics.opengl.programs.ProgramsManager;
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
	final public static int STRETCH_TEXTURE_RENDERER = 0;
	final public static int REPEAT_TEXTURE_RENDERER = 1;
	final public static int TRANSITION_TEXTURE_RENDERER = 2;
	final public static int PARTICLES_RENDERER = 3;
	
	// Final private Variables
	final private multigear.mginterface.engine.Configuration.OptimizedKey mOptimizedKey;
	final private ProgramsManager mProgramsManager;
	
	/*
	 * Construtor
	 */
	public Renderer(multigear.mginterface.engine.Multigear engine) {
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
	final public void onCreate(final multigear.general.utils.Ref2F size) {
		// Install all programs
		mProgramsManager.installPrograms(size);
	    // Projection Matrix
	    GLES20.glViewport(0, 0, (int)size.XAxis, (int)size.YAxis);
		// Enable properties
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
		//GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		//gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
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
			final int color = (int)getEngine().getConfiguration().getFloatAttr(multigear.mginterface.engine.Configuration.ATTR_BACKGROUND_COLOR);
			final float red = Color.red(color) / 255f;
			final float green = Color.green(color) / 255f;
			final float blue = Color.blue(color) / 255f;
			final float alpha = Color.alpha(color) / 255f;
			GLES20.glClearColor(red, green, blue, alpha);
		}
		GLES20.glClearColor(0, 0, 0, 0);
		// Clear Screen
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
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
