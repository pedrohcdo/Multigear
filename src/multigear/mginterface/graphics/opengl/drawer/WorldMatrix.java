package multigear.mginterface.graphics.opengl.drawer;

import android.graphics.Matrix;

/**
 * Matrix Row
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class WorldMatrix {
	
	// Final Private Variables
	final private Matrix[] mMatrixQueue;
	final private float[] mMatrix3x3;
	final private Matrix mFinalTransformations = new Matrix();
	
	// Private Variables
	public int mIndex;
	private Matrix mRoomTransformations;
	private boolean mPostTransformationUse = true;
	
	/**
	 * Constructor
	 * 
	 * @param rowsSize
	 */
	public WorldMatrix(final int rowsSize) {
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
		if(transformations == null)
			throw new IllegalArgumentException("Transformations can not be null.");
		mRoomTransformations = transformations;
	}
	
	
	/**
	 * Enable pre transformation matrix
	 */
	final public void enableSceneState() {
		mPostTransformationUse = true;
	}
	
	/**
	 * Disable pre transformations matrix
	 */
	final public void disableSceneState() {
		mPostTransformationUse = false;
	}
	
	/**
	 * Psh a new array with the previous values.
	 */
	final public void push() {
		if(mIndex >= mMatrixQueue.length)
			throw new RuntimeException("Could not get a new matrix, the limit has been reached.");
		mIndex += 1;
		mMatrixQueue[mIndex].set(mMatrixQueue[mIndex-1]);
	}
	
	/**
	 * Push a new identity matrix.
	 */
	final public void pushIdentity() {
		if(mIndex >= mMatrixQueue.length)
			throw new RuntimeException("Could not get a new matrix, the limit has been reached.");
		mIndex += 1;
		mMatrixQueue[mIndex].reset();
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
		values[0] = mMatrix3x3[0];
		values[1] = mMatrix3x3[3];
		values[4] = mMatrix3x3[1];
		values[5] = mMatrix3x3[4];
		//values[10] = 1.0f;
		values[12] = mMatrix3x3[2];
		values[13] = mMatrix3x3[5];
		values[15] = mMatrix3x3[8];
	}
	
	/**
	 * Copy Values.<br>
	 * For best performance for this method does not clear the matrix.
	 * @param values
	 */
	final public void copyValues9x9(final float[] values) {
		values[0] = mMatrix3x3[0];
		values[1] = mMatrix3x3[1];
		values[2] = mMatrix3x3[2];
		values[3] = mMatrix3x3[3];
		values[4] = mMatrix3x3[4];
		values[5] = mMatrix3x3[5];
		values[8] = 1.0f;
	}
	
	/**
	 * Set Matrix Identity
	 */
	final public void setIdenity() {
		mMatrixQueue[mIndex].reset();
	}
	
	/**
	 * Swap transformations
	 */
	final public void swap() {
		mFinalTransformations.set(mMatrixQueue[mIndex]);
		if(mPostTransformationUse)
			mFinalTransformations.postConcat(mRoomTransformations);
		mFinalTransformations.getValues(mMatrix3x3);
	}
	
	/**
	 * Projeta um ponto
	 * 
	 * @param [in] Vertices de entrada 2x1
	 * @param [out] Vertices de saida 2x1
	 */
	final public void project(final float in[], final float out[]) {
		out[0] = mMatrix3x3[0] * in[0] + mMatrix3x3[1] * in[1] + mMatrix3x3[2];
		out[1] = mMatrix3x3[3] * in[0] + mMatrix3x3[4] * in[1] + mMatrix3x3[5];
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


	/**
	 * Scale Matrix
	 * @param x
	 * @param y
	 */
	final public void preScalef(final float x, final float y) {
		mMatrixQueue[mIndex].preScale(x, y);
	}
	
	/**
	 * Translate Matrix
	 * @param x
	 * @param y
	 */
	final public void preTranslatef(final float x, final float y) {
		mMatrixQueue[mIndex].preTranslate(x, y);
	}
	
	/**
	 * Rotate Matrix
	 * @param degrees
	 */
	final public void preRotatef(final float degrees) {
		mMatrixQueue[mIndex].preRotate(degrees);
	}
	
	/**
	 * Skew Matrix
	 * @param skewX
	 * @param skewY
	 */
	final public void preSkewf(final float skewX, final float skewY) {
		mMatrixQueue[mIndex].preSkew(skewX, skewY);
	}
	
	/**
	 * Concat Matrix
	 * @param matrix
	 */
	final public void preConcatf(final float matrix[]) {
		Matrix concat = new Matrix();
		concat.setValues(matrix);
		mMatrixQueue[mIndex].preConcat(concat);
	}
	
	
}
