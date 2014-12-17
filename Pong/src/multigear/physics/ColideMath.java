package multigear.physics;

import android.graphics.RectF;

/**
 * 
 * Verifica colisões.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class ColideMath {
	
	/*
	 * Retorna posição de uma interseção entre duas retas
	 */
	final static protected multigear.general.utils.Ref2F getIntersectionLines(multigear.general.utils.Line2D a, multigear.general.utils.Line2D b) {
		// Get Positions
		final int x1 = (int)Math.floor(a.Start.XAxis);
		final int x2 = (int)Math.ceil(a.End.XAxis);
		final int x3 = (int)Math.floor(b.Start.XAxis);
		final int x4 = (int)Math.ceil(b.End.XAxis);
		final int y1 = (int)Math.floor(a.Start.YAxis);
		final int y2 = (int)Math.ceil(a.End.YAxis);
		final int y3 = (int)Math.floor(b.Start.YAxis);
		final int y4 = (int)Math.ceil(b.End.YAxis);
		
		int d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
		if (d == 0)
			return null;
		int pre = (x1 * y2 - y1 * x2);
		int post = (x3 * y4 - y3 * x4);
		int x = (pre * (x3 - x4) - (x1 - x2) * post) / d;
		int y = (pre * (y3 - y4) - (y1 - y2) * post) / d;
		if (x < Math.min(x1, x2) || x > Math.max(x1, x2) || x < Math.min(x3, x4) || x > Math.max(x3, x4))
			return null;
		if (y < Math.min(y1, y2) || y > Math.max(y1, y2) || y < Math.min(y3, y4) || y > Math.max(y3, y4))
			return null;
		
		return multigear.general.utils.KernelUtils.ref2d(x, y);
	}
	
	/*
	 * Retorna o retangulo entre os vertices
	 */
	final static public RectF getRectangle(multigear.general.utils.Ref2F[] vertices) {
		// Ensure extremes
		float minX = (float)(vertices[0].XAxis);
		float maxX = minX;
		float minY = (float)(vertices[0].YAxis);
		float maxY = minY;
		// Limit extremes
		for(int i=1; i<vertices.length; i++) {
			final float vX = (float)(vertices[i].XAxis);
			final float vY = (float)(vertices[i].YAxis);
			// Get max extremes
			maxX = Math.max(maxX, vX);
			minX = Math.min(minX, vX);
			maxY = Math.max(maxY, vY);
			minY = Math.min(minY, vY);
		}
		// Define Edges
		return new RectF(minX, minY, maxX, maxY);
	}
	
	/**
	 * Return center of Shape.
	 * 
	 * @param shape Shape used for get center.
	 */
	final static public multigear.general.utils.Ref2F getCenterOfShape(final multigear.physics.Shape shape) {
		float totalX = 0;
		float totalY = 0;
		for(int i=0; i<shape.getSize(); i++) {
			final multigear.general.utils.Ref2F vertice = shape.getVertice(i);
			totalX += vertice.XAxis;
			totalY += vertice.YAxis;
		}
		final float centerX = totalX / shape.getSize();
		final float centerY = totalY / shape.getSize();
		return multigear.general.utils.KernelUtils.ref2d(centerX, centerY);
	}
	
	/**
	 * Subdive Shape in n-vertices TriangleShapes.
	 * 
	 * @param shape Shape used to subdivide
	 * @return Return package of subtriangles
	 */
	final static public multigear.physics.Shape[] subdivideShapeInCenteredTriangles(final multigear.physics.Shape shape) {
		final int size = shape.getSize();
		if(size < 3)
			return null;
		final multigear.physics.Shape[] shapes = new multigear.physics.Shape[size];
		final multigear.general.utils.Ref2F center = getCenterOfShape(shape);
		// Create Uniform Triangles
		for(int i=0; i<(size-1); i++) {
			final multigear.general.utils.Ref2F verticeA = shape.getVertice(i);
			final multigear.general.utils.Ref2F verticeB = shape.getVertice(i + 1);
			final multigear.general.utils.Ref2F[] trianglePackage = new multigear.general.utils.Ref2F[3];
			trianglePackage[0] = verticeA;
			trianglePackage[1] = verticeB;
			trianglePackage[2] = center;
			shapes[i] = multigear.physics.Shape.createShape(trianglePackage);
		}
		// Close Shape
		final multigear.general.utils.Ref2F verticeA = shape.getVertice(size-1);
		final multigear.general.utils.Ref2F verticeB = shape.getVertice(0);
		final multigear.general.utils.Ref2F[] trianglePackage = new multigear.general.utils.Ref2F[3];
		trianglePackage[0] = verticeA;
		trianglePackage[1] = verticeB;
		trianglePackage[2] = center;
		shapes[size-1] = multigear.physics.Shape.createShape(trianglePackage);
		return shapes;
	}
	
	/**
	 * Get triangle shape area.
	 * 
	 * @param triangle Triangle Shape.
	 * @return Return a triangle Area.
	 */
	final static public float getTriangleShapeArea(final multigear.physics.Shape triangle) {
		multigear.general.utils.Ref2F vertice = triangle.getVertice(0);
		float minX = vertice.XAxis;
		float maxX = vertice.XAxis;
		float minY = vertice.YAxis;
		float maxY = vertice.YAxis;
		for(int i=1; i<3; i++) {
			vertice = triangle.getVertice(i);
			final float x = vertice.XAxis;
			final float y = vertice.YAxis;
			minX = Math.min(minX, x);
			maxX = Math.max(maxX, x);
			minY = Math.min(minY, y);
			maxY = Math.max(maxY, y);
		}
		final float width = Math.abs(maxX - minX);
		final float height = Math.abs(maxY - minY);
		return (width * height) / 2;
	}
	/**
	 * Relative Difference
	 */
	private static float relativeDifference(float d1, float d2) {
		return (d1 == d2) ? 0 : Math.abs(d1 - d2) / Math.max(Math.abs(d1), Math.abs(d2));
	}
	
	/**
	 * Nearly Equal
	 */
	private static boolean nearlyEqual(float d1, float d2) {
		return (relativeDifference(d1, d2) <= 0.01);
	}
	
	/**
	 * Find Point side line
	 */
	private static int findSide(float ax, float ay, float bx, float by, float cx, float cy) {
		if (nearlyEqual(bx - ax, 0)) { // vertical line
			if (cx < bx) {
				return by > ay ? 1 : -1;
			}
			if (cx > bx) {
				return by > ay ? -1 : 1;
			}
			return 0;
		}
		if (nearlyEqual(by - ay, 0)) { // horizontal line
			if (cy < by) {
				return bx > ax ? -1 : 1;
			}
			if (cy > by) {
				return bx > ax ? 1 : -1;
			}
			return 0;
		}
		float slope = (by - ay) / (bx - ax);
		float yIntercept = ay - ax * slope;
		float cSolution = (slope * cx) + yIntercept;
		if (slope != 0) {
			if (cy > cSolution) {
				return bx > ax ? 1 : -1;
			}
			if (cy < cSolution) {
				return bx > ax ? -1 : 1;
			}
			return 0;
		}
		return 0;
	}
	
	/**
	 * Check if point over Triangle Shape.
	 * This metode use Area calculation.
	 * 
	 * @param point Point to check.
	 * @param triangle Triangle.
	 * @return Return true if Over.
	 */
	final static public boolean isOverTriangleShape(final multigear.general.utils.Ref2F point, final multigear.physics.Shape triangle) {
		final multigear.general.utils.Ref2F a = triangle.getVertice(0);
		final multigear.general.utils.Ref2F b = triangle.getVertice(1);
		final multigear.general.utils.Ref2F c = triangle.getVertice(2);
		final int side1 = findSide(a.XAxis, a.YAxis, b.XAxis, b.YAxis, point.XAxis, point.YAxis);
		final int side2 = findSide(b.XAxis, b.YAxis, c.XAxis, c.YAxis, point.XAxis, point.YAxis);
		final int side3 = findSide(c.XAxis, c.YAxis, a.XAxis, a.YAxis, point.XAxis, point.YAxis);
		return (side1 + side2 + side3 == 3);
	}
	
	/**
	 * Get normal vector in over of Shape.
	 * 
	 * @param point Point to check over.
	 * @param shape Shape to check normal.
	 * @return Return normal vector.
	 *         Return null if not is over.
	 */
	final static public multigear.general.utils.Vector2D getOverShapeNormal(final multigear.general.utils.Ref2F point, final multigear.physics.Shape shape) {
		final multigear.physics.Shape[] triangles = subdivideShapeInCenteredTriangles(shape);
		for(int i=0; i<triangles.length; i++) {
			if(isOverTriangleShape(point, triangles[i])) {
				// Last Triangle
				if(i == triangles.length - 1) {
					final multigear.general.utils.Ref2F verticeA = shape.getVertice(i);
					final multigear.general.utils.Ref2F verticeB = shape.getVertice(0);
					return new multigear.general.utils.Vector2D(verticeA, verticeB);
				// Uniform Triangles
				} else {
					final multigear.general.utils.Ref2F verticeA = shape.getVertice(i);
					final multigear.general.utils.Ref2F verticeB = shape.getVertice(i+1);
					return new multigear.general.utils.Vector2D(verticeA, verticeB);
				}
			}
		}
		return null;
	}
	
	
}
