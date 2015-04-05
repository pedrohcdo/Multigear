package multigear.mginterface.scene.components;

import multigear.general.utils.Vector2;
import multigear.mginterface.engine.eventsmanager.GlobalClock;
import multigear.mginterface.scene.Scene;
import multigear.mginterface.tools.touch.DragDetector;
import multigear.mginterface.tools.touch.DragDetectorListener;
import multigear.mginterface.tools.touch.FocusDetector;
import multigear.mginterface.tools.touch.FocusDetectorListener;
import multigear.mginterface.tools.touch.RotateDetector;
import multigear.mginterface.tools.touch.RotateDetectorListener;
import multigear.mginterface.tools.touch.ScaleDetector;
import multigear.mginterface.tools.touch.ScaleDetectorListener;
import multigear.mginterface.tools.touch.TouchEventsDetector;
import multigear.mginterface.tools.touch.TouchEventsDetectorListener;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Camera Class
 * 
 * @author user
 *
 */
final public class Camera {
	
	/**
	 * Touchable Listener
	 */
	final private TouchableListener mTouchableListener = new TouchableListener() {
		
		/**
		 * On Touch Event
		 */
		@Override
		public boolean onTouch(Scene scene, MotionEvent motionEvent) {
			touch(motionEvent);
			return false;
		}
	};
	
	/**
	 * Updatable Listener
	 */
	final private UpdatableListener mUpdatableListener = new UpdatableListener() {
		
		@Override
		public void onUpdate(Scene scene) {
			update();
		}
	};
	
	/**
	 * Drag Detector Listener
	 */
	final private DragDetectorListener mDragDetectorListener = new DragDetectorListener() {
		
		/**
		 * On Drag
		 */
		@Override
		public void onDrag(Vector2 draged) {
			drag(draged);
		}
	};
	
	/**
	 * Scale Detector Listener
	 */
	final private ScaleDetectorListener mScaleDetectorListener = new ScaleDetectorListener() {

		/**
		 * On Scale
		 */
		@Override
		public void onScale(float pixels) {
			scale(pixels);
		}
	};
	
	/**
	 * Rotate Detector Listener
	 */
	final private RotateDetectorListener mRotateDetectorListener = new RotateDetectorListener() {
		
		/**
		 * On Rotate
		 */
		@Override
		public void onRotate(float angle) {
			rotate(angle);
		}
	};
	
	/**
	 * Focus Detector Listener
	 */
	final private FocusDetectorListener mFocusDetectorListener = new FocusDetectorListener() {
		
		/**
		 * On Focus
		 */
		@Override
		public void onFocus(Vector2 focused) {
			focus(focused);
		}
	};
	
	/**
	 * Touch Events Detector Listener
	 */
	final private TouchEventsDetectorListener mTouchEventsDetectorListener = new TouchEventsDetectorListener() {

		/**
		 * On Touch
		 */
		@Override
		public void onTouch(final int pointerCount) {
			if(pointerCount == 1)
				touch();
		}

		/**
		 * On Untouch
		 */
		@Override
		public void onUntouch(final int pointerCount) {
			if(pointerCount == 0)
				untouch();
		}
	};
	
	// Final Private Variables
	final private DragDetector mDragDetector = new DragDetector(mDragDetectorListener);
	final private ScaleDetector mScaleDetector = new ScaleDetector(mScaleDetectorListener);
	final private RotateDetector mRotateDetector = new RotateDetector(mRotateDetectorListener);
	final private FocusDetector mFocusDetector = new FocusDetector(mFocusDetectorListener);
	final private TouchEventsDetector mTouchEventsDetector = new TouchEventsDetector(mTouchEventsDetectorListener);
	final private Vector2 mScaleLimit = new Vector2(-1, -1);
	
	// Private Variables
	private Scene mScene;
	private boolean mAttached;
	private boolean mDrag = false;
	private boolean mScale = false;
	private boolean mRotate = false;
	private RectF mLimits = null;
	
	private Vector2 mSmoothing = new Vector2();
	private boolean mSmooth = false;
	private float mSmoothFriction = 1.5f;
	private long mLastTime = GlobalClock.currentTimeMillis();
	private boolean mSmoothLocked = false;
	
	/**
	 * Constructor
	 */
	public Camera() {}
	
