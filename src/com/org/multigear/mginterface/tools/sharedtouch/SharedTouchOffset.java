package com.org.multigear.mginterface.tools.sharedtouch;

import com.org.multigear.general.utils.Vector2;

/**
 * Shared Touch Offset
 * 
 * @author user
 *
 */
final public class SharedTouchOffset {

	// Public Variables
	public Vector2 sourceOffset;
	public Vector2 receiveOffset;
	public float sourceAdjust;
	public float receivedAdjust;
	
	/**
	 * Constructor
	 */
	public SharedTouchOffset(final Vector2 sourceOffset, final Vector2 receiveOffset, final float sourceAdjust, final float receiveAdjust) {
		this.sourceOffset = sourceOffset;
		this.receiveOffset = receiveOffset;
		this.sourceAdjust = sourceAdjust;
		this.receivedAdjust = receiveAdjust;
	}
	
	/**
	 * Constructor
	 */
	public SharedTouchOffset() {
		sourceOffset = new Vector2();
		receiveOffset = new Vector2();
		sourceAdjust = 1;
		receivedAdjust = 1;
	}
	
	/**
	 * Clone
	 */
	@Override
	final public SharedTouchOffset clone() {
		return new SharedTouchOffset(sourceOffset.clone(), receiveOffset.clone(), sourceAdjust, receivedAdjust);
	}
}
