package multigear.physics;

/**
 * 
 * Shape Object.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class Shape {
	
	// Private Variables
	final private multigear.general.utils.Ref2F[] mVertices;
	
	/*
	 * Construtor
	 */
	public Shape(final int size) {
		mVertices = new multigear.general.utils.Ref2F[size];
		for(int i=0; i<size; i++)
			mVertices[i] = multigear.general.utils.KernelUtils.ref2d(0, 0);
	}
	
	/*
	 * Seta uma posição da forma
	 */
	final public void setVertice(final int index, final multigear.general.utils.Ref2F vertice) {
		mVertices[index] = vertice;
	}
	
	/*
	 * Pega uma posição da forma
	 */
	final public multigear.general.utils.Ref2F getVertice(final int index) {
		return mVertices[index];
	}
	
	/*
	 * Retorna o pacote de vertices
	 */
	final public multigear.general.utils.Ref2F[] getVertices() {
		return mVertices;
	}
	
	/*
	 * Retorna o tamanho da forma
	 */
	final public int getSize() {
		return mVertices.length;
	}
	
	/**
	 * Copy vertices to Shape, base on float[] vertices.
	 * The vertices have to be organized like this:
	 * 
	 * [Ax, Ay, Bx, By, ...
	 * 
	 * @param vertices Package of float[] vertices
	 * @return
	 */
	final public boolean copy(final float[] vertices) {
		final int size = vertices.length / 2;
		final int mod = vertices.length % 2;
		if(mod != 0 || size != getSize())
			return false;
		for(int i=0; i<size; i++) {
			mVertices[i].XAxis = vertices[i * 2];
			mVertices[i].YAxis = vertices[i * 2 + 1];
		}
		return true;
	}
	
	/**
	 * Copy vertices to Shape, base on Utils.Ref2D package.
	 * 
	 * @param vertices Package of Utils.Ref2D vertices
	 * @return
	 */
	final public boolean copy(final multigear.general.utils.Ref2F[] vertices) {
		if(vertices.length != getSize())
			return false;
		for(int i=0; i<vertices.length; i++) {
			mVertices[i] = vertices[i].clone();
		}
		return true;
	}
	
	/**
	 * Create a Shape base on float[] vertices.
	 * The vertices have to be organized like this:
	 * 
	 * [Ax, Ay, Bx, By, ...
	 * 
	 * @param vertices Package of float[] vertices
	 * @return
	 */
	final static public Shape createShape(final float[] vertices) {
		final int size = vertices.length / 2;
		final int mod = vertices.length % 2;
		if(mod != 0)
			return null;
		final Shape shape = new Shape(size);
		for(int i=0; i<size; i++) {
			shape.mVertices[i].XAxis = vertices[i * 2];
			shape.mVertices[i].YAxis = vertices[i * 2 + 1];
		}
		return shape;
	}
	
	/**
	 * Create a Shape base on Utils.Ref2D package.
	 * 
	 * @param vertices Package of Utils.Ref2D vertices
	 * @return
	 */
	final static public Shape createShape(final multigear.general.utils.Ref2F[] vertices) {
		final Shape shape = new Shape(vertices.length);
		for(int i=0; i<vertices.length; i++) {
			shape.mVertices[i] = vertices[i].clone();
		}
		return shape;
	}
}
