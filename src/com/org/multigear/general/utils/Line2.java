package com.org.multigear.general.utils;

/**
 * 
 * Classe utilisada como Linha.
 * 
 * @author PedroH, RaphaelB
 *
 * Property SpringBall.
 */
final public class Line2 {
	
	// Variables
	public Vector2 start;
	public Vector2 end;
	
	/*
	 * Comnstrutor
	 */
	public Line2(Vector2 start, Vector2 end) {
		this.start = start;
		this.end = end;
	}
	
	/**
	 * Get Distance to Vector2
	 * @param point
	 * @return
	 */
	public float distanceToPoint(final Vector2 point) {
		final float dx = end.x - start.x;
		final float dy = end.y - start.y;
		final float length = (float)Math.hypot(dx, dy);
		return ((point.x - start.x) * dy - 
				(point.y - start.y) * dx) / length;
	}
}