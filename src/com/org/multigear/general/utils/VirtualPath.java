package com.org.multigear.general.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Virtual Path
 * 
 * @author user
 *
 */
final public class VirtualPath {
	
	/**
	 * Path Object
	 * 
	 * @author user
	 *
	 */
	private interface PathObject {
		public float distance();
		public Vector2 position(final float time);
	}
	
	/**
	 * Line Path
	 * 
	 * @author user
	 *
	 */
	final private class LinePath implements PathObject {
		
		// Final Private Variables
		final private float mAngle;
		final private float mDistance;
		
		/**
		 * Constructor
		 * 
		 * @param ang
		 * @param distance
		 */
		public LinePath(final float ang, final float distance) {
			mAngle = ang;
			mDistance = distance;
		}
		
		/**
		 * Get Distance
		 */
		@Override
		public float distance() {
			return mDistance;
		}
		
		/**
		 * Get Position
		 */
		@Override
		public Vector2 position(float time) {
			final double rad = GeneralUtils.degreeToRad(mAngle);
			return new Vector2((float)Math.cos(rad) * mDistance * time, (float)Math.sin(rad) * mDistance * time);
		}
	}
	
	// Final Private Variables
	final private List<PathObject> mPathObjects = new ArrayList<PathObject>();
	
	// Private Variables
	private Vector2 mPosition = new Vector2();
	
	/**
	 * Set Virtual Path position offset
	 */
	final public void setPosition(final Vector2 position) {
		mPosition = position;
	}
	
	/**
	 * Get Virtual Path position offset
	 */
	final public Vector2 getPosition() {
		return mPosition.clone();
	}
	
	/**
	 * Add Line Path
	 * 
	 * @param ang
	 * @param distance
	 */
	final public void addLine(final float ang, final float distance) {
		mPathObjects.add(new LinePath(ang, distance));
	}
	
	/**
	 * Get position in time
	 * 
	 * @param time
	 */
	final public Vector2 getPosition(float time) {
		final float peace = 1.0f / getTotalDistance();
		float timeLine = 0;
		Vector2 position = new Vector2(mPosition);
		for(final PathObject pathObject : mPathObjects) {
			final float pathPeace = pathObject.distance() * peace;
			if(time <= (pathPeace + timeLine)) {
				time = (time - timeLine) / pathPeace;
				return Vector2.sum(position, pathObject.position(time));
			} else {
				position = Vector2.sum(position, pathObject.position(1));
				timeLine += pathPeace;
			}
		}
		return new Vector2();
	}
	
	/**
	 * Get total Distance
	 * @return
	 */
	final public float getTotalDistance() {
		float distance = 0;
		for(final PathObject pathObject : mPathObjects)
			distance += pathObject.distance();
		return distance;
	}
}