	/**
	 * 
	 * @param scene
	 */
	final public void attach(final Scene scene) {
		mScene = scene;
		scene.addTouchableListener(mTouchableListener);
		scene.addUpdatableListener(mUpdatableListener);
		mAttached = true;
	}
	
	/**
	 * Detach this camera from the scene
	 */
	final public void detach() {
		if(!mAttached)
			throw new RuntimeException("The camera has been removed from the scene.");
		mScene.removeTouchableListener(mTouchableListener);
		mScene.removeUpdatableListener(mUpdatableListener);
		mAttached = false;
		mScene = null;
	}
	
	/**
	 * Set Drag
	 * @param enable Enable or Disable
	 */
	final public void setDrag(boolean enable) {
		mDrag = enable;
	}
	
	/**
	 * Set Scale
	 * @param enable Enable or Disable
	 */
	final public void setScale(boolean enable) {
		mScale = enable;
	}
	
	/**
	 * Set Rotate
	 * @param enable Enable or Disable, if true automatically set limits null, 
	 * why not support the two together
	 */
	final public void setRotate(boolean enable) {
		if(enable)
			setLimits(null);
		mRotate = enable;
	}
	
	/**
	 * Set Limits
	 * @param limits Limits, set null to unlimited, if limits != null 
	 * automatically set rotate false, why not support the two together
	 */
	final public void setLimits(final RectF limits) {
		if(limits != null)
			setRotate(false);
		mLimits = limits;
	}
	
	/**
	 * Set Scale Limit
	 * @param min Min, -1 to unlimited min
	 * @param max Max, -1 to unlimited max
	 */
	final public void setScaleLimit(final float min, final float max) {
		mScaleLimit.x = min;
		mScaleLimit.y = max;
	}
	
	/**
	 * Set Smooth
	 * @param enable Enable or Disable
	 */
	final public void setSmooth(final boolean enable) {
		mSmooth = true;
	}
	
	/**
	 * Set Smooth Friction
	 * @param friction, 1 is min
	 */
	final public void setSmoothFriction(final float friction) {
		mSmoothFriction = Math.max(1, friction);
	}
	
	/**
	 * Get Drag
	 * @param enable Enable or Disable
	 */
	final public boolean getDrag() {
		return mDrag;
	}
	
	/**
	 * Get Scale
	 * @param enable Enable or Disable
	 */
	final public boolean getScale() {
		return mScale;
	}
	
	/**
	 * Get Rotate
	 */
	final public boolean getRotate() {
		return mRotate;
	}
	
	/**
	 * Get Limits
	 */
	final public RectF getLimits() {
		return new RectF(mLimits);
	}
	
	/**
	 * Get Scale Limit
	 * @return Vector2(min, max)
	 */
	final public Vector2 getScaleLimit() {
		return mScaleLimit.clone();
	}
	
	/**
	 * Set Smooth
	 * @return Smooth
	 */
	final public boolean getSmooth() {
		return mSmooth;
	}
	
	/**
	 * Get Smooth Friction
	 * @return Smooth Friction
	 */
	final public float getSmoothFriction() {
		return mSmoothFriction;
	}
	
	/**
	 * Touch
	 * 
	 * @param motionEvent
	 */
	final private void touch(final MotionEvent motionEvent) {
		mDragDetector.touch(motionEvent);
		mScaleDetector.touch(motionEvent);
		mRotateDetector.touch(motionEvent);
		mFocusDetector.touch(motionEvent);
		mTouchEventsDetector.touch(motionEvent);
	}

	/**
	 * Update
	 * 
	 */
	final private void update() {
		if(mSmooth && !mSmoothLocked) {
			mScene.setPosition(Vector2.sum(mScene.getPosition(), mSmoothing));
			final float elapsedTime = (GlobalClock.currentTimeMillis() - mLastTime) / 17.0f;
			final float frictionForce = Math.max(1, mSmoothFriction * elapsedTime);
			if(frictionForce <= 0)
				mSmoothing = new Vector2();
			else
				mSmoothing = Vector2.div(mSmoothing, new Vector2(frictionForce, frictionForce));
			updateLimits();
		}
		mLastTime = GlobalClock.currentTimeMillis();
	}
	
