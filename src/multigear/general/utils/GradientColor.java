package multigear.general.utils;

import android.graphics.Path.Direction;

/**
 * Gradient Color
 * 
 * @author user
 *
 */
final public class GradientColor {
	
	/**
	 * 
	 * @author user
	 *
	 */
	final public class InterpolatedBlock {
		
		// Private Variables
		private Color[] colors;
		
		/**
		 * Get Color
		 * 
		 * @param index
		 * @return
		 */
		final public Color getColor(final int index) {
			if(index < 0 || index >= colors.length)
				throw new IndexOutOfBoundsException();
			return colors[index];
		}
	}
	
	// Conts
	final public static int GUIDE_VERTICAL = 0;
	final public static int GUIDE_HORIZONTAL = 1;
	
	// Final Private Variables
	final private Color[] mColors;
	final private int mGuide;
	
	/**
	 * COnstructor
	 * 
	 * @param color1
	 * @param color2
	 */
	public GradientColor(final Color color1, final Color color2, final int guide) {
		mColors = new Color[] {color1, color2};
		mGuide = guide;
	}
	
	/**
	 * Get Guide
	 * @return
	 */
	final public int getGuide() {
		return mGuide;
	}
	
	/**
	 * Interpolate
	 * @param blocks
	 */
	final public InterpolatedBlock[] interpolate(int size) {
		if(size == 0)
			return new InterpolatedBlock[0];
		final InterpolatedBlock[] blocks = new InterpolatedBlock[size];
		switch(mGuide) {
		case GUIDE_VERTICAL:
		{
			final Color[] vertices = new Color[4];
			vertices[0] = mColors[0];
			vertices[1] = mColors[0];
			vertices[2] = mColors[1];
			vertices[3] = mColors[1];
			final InterpolatedBlock block = new InterpolatedBlock();
			block.colors = vertices;
			for(int i=0; i<size; i++)
				blocks[i] = block;
		}
			break;
		case GUIDE_HORIZONTAL:
			for(int i=1; i<=size; i++) {
				final Color left = GeneralUtils.mix(mColors[0], mColors[1], (i - 1) / (size * 1.0f));
				final Color right = GeneralUtils.mix(mColors[0], mColors[1], i / (size * 1.0f));
				final Color[] vertices = new Color[4];
				vertices[0] = left;
				vertices[1] = right;
				vertices[2] = right;
				vertices[3] = left;
				final InterpolatedBlock block = new InterpolatedBlock();
				block.colors = vertices;
				blocks[i-1] = block;
			}
		}
		return blocks;
	}
}
