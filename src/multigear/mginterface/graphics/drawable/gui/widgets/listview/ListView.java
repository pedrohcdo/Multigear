package multigear.mginterface.graphics.drawable.gui.widgets.listview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import multigear.general.utils.Color;
import multigear.general.utils.Vector2;
import multigear.mginterface.engine.eventsmanager.GlobalClock;
import multigear.mginterface.graphics.animations.AnimationOpacity;
import multigear.mginterface.graphics.drawable.gui.Canvas;
import multigear.mginterface.graphics.drawable.gui.widgets.listview.ListViewAdapter.ItemHolder;
import multigear.mginterface.graphics.drawable.polygon.Polygon;
import multigear.mginterface.graphics.drawable.sprite.Sprite;
import multigear.mginterface.graphics.drawable.widget.Widget;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.drawer.WorldMatrix;
import multigear.mginterface.graphics.opengl.texture.Texture;
import multigear.mginterface.scene.Scene;
import multigear.mginterface.scene.components.receivers.Drawable;
import multigear.mginterface.tools.touch.ImpulseDetector;
import multigear.mginterface.tools.touch.ImpulseDetectorListener;
import multigear.mginterface.tools.touch.PullDetector;
import multigear.mginterface.tools.touch.PullDetectorListener;
import multigear.mginterface.tools.touch.TouchEventsDetector;
import multigear.mginterface.tools.touch.TouchEventsDetectorListener;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Simple Dialog
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class ListView extends Widget {
	
	
	/**
	 * Pointer
	 * 
	 * @author user
	 *
	 */
	final private class Pointer {
		
		int id;
		Vector2 framePosition;
	}
	
	/**
	 * Touch Events Detector
	 */
	private TouchEventsDetector mTouchEventsDetector = new TouchEventsDetector(new TouchEventsDetectorListener() {
		
		@Override
		public void onTouch(int pointerCount) {
			if(pointerCount == 1) {
				mReleasedToImpulse = false;
				mPullStarted = true;
				mPull = 0;
				mSmoth = 0;
			}
		}
		
		@Override
		public void onUntouch(int pointerCount) {
			if(pointerCount == 0) {
				mScroll = getFinalPosition();
				mPullStarted = false;
				mClickLock = true;
				mPointers.clear();
				updatePosition();
			}
		}
	});
	
	/**
	 * Pull Detector
	 */
	final private PullDetector mPullDetector = new PullDetector(new PullDetectorListener() {
		
		@Override
		final public void onPull(Vector2 start, Vector2 end) {
			end = transformPosition(end);
			start = transformPosition(start);
			switch(mOrientation) {
			case HORIZONTAL:
				onPullHorizontal(start, end);
				break;
			case VERTICAL:
				onPullVertical(start, end);
				break;
			}
		}
		
		/**
		 * Vertical pull
		 * @param start
		 * @param end
		 */
		public void onPullVertical(final Vector2 start, final Vector2 end) {
			final float pull = end.y - start.y;
			if(mClickLock) {
				if(Math.abs(pull) >= PRESS_MARK * mScene.getDensity()) {
					mClickLock = false;
					mClickLockPhase = 0;
					mPointers.clear();
					// Unselect
					if(mStyle == Style.CLICKABLE) {
						mCursorLayer.setOpacity(0);
						mIndex = -1;
					}
					mPullDetector.reset();
					mReleasedToImpulse = true;
					// Cancel items holder touch
					MotionEvent cancell = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0);
					if(mSelectListAdapter != null) {
						for(int i=0; i<mItemsHolder.length; i++) {
							mItemsHolder[i].touch(cancell);
						}
					}
					cancell.recycle();
				}
			} else {
				mPull = pull;
				updatePosition();
			}
		}
		
		/**
		 * Horizontal Pull
		 * @param start
		 * @param end
		 */
		public void onPullHorizontal(final Vector2 start, final Vector2 end) {
			final float pull = end.x - start.x;
			if(mClickLock) {
				if(Math.abs(pull) >= PRESS_MARK * mScene.getDensity()) {
					mClickLock = false;
					mClickLockPhase = 0;
					mPointers.clear();
					// Unselect
					if(mStyle == Style.CLICKABLE) {
						mCursorLayer.setOpacity(0);
						mIndex = -1;
					}
					mPullDetector.reset();
					mReleasedToImpulse = true;
					// Cancel items holder touch
					MotionEvent cancell = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0);
					if(mSelectListAdapter != null) {
						for(int i=0; i<mItemsHolder.length; i++) {
							mItemsHolder[i].touch(cancell);
						}
					}
					cancell.recycle();
				}
			} else {
				mPull = pull;
				updatePosition();
			}
		}
	});
	
 	/**
	 * Drag Detector
	 */
	private ImpulseDetector mImpulseDetector;
	private ImpulseDetectorListener mImpulseDetectorListener = new ImpulseDetectorListener() {
		
		@Override
		public void onImpulse(Vector2 draged) {
			draged = transformVector(draged);
			
			switch(mOrientation) {
			case HORIZONTAL:
				if(mReleasedToImpulse) {
					mSmoth = (mSmoth + (draged.x / mScene.getDensity()));
					mReleasedToImpulse = false;
				}
				break;
			case VERTICAL:
				if(mReleasedToImpulse) {
					mSmoth = (mSmoth + (draged.y / mScene.getDensity()));
					mReleasedToImpulse = false;
				}
				break;
			}
		}
	};
	
	/**
	 * List Style
	 * 
	 * @author user
	 *
	 */
	public enum Style {
		
		/* Conts */
		SELECTABLE,
		CLICKABLE,
		UNSELECTABLE;
	}
	
	/**
	 * Orientation
	 * 
	 * @author user
	 *
	 */
	public enum Orientation {
		VERTICAL,
		HORIZONTAL;
	}
	
	/**
	 * Drawing Holder type
	 * @author user
	 *
	 */
	public enum DrawingHolder {
		
		/* Conts */
		BACKGROUND,
		ITEM;
	}
	
	/**
	 * Attributes
	 * 
	 * @author user
	 *
	 */
	final public static class Attributes {
		public float border;
		public float padding;
	}
	

	// Constants
	final private float MIN_VERTICAL_SCROLL_SIZE = 30;
	final private float PRESS_MARK = 10;
	
	final private int VERTICAL_SCROLL_ANIM_APPEAR = 0;
	final private int VERTICAL_SCROLL_ANIM_DISAPPEAR = 1;
	
	//
	final public static int TOUCH_UNUSE = 0;
	final public static int TOUCH_CONSUME = 1;
	final public static int TOUCH_INTERCEPT = 2;
	
	// Final Private Variables
	private Drawable mBackLayer;
	private Polygon mLimitLayerTop, mLimitLayerBottom;
	private ListViewAdapter mSelectListAdapter;
	private Polygon mCursorLayer, mScrollLayer;
	private Polygon mStencil;
	
	final private List<Pointer> mPointers = new ArrayList<Pointer>();
	
	// Private Variables
	private int mIndex = -1;
	private ListViewListener mSelectListener;
	private Scene mScene;
	private float mPull = 0;
	private boolean mReleasedToImpulse = false;
	private boolean mPullStarted = false;
	private float mScroll = 0;
	private float mSmoth = 0;
	private float mScrollSize;
	private int mScrollAnimation = -1;
	private long mLastTime = GlobalClock.currentTimeMillis();
	private float mLastScroll = 0;
	private boolean mClickLock = true;
	private int mClickLockPhase = 0;
	private long mClickLockWait = 0;
	private float mDrawPosition = 0;
	private Attributes mAttributes = new Attributes();
	private Style mStyle = Style.SELECTABLE;
	private Orientation mOrientation = Orientation.VERTICAL;
	private ItemHolder[] mItemsHolder;
	private int mItemsCount = 0;
	private boolean mScrollable = true;
	
	/**
	 * Constructor
	 * @param scene
	 */
	public ListView(Scene scene, final Vector2 size) {
		super();
		mImpulseDetector = new ImpulseDetector(scene.getDensity(), mImpulseDetectorListener);
		mScene = scene; 
		mIndex = -1;
		mPosition = new Vector2(0, 0);
		mSelectListener = null;
		// Set Attributes
		mAttributes.border = mScene.getDensityParser().smallerValue(30);
		mAttributes.padding = mScene.getDensityParser().smallerValue(25);
		measure(size);
	}
	
	/**
	 * Constructor
	 * @param scene
	 * @param attributes
	 */
	public ListView(final Scene scene, final Attributes attributes, final Vector2 size) {
		super();
		mImpulseDetector = new ImpulseDetector(scene.getDensity(), mImpulseDetectorListener);
		mScene = scene; 
		mIndex = -1;
		mPosition = new Vector2(0, 0);
		mSelectListener = null;
		mAttributes.border = attributes.border;
		mAttributes.padding = attributes.padding;
		measure(size);
	}
	
	/**
	 * Set Size
	 * 
	 */
	@Override
	public void setSize(Vector2 size) {
		measure(size);
	}
	
	/**
	 * Set Scrollable
	 * @param scrollable
	 */
	final public void setScrollable(final boolean scrollable) {
		mScrollable = scrollable;
		if(!mScrollable) {
			mImpulseDetector.reset();
			mPullDetector.reset();
			mTouchEventsDetector.reset();
		}
	}
	
	/**
	 * Reset
	 */
	final public void reset() {
		mImpulseDetector.reset();
		mPullDetector.reset();
		mTouchEventsDetector.reset();
		mPull = 0;
		mPullStarted = false;
		mClickLock = true;
		mClickLockPhase = 0;
		mClickLockWait = 0;
		mCursorLayer.setOpacity(0);
		reshapeScroll();
		setScrollAnim(VERTICAL_SCROLL_ANIM_DISAPPEAR);
		mScrollLayer.getAnimationStack().clear();
		mScrollLayer.getAnimationStack().addAnimation(new AnimationOpacity(1, 0, 0));
		mScrollLayer.getAnimationStack().start();
		mPointers.clear();
	}
	
	/**
	 * Set Scroll position
	 */
	final public void setScrollPosition(float position) {
		mScroll = Math.max(0, Math.min(position, getMaxScroll())) * -1;
		mLastScroll = mScroll;
		updatePosition();
		if(mScrollable)
			setScrollAnim(VERTICAL_SCROLL_ANIM_APPEAR);
	}
	
	/**
	 * Get Index
	 * 
	 * @return
	 */
	final public int getIndex() {
		return mIndex;
	}
	
	/**
	 * Get Scroll Position
	 * @return
	 */
	final public float getScrollPosition() {
		return mScroll * -1;
	}
	
	/**
	 * Get Scrollable
	 * @return
	 */
	final public boolean getScrollable() {
		return mScrollable;
	}
	
	/**
	 * Set List Adapater
	 * @param adapter
	 */
	final public void setAdapter(final ListViewAdapter adapter) {
		if(adapter == null) {
			mSelectListAdapter = null;
			mCursorLayer.setOpacity(0);
			mIndex = -1;
			reshapeScroll();
			return;
		}
		mSelectListAdapter = adapter;
		mItemsCount = mSelectListAdapter.getCount();
		mItemsHolder = new ItemHolder[mItemsCount];
		for(int i=0; i<mItemsCount; i++)
			mItemsHolder[i] = mSelectListAdapter.createItem(i, null);
		reshapeScroll();
	}
	
	/**
	 * Reset Adapter and reset items
	 */
	final public void resetAdapter() {
		if(mSelectListAdapter == null)
			return;
		mItemsCount = mSelectListAdapter.getCount();
		final ItemHolder[] lastItemsHolder = mItemsHolder;
		mItemsHolder = new ItemHolder[mItemsCount];
		for(int i=0; i<mItemsCount; i++) {
			if(i < lastItemsHolder.length)
				mItemsHolder[i] = mSelectListAdapter.createItem(i, lastItemsHolder[i]);
			else
				mItemsHolder[i] = mSelectListAdapter.createItem(i, null);
		}
		if(lastItemsHolder.length != mItemsCount)
			reshapeScroll();
	}
	
	/**
	 * Set list type
	 * @param type
	 */
	final public void setStyle(final Style type) {
		mStyle = type;
	}
	
	/**
	 * Set Orientation
	 * 
	 * @param orientation
	 */
	final public void setOrientation(final Orientation orientation) {
		mOrientation = orientation;
		reshapeScroll();
	}
	
	/**
	 * Get Adapter
	 * @return
	 */
	final public ListViewAdapter getAdapter() {
		return mSelectListAdapter;
	}
	
	/**
	 * Get list type
	 * @param type
	 */
	final public Style getStyle() {
		return mStyle;
	}
	
	/**
	 * Get Orientation
	 * @return
	 */
	final public Orientation getOrientation() {
		return mOrientation;
	}
	
	/**
	 * Set Limit Drawable Layer
	 * @param drawable
	 */
	final public void setLimitTexture(final Texture texture) {
		float b = mAttributes.border;
		float w = getSize().x - mAttributes.border * 2;
		float h = w / texture.getSize().aspectRatio();
		
		mLimitLayerTop = Polygon.createRectangle(new Vector2(w, h));
		mLimitLayerBottom = Polygon.createRectangle(new Vector2(w, h));
		mLimitLayerBottom.setMirror(false, true);
		
		mLimitLayerTop.setTexture(texture);
		mLimitLayerBottom.setTexture(texture);
		
		mLimitLayerTop.setPosition(new Vector2(b, b));
		mLimitLayerBottom.setPosition(new Vector2(b, getSize().y - b));
	}
	
	/**
	 * Set Background Drawable
	 * @param back
	 */
	final public void setBackground(final Drawable bg) {
		mBackLayer = bg;
	}
	
	/**
	 * Set Background
	 * @param text
	 */
	final public void setBackground(final Texture text) {
		final Sprite sprite = new Sprite();
		sprite.setTexture(text);
		sprite.setSize(mSize);
		mBackLayer = sprite;
	}
	
	/**
	 * Set Background
	 * @param color
	 */
	final public void setBackground(final Color color) {
		final Polygon polygon = Polygon.createRectangle(mSize);
		polygon.setColor(color);
		mBackLayer = polygon;
	}
	
	/**
	 * Set Background
	 * @param color
	 */
	final public void setBackground(final Color color, float radius, float detail) {
		final Polygon polygon = Polygon.createRoundedRectangle(mSize, radius, detail);
		polygon.setColor(color);
		mBackLayer = polygon;
	}
	
	/**
	 * Set Scroll Color
	 * @param color
	 */
	final public void setScrollColor(final Color color) {
		mScrollLayer.setColor(color);
	}
	
	/**
	 * Set Select Listener
	 * 
	 * @param selectListener
	 */
	final public void setSelectListener(final ListViewListener selectListener) {
		mSelectListener = selectListener;
	}
	
	/**
	 * Measure this Header by the Size
	 * 
	 * @param size
	 *            Size
	 */
	final private void measure(final Vector2 size) {
		super.setSize(size.clone());
		setupStencil();
		
		setupCursor();
		setupScroll();
		setBackground(Color.TRANSPARENT);
	}
	
	/*
	 * Setup Cursor
	 */
	final public void setupCursor() {
		
		mCursorLayer = new Polygon();
		mCursorLayer.setColor(Color.WHITE);
		mCursorLayer.setOpacity(0.7f);

	}
	
	/**
	 * Refresh Cursor
	 */
	final public void refreshCursor(final float cell) {
		mCursorLayer.clearVertices();
		switch(mOrientation) {
		case HORIZONTAL:
			mCursorLayer.addVertices(Polygon.createRoundedRectangle(new Vector2(cell, getSize().y - mAttributes.border * 2), mAttributes.border, 10));
			break;
		case VERTICAL:
			mCursorLayer.addVertices(Polygon.createRoundedRectangle(new Vector2(getSize().x - mAttributes.border * 2, cell), mAttributes.border, 10));
			break;
		
		}
		
	}
	
	/**
	 * Setup Stencil
	 */
	final public void setupStencil() {
		mStencil = Polygon.createRectangle(new Vector2(mSize.x - mAttributes.border * 2, mSize.y - mAttributes.border * 2));
		mStencil.setPosition(new Vector2(mAttributes.border, mAttributes.border));
	}
	
	/**
	 * Setup Scroll
	 */
	final private void setupScroll() {
		Vector2 size;
		switch(mOrientation) {
		case HORIZONTAL:
			mScrollSize = getSize().x - mAttributes.border * 2;
			size = new Vector2(mScrollSize, mScene.getDensityParser().smallerValue(10));
			mScrollLayer = Polygon.createRoundedRectangle(size, mScene.getDensityParser().smallerValue(5), 10);
			mScrollLayer.setPosition(new Vector2(mAttributes.border, getSize().y - (size.y + mAttributes.border)));
			mScrollLayer.setColor(Color.WHITE);
			mScrollLayer.setOpacity(0);
			mScrollLayer.setZ(99999);
			updateScroll();
			break;
		case VERTICAL:
			mScrollSize = getSize().y - mAttributes.border * 2;
			size = new Vector2(mScene.getDensityParser().smallerValue(10), mScrollSize);
			mScrollLayer = Polygon.createRoundedRectangle(size, mScene.getDensityParser().smallerValue(5), 10);
			mScrollLayer.setPosition(new Vector2(getSize().x - (size.x + mAttributes.border), mAttributes.border));
			mScrollLayer.setColor(Color.WHITE);
			mScrollLayer.setOpacity(0);
			mScrollLayer.setZ(99999);
			updateScroll();
			break;
		
		}
		
	}
	
	/**
	 * Reshape Scroll
	 */
	final private void reshapeScroll() {
		float minSize = mScene.getDensityParser().smallerValue(MIN_VERTICAL_SCROLL_SIZE);
		float contentSize;
		float finalSize;
		Vector2 size;
		switch(mOrientation) {
		case HORIZONTAL:
			contentSize = getContentSize();
			finalSize = getSize().x - mAttributes.border * 2;
			if(contentSize != -1)
				finalSize = (finalSize / contentSize) * getSize().y;
			finalSize = Math.max(minSize, finalSize);
			mScrollSize = finalSize;
			size = new Vector2(finalSize, mScene.getDensityParser().smallerValue(10));
			mScrollLayer.setPosition(new Vector2(mAttributes.border, getSize().y - (size.y + mAttributes.border)));
			mScrollLayer.clearVertices();
			mScrollLayer.addVertices(Polygon.createRoundedRectangle(size, mScene.getDensityParser().smallerValue(5), 10));
			setScrollAnim(VERTICAL_SCROLL_ANIM_APPEAR);
			break;
		case VERTICAL:
			contentSize = getContentSize();
			finalSize = getSize().y - mAttributes.border * 2;
			if(contentSize != -1)
				finalSize = (finalSize / contentSize) * getSize().y;
			finalSize = Math.max(minSize, finalSize);
			mScrollSize = finalSize;
			size = new Vector2(mScene.getDensityParser().smallerValue(10), finalSize);
			mScrollLayer.setPosition(new Vector2(getSize().x - (size.x + mAttributes.border), mAttributes.border));
			mScrollLayer.clearVertices();
			mScrollLayer.addVertices(Polygon.createRoundedRectangle(size, mScene.getDensityParser().smallerValue(5), 10));
			setScrollAnim(VERTICAL_SCROLL_ANIM_APPEAR);
			break;
		}
	}
	
	/**
	 * Add pointer
	 * 
	 * @param id
	 * @param touch
	 */
	final private void addPointer(final MotionEvent touch) {
		final int index = MotionEventCompat.getActionIndex(touch);
		final int id = MotionEventCompat.getPointerId(touch, index);
		final Vector2 position = new Vector2(MotionEventCompat.getX(touch, index), MotionEventCompat.getY(touch, index));
		removePointer(id);
		final Pointer pointer = new Pointer();
		pointer.id = id;
		pointer.framePosition = position;
		mPointers.add(pointer);
	}
	
	/**
	 * Remove Pointer
	 * @param id
	 */
	final private Pointer removePointer(final MotionEvent touch) {
		final int index = MotionEventCompat.getActionIndex(touch);
		final int id = MotionEventCompat.getPointerId(touch, index);
		return removePointer(id);
	}
	
	/**
	 * Remove Pointer
	 * @param id
	 */
	final private Pointer removePointer(final int id) {
		Pointer returnPointer = null;
		final Iterator<Pointer> iterator = mPointers.iterator();
		while(iterator.hasNext()) {
			Pointer pointer = iterator.next();
			if(pointer.id == id) {
				iterator.remove();
				returnPointer = pointer;
			}
		}
		return returnPointer;
	}
	
	/*
	 * Touch
	 */
	@Override
	public void onTouch(MotionEvent motionEvent) {
		if(mSelectListAdapter == null || mItemsCount == 0)
			return;

		boolean itemConsumed = false;
		boolean itemIntercepted = false;
		
		if(mClickLock) {
			
			
			// Processs Itens click
			float filling = mDrawPosition;
			float min;
			float max;
			
			switch(mOrientation) {
			case HORIZONTAL:
				min = 0;
				max = getSize().x - mAttributes.border * 2;
				
				for(int index=0; index<mItemsCount; index++) {
					ListViewAdapter.ItemHolder item = mItemsHolder[index];
					float cellSize = item.getHeight() + mAttributes.padding * 2;
					
					float left = filling;
					float right = left + cellSize;
					
					if(left < max && right >= min) {
						int uTouch = item.touch(motionEvent);
						if((uTouch & TOUCH_CONSUME) == TOUCH_CONSUME) {
							mClickLockPhase = 0;
							if(mStyle == Style.CLICKABLE) {
								mCursorLayer.setOpacity(0);
								mIndex = 0;
							}
							itemConsumed = true;
						}
						if((uTouch & TOUCH_INTERCEPT) == TOUCH_INTERCEPT) {
							itemIntercepted = true;
							mTouchEventsDetector.reset();
							mPullDetector.reset();
							mImpulseDetector.reset();
						}
					} else if(left >= max) {
						break;
					}
					filling += cellSize;
				}
				
				break;
			case VERTICAL:
				min = 0;
				max = getSize().y - mAttributes.border * 2;
				
				for(int index=0; index<mItemsCount; index++) {
					ListViewAdapter.ItemHolder item = mItemsHolder[index];
					float cellSize = item.getHeight() + mAttributes.padding * 2;
					
					float top = filling;
					float bottom = top + cellSize;
					
					if(top < max && bottom >= min) {
						int uTouch = item.touch(motionEvent);
						if((uTouch & TOUCH_CONSUME) == TOUCH_CONSUME) {
							mClickLockPhase = 0;
							if(mStyle == Style.CLICKABLE) {
								mCursorLayer.setOpacity(0);
								mIndex = 0;
							}
							itemConsumed = true;
						}
						if((uTouch & TOUCH_INTERCEPT) == TOUCH_INTERCEPT) {
							itemIntercepted = true;
							mTouchEventsDetector.reset();
							mPullDetector.reset();
							mImpulseDetector.reset();
						}
					} else if(top >= max) {
						break;
					}
					filling += cellSize;
				}
				
				break;
			
			}
			
			
			
			// If item not consume touch, process in this list
			if(!itemConsumed && mStyle != Style.UNSELECTABLE) {
				switch(MotionEventCompat.getActionMasked(motionEvent)) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					addPointer(motionEvent);
					mClickLockPhase = 1;
					mClickLockWait = GlobalClock.currentTimeMillis();
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					final Pointer pointer = removePointer(motionEvent);
	
					if(mStyle == Style.SELECTABLE) {
						if(mPointers.size() == 0 && pointer != null)
							selectItem(pointer);
						mClickLockPhase = 0;
					} else if(mStyle == Style.CLICKABLE) {
						// No pressed
						// Post Click
						if(mClickLockPhase == 1) {
							mIndex = -1;
							cursorToItem(pointer);
							mClickLockPhase = 3;
							
							Log.d("LogTest", "Index: " + mIndex);
							
							
							mClickLockWait = GlobalClock.currentTimeMillis();
						// Pressing + release, perform click
						} else if(mClickLockPhase == 2) {
							if(mSelectListener != null && mIndex >= 0)
								mSelectListener.onSelect(mIndex);
							mCursorLayer.setOpacity(0);
							mIndex = -1;
							mClickLockPhase = 0;
						}
					}
					
					break;
				case MotionEvent.ACTION_CANCEL:
					mPointers.clear();
					break;
				}
			}
			
		}
		
		if(!itemIntercepted && mScrollable) {
			mTouchEventsDetector.touch(motionEvent);
			mPullDetector.touch(motionEvent);
			mImpulseDetector.touch(motionEvent);
		}
		
	}
	
	/**
	 * Get filled items
	 * @return
	 */
	final private float getFilledItemsSize() {
		if(mSelectListAdapter == null)
			return 0;
		float filled = 0;
		for(int index=0; index<mItemsCount; index++) {
			final ListViewAdapter.ItemHolder item = mItemsHolder[index];
			filled += mAttributes.padding * 2 + item.getHeight();
		}
		return filled;
	}
	
	/**
	 * Get Index in Items
	 * @param position
	 * @return
	 */
	final private int getIndexInItems(final float position) {
		if(mSelectListAdapter == null)
			return 0;
		float fill = 0;
		for(int index=0; index<mItemsCount; index++) {
			final ListViewAdapter.ItemHolder item = mItemsHolder[index];
			fill += mAttributes.padding * 2 + item.getHeight();
			if(fill >= position)
				return index;
		}
		return mItemsCount-1;
	}
	
	/**
	 * Get items position by index
	 * @param index
	 * @return
	 */
	final private float getPositionIndex(final int index) {
		if(mSelectListAdapter == null)
			return 0;
		float position = 0;
		for(int j=0; j<index; j++) {
			final ListViewAdapter.ItemHolder item = mItemsHolder[j];
			position += mAttributes.padding * 2 + item.getHeight();
		}
		return position;
	}
	
	/**
	 * On Item Press
	 */
	final private void cursorToItem(final Pointer pointer) {
		if(mSelectListAdapter == null)
			return;
		
		// Prepare
		final float borderX = mAttributes.border;
		final float borderY = mAttributes.border;
		
		final Vector2 clickPos = transformPosition(pointer.framePosition);
		float filled;
		
		// Orient
		switch(mOrientation) {
		case HORIZONTAL:
			
			float maxX = (getSize().x - mAttributes.border);
			// do not use "padding * 2"
			filled = (mAttributes.border + getFilledItemsSize());
			if(filled <= maxX)
				maxX = filled;
			
			if(clickPos.y >= borderY && clickPos.y <= (getSize().y - mAttributes.border) && clickPos.x >= borderX && clickPos.x <= maxX) {
					
				final Vector2 normalizedPos = Vector2.sub(clickPos, new Vector2(borderX, borderY));
					
				final int index = (int)Math.max(0, Math.min(mItemsCount-1, getIndexInItems((normalizedPos.x ) - getFinalPosition())));
		
				if(mIndex != index) {
					final Vector2 cursorDesignedPos = new Vector2(getPositionIndex(index) + mAttributes.border, mAttributes.border);
					
					refreshCursor(mItemsHolder[index].getHeight() + mAttributes.padding * 2);
					mCursorLayer.setOpacity(0.7f);
					//mCursorLayer.setPosition(mCursorPosition);
					mCursorLayer.getAnimationStack().clear();
					mCursorLayer.getAnimationStack().addAnimation(new AnimationOpacity(50, 0, 1));
					mCursorLayer.getAnimationStack().start();
					
					final float cellSize = mAttributes.padding * 2 + mItemsHolder[index].getHeight();
					final float scroll = getFinalPosition();
					final float cursorPos = cursorDesignedPos.x + scroll;
					final float cursorPosH = cursorPos + cellSize;
					
					if(cursorPos < mAttributes.border)
						mScroll += mAttributes.border - cursorPos;
					else if(cursorPosH > (getSize().x - mAttributes.border))
						mScroll -= cursorPosH - (getSize().x - mAttributes.border);
					
					updatePosition();
					
					mIndex = index;
				}
			}
			break;
		case VERTICAL:
			float maxY = (getSize().y - mAttributes.border);
			// do not use "padding * 2"
			filled = (mAttributes.border + getFilledItemsSize());
			if(filled <= maxY)
				maxY = filled;
				
			if(clickPos.x >= borderX && clickPos.x <= (getSize().x - mAttributes.border) && clickPos.y >= borderY && clickPos.y <= maxY) {
					
				final Vector2 normalizedPos = Vector2.sub(clickPos, new Vector2(borderX, borderY));
					
				final int index = (int)Math.max(0, Math.min(mItemsCount-1, getIndexInItems((normalizedPos.y ) - getFinalPosition())));
		
				if(mIndex != index) {
					final Vector2 cursorDesignedPos = new Vector2(mAttributes.border, getPositionIndex(index) + mAttributes.border);
					
					refreshCursor(mItemsHolder[index].getHeight() + mAttributes.padding * 2);
					mCursorLayer.setOpacity(0.7f);
					//mCursorLayer.setPosition(mCursorPosition);
					mCursorLayer.getAnimationStack().clear();
					mCursorLayer.getAnimationStack().addAnimation(new AnimationOpacity(50, 0, 1));
					mCursorLayer.getAnimationStack().start();
					
					final float cellSize = mAttributes.padding * 2 + mItemsHolder[index].getHeight();
					final float scroll = getFinalPosition();
					final float cursorPos = cursorDesignedPos.y + scroll;
					final float cursorPosH = cursorPos + cellSize;
					
					if(cursorPos < mAttributes.border)
						mScroll += mAttributes.border - cursorPos;
					else if(cursorPosH > (getSize().y - mAttributes.border))
						mScroll -= cursorPosH - (getSize().y - mAttributes.border);
					
					updatePosition();
					
					mIndex = index;
				}
			}
			break;
		}
	}
	
	/**
	 * On Item Press
	 */
	final private void selectItem(final Pointer pointer) {
		if(mSelectListAdapter == null)
			return;
		
		// Prepare
		final float borderX = mAttributes.border;
		final float borderY = mAttributes.border;
		
		final Vector2 clickPos = transformPosition(pointer.framePosition);
		
		float filled;
		
		// Orient
		switch(mOrientation) {
		case HORIZONTAL:
			
			float maxX = getSize().x - mAttributes.border;
			// do not use "padding * 2"
			filled = mAttributes.border + getFilledItemsSize();
			if(filled <= maxX)
				maxX = filled;
			
			if(clickPos.y >= borderY && clickPos.y <= (getSize().y - mAttributes.border) && clickPos.x >= borderX && clickPos.x <= maxX) {
					
				final Vector2 normalizedPos = Vector2.sub(clickPos, new Vector2(borderX, borderY));
					
				final int index = (int)Math.max(0, Math.min(mItemsCount-1, getIndexInItems((normalizedPos.x ) - getFinalPosition())));
		
				if(mIndex != index) {
					final Vector2 cursorDesignedPos = new Vector2(getPositionIndex(index) + mAttributes.border, mAttributes.border);
					
					refreshCursor(mItemsHolder[index].getHeight() + mAttributes.padding * 2);
					mCursorLayer.setOpacity(0.7f);
					//mCursorLayer.setPosition(mCursorPosition);
					mCursorLayer.getAnimationStack().clear();
					mCursorLayer.getAnimationStack().addAnimation(new AnimationOpacity(50, 0, 1));
					mCursorLayer.getAnimationStack().start();
					
					final float cellSize = mAttributes.padding * 2 + mItemsHolder[index].getHeight();
					final float scroll = getFinalPosition();
					final float cursorPos = cursorDesignedPos.x + scroll;
					final float cursorPosH = cursorPos + cellSize;
					
					if(cursorPos < mAttributes.border)
						mScroll += mAttributes.border - cursorPos;
					else if(cursorPosH > (getSize().x - mAttributes.border))
						mScroll -= cursorPosH - (getSize().x - mAttributes.border);
					
					updatePosition();
					
					if(mSelectListener != null)
						mSelectListener.onSelect(index);
					mIndex = index;
				}
			}
			break;
		case VERTICAL:
			float maxY = (getSize().y - mAttributes.border);
			// do not use "padding * 2"
			filled = (mAttributes.border + getFilledItemsSize());
			if(filled <= maxY)
				maxY = filled;
				
			if(clickPos.x >= borderX && clickPos.x <= (getSize().x - mAttributes.border) && clickPos.y >= borderY && clickPos.y <= maxY) {
					
				final Vector2 normalizedPos = Vector2.sub(clickPos, new Vector2(borderX, borderY));
					
				final int index = (int)Math.max(0, Math.min(mItemsCount-1, getIndexInItems((normalizedPos.y ) - getFinalPosition())));
		
				if(mIndex != index) {
					final Vector2 cursorDesignedPos = new Vector2(mAttributes.border, getPositionIndex(index) + mAttributes.border);
					
					refreshCursor(mItemsHolder[index].getHeight() + mAttributes.padding * 2);
					mCursorLayer.setOpacity(0.7f);
					//mCursorLayer.setPosition(mCursorPosition);
					mCursorLayer.getAnimationStack().clear();
					mCursorLayer.getAnimationStack().addAnimation(new AnimationOpacity(50, 0, 1));
					mCursorLayer.getAnimationStack().start();
					
					final float cellSize = mAttributes.padding * 2 + mItemsHolder[index].getHeight();
					final float scroll = getFinalPosition();
					final float cursorPos = cursorDesignedPos.y + scroll;
					final float cursorPosH = cursorPos + cellSize;
					
					if(cursorPos < mAttributes.border)
						mScroll += mAttributes.border - cursorPos;
					else if(cursorPosH > (getSize().y - mAttributes.border))
						mScroll -= cursorPosH - (getSize().y - mAttributes.border);
					
					updatePosition();
					
					if(mSelectListener != null)
						mSelectListener.onSelect(index);
					mIndex = index;
				}
			}
			break;
		}
	}
	
	/**
	 * Update Pull
	 */
	final private float getFinalPosition() {
		if(!mPullStarted)
			return mScroll;
		
		
		float maxScroll = getMaxScroll();
		float peace = 0;
		float pulloff = mPull;
		float pullon = 0;
		
		// Orient
		switch(mOrientation) {
		case HORIZONTAL:
			peace = getSize().x * 0.2f;
			break;
		case VERTICAL:
			peace = getSize().y * 0.2f;
			break;
		}
		
		// Pull
		if(pulloff >= 0) {
			if(pulloff + mScroll > 0) {
				if(mScroll < 0) {
					pullon += mScroll * -1;
					pulloff = Math.max(0, pulloff + mScroll);
				}
				final float value = (float)(Math.pow(pulloff, 2) / (Math.pow(pulloff, 2) + Math.pow(peace, 2))) * (peace / 2);
				pullon += value;
			} else {
				pullon += pulloff;
			}
		} else {
			if(pulloff + mScroll < -maxScroll) {
				if(mScroll > -maxScroll) {
					final float diff = mScroll + maxScroll;
					pulloff = Math.min(0, pulloff + diff);
					pullon -= diff;
				}
				final float value = (float)(Math.pow(pulloff, 2) / (Math.pow(pulloff, 2) + Math.pow(peace, 2))) * (peace / 2);
				pullon -= value;
			} else {
				pullon += pulloff;
			}
		}
		
		return mScroll + pullon;
		
		
		
	}
	
	/**
	 * Get Scroll Max
	 * @return
	 */
	final public float getMaxScroll() {
		if(mSelectListAdapter == null)
			return 0;
		final float filled = (mAttributes.border * 2 + getFilledItemsSize());
		switch(mOrientation) {
		case HORIZONTAL:
			if(filled >= mSize.x)
				return filled - mSize.x;
			break;
		case VERTICAL:
			if(filled >= mSize.y)
				return filled - mSize.y;
			break;
		}
		return 0;
	}
	
	/**
	 * Get Content Size
	 * @return
	 */
	final private float getContentSize() {
		final float filled = getFilledItemsSize();
		switch(mOrientation) {
		case HORIZONTAL:
			if(filled >= mSize.x)
				return filled;
			break;
		case VERTICAL:
			if(filled >= mSize.y)
				return filled;
			break;
		}
		return -1;
	}
	
	/**
	 * Update Position
	 */
	final private void updatePosition() {
		mDrawPosition = getFinalPosition();
	}
	
	/**
	 * Update
	 */
	@Override
	protected void onUpdate() {
		float maxScroll = getMaxScroll();
		if(!mPullStarted) {

			if(mSmoth > 0) {
				if(mScroll > 0){
					mSmoth = 0;
				} else if(mScroll + mSmoth > 0) {
					mSmoth = Math.abs(mScroll);
				}
			} else if(mSmoth < 0) {
				if(mScroll < -maxScroll) {
					mSmoth = 0;
				} else if(mScroll + mSmoth < -maxScroll) {
					mSmoth = -maxScroll - mScroll;
				}
			}
			
			
			mScroll += mSmoth;
			mSmoth *= 0.95;

			
			if(mScroll > 0)
				mScroll = Math.max(0, mScroll - 10);
			else if(mScroll < -maxScroll)
				mScroll = Math.min(-maxScroll, mScroll + 10);
			
			updatePosition();
		}
		updateScroll();
		
		// If pressing
		if(mClickLock && mClickLockPhase == 1 && (GlobalClock.currentTimeMillis() - mClickLockWait) >= 150) {
			if(mPointers.size() == 1 && mStyle == Style.CLICKABLE)
				cursorToItem(mPointers.get(0));
			mClickLockPhase = 2;
		}
		
		// If post have click
		if(mClickLockPhase == 3 && (GlobalClock.currentTimeMillis() - mClickLockWait) >= 150) {
			if(mStyle == Style.CLICKABLE && mSelectListener != null && mIndex >= 0)
				mSelectListener.onSelect(mIndex);
			mIndex = -1;
			mCursorLayer.setOpacity(0);
			mClickLockPhase = 0;
		}
	}
	
	/**
	 * Draw List
	 */
	@Override
	protected void onDraw(final Drawer drawer, final DrawingLayer drawingLayer) {
		if(drawingLayer == DrawingLayer.LAYER_BOTTOM) {
			mBackLayer.draw(drawer);
			return;
		}
		
		if(mSelectListAdapter == null) {
			mScrollLayer.draw(drawer);
			return;
		}
		
		drawer.drawStencil(mStencil);
		
		WorldMatrix matrix = drawer.getWorldMatrix();
		matrix.push();
		
		switch(mOrientation) {
		case HORIZONTAL:
		{
			matrix.preTranslatef(mDrawPosition + mAttributes.border, mAttributes.border);
			
			float filling = mDrawPosition;
			float min = 0;
			float max = getSize().x - mAttributes.border * 2;
			
			float backHeight = getSize().y - mAttributes.border * 2;
			float itemHeight = backHeight - mAttributes.border * 2;
			
			//Canvas canvas = new Canvas(drawer);
			
			for(int index=0; index<mItemsCount; index++) {
				ListViewAdapter.ItemHolder item = mItemsHolder[index];
				float cellSize = item.getHeight() + mAttributes.padding * 2;
				
				float left = filling;
				float right = left + cellSize;
				
				if(left < max && right >= min) {
					item.draw(drawer, DrawingHolder.BACKGROUND, new Vector2(cellSize, backHeight));
					if(index == mIndex)
						mCursorLayer.draw(drawer);
					matrix.preTranslatef(mAttributes.padding, mAttributes.border);
					item.draw(drawer, DrawingHolder.ITEM, new Vector2(item.getHeight(), itemHeight));
					matrix.preTranslatef(item.getHeight() + mAttributes.padding, -mAttributes.border);
				} else if(left >= max) {
					break;
				} else
					matrix.preTranslatef(mAttributes.padding * 2 + item.getHeight(), 0);
				
				//if(index < (mItemsCount - 1))
				//	canvas.drawRect(Color.BLACK, new Vector2(), new Vector2(2, backHeight));

				
				filling += cellSize;
			}
			break;
		}
		case VERTICAL:
		{
			matrix.preTranslatef(mAttributes.border, mDrawPosition + mAttributes.border);
			
			
			float filling = mDrawPosition;
			float min = 0;
			float max = getSize().y - mAttributes.border * 2;
			
			float backWidth = getSize().x - mAttributes.border * 2;
			float itemWidth = backWidth - mAttributes.border * 2;
			
			//Canvas canvas = new Canvas(drawer);
			
			for(int index=0; index<mItemsCount; index++) {
				ListViewAdapter.ItemHolder item = mItemsHolder[index];
				float cellSize = item.getHeight() + mAttributes.padding * 2;
				
				float top = filling;
				float bottom = top + cellSize;
				
				if(top < max && bottom >= min) {
					item.draw(drawer, DrawingHolder.BACKGROUND, new Vector2(backWidth, cellSize));
					if(index == mIndex)
						mCursorLayer.draw(drawer);
					matrix.preTranslatef(mAttributes.border, mAttributes.padding);
					item.draw(drawer, DrawingHolder.ITEM, new Vector2(itemWidth, item.getHeight()));
					matrix.preTranslatef(-mAttributes.border, item.getHeight() + mAttributes.padding);
				} else if(top >= max) {
					break;
				} else
					matrix.preTranslatef(0, mAttributes.padding + item.getHeight() + mAttributes.padding);
				
				//if(index < (mItemsCount - 1))
				//	canvas.drawRect(Color.BLACK, new Vector2(), new Vector2(backWidth, 2));

				
				filling += cellSize;
			}
			break;
		}
		}
			
		matrix.pop();
		
		
		if(mLimitLayerTop != null) {
			
			mLimitLayerTop.draw(drawer);
			mLimitLayerBottom.draw(drawer);
			
			
			
		}
		
		drawer.eraseStencil(mStencil);
		
		
		
		mScrollLayer.draw(drawer);
	}
	
	/**
	 * Set Scroll Anim State
	 * @param state
	 */
	final private void setScrollAnim(final int state) {
		mLastTime = GlobalClock.currentTimeMillis();
		if(mScrollAnimation == state)
			return;
		mScrollAnimation = state;
		switch(mScrollAnimation) {
		case VERTICAL_SCROLL_ANIM_APPEAR:
			mScrollLayer.setOpacity(1);
			mScrollLayer.getAnimationStack().clear();
			mScrollLayer.getAnimationStack().addAnimation(new AnimationOpacity(200, 0, 1));
			mScrollLayer.getAnimationStack().start();
			break;
		case VERTICAL_SCROLL_ANIM_DISAPPEAR:
			mScrollLayer.getAnimationStack().clear();
			mScrollLayer.getAnimationStack().addAnimation(new AnimationOpacity(200, 1, 0));
			mScrollLayer.getAnimationStack().start();
			break;
		}
	}
	
	/**
	 * Update Scroll
	 */
	final private void updateScroll() {
		// Update Position and Measure
		final float minSize = mScene.getDensityParser().smallerValue(MIN_VERTICAL_SCROLL_SIZE);
		final float contentSize = getContentSize();
		final float maxScroll = getMaxScroll();
		final float finalScrollPosition = getFinalPosition();
		float finalScale = 0;
		Vector2 finalPosition;
		
		switch(mOrientation) {
		case HORIZONTAL:
			finalPosition = new Vector2(0, mScrollLayer.getPosition().y);
			if(contentSize == -1) {
				finalPosition.x = mAttributes.border;
				finalScale = 1;
			} else {
				float scaler = mScrollSize;
				float position = finalScrollPosition;
				
				if(position > 0)
					scaler += position;
				if(position < -maxScroll)
					scaler += ((-maxScroll) - position);
				
				position = Math.min(0, position);
				position = Math.max(-maxScroll, position);

				float finalSize = ((getSize().x - mAttributes.border * 2) / contentSize) * getSize().x;
				finalScale = Math.max(minSize, (finalSize / scaler) * mScrollSize) / mScrollSize;
				
				final float view = getSize().x - mAttributes.border * 2;
				finalPosition.x = mAttributes.border + ((position * -1) / maxScroll) * (view - (finalScale * mScrollSize));
			}
			mScrollLayer.setScale(new Vector2(finalScale, 1));
			mScrollLayer.setPosition(finalPosition);
			break;
		case VERTICAL:
			finalPosition = new Vector2(mScrollLayer.getPosition().x, 0);
			if(contentSize == -1) {
				finalPosition.y = mAttributes.border;
				finalScale = 1;
			} else {
				float scaler = mScrollSize;
				float position = finalScrollPosition;
				
				if(position > 0)
					scaler += position;
				if(position < -maxScroll)
					scaler += ((-maxScroll) - position);
				
				position = Math.min(0, position);
				position = Math.max(-maxScroll, position);

				float finalSize = ((getSize().y - mAttributes.border * 2) / contentSize) * getSize().y;
				finalScale = Math.max(minSize, (finalSize / scaler) * mScrollSize) / mScrollSize;
				
				final float view = getSize().y - mAttributes.border * 2;
				finalPosition.y = mAttributes.border + ((position * -1) / maxScroll) * (view - (finalScale * mScrollSize));
			}
			mScrollLayer.setScale(new Vector2(1, finalScale));
			mScrollLayer.setPosition(finalPosition);
			break;
		}
		
		
		// Update Animation
		// If you have a considerable movement
		if((Math.abs(mLastScroll - finalScrollPosition) > mAttributes.border / 3) || 
			(finalScrollPosition > 0 || finalScrollPosition < -maxScroll)) {
			setScrollAnim(VERTICAL_SCROLL_ANIM_APPEAR);
			mLastScroll = finalScrollPosition;
			mLastTime = GlobalClock.currentTimeMillis();
		}
		if((GlobalClock.currentTimeMillis() - mLastTime) >= 1000) {
			setScrollAnim(VERTICAL_SCROLL_ANIM_DISAPPEAR);
			mLastTime = GlobalClock.currentTimeMillis();
		}
		
	}
}
