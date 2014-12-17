package multigear.physics;

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
	final static public int detect(final int mode, final multigear.general.utils.Ref2F[] bodyA, final multigear.general.utils.Ref2F[] bodyB) {
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
	final static private int detectRectangle(final multigear.general.utils.Ref2F[] bodyA, final multigear.general.utils.Ref2F[] bodyB) {
		// Make Rectangle
		final RectF rectangle = multigear.physics.ColideMath.getRectangle(bodyA);
		// Search for colide vertice
		for(int i=0; i<bodyB.length; i++) {
			final float vX = (float) bodyB[i].XAxis;
			final float vY = (float) bodyB[i].YAxis;
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
	final static private int detectRectangles(final multigear.general.utils.Ref2F[] bodyA, final multigear.general.utils.Ref2F[] bodyB) {
		// Get Rectangles size
		final int rectanglesSize = bodyA.length / 4;
		final multigear.general.utils.Ref2F[] rectangle = new multigear.general.utils.Ref2F[4];
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
	final static private int detectBottomLimited(final multigear.general.utils.Ref2F[] bodyA, final multigear.general.utils.Ref2F[] bodyB) {
		// Get bottom
		final double bottom = bodyA[0].YAxis;
		// Search for colide Bottom
		for(int i=0; i<bodyB.length; i++) {
			final float vY = (float) bodyB[i].YAxis;
			if(vY >= bottom) {
				// Colided vertice
				return i;
			}
		}
		// No colide
		return -1;
	}
}