	/**
	 * Drag
	 * 
	 * @param draged
	 */
	final private void drag(final Vector2 draged) {
		if(mDrag) {
			mScene.setPosition(Vector2.sum(mScene.getPosition(), draged));
			mSmoothing = Vector2.div(Vector2.sum(mSmoothing, draged), 2);
			updateLimits();
		}
	}
	
	/**
	 * Scale
	 * 
	 * @param pixels
	 */
	final private void scale(final float pixels) {
		if(mScale) {
			float scale = mScene.getScale().y;
			if(scale == 0)
				scale = 0.001f;
			final float scaledPixels = pixels * scale * 2.5f;
			float newScale = (mScene.getScreenSize().y * mScene.getScale().y + scaledPixels) / mScene.getScreenSize().y;
			if(mScaleLimit.x >= 0)
				newScale = Math.max(mScaleLimit.x, newScale);
			if(mScaleLimit.y >= 0)
				newScale = Math.min(mScaleLimit.y, newScale);
			mScene.setScale(new Vector2(newScale, newScale));
			updateLimits();
		}
	}
	
	/**
	 * Rotate
	 * 
	 * @param angle
	 */
	final private void rotate(final float angle) {
		if(mRotate)
			mScene.setAngle(mScene.getAngle() + angle);
	}
	
	/**
	 * Focus
	 * 
	 * @param focused
	 */
	final private void focus(final Vector2 focused) {
		Vector2 centeredPosition = Vector2.sub(focused, mScene.getPosition());
		Vector2 rotatedDirection = Vector2.rotate(centeredPosition, 360-mScene.getAngle());
		Vector2 scaledDirection = Vector2.div(rotatedDirection, mScene.getScale());
		Vector2 newFocal = Vector2.sum(mScene.getCenter(), scaledDirection);
		Vector2 focalDiff = Vector2.sub(newFocal, mScene.getCenter());
		Vector2 align = Vector2.scale(Vector2.rotate(focalDiff, mScene.getAngle()), mScene.getScale());
		mScene.setCenter(newFocal);
		mScene.setPosition(Vector2.sum(mScene.getPosition(), align));
	}
	
	/**
	 * Touch
	 */
	final private void touch() {
		if(mScale || mDrag || mRotate)
			mSmoothLocked = true;
	}
	
	/**
	 * Untouch
	 */
	final private void untouch() {
		mSmoothLocked = false;
	}
	
	/**
	 * Scale Centered Rect
	 * @param rect
	 * @param center
	 * @return
	 */
	final private RectF scaleCenteredRect(final RectF rect, final Vector2 center, final Vector2 scale) {
		final float left = (mLimits.left + center.x) * scale.x - center.x;
		final float top = (mLimits.top + center.y) * scale.y - center.y;
		final float right = (mLimits.right - center.x) * scale.x + center.x;
		final float bottom = (mLimits.bottom - center.y) * scale.y + center.y;
		return new RectF(left, top, right, bottom);
	}
	
	/**
	 * Update Limits
	 */
	final private void updateLimits() {
		if(mLimits == null)
			return;
		final Vector2 center = mScene.getCenter();
		final Vector2 scale = mScene.getScale();
		final Vector2 scenePosition = Vector2.sub(mScene.getPosition(), center);
		final Vector2 sceneSize = mScene.getScreenSize();
		RectF scaledCenter = scaleCenteredRect(mLimits, center, scale);
		final float minX = scaledCenter.left;
		final float minY = scaledCenter.top;
		final float maxX = scaledCenter.right;
		final float maxY = scaledCenter.bottom;
		float newSceneX = scenePosition.x;
		float newSceneY = scenePosition.y;
		final float sceneW = sceneSize.x;
		final float sceneH = sceneSize.y;
		final float sceneMaxX = sceneW - maxX;
		final float sceneMaxY = sceneH - maxY;
		if(newSceneX > minX)
			newSceneX = minX;
		if(newSceneX < sceneMaxX)
			newSceneX = sceneMaxX;
		if(newSceneY > minY)
			newSceneY = minY;
		if(newSceneY < sceneMaxY)
			newSceneY = sceneMaxY;
		mScene.setPosition(Vector2.sum(new Vector2(newSceneX, newSceneY), mScene.getCenter()));
	}
}
