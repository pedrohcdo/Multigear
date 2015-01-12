package multigear.general.utils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Global FloatBuffer.<br><br>
 * This Class is Thread-Safe.
 * 
 * @author user
 *
 */
final public class GlobalFloatBuffer {

	/**
	 * Item
	 * 
	 * @author user
	 *
	 */
	final private static class Item {
		
		private FloatBuffer mBuffer;
		private boolean mUsing = false;
	}
	
	// Final Static Variables
	final private static List<Item> mItems = new ArrayList<Item>();
	final private static Object mLock = new Object();
	
	/** Is is module */
	private GlobalFloatBuffer() {}
	
	/**
	 * Obtain FloatBuffer with position 0 and limit is greater than or equal <b>needLength</b>
	 * 
	 * @return Global FloatBuffer
	 */
	final public static FloatBuffer obtain(final int needLength) {
		synchronized (mLock) {
			for(final Item item : mItems) {
				if(!item.mUsing) {
					realloc(item, needLength);
					item.mUsing = true;
					return item.mBuffer;
				}
			}
			final Item item = new Item();
			item.mBuffer = GeneralUtils.createFloatBuffer(needLength);
			item.mUsing = true;
			mItems.add(item);
			return item.mBuffer;
		}
	}
	
	/**
	 * Obtain FloatBuffer with points values (x1, y1, x2, y2, ..etc) and set position 0, 
	 * the limit is greater than or equal <b>points.lenght*2</b>
	 * 
	 * @return Global FloatBuffer
	 */
	final public static FloatBuffer obtain(final Vector2[] points) {
		final FloatBuffer buffer = obtain(points.length * 2);
		for(final Vector2 vector : points) {
			buffer.put(vector.x);
			buffer.put(vector.y);
		}
		buffer.position(0);
		return buffer;
	}
	
	/**
	 * Obtain FloatBuffer with FloatBuffer and set position 0, 
	 * the limit is greater than or equal <b>buffer.position()</b>
	 * 
	 * @return Global FloatBuffer
	 */
	final public static FloatBuffer obtain(final FloatBuffer buffer) {
		final FloatBuffer newBuffer = obtain(buffer.position());
		for(int i=0; i<buffer.position(); i++) {
			newBuffer.put(buffer.get(i));
		}
		newBuffer.position(0);
		return newBuffer;
	}
	
	/**
	 * Obtain FloatBuffer with float values and set position 0, 
	 * the limit is greater than or equal <b>needLength</b>
	 * 
	 * @return Global FloatBuffer
	 */
	final public static FloatBuffer obtain(final float[] values) {
		final FloatBuffer buffer = obtain(values.length);
		for(final float value : values) {
			buffer.put(value);
		}
		buffer.position(0);
		return buffer;
	}
	
	/**
	 * Release FLoatBuffer for other uses
	 */
	final public static void release(final FloatBuffer buffer) {
		synchronized (mLock) {
			for(final Item item : mItems) {
				if(item.mBuffer == buffer) {
					item.mUsing = false;
					break;
				}
			}
		}
	}
	
	/**
	 * Realloc Item and set position 0
	 * @param item Item
	 * @param length Length of item buffer
	 */
	final private static void realloc(final Item item, final int length) {
		if(length > item.mBuffer.limit()) {
			final FloatBuffer buffer = item.mBuffer;
			item.mBuffer = GeneralUtils.createFloatBuffer((int)(length * 0.1f));
			buffer.position(0);
			item.mBuffer.put(buffer);
		}
		item.mBuffer.position(0);
	}
}
