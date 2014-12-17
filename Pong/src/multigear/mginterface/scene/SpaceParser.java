package multigear.mginterface.scene;




/**
 * Base Plane Support
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class SpaceParser {
	
	// Final Private Variables
	final private multigear.mginterface.scene.Scene mRoom;
	final private multigear.mginterface.engine.Configuration.OptimizedKey mOptimizedKey;
	
	/*
	 * Construtor
	 */
	public SpaceParser(final multigear.mginterface.scene.Scene room) {
		mRoom = room;
		mOptimizedKey = room.getConfiguration().createOptimizedKey(multigear.mginterface.engine.Configuration.ATTR_BASE_DPI);
	}
	
	/**
	 * Return Scale Factor
	 * 
	 * @return
	 */
	final public float getScaleFactor() {
		// Scale Factor
		float scaleFactor;
		// Get Base Dpi
		final float baseDensity = mOptimizedKey.getFloatAttr();
		// Default Scale
		if(baseDensity == -1) {
			scaleFactor = mRoom.getDPI() / mRoom.getRealDPI();
		// Calculare Scale Factor
		}else
			scaleFactor = mRoom.getDPI() / baseDensity;
		// Return Scale Factor
		return scaleFactor;
	}
	
	/**
	 * Return Inverse Scale Factor
	 * 
	 * @return
	 */
	final public float getInverseScaleFactor() {
		// Scale Factor
		float scaleFactor;
		// Get Base Dpi
		final float baseDensity = mOptimizedKey.getFloatAttr();
		// Default Scale
		if(baseDensity == -1)
			scaleFactor = 1;
		// Calculare Scale Factor
		else
			scaleFactor = baseDensity / mRoom.getDPI();
		// Return Scale Factor
		return scaleFactor;
	}
	
	/*
	 * Retorna o tamanho da tela
	 */
	final public multigear.general.utils.Ref2F getScreenSize() {
		final multigear.general.utils.Ref2F size = multigear.general.utils.KernelUtils.ref2d(0, 0);
		final float scaleFactor = getScaleFactor();
		size.XAxis = mRoom.getScreenSize().XAxis / scaleFactor;
		size.YAxis = mRoom.getScreenSize().YAxis / scaleFactor;
		return size;
	}
	
	/*
	 * Passa um valor para a base
	 */
	final public float parseToBase(final float value) {
		return value * getScaleFactor();
	}
	
	/**
	 * Parse to inverse of Base
	 * 
	 * @param value
	 * @return
	 */
	final public double parseToInverseBase(final double value) {
		return value / getScaleFactor();
	}
	
	/*
	 * Passa uma referencia2d para a base
	 */
	final public multigear.general.utils.Ref2F parseToBase(final multigear.general.utils.Ref2F ref2d) {
		final float scaleFactor = getScaleFactor();
		return multigear.general.utils.KernelUtils.ref2d(ref2d.XAxis * scaleFactor, ref2d.YAxis * scaleFactor);
	}
}
