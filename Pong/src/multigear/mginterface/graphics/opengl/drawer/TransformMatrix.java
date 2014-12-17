package multigear.mginterface.graphics.opengl.drawer;


/**
 * Matrix Drawer
 * 
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class TransformMatrix {
	
	// Final Private Variables
	final private MatrixRow mMatrixRow;
	
	/**
	 * Constructor
	 * @param matrixRow
	 */
	public TransformMatrix(final MatrixRow matrixRow) {
		mMatrixRow = matrixRow;
	}
	
	/**
	 * Translate Matrix
	 */
	public void translate(final float translateX, final float translateY) {
		mMatrixRow.postTranslatef(translateX, translateY);
	}
	
	/**
	 * Scale Matrix
	 */
	public void scale(final float scaleX, final float scaleY) {
		mMatrixRow.postScalef(scaleX, scaleY);
	}
	
	/**
	 * Rotate Matrix
	 */
	public void rotate(final float degrees) {
		mMatrixRow.postRotatef(degrees);
	}
	
	/**
	 * Concat Matrix
	 * 
	 * @param dotMatrix Matrix 3x3
	 */
	public void dot(final float matrix[]) {
		mMatrixRow.postConcatf(matrix);
	}
	
	/**
	 * Translate Matrix
	 */
	public void translate(final double translateX, final double translateY) {
		mMatrixRow.postTranslatef((float)translateX, (float)translateY);
	}
	
	/**
	 * Scale Matrix
	 */
	public void scale(final double scaleX, final double scaleY) {
		mMatrixRow.postScalef((float)scaleX, (float)scaleY);
	}
	
	/**
	 * Rotate Matrix
	 */
	public void rotate(final double degrees) {
		mMatrixRow.postRotatef((float)degrees);
	}
}
