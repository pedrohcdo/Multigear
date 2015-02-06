package multigear.general.utils.buffers;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import multigear.general.utils.GeneralUtils;
import multigear.general.utils.Vector2;

/**
 * Global IntBuffer.<br><br>
 * This Class is Thread-Safe.
 * 
 * @author user
 *
 */
final public class GlobalIntBuffer {

	/**
	 * Item
	 * 
	 * @author user
	 *
	 */
	final private static class Item {
		
		private IntBuffer mBuffer;
		private boolean mUsing = false;
	}
	
	// Final Static Variables
	final private static List<Item> mItems = new ArrayList<Item>();
	final private static Object mLock = new Object();
	
	/** Is is module */
	private GlobalIntBuffer() {}
	
	/**
	 * Obtain IntBuffer with position 0 and limit is greater than or equal <b>needLength</b>
	 * 
	 * @return Global IntBuffer
	 */
	final public static IntBuffer obtain(final int needLength) {
		synchronized (mLock) {
			for(final Item item : mItems) {
				if(!item.mUsing) {
					realloc(item, needLength);
					item.mUsing = true;
					return item.mBuffer;
				}
			}
			final Item item = new Item();
			item.mBuffer = GeneralUtils.createIntBuffer(needLength);
			item.mUsing = true;
			mItems.add(item);
			return item.mBuffer;
		}
	}
	
	/**
	 * Obtain IntBuffer with points values (x1, y1, x2, y2, ..etc) and set position 0, 
	 * the limit is greater than or equal <b>points.lenght*2</b>
	 * 
	 * @return Global IntBuffer
	 */
	final public static IntBuffer obtain(final Vector2[] points) {
		final IntBuffer buffer = obtain(points.length * 2);
		for(final Vector2 vector : points) {
			buffer.put((int)vector.x);
			buffer.put((int)vector.y);
		}
		buffer.position(0);
		return buffer;
	}
	
	/**
	 * Obtain IntBuffer with all the remaining ints of 
	 * the src int buffer to this buffer's current position.
	 * The position of the src is not changed.
	 * 
	 * @return Global IntBuffer
	 */
	final public static IntBuffer obtain(final IntBuffer buffer) {
		final IntBuffer newBuffer = obtain(buffer.remaining());
		final int lastPosition = buffer.position();
		newBuffer.put(buffer);
		buffer.position(lastPosition);
		newBuffer.position(0);
		return newBuffer;
	}
	
	/**
	 * Obtain IntBuffer with int values and set position 0, 
	 * the limit is greater than or equal <b>needLength</b>
	 * 
	 * @return Global IntBuffer
	 */
	final public static IntBuffer obtain(final int[] values) {
		final IntBuffer buffer = obtain(values.length);
		buffer.put(values);
		buffer.position(0);
		return buffer;
	}
	
	/**
	 * Release FLoatBuffer for other uses
	 */
	final public static void release(final IntBuffer buffer) {
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
		if(length > item.mBuffer.limit())
			item.mBuffer = GeneralUtils.createIntBuffer((int)(length * 1.1f));
		item.mBuffer.position(0);
	}
}