package multigear.mginterface.graphics.opengl.drawer;

import android.graphics.Matrix;

/**
 * Matrix Row
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class MatrixRow {
	
	// Final Private Variables
	final private Matrix[] mMatrixQueue;
	final private float[] mMatrix3x3;
	
	// Private Variables
	public int mIndex;
	private Matrix mRoomTransformations;
	
	
	/**
	 * Constructor
	 * 
	 * @param rowsSize
	 */
	public MatrixRow(final int rowsSize) {
		mMatrixQueue = new Matrix[rowsSize];
		mMatrix3x3 = new float[9];
		mRoomTransformations = new Matrix();
		for(int i=0; i<rowsSize; i++)
			mMatrixQueue[i] = new Matrix();
		mIndex = 0;
	}
	
	/**
	 * Set Post Room Transformations
	 * @param transformations
	 */
	final public void setRoomTransformations(final Matrix transformations) {
		mRoomTransformations = transformations;
	}
	
	/**
	 * Push new Matrix
	 */
	final public void push() {
		if(mIndex >= mMatrixQueue.length)
			throw new RuntimeException("Could not get a new matrix, the limit has been reached.");
		mIndex += 1;
		mMatrixQueue[mIndex].set(mMatrixQueue[mIndex-1]);
	}
	
	/**
	 * Pop matrix
	 */
	final public void pop() {
		if(mIndex < 0)
			throw new RuntimeException("Was not possible to drop the matrix, no longer exists in the matrix stack.");
		mIndex -= 1;
	}
	
	/**
	 * Copy Values.<br>
	 * For best performance for this method does not clear the matrix.
	 * @param values
	 */
	final public void copyValues(final float[] values) {
		
		Matrix finalTransformations = new Matrix(mMatrixQueue[mIndex]);
		finalTransformations.postConcat(mRoomTransformations);
		
		finalTransformations.getValues(mMatrix3x3);
		
		values[0] = mMatrix3x3[0];
		values[1] = mMatrix3x3[3];
		
		values[4] = mMatrix3x3[1];
		values[5] = mMatrix3x3[4];
		
		values[10] = 1.0f;
		
		values[12] = mMatrix3x3[2];
		values[13] = mMatrix3x3[5];
		
		values[15] = mMatrix3x3[8];
	}
	
	/**
	 * Set Matrix Identity
	 */
	final public void setIdenity() {
		mMatrixQueue[mIndex].reset();
	}
	
	/**
	 * Scale Matrix
	 * @param x
	 * @param y
	 */
	final public void postScalef(final float x, final float y) {
		mMatrixQueue[mIndex].postScale(x, y);
	}
	
	/**
	 * Translate Matrix
	 * @param x
	 * @param y
	 */
	final public void postTranslatef(final float x, final float y) {
		mMatrixQueue[mIndex].postTranslate(x, y);
	}
	
	/**
	 * Rotate Matrix
	 * @param degrees
	 */
	final public void postRotatef(final float degrees) {
		mMatrixQueue[mIndex].postRotate(degrees);
	}
	
	/**
	 * Skew Matrix
	 * @param skewX
	 * @param skewY
	 */
	final public void postSkewf(final float skewX, final float skewY) {
		mMatrixQueue[mIndex].postSkew(skewX, skewY);
	}
	
	/**
	 * Concat Matrix
	 * @param matrix
	 */
	final public void postConcatf(final float matrix[]) {
		Matrix concat = new Matrix();
		concat.setValues(matrix);
		mMatrixQueue[mIndex].postConcat(concat);
	}
	
	
}
