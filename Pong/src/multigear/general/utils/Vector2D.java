package multigear.general.utils;

/**
 * 
 * Classe utilisada como Vetor do objeto.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property SpringBall.
 */
public class Vector2D {
	
	// Constants
	final private double A2R = Math.PI / 180;
	final private double R2A = 180 / Math.PI;
	
	// Variables
	private float mX;
	private float mY;
	private float mSpeedMod;
	
	/*
	 * Construtor completo
	 */
	public Vector2D(final float vx, final float vy) {
		if (vx == 0 && vy == 0) {
			mX = 0;
			mY = 0;
			mSpeedMod = 0;
		} else {
			final float mod = (float)Math.sqrt(vx * vx + vy * vy);
			mX = vx / mod;
			mY = vy / mod;
			mSpeedMod = mod;
		}
	}
	
	/*
	 * Construtor desmembrado
	 */
	public Vector2D(final float x, final float y, final float speed) {
		mX = x;
		mY = y;
		mSpeedMod = speed;
	}
	
	/*
	 * Construtor por dois pontos
	 */
	public Vector2D(final multigear.general.utils.Ref2F va, final multigear.general.utils.Ref2F vb) {
		this(vb.XAxis - va.XAxis, vb.YAxis - va.YAxis);
	}
	
	/*
	 * Construtor por um angulo
	 */
	public Vector2D(final float angle) {
		final double rad = angle * A2R;
		mX = (float)Math.cos(rad);
		mY = (float)Math.sin(rad);
		mSpeedMod = 0;
	}
	
	/*
	 * Construtor clone
	 */
	public Vector2D(final Vector2D vector) {
		mX = vector.getNormalX();
		mY = vector.getNormalY();
		mSpeedMod = vector.getSpeedMod();
	}
	
	/*
	 * Retorna velocidade em X
	 */
	public float getX() {
		return mX * mSpeedMod;
	}
	
	/*
	 * Retorna velocidade em Y
	 */
	public float getY() {
		return mY * mSpeedMod;
	}
	
	/*
	 * Retorna velocidade normal em X
	 */
	public float getNormalX() {
		return mX;
	}
	
	/*
	 * Retorna velocidade normal em Y
	 */
	public float getNormalY() {
		return mY;
	}
	
	/*
	 * Retorna a velocidade do vetor em modulo
	 */
	public float getSpeedMod() {
		return mSpeedMod;
	}
	
	/*
	 * Altera velocidade
	 */
	public void setSpeedMod(final float speed) {
		mSpeedMod = speed;
	}
	
	/**
	 * Set X, Y Component
	 */
	public void setXY (final float vx, final float vy) {
		if (vx == 0 && vy == 0) {
			mX = 0;
			mY = 0;
			mSpeedMod = 0;
		} else {
			final float mod = (float)Math.sqrt(vx * vx + vy * vy);
			mX = vx / mod;
			mY = vy / mod;
			mSpeedMod = mod;
		}
	}
	
	/*
	 * Troca o eixo X de direção
	 */
	final public void swipeX() {
		mX *= -1;
	}
	
	/*
	 * Troca o eixo Y de direção
	 */
	final public void swipeY() {
		mY *= -1;
	}
	
	/*
	 * Retorna um novo vetor dividido por um valor
	 */
	final public Vector2D sub(final float value) {
		return new Vector2D(mX, mY, mSpeedMod / value);
	}
	
	/*
	 * Altera velocidade
	 */
	public void setAngle(final float angle) {
		final double rad = angle * A2R;
		mX = (float)Math.cos(rad);
		mY = (float)Math.sin(rad);
	}
	
	/*
	 * Aplica uma força
	 */
	public void applyForce(final Vector2D force) {
		final float xR = this.getX() + force.getX();
		final float yR = this.getY() + force.getY();
		if (xR == 0 && yR == 0) {
			mX = 0;
			mY = 0;
			mSpeedMod = 0;
		} else {
			final float mod = (float)Math.sqrt(xR * xR + yR * yR);
			mX = xR / mod;
			mY = yR / mod;
			mSpeedMod = mod;
		}
	}
	
