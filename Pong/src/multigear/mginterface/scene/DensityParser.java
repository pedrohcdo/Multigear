package multigear.mginterface.scene;


/**
 * DensityParser
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class DensityParser {
	
	// Final Private Variables
	final private multigear.mginterface.scene.Scene mRoom;
	final private multigear.mginterface.engine.Configuration.OptimizedKey mOptimizedKeyA, mOptimizedKeyB;
	
	// Private Variables
	private float mBaseDensity;
	private multigear.general.utils.Ref2F mBaseScreen;
	
	/*
	 * Construtor
	 */
	public DensityParser(final multigear.mginterface.scene.Scene room) {
		mRoom = room;
		mOptimizedKeyA = room.getConfiguration().createOptimizedKey(multigear.mginterface.engine.Configuration.ATTR_BASE_DENSITY);
		mOptimizedKeyB = room.getConfiguration().createOptimizedKey(multigear.mginterface.engine.Configuration.ATTR_BASE_SCREEN);
		mBaseDensity = mRoom.getConfiguration().getFloatAttr(multigear.mginterface.engine.Configuration.ATTR_BASE_DENSITY);
		mBaseScreen = mRoom.getConfiguration().getRef2DAttr(multigear.mginterface.engine.Configuration.ATTR_BASE_SCREEN);
	}
	
	/*
	 * Update
	 */
	final public void update() {
		mBaseDensity = mOptimizedKeyA.getFloatAttr();
		mBaseScreen = mOptimizedKeyB.getRef2DAttr();
	}
	
	/**
	 * Return Proportional Reference 2D in Base Screen
	 * @param x Static X
	 * @param y Static Y
	 * @return Proportional Reference 2D
	 */
	final public multigear.general.utils.Ref2F smallerRef2D(final float x, final float y) {
		final float density = mRoom.getDensity();
		final multigear.general.utils.Ref2F screenSize = mRoom.getScreenSize();
		float baseDensity = mBaseDensity;
		multigear.general.utils.Ref2F baseScreenSize = mBaseScreen;
		
		// If default value, set as default density
		if(baseDensity == multigear.mginterface.engine.Configuration.DEFAULT_VALUE)
			baseDensity = density;
		// If default value, set as default display
		if(baseScreenSize == multigear.mginterface.engine.Configuration.DEFAULT_REF2D)
			baseScreenSize = screenSize;
		final multigear.general.utils.Ref2F ref2D = multigear.general.utils.KernelUtils.ref2d(x, y);
		return multigear.general.utils.GeneralUtils.calculateIndividualRef2DSmaller(ref2D, baseScreenSize, screenSize);
	}
	
	/**
	 * Return Proportional Reference 2D in Base Screen
	 * @param x Static X
	 * @param y Static Y
	 * @return Proportional Reference 2D
	 */
	final public multigear.general.utils.Ref2F biggerRef2D(final float x, final float y) {
		final float density = mRoom.getDensity();
		final multigear.general.utils.Ref2F screenSize = mRoom.getScreenSize();
		float baseDensity = mBaseDensity;
		multigear.general.utils.Ref2F baseScreenSize = mBaseScreen;
		
		// If default value, set as default density
		if(baseDensity == multigear.mginterface.engine.Configuration.DEFAULT_VALUE)
			baseDensity = density;
		// If default value, set as default display
		if(baseScreenSize == multigear.mginterface.engine.Configuration.DEFAULT_REF2D)
			baseScreenSize = screenSize;
		final multigear.general.utils.Ref2F ref2D = multigear.general.utils.KernelUtils.ref2d(x, y);
		return multigear.general.utils.GeneralUtils.calculateIndividualRef2DBigger(ref2D, baseScreenSize, screenSize);
	}
	
	/**
	 * Return Proportional Value
	 * @param value Value
	 * @return Proportional Value
	 */
	final public float smallerValue(final float value) {
		final multigear.general.utils.Ref2F screenSize = mRoom.getScreenSize();
		multigear.general.utils.Ref2F baseScreenSize = mBaseScreen;
		// If default value, set as default display
		if(baseScreenSize == multigear.mginterface.engine.Configuration.DEFAULT_REF2D)
			baseScreenSize = screenSize;
		return multigear.general.utils.GeneralUtils.calculateIndividualValueSmaller(value, baseScreenSize, screenSize);
	}
	
	/**
	 * Return Proportional Value
	 * @param value Value
	 * @return Proportional Value
	 */
	final public float biggerValue(final float value) {
		final multigear.general.utils.Ref2F screenSize = mRoom.getScreenSize();
		multigear.general.utils.Ref2F baseScreenSize = mBaseScreen;
		// If default value, set as default display
		if(baseScreenSize == multigear.mginterface.engine.Configuration.DEFAULT_REF2D)
			baseScreenSize = screenSize;
		return multigear.general.utils.GeneralUtils.calculateIndividualValueBigger(value, baseScreenSize, screenSize);
	}
}
