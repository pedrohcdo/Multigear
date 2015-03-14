package multigear.mginterface.graphics.drawable.gui.widgets.List;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import multigear.general.utils.Color;
import multigear.general.utils.Vector2;
import multigear.mginterface.engine.eventsmanager.GlobalClock;
import multigear.mginterface.graphics.animations.AnimationOpacity;
import multigear.mginterface.graphics.animations.AnimationSet;
import multigear.mginterface.graphics.drawable.gui.Canvas;
import multigear.mginterface.graphics.drawable.polygon.Polygon;
import multigear.mginterface.graphics.drawable.widget.Widget;
import multigear.mginterface.graphics.drawable.widget.WidgetPolygonLayer;
import multigear.mginterface.graphics.drawable.widget.WidgetSpriteLayer;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.drawer.WorldMatrix;
import multigear.mginterface.graphics.opengl.texture.Texture;
import multigear.mginterface.scene.Scene;
import multigear.mginterface.tools.touch.ImpulseDetector;
import multigear.mginterface.tools.touch.ImpulseDetectorListener;
import multigear.mginterface.tools.touch.PullDetector;
import multigear.mginterface.tools.touch.PullDetectorListener;
import multigear.mginterface.tools.touch.TouchEventsDetector;
import multigear.mginterface.tools.touch.TouchEventsDetectorListener;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

