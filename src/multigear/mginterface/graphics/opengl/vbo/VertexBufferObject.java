package multigear.mginterface.graphics.opengl.vbo;

import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import android.annotation.SuppressLint;
import android.opengl.GLES20;

/**
 * Vertex Buffer Object
 * 
 * @author user
 * 
 */
final public class VertexBufferObject {

	/**
	 * Usage
	 * 
	 * @author user
	 * 
	 */
	public enum Usage {

		STATIC_DRAW(GLES20.GL_STATIC_DRAW), 
		DYNAMIC_DRAW(GLES20.GL_DYNAMIC_DRAW);

		// Final Private Variables
		final private int mConst;

		/**
		 * Constructor
		 */
		Usage(int const_) {
			mConst = const_;
		}

		/**
		 * Get GL Const
		 * 
		 * @return
		 */
		private int getConst() {
			return mConst;
		}
	}

	/**
	 * Target
	 * 
	 * @author user
	 * 
	 */
	public enum Target {

		ARRAY_BUFFER(GLES20.GL_ARRAY_BUFFER), 
		ELEMENT_ARRAY_BUFFER(GLES20.GL_ELEMENT_ARRAY_BUFFER);

		// Final Private Variables
		final private int mConst;

		/**
		 * Constructor
		 */
		Target(int const_) {
			mConst = const_;
		}

		/**
		 * Get GL Const
		 * 
		 * @return
		 */
		private int getConst() {
			return mConst;
		}
	}
	
	/**
	 * Type
	 * 
	 * @author user
	 * 
	 */
	public enum Type {
		
		SHORT(GLES20.GL_SHORT),
		INT(GLES20.GL_INT),
		FLOAT(GLES20.GL_FLOAT);

		// Final Private Variables
		final private int mConst;

		/**
		 * Constructor
		 */
		Type(int const_) {
			mConst = const_;
		}

		/**
		 * Get GL Const
		 * 
		 * @return
		 */
		private int getConst() {
			return mConst;
		}
	}

	// Final Private Variables
	final private int mHandle;
	final private Target mTarget;
	final private Usage mUsage;
	
	// Private Variables
	private boolean mFirstSet = false;
	private Type mType;
	
	/**
	 * Constructor
	 * 
	 * @param handle
	 */
	private VertexBufferObject(final int handle, final Target target, final Usage usage) {
		mHandle = handle;
		mTarget = target;
		mUsage = usage;
	}

	/**
	 * Create Vertex Buffer Object.<br>
	 * <b>Note:</b> Use in GLThread
	 */
	final public static VertexBufferObject create(final Target target, final Usage usage) {
		final int[] handles = new int[1];
		GLES20.glGenBuffers(1, handles, 0);
		return new VertexBufferObject(handles[0], target, usage);
	}

	/**
	 * Set Buffer
	 * 
	 * @param buffer Vertex Buffer
	 * @param size Count of Components Per Vertex
	 */
	final public void setBuffer(final IntBuffer buffer, final int size) {
		GLES20.glBindBuffer(mTarget.getConst(), mHandle);
		GLES20.glBufferData(mTarget.getConst(), size * 4, buffer, mUsage.getConst());
		GLES20.glBindBuffer(mTarget.getConst(), 0);
		mType = Type.INT;
	}
	
	/**
	 * Set Buffer
	 * 
	 * @param buffer Vertex Buffer
	 * @param size Count of Components Per Vertex
	 */
	final public void setBuffer(final FloatBuffer buffer, final int size) {
		GLES20.glBindBuffer(mTarget.getConst(), mHandle);
		GLES20.glBufferData(mTarget.getConst(), size * 4, buffer, mUsage.getConst());
		GLES20.glBindBuffer(mTarget.getConst(), 0);
		mType = Type.FLOAT;
	}
	
	/**
	 * Set Buffer
	 * 
	 * @param buffer Vertex Buffer
	 * @param size Count of Components Per Vertex
	 */
	final public void setBuffer(final ShortBuffer buffer, final int size) {
		GLES20.glBindBuffer(mTarget.getConst(), mHandle);
		GLES20.glBufferData(mTarget.getConst(), size * 2, buffer, mUsage.getConst());
		GLES20.glBindBuffer(mTarget.getConst(), 0);
		mType = Type.SHORT;
	}
	
	/**
	 * Use this VBO
	 * @param size Count of Components Per Vertex
	 * @return
	 */
	@SuppressLint("NewApi") 
	final public void use(final int attribHandle, final int count, final boolean normalized, final int stride, final int offset) {
		GLES20.glBindBuffer(mTarget.getConst(), mHandle);
	    GLES20.glVertexAttribPointer(attribHandle, count, mType.getConst(), normalized, stride, offset);
	    GLES20.glBindBuffer(mTarget.getConst(), 0);
	}
	
	/**
	 * Bind VBO
	 */
	final public void bind() {
		GLES20.glBindBuffer(mTarget.getConst(), mHandle);
	}
	
	/**
	 * Unbind
	 */
	final public void unbind() {
		GLES20.glBindBuffer(mTarget.getConst(), 0);
	}
	
	/**
	 * Get Type
	 * @return
	 */
	final public int getType() {
		return mType.getConst();
	}
	
	/**
	 * Destroy VBO
	 */
	final public void destroy() {
		GLES20.glDeleteBuffers(1, new int[] {mHandle}, 0);
	}
}
