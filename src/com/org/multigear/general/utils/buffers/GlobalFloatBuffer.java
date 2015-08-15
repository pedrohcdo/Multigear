package com.org.multigear.general.utils.buffers;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.org.multigear.general.utils.GeneralUtils;
import com.org.multigear.general.utils.Vector2;

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
	 * Obtain FloatBuffer with all the remaining floats of 
	 * the src float buffer to this buffer's current position.
	 * The position of the src is not changed.
	 * 
	 * @return Global FloatBuffer
	 */
	final public static FloatBuffer obtain(final FloatBuffer buffer) {
		final FloatBuffer newBuffer = obtain(buffer.remaining());
		final int lastPosition = buffer.position();
		newBuffer.put(buffer);
		buffer.position(lastPosition);
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
		buffer.put(values);
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
		if(length > item.mBuffer.limit())
			item.mBuffer = GeneralUtils.createFloatBuffer((int)(length * 1.1f));
		item.mBuffer.position(0);
	}
}