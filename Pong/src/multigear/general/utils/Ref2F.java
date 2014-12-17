package multigear.general.utils;


/**
 * 
 * Referencia utilisada para 2 Dimensoes.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class Ref2F {
	
	// Public References
	public float XAxis;
	public float YAxis;
	
	/*
	 * Construtor normal
	 */
	public Ref2F() {}
	
	/*
	 * COnstrutir por valores
	 */
	public Ref2F(final float xa, final float ya) {
		XAxis = xa;
		YAxis = ya;
	}
	
	/**
	 * Apply force on this Reference.
	 * 
	 * @param force Force used to apply.
	 */
	final public void applyForce(final multigear.general.utils.Vector2D force) {
		XAxis += force.getX();
		YAxis += force.getY();
	}
	
	/**
	 * Create a clone of this object and converts to Vector2D object.
	 * 
	 * @return Return a converted object.
	 */
	final public multigear.general.utils.Vector2D toVector2D() {
		return new multigear.general.utils.Vector2D(XAxis, YAxis);
	}
	
	/*
	 * Retorna uma copia do objeto
	 */
	final public Ref2F clone() {
		return new Ref2F(XAxis, YAxis);
	}
	
	/**
	 * Set Values
	 * @param ref
	 */
	final public void set(final Ref2F ref) {
		XAxis = ref.XAxis;
		YAxis = ref.YAxis;
	}
	
	/**
	 * Set Values
	 * @param ref
	 */
	final public void set(final float xAxis, final float yAxis) {
		XAxis = xAxis;
		YAxis = yAxis;
	}
	
	/**
	 * Set Values
	 * @param ref
	 */
	final public void set(final long xAxis, final long yAxis) {
		XAxis = xAxis;
		YAxis = yAxis;
	}
	
	/**
	 * Set Values
	 * @param ref
	 */
	final public void set(final double xAxis, final double yAxis) {
		XAxis = (float)xAxis;
		YAxis = (float)yAxis;
	}
	
	/**
	 * Set Values
	 * @param ref
	 */
	final public void set(final int xAxis, final int yAxis) {
		XAxis = xAxis;
		YAxis = yAxis;
	}
	
	/*
	 * Retorna true caso os objetos tenham atributos iguais
	 */
	final public boolean equals(final multigear.general.utils.Ref2F ref2d) {
		if(ref2d == null)
			return false;
		return (XAxis == ref2d.XAxis && YAxis == ref2d.YAxis);
	}
	
	/**
	 * Addition operation
	 * 
	 * @param add Ref2D
	 * @return Result of operation
	 */
	final public Ref2F add(final Ref2F add) {
		XAxis += add.XAxis;
		YAxis += add.YAxis;
		return this;
	}
	
	/**
	 * Addition operation
	 * 
	 * @param add Ref2D
	 * @return Result of operation
	 */
	final public Ref2F add(final float x, final float y) {
		XAxis += x;
		YAxis += y;
		return this;
	}
	
	/**
	 * Subtraction operation
	 * 
	 * @param add Ref2D
	 * @return Result of operation
	 */
	final public Ref2F sub(final Ref2F sub) {
		XAxis -= sub.XAxis;
		YAxis -= sub.YAxis;
		return this;
	}
	
	/**
	 * Multiplication operation
	 * 
	 * @param add Ref2D
	 * @return Result of operation
	 */
	final public Ref2F mul(final Ref2F mul) {
		XAxis *= mul.XAxis;
		YAxis *= mul.YAxis;
		return this;
	}

	/**
	 * Multiplication operation
	 * 
	 * @param add Ref2D
	 * @return Result of operation
	 */
	final public Ref2F mul(final float mul) {
		XAxis *= mul;
		YAxis *= mul;
		return this;
	}
	
	/**
	 * Multiplication operation
	 * 
	 * @param add Ref2D
	 * @return Result of operation
	 */
	final public Ref2F mul(final double mul) {
		XAxis *= mul;
		YAxis *= mul;
		return this;
	}
	
	/**
	 * Multiplication operation
	 * 
	 * @param add Ref2D
	 * @return Result of operation
	 */
	final public Ref2F mul(final int mul) {
		XAxis *= mul;
		YAxis *= mul;
		return this;
	}
	
	/**
	 * Multiplication operation
	 * 
	 * @param add Ref2D
	 * @return Result of operation
	 */
	final public Ref2F mul(final long mul) {
		XAxis *= mul;
		YAxis *= mul;
		return this;
	}

	/**
	 * Division operation
	 * 
	 * @param add Ref2D
	 * @return Result of operation
	 */
	final public Ref2F div(final Ref2F div) {
		XAxis /= div.XAxis;
		YAxis /= div.YAxis;
		return this;
	}
	
	/**
	 * Division operation
	 * 
	 * @param add Ref2D
	 * @return Result of operation
	 */
	final public Ref2F div(final float div) {
		XAxis /= div;
		YAxis /= div;
		return this;
	}
	
	/**
	 * Division operation
	 * 
	 * @param add Ref2D
	 * @return Result of operation
	 */
	final public Ref2F div(final double div) {
		XAxis /= div;
		YAxis /= div;
		return this;
	}
	
	/**
	 * Division operation
	 * 
	 * @param add Ref2D
	 * @return Result of operation
	 */
	final public Ref2F div(final int div) {
		XAxis /= div;
		YAxis /= div;
		return this;
	}
	
	/**
	 * Division operation
	 * 
	 * @param add Ref2D
	 * @return Result of operation
	 */
	final public Ref2F div(final long div) {
		XAxis /= div;
		YAxis /= div;
		return this;
	}
	
	/**
	 * Returns the diagonal of two references.
	 * 
	 * @return sqrt(XAxis^2, YAxis^2)
	 */
	final public double hypot() {
		return Math.hypot(XAxis, YAxis);
	}
	
	/*
	 * Retorna o texto referente
	 */
	final public String toString() {
		return "<Ref2D: XAxis=" + XAxis + " YAxis=" + YAxis + ">";
	}
}
