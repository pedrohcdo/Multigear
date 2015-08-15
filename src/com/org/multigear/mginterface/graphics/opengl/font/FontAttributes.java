package com.org.multigear.mginterface.graphics.opengl.font;

import com.org.multigear.general.utils.Vector2;

/**
 * Font Attributes
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class FontAttributes {
	
	// Private Variables
	private Vector2 mPadd = new Vector2(0, 0);
	private boolean mUseMetrics = true;
	private boolean mLinear = false;
	
	/**
	 * Constructor
	 * 
	 * @param padd Padd
	 * @param useMetrics Use metrics guid
	 * @param isLinear Set linear text
	 */
	public FontAttributes(Vector2 padd, boolean useMetrics, boolean isLinear) {
		mPadd = padd;
		mUseMetrics = useMetrics;
		mLinear = isLinear;
	}
	
	/**
	 * Constructor with<br>
	 * <dd>Padding: <b>Vector2(0, 0)</b><br>
	 * <dd>Use Metric: <b>false</b><br>
	 * <dd>Linear: <b>false</b><br>
	 */
	public FontAttributes() {
		mPadd = new Vector2();
		mUseMetrics = false;
		mLinear = false;
	}
	
	/**
	 * Get Padd
	 * 
	 * @return
	 */
	public Vector2 getPadd() {
		return mPadd;
	}
	
	/**
	 * Set Padd
	 * @param mPadd
	 */
	public void setPadd(Vector2 padd) {
		this.mPadd = padd;
	}
	
	/**
	 * Is use Metrics
	 * 
	 * @return
	 */
	public boolean isUseMetrics() {
		return mUseMetrics;
	}
	
	/**
	 * Set use Metrics
	 * @param mUseMetrics
	 */
	public void setUseMetrics(boolean useMetrics) {
		this.mUseMetrics = useMetrics;
	}
	
	/**
	 * Is Linear
	 * @return
	 */
	public boolean isLinear() {
		return mLinear;
	}
	
	/**
	 * Set Linear
	 * @param mLinear
	 */
	public void setLinear(boolean linear) {
		this.mLinear = linear;
	}
}