	/*
	 * Aplica força contraria
	 */
	public void applyOpForce(final Vector2D force) {
		final float xR = this.getX() - force.getX();
		final float yR = this.getY() - force.getY();
		if (xR == 0 && yR == 0) {
			mX = 0;
			mY = 0;
			mSpeedMod = 0;
		} else {
			final float mod = (float)Math.sqrt(xR * xR + yR * yR);
			mX = xR / mod;
			mY = yR / mod;
			mSpeedMod = mod;
		}
	}
	
	/*
	 * Aplica uma força com tempo
	 * 
	 * Clock: Tempo decorrido Density: Densidade da tela, utilise 1 quando não
	 * houver necessidades.
	 */
	public void applyForceClocked(final Vector2D force, final float clock) {
		final float xR = this.getX() + force.getX() * clock;
		final float yR = this.getY() + force.getY() * clock;
		if (xR == 0 && yR == 0) {
			mX = 0;
			mY = 0;
			mSpeedMod = 0;
		} else {
			final float mod = (float)Math.sqrt(xR * xR + yR * yR);
			mX = xR / mod;
			mY = yR / mod;
			mSpeedMod = mod;
		}
	}
	
	/*
	 * Aplica força contraria com tempo
	 * 
	 * Clock: Tempo decorrido Density: Densidade da tela, utilise 1 quando não
	 * houver necessidades.
	 */
	public void applyOpForceClocked(final Vector2D force, final float clock) {
		final float xR = this.getX() - force.getX() * clock;
		final float yR = this.getY() - force.getY() * clock;
		if (xR == 0 && yR == 0) {
			mX = 0;
			mY = 0;
			mSpeedMod = 0;
		} else {
			final float mod = (float)Math.sqrt(xR * xR + yR * yR);
			mX = xR / mod;
			mY = yR / mod;
			mSpeedMod = mod;
		}
	}
	
	/*
	 * Cria uma atrito
	 */
	public void friction(final float f) {
		mSpeedMod /= f;
	}
	
	/*
	 * Cria uma atrito com tempo
	 */
	public void frictionClocked(final float f, final float clock) {
		final float frictionSpeed = Math.max((mSpeedMod - (mSpeedMod / f)) * clock, 0);
		mSpeedMod -= frictionSpeed;
	}
	
	/*
	 * Reflete por outro vetor
	 */
	public void reflectByNormal(final Vector2D normal) {
		final float dot = 2 * (normal.getNormalX() * mX + normal.getNormalY() * mY);
		final float dotNx = normal.getNormalX() * dot;
		final float dotNy = normal.getNormalY() * dot;
		final float resX = mX - dotNx;
		final float resY = mY - dotNy;
		mX = -resX;
		mY = -resY;
	}
	
	/*
	 * Rotaciona 90 graus
	 */
	public void rotate90Deg() {
		final float lastY = mY;
		mY = -mX;
		mX = lastY;
	}
	
	/*
	 * Rotaciona 180 graus
	 */
	public void rotate180Deg() {
		mX = -mX;
		mY = -mY;
	}
	
	/*
	 * Rotaciona 90 graus
	 */
	public void rotate90DegOp() {
		final float lastY = mY;
		mY = mX;
		mX = -lastY;
	}
	
	/*
	 * Rotaciona em x graus
	 */
	public void rotateDeg(final float deg) {
		final double rad = deg * A2R;
		final double cos = Math.cos(rad);
		final double sin = Math.sin(rad);
		final double nX = mX * cos - mY * sin;
		final double nY = mX * sin + mY * cos;
		mX = (float)nX;
		mY = (float)nY;
	}
	
	/*
	 * Cancela a força em Y
	 */
	public void cancellY() {
		mSpeedMod = Math.abs(mX * mSpeedMod);
		mY = 0;
	}
	
	/*
	 * Retorna direção do vetor
	 */
	final public int getDirection() {
		float rad = (float)Math.atan2(mY, mX);
		int direction = (int) Math.round(rad * R2A) + 90;
		return direction;
	}
	
	/*
	 * Retorna uma copia do vetor
	 * 
	 * @see java.lang.Object#clone()
	 */
	final public Vector2D clone() {
		return new Vector2D(mX, mY, mSpeedMod);
	}
	
	/*
	 * Freia em Y
	 */
	final public void slowMinY() {
		if (mY < 0) {
			mSpeedMod = 0.1f;
		}
	}
	
	/*
	 * Retorna o texto referente
	 */
	final public String toString() {
		return "<Vector2D: XAxis=" + getX() + " YAxis=" + getY() + ">";
	}
}
