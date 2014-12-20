package multigear.mginterface.graphics.opengl.programs;

import multigear.general.utils.Vector2;

/**
 * Simple Renderer Program
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public abstract class BaseProgram {
	
	// Private Variables
	private int mProgramHandle;
	
	/**
	 * Constructor
	 * @param manager
	 */
	public BaseProgram() {
		mProgramHandle = -1;
	}
	
	/**
	 * Set Program Handle after installed by ProgramsManager.
	 * @param handle
	 */
	final protected void setHandle(final int handle) {
		mProgramHandle = handle;
	}
	
	/**
	 * Get Program Handle.
	 * @return
	 */
	final protected int getHandle() {
		return mProgramHandle;
	}
	
	/**
	 * Unuse Program
	 */
	final protected void unuse() {
		onUnused();
	}
	
	/** Load Vertex Shader */
	protected abstract String onLoadVertexShader();
	
	/** Load Fragment Shader */
	protected abstract String onLoadFragmentShader();
	
	/** Prepare to draw call  */
	public abstract void onPrepare(final float[] transformMatrix, final float[] blendColor);
	
	/** Setup Program */
	protected abstract void onSetup(final Vector2 screenSize);
	
	/** Unused Program */
	protected abstract void onUnused();
}