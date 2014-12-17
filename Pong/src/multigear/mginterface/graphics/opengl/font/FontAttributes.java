package multigear.mginterface.graphics.opengl.font;

import multigear.general.utils.Ref2F;

/**
 * Font Attributes
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class FontAttributes {
	
	// Private Variables
	private Ref2F mPadd = new Ref2F(0, 0);
	private boolean mUseMetrics = false;
	private boolean mLinear = false;
	
	/**
	 * Constructor
	 * 
	 * @param padd Padd
	 * @param useMetrics Use metrics guid
	 * @param isLinear Set linear text
	 */
	public FontAttributes(Ref2F padd, boolean useMetrics, boolean isLinear) {
		mPadd = padd;
		mUseMetrics = useMetrics;
		mLinear = isLinear;
	}
	
	/**
	 * Get Padd
	 * 
	 * @return
	 */
	public Ref2F getPadd() {
		return mPadd;
	}
	
	/**
	 * Set Padd
	 * @param mPadd
	 */
	public void setPadd(Ref2F padd) {
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