/**
 * Simple Dialog
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class SelectList extends Widget {
	
	
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
	private PullDetector mPullDetector = new PullDetector(new PullDetectorListener() {
		
		@Override
		public void onPull(Vector2 start, Vector2 end) {
			final float pull = end.y - start.y;
			if(mClickLock) {
				if(Math.abs(pull) >= PRESS_MARK * mScene.getDensity()) {
					mClickLock = false;
					mClickLockPhase = 0;
					mPointers.clear();
					// Unselect
					if(mType == Type.CLICKABLE) {
						mCursorLayer.setOpacity(0);
						mIndex = -1;
					}
					mPullDetector.reset();
					mReleasedToImpulse = true;
				}
			} else {
				mPull = pull;
				updatePosition();
			}
		}
	});
	
	/**
	 * List Type
	 * 
	 * @author user
	 *
	 */
	public enum Type{
		
		/* Conts */
		SELECTABLE,
		CLICKABLE;
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
	 * Drag Detector
	 */
	private ImpulseDetector mImpulseDetector;
	private ImpulseDetectorListener mImpulseDetectorListener = new ImpulseDetectorListener() {
		
		@Override
		public void onImpulse(Vector2 draged) {
			if(mReleasedToImpulse) {
				mSmoth = (mSmoth + (draged.y / mScene.getDensity()));
				mReleasedToImpulse = false;
			}
		}
	};
	
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
	final private float PRESS_MARK = 25;
	
	final private int VERTICAL_SCROLL_ANIM_APPEAR = 0;
	final private int VERTICAL_SCROLL_ANIM_DISAPPEAR = 1;
	
	// Final Private Variables
	private WidgetSpriteLayer mBackLayer;
	private SelectListAdapter mSelectListAdapter;
	private Polygon mCursorLayer, mVerticalScrollLayer;
	
	final private List<Pointer> mPointers = new ArrayList<Pointer>();
	
	// Private Variables
	private int mIndex = -1;
	private SelectListListener mSelectListener;
	private Scene mScene;
	private float mPull = 0;
	private boolean mReleasedToImpulse = false;
	private boolean mPullStarted = false;
	private float mScroll = 0;
	private float mSmoth = 0;
	private float mVerticalScrollHeight;
	private int mVerticalScrollAnimation = -1;
	private long mLastTime = GlobalClock.currentTimeMillis();
	private float mLastScroll = 0;
	private boolean mClickLock = true;
	private int mClickLockPhase = 0;
	private long mClickLockWait = 0;
	private float mDrawPosition = 0;
	private Attributes mAttributes = new Attributes();
	private Texture mBackTexture = null;
	private Type mType = Type.SELECTABLE;
	
	/**
	 * Constructor
	 * @param scene
	 */
	public SelectList(Scene scene, final Vector2 size) {
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
	public SelectList(final Scene scene, final Attributes attributes, final Vector2 size) {
		super();
		mImpulseDetector = new ImpulseDetector(scene.getDensity(), mImpulseDetectorListener);
		mScene = scene; 
		mIndex = -1;
		mPosition = new Vector2(0, 0);
		mSelectListener = null;
		mAttributes = attributes;
		measure(size);
	}

	/**
	 * Set List Adapater
	 * @param adapter
	 */
	final public void setAdapter(final SelectListAdapter adapter) {
		if(adapter == null) {
			mSelectListAdapter = null;
			mCursorLayer.setOpacity(0);
			mIndex = -1;
			reshapeVerticalScroll();
			return;
		}
		mSelectListAdapter = adapter;
		final float itemWidth = getSize().x;
		for(int i=0; i<mSelectListAdapter.getCount(); i++)
			mSelectListAdapter.createItem(i, itemWidth);
		reshapeVerticalScroll();
	}
	
	/**
	 * Set list type
	 * @param type
	 */
	final public void setType(final Type type) {
		mType = type;
	}
	
	/**
	 * Get list type
	 * @param type
	 */
	final public Type getType() {
		return mType;
	}
	
	/**
	 * Set Background texture
	 * @param back
	 */
	final public void setBackgroundTexture(final Texture back) {
		mBackLayer.setTexture(back);
		mBackLayer.setSize(mSize);
	}
	
	/**
	 * Set Select Listener
	 * 
	 * @param selectListener
	 */
	final public void setSelectListener(final SelectListListener selectListener) {
		mSelectListener = selectListener;
	}
	
	/**
	 * Measure this Header by the Size
	 * 
	 * @param size
	 *            Size
	 */
	final private void measure(final Vector2 size) {
		setSize(size.clone());
		setupBack();
		
		setupCursor();
		setupVerticalScroll();
		
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
	final public void refreshCursor(final float cellHeight) {
		mCursorLayer.clearVertices();
		mCursorLayer.addVertices(Polygon.createRoundedRectangle(new Vector2(getSize().x - mAttributes.border * 2, cellHeight), mAttributes.border));
	}
	
	/*
	 * Setup Back
	 */
	final public void setupBack() {
		mBackLayer = new WidgetSpriteLayer();
		//mBackLayer.setPosition(new Vector2(0, mHeaderLayer.getSize().y * 0.67f));
		
		addLayer(mBackLayer);
	}
	
	/**
	 * Setup Vertical Scroll
	 */
	final private void setupVerticalScroll() {
		mVerticalScrollHeight = getSize().y - mAttributes.border * 2;
		Vector2 size = new Vector2(mScene.getDensityParser().smallerValue(10), mVerticalScrollHeight);
		mVerticalScrollLayer = Polygon.createRoundedRectangle(size, mScene.getDensityParser().smallerValue(5));
		mVerticalScrollLayer.setPosition(new Vector2(getSize().x - (size.x + mAttributes.border), mAttributes.border));
		mVerticalScrollLayer.setColor(Color.WHITE);
		mVerticalScrollLayer.setOpacity(0);
		mVerticalScrollLayer.setZ(99999);
		updateVerticalScroll();
	}
	
	/**
	 * Reshape Vertical Scroll
	 */
	final private void reshapeVerticalScroll() {
		final float minSize = mScene.getDensityParser().smallerValue(MIN_VERTICAL_SCROLL_SIZE);
		final float contentSize = getContentHeight();
		float finalSize = getSize().y - mAttributes.border * 2;
		if(contentSize != -1)
			finalSize = (finalSize / contentSize) * getSize().y;
		finalSize = Math.max(minSize, finalSize);
		mVerticalScrollHeight = finalSize;
		Vector2 size = new Vector2(mScene.getDensityParser().smallerValue(10), finalSize);
		mVerticalScrollLayer.clearVertices();
		mVerticalScrollLayer.addVertices(Polygon.createRoundedRectangle(size, mScene.getDensityParser().smallerValue(5)));
		setVerticalScrollAnim(VERTICAL_SCROLL_ANIM_APPEAR);
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
		if(mSelectListAdapter == null || mSelectListAdapter.getCount() == 0)
			return;

		if(mClickLock) {
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

				if(mType == Type.SELECTABLE) {
					if(mPointers.size() == 0 && pointer != null)
						selectItem(pointer);
					mClickLockPhase = 0;
				} else if(mType == Type.CLICKABLE) {
					// No pressed
					// Post Click
					if(mClickLockPhase == 1) {
						cursorToItem(pointer);
						mClickLockPhase = 3;
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
		
		mTouchEventsDetector.touch(motionEvent);
		mPullDetector.touch(motionEvent);
		mImpulseDetector.touch(motionEvent);
		
	}
	
	/**
	 * Get filled items
	 * @return
	 */
	final private float getFilledItems() {
		if(mSelectListAdapter == null)
			return 0;
		float filled = 0;
		for(int index=0; index<mSelectListAdapter.getCount(); index++) {
			final SelectListAdapter.ItemHolder item = mSelectListAdapter.getItem(index);
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
		for(int index=0; index<mSelectListAdapter.getCount(); index++) {
			final SelectListAdapter.ItemHolder item = mSelectListAdapter.getItem(index);
			fill += mAttributes.padding * 2 + item.getHeight();
			if(fill >= position)
				return index;
		}
		return mSelectListAdapter.getCount()-1;
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
			final SelectListAdapter.ItemHolder item = mSelectListAdapter.getItem(j);
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
		
		final AnimationSet set = getAnimationStack().animateFrame();
		
		final Vector2 scale = Vector2.scale(set.getScale(), getScale());
		
		final float borderX = mAttributes.border * scale.x;
		final float borderY = mAttributes.border * scale.y;
		
		final Vector2 clickPos = Vector2.sub(pointer.framePosition, Vector2.sub(getPosition(), Vector2.scale(getCenter(), scale)));
		float maxY = (getSize().y - mAttributes.border) * scale.y;
		// do not use "padding * 2"
		final float filled = (mAttributes.border + getFilledItems()) * scale.y;
		if(filled <= maxY)
			maxY = filled;
			
		if(clickPos.x >= borderX && clickPos.x <= (getSize().x - mAttributes.border) * scale.x && clickPos.y >= borderY && clickPos.y <= maxY) {
				
			final Vector2 normalizedPos = Vector2.sub(clickPos, new Vector2(borderX, borderY));
				
			final int index = (int)Math.max(0, Math.min(mSelectListAdapter.getCount()-1, getIndexInItems((normalizedPos.y ) / scale.y - getFinalPosition())));
	
			if(mIndex != index) {
				final Vector2 cursorDesignedPos = new Vector2(mAttributes.border, getPositionIndex(index) + mAttributes.border);
				
				refreshCursor(mSelectListAdapter.getItem(index).getHeight() + mAttributes.padding * 2);
				mCursorLayer.setOpacity(0.7f);
				//mCursorLayer.setPosition(mCursorPosition);
				mCursorLayer.getAnimationStack().clear();
				mCursorLayer.getAnimationStack().addAnimation(new AnimationOpacity(50, 0, 1));
				mCursorLayer.getAnimationStack().start();
				
				final float cellSize = mAttributes.padding * 2 + mSelectListAdapter.getItem(index).getHeight();
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
	}
	
	/**
	 * On Item Press
	 */
	final private void selectItem(final Pointer pointer) {
		if(mSelectListAdapter == null)
			return;
		
		final AnimationSet set = getAnimationStack().animateFrame();
		
		final Vector2 scale = Vector2.scale(set.getScale(), getScale());
		
		final float borderX = mAttributes.border * scale.x;
		final float borderY = mAttributes.border * scale.y;
		
		final Vector2 clickPos = Vector2.sub(pointer.framePosition, Vector2.sub(getPosition(), Vector2.scale(getCenter(), scale)));
		float maxY = (getSize().y - mAttributes.border) * scale.y;
		// do not use "padding * 2"
		final float filled = (mAttributes.border + getFilledItems()) * scale.y;
		if(filled <= maxY)
			maxY = filled;
			
		if(clickPos.x >= borderX && clickPos.x <= (getSize().x - mAttributes.border) * scale.x && clickPos.y >= borderY && clickPos.y <= maxY) {
				
			final Vector2 normalizedPos = Vector2.sub(clickPos, new Vector2(borderX, borderY));
				
			final int index = (int)Math.max(0, Math.min(mSelectListAdapter.getCount()-1, getIndexInItems((normalizedPos.y ) / scale.y - getFinalPosition())));
	
			if(mIndex != index) {
				final Vector2 cursorDesignedPos = new Vector2(mAttributes.border, getPositionIndex(index) + mAttributes.border);
				
				refreshCursor(mSelectListAdapter.getItem(index).getHeight() + mAttributes.padding * 2);
				mCursorLayer.setOpacity(0.7f);
				//mCursorLayer.setPosition(mCursorPosition);
				mCursorLayer.getAnimationStack().clear();
				mCursorLayer.getAnimationStack().addAnimation(new AnimationOpacity(50, 0, 1));
				mCursorLayer.getAnimationStack().start();
				
				final float cellSize = mAttributes.padding * 2 + mSelectListAdapter.getItem(index).getHeight();
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
		} else {
			mIndex = -1;
			mCursorLayer.setOpacity(0);
		}
	}
	
	/**
	 * Update Pull
	 */
	final private float getFinalPosition() {
		if(!mPullStarted)
			return mScroll;
		
		
		float maxScroll = getMaxScroll();
		final float peace = getSize().y * 0.2f;
		float pulloff = mPull;
		float pullon = 0;
		
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
	final private float getMaxScroll() {
		if(mSelectListAdapter == null)
			return 0;
		final float filled = (mAttributes.border * 2 + getFilledItems());
		if(filled >= mSize.y)
			return filled - mSize.y;
		return 0;
	}
	
	/**
	 * Get Content Size
	 * @return
	 */
	final private float getContentHeight() {
		final float filled = getFilledItems();
		if(filled >= mSize.y)
			return filled;
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
		final AnimationSet set = getAnimationStack().animateFrame();
		final Vector2 scale = Vector2.scale(set.getScale(), getScale());
		final float x = getPosition().x - getCenter().x * scale.x;
		final float y = getPosition().y - getCenter().y * scale.y;
		final float borderX = mAttributes.border * scale.x;
		final float borderY = mAttributes.border * scale.y;
		mCursorLayer.setViewport((int)(borderX + x), (int)(borderY + y), (int)(getSize().x * scale.x - borderX * 2), (int)(getSize().y * scale.y - borderY * 2));
		
		
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
		updateVerticalScroll();
		
		// If pressing
		if(mClickLock && mClickLockPhase == 1 && (GlobalClock.currentTimeMillis() - mClickLockWait) >= 150) {
			if(mPointers.size() == 1 && mType == Type.CLICKABLE)
				cursorToItem(mPointers.get(0));
			mClickLockPhase = 2;
		}
		
		// If post have click
		if(mClickLockPhase == 3 && (GlobalClock.currentTimeMillis() - mClickLockWait) >= 100) {
			if(mType == Type.CLICKABLE && mSelectListener != null && mIndex >= 0)
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
		if(drawingLayer == DrawingLayer.LAYER_BOTTOM)
			return;
		
		if(mSelectListAdapter == null) {
			mVerticalScrollLayer.draw(drawer);
			return;
		}
		
		WorldMatrix matrix = drawer.getWorldMatrix();
		matrix.push();
		matrix.postTranslatef(mAttributes.border, mDrawPosition + mAttributes.border);
		
		drawer.snip(mCursorLayer.getViewport());
		
		float filling = mDrawPosition;
		float min = 0;
		float max = getSize().y - mAttributes.border * 2;
		
		float backWidth = getSize().x - mAttributes.border * 2;
		float itemWidth = backWidth - mAttributes.border * 2;
		
		Canvas canvas = new Canvas(drawer);
		
		for(int index=0; index<mSelectListAdapter.getCount(); index++) {
			SelectListAdapter.ItemHolder item = mSelectListAdapter.getItem(index);
			float cellSize = item.getHeight() + mAttributes.padding * 2;
			
			float top = filling;
			float bottom = top + cellSize;
			
			if(top < max && bottom >= min) {
				item.draw(drawer, DrawingHolder.BACKGROUND, new Vector2(backWidth, cellSize));
				if(index == mIndex)
					mCursorLayer.draw(drawer);
				matrix.postTranslatef(mAttributes.border, mAttributes.padding);
				item.draw(drawer, DrawingHolder.ITEM, new Vector2(itemWidth, item.getHeight()));
				matrix.postTranslatef(-mAttributes.border, item.getHeight() + mAttributes.padding);
			} else if(top >= max) {
				break;
			} else
				matrix.postTranslatef(0, mAttributes.padding + item.getHeight() + mAttributes.padding);
			
			if(index < (mSelectListAdapter.getCount() - 1))
				canvas.drawRect(Color.BLACK, new Vector2(), new Vector2(backWidth, 2));

			
			filling += cellSize;
		}
		
		matrix.pop();
		
		mVerticalScrollLayer.draw(drawer);
		
		
	}
	
	/**
	 * Ser Vertical Scroll Anim State
	 * @param state
	 */
	final private void setVerticalScrollAnim(final int state) {
		mLastTime = GlobalClock.currentTimeMillis();
		if(mVerticalScrollAnimation == state)
			return;
		mVerticalScrollAnimation = state;
		switch(mVerticalScrollAnimation) {
		case VERTICAL_SCROLL_ANIM_APPEAR:
			mVerticalScrollLayer.setOpacity(1);
			mVerticalScrollLayer.getAnimationStack().clear();
			mVerticalScrollLayer.getAnimationStack().addAnimation(new AnimationOpacity(200, 0, 1));
			mVerticalScrollLayer.getAnimationStack().start();
			break;
		case VERTICAL_SCROLL_ANIM_DISAPPEAR:
			mVerticalScrollLayer.getAnimationStack().clear();
			mVerticalScrollLayer.getAnimationStack().addAnimation(new AnimationOpacity(200, 1, 0));
			mVerticalScrollLayer.getAnimationStack().start();
			break;
		}
	}
	
	/**
	 * Update Vertical Scroll
	 */
	final private void updateVerticalScroll() {
		// Update Position and Measure
		final float minSize = mScene.getDensityParser().smallerValue(MIN_VERTICAL_SCROLL_SIZE);
		final float contentSize = getContentHeight();
		final float maxScroll = getMaxScroll();
		final float finalScrollPosition = getFinalPosition();
		float finalScale = 0;
		Vector2 finalPosition = new Vector2(mVerticalScrollLayer.getPosition().x, 0);
		if(contentSize == -1) {
			finalPosition.y = mAttributes.border;
			finalScale = 1;
		} else {
			float scaler = mVerticalScrollHeight;
			float position = finalScrollPosition;
			
			if(position > 0)
				scaler += position;
			if(position < -maxScroll)
				scaler += ((-maxScroll) - position);
			
			position = Math.min(0, position);
			position = Math.max(-maxScroll, position);

			float finalSize = ((getSize().y - mAttributes.border * 2) / contentSize) * getSize().y;
			finalScale = Math.max(minSize, (finalSize / scaler) * mVerticalScrollHeight) / mVerticalScrollHeight;
			
			final float view = getSize().y - mAttributes.border * 2;
			finalPosition.y = mAttributes.border + ((position * -1) / maxScroll) * (view - (finalScale * mVerticalScrollHeight));
		}
		mVerticalScrollLayer.setScale(new Vector2(1, finalScale));
		mVerticalScrollLayer.setPosition(finalPosition);
		
		// Update Animation
		// If you have a considerable movement
		if((Math.abs(mLastScroll - finalScrollPosition) > mAttributes.border / 3) || 
			(finalScrollPosition > 0 || finalScrollPosition < -maxScroll)) {
			setVerticalScrollAnim(VERTICAL_SCROLL_ANIM_APPEAR);
			mLastScroll = finalScrollPosition;
			mLastTime = GlobalClock.currentTimeMillis();
		}
		if((GlobalClock.currentTimeMillis() - mLastTime) >= 1000) {
			setVerticalScrollAnim(VERTICAL_SCROLL_ANIM_DISAPPEAR);
			mLastTime = GlobalClock.currentTimeMillis();
		}
		
	}
}
