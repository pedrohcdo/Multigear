package multigear.mginterface.graphics.opengl.programs;

import java.util.ArrayList;
import java.util.List;

import multigear.general.utils.Vector2;
import android.opengl.GLES20;
import android.util.Log;

/**
 * Programs Manager
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class ProgramsManager {
	
	// Conts
	final public static int STRETCH_TEXTURE_RENDERER_PROGRAM = 0;
	final public static int REPEAT_TEXTURE_RENDERER_PROGRAM = 1;
	final public static int TRANSITION_TEXTURE_RENDERER_PROGRAM = 2;
	final public static int PARTICLE_RENDERER_PROGRAM = 3;
	
	// Final Private Variables
	final private List<BaseProgram> mInstalledPrograms = new ArrayList<BaseProgram>();
	
	// Private Variables
	private int mProgramUsed = -1;
	private BaseProgram mProgramInstance;
	
	/**
	 * Load Shader GLSL.
	 * <p>
	 * @param type Shader type.
	 * @param source Shader Source.
	 * <p>
	 * @return Handle of Shader loaded
	 */
	final private int loadShader(final int type, final String source) {
		final int handle = GLES20.glCreateShader(type);
		GLES20.glShaderSource(handle, source);
		GLES20.glCompileShader(handle);
		return handle;
	}
	
	/**
	 * Install Program
	 * @param programClass
	 */
	final private void installProgram(final Class<? extends BaseProgram> programClass, final Vector2 screenSize) {
		try {
			final BaseProgram program = programClass.newInstance();
			final int vertexShaderHandle = loadShader(GLES20.GL_VERTEX_SHADER, program.onLoadVertexShader());
			final int fragmentShaderHandle = loadShader(GLES20.GL_FRAGMENT_SHADER, program.onLoadFragmentShader());
			program.setHandle(GLES20.glCreateProgram());
			GLES20.glAttachShader(program.getHandle(), vertexShaderHandle);
			GLES20.glAttachShader(program.getHandle(), fragmentShaderHandle);
			GLES20.glLinkProgram(program.getHandle());
			program.onSetup(screenSize);
			mInstalledPrograms.add(program);
		} catch (Exception e) {
		}
	}
	
	/**
	 * Install all programs.<p>
	 * Note: This method will be called automatically by the renderer.
	 * Do not call this method manually, because it can cost a high CPU cycle and can cause overrun error.
	 */
	final public void installPrograms(final Vector2 screenSize) {
		installProgram(StretchTextureRenderer.class, screenSize);
		installProgram(RepeatTextureRenderer.class, screenSize);
		installProgram(TransitionTextureRenderer.class, screenSize);
		installProgram(ParticlesTextureRenderer.class, screenSize);
	}
	
	/**
	 * Uses a program.
	 * <p>
	 * @param program Desired program
	 * @return Return used Program
	 */
	final public BaseProgram useProgram(final int program) {
		if(mProgramUsed == program)
			return mProgramInstance;
		final BaseProgram installedProgram = mInstalledPrograms.get(program);
		GLES20.glUseProgram(installedProgram.getHandle());
		mProgramUsed = program;
		mProgramInstance = installedProgram;
		return mProgramInstance;
	}
}
