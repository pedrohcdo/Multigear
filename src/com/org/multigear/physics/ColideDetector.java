package com.org.multigear.physics;

import com.org.multigear.general.utils.Vector2;

import android.graphics.RectF;

/**
 * 
 * Detecta colisão entre vertices.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Booclass.
 */
final public class ColideDetector {
	
	// Constants
	final static public int BODY_A_RECTANGLE = 0;
	final static public int BODY_A_RECTANGLES = 1;
	final static public int BODY_A_BOTTOM_LIMITED = 2;

	/*
	 * Desabilitando construtor
	 */
	private ColideDetector() {}
	
	/*
	 * Detector de colisão
	 */
	final static public int detect(final int mode, final Vector2[] bodyA, final Vector2[] bodyB) {
		switch(mode) {
			case BODY_A_RECTANGLE:
				return detectRectangle(bodyA, bodyB);
			case BODY_A_RECTANGLES:
				return detectRectangles(bodyA, bodyB);
			case BODY_A_BOTTOM_LIMITED:
				return detectBottomLimited(bodyA, bodyB);
		}
		return -1;
	}
	
	/*
	 * Detector de colisão em forma de retangulo
	 */
	final static private int detectRectangle(final Vector2[] bodyA, final Vector2[] bodyB) {
		// Make Rectangle
		final RectF rectangle = com.org.multigear.physics.ColideMath.getRectangle(bodyA);
		// Search for colide vertice
		for(int i=0; i<bodyB.length; i++) {
			final float vX = (float) bodyB[i].x;
			final float vY = (float) bodyB[i].y;
			if(rectangle.contains(vX, vY)) {
				// Colided vertice
				return i;
			}
		}
		// No colide
		return -1;
	}
	
	/*
	 * Detector de colisão em forma de retangulo em poligono
	 */
	final static private int detectRectangles(final Vector2[] bodyA, final Vector2[] bodyB) {
		// Get Rectangles size
		final int rectanglesSize = bodyA.length / 4;
		final Vector2[] rectangle = new Vector2[4];
		// Check colide with rectangles
		for(int i=0; i<rectanglesSize; i++) {
			final int j = i * 4;
			rectangle[0] = bodyA[j];
			rectangle[1] = bodyA[j+1];
			rectangle[2] = bodyA[j+2];
			rectangle[3] = bodyA[j+3];
			final int colideIndex = detectRectangle(rectangle, bodyB);
			if(colideIndex != -1)
				return colideIndex;
		}
		// No colide
		return -1;
	}
	
	/*
	 * Detector de colisão com limite inferior
	 */
	final static private int detectBottomLimited(final Vector2[] bodyA, final Vector2[] bodyB) {
		// Get bottom
		final double bottom = bodyA[0].y;
		// Search for colide Bottom
		for(int i=0; i<bodyB.length; i++) {
			final float vY = (float) bodyB[i].y;
			if(vY >= bottom) {
				// Colided vertice
				return i;
			}
		}
		// No colide
		return -1;
	}
}
