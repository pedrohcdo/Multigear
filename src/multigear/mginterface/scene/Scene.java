package multigear.mginterface.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import multigear.cache.CacheManager;
import multigear.communication.tcp.support.ParentAttributes;
import multigear.general.utils.Measure;
import multigear.general.utils.Vector2;
import multigear.mginterface.engine.Configuration;
import multigear.mginterface.graphics.animations.AnimationSet;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.drawer.MatrixRow;
import multigear.mginterface.graphics.opengl.font.FontManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * 
 * Scene Basica, utilizado para criar Customs Scene.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public abstract class Scene extends multigear.mginterface.scene.Installation {
	
	/**
	 * Handler for Control Procedures
	 * 
	 * @author PedroH, RaphaelB
	 * 
	 *         Property Createlier.
	 */
	final private class Handler {
		
		// Consts
		final private static int INSERT_DRAWABLE = 1;
		final private static int REMOVE_DRAWABLE = 2;
		
		// Final Private Variables
		final private int mHandlerCode;
		final private Object mHandlerObject;
		
		/**
		 * Constructor
		 * 
		 * @param code
		 */
		private Handler(final int code) {
			mHandlerCode = code;
			mHandlerObject = null;
		}
		
		/**
		 * Constructor
		 * 
		 * @param code
		 */
		private Handler(final int code, final Object object) {
			mHandlerCode = code;
			mHandlerObject = object;
		}
		
		/**
		 * Call Proc
		 */
		final private void call() {
			switch (mHandlerCode) {
				case INSERT_DRAWABLE:
					mDrawables.add((multigear.mginterface.graphics.drawable.BaseDrawable) mHandlerObject);
					break;
				case REMOVE_DRAWABLE:
					mDrawables.remove(mHandlerObject);
			}
		}
	}
	
	/**
	 * Comparador utilisado para ordenamento de sobreposição para todos Sprites
	 * para fins de Desenho.
	 */
	final private Comparator<multigear.mginterface.graphics.drawable.BaseDrawable> mDrawablesComparatorDraw = new Comparator<multigear.mginterface.graphics.drawable.BaseDrawable>() {
		
		/*
		 * Comparador
		 */
		@Override
		public int compare(multigear.mginterface.graphics.drawable.BaseDrawable lhs, multigear.mginterface.graphics.drawable.BaseDrawable rhs) {
			return lhs.getZ() - rhs.getZ();
		}
	};
	
	/**
	 * Comparador utilisado para ordenamento de sobreposição para todos Sprites
	 * para fins de Touch.
	 */
	final private Comparator<multigear.mginterface.graphics.drawable.BaseDrawable> mDrawablesComparatorTouch = new Comparator<multigear.mginterface.graphics.drawable.BaseDrawable>() {
		
		/*
		 * Comparador
		 */
		@Override
		public int compare(multigear.mginterface.graphics.drawable.BaseDrawable lhs, multigear.mginterface.graphics.drawable.BaseDrawable rhs) {
			return rhs.getZ() - lhs.getZ();
		}
	};
	
	// Constatns
	final static private int ERROR_PRIVATEMETHOD_CODE = 0x5;
	final static private int ERROR_MISTAKE_CODE = 0x6;
	
	/** Communication Suport. */
	final static public int FUNC_VIRTUAL_DPI = 0x1;
	
	// Final Private Variables
	final private List<multigear.mginterface.graphics.drawable.BaseDrawable> mDrawables;
	final private List<multigear.physics.VirtualSpriteObject> mVirtualSpriteObjects;
	final private multigear.mginterface.scene.InstallManager mInstallManager;
	final private List<Scene.Handler> mHandler;
	
	// Private Variables
	private long mThisTime;
	private multigear.mginterface.engine.Multigear mEngine;
	private float mDPI, mDensity, mScaledDensity;
	private int mWidthPixels, mHeightPixels;
	private int mFlags;
	// private Interface.Room.SpaceParser mSpaceConversion;
	// private Interface.Room.ProportionParser mProportionalSupport;
	private Vector<MotionEvent> mMotionEventList;
	private multigear.mginterface.graphics.animations.AnimationStack mAnimationStack = new multigear.mginterface.graphics.animations.AnimationStack(this);
	private Vector2 mScale = new Vector2(1, 1);
	private Vector2 mPosition = new Vector2(0, 0);
	private float mOpacity = 1.0f;
	private Vector2 mSize = new Vector2(32, 32);
	private Vector2 mCenter = new Vector2(0, 0);
	private Vector2 mScroll = new Vector2(0, 0);
	private float mAngle = 0;
	private boolean mTouchable = true;
	private int mID = 0;
	
	/**
	 * Constructor
	 */
	public Scene() {
		mDrawables = new ArrayList<multigear.mginterface.graphics.drawable.BaseDrawable>();
		mHandler = new ArrayList<Scene.Handler>();
		mVirtualSpriteObjects = new ArrayList<multigear.physics.VirtualSpriteObject>();
		mEngine = null;
		mFlags = 0;
		mMotionEventList = new Vector<MotionEvent>();
		mInstallManager = new multigear.mginterface.scene.InstallManager(this);
	}
	
	/**
	 * Add new Handle
	 * 
	 * @param code
	 * @param object
	 */
	final private void addHandle(final int code, final Object object) {
		mHandler.add(new Scene.Handler(code, object));
	}
	
	/**
	 * Get InstallManager.
	 * 
	 * @return {@link multigear.mginterface.scene.InstallManager}
	 */
	final multigear.mginterface.scene.InstallManager getInstallManager() {
		return mInstallManager;
	}
	
	/*
	 * Altera a engine
	 */
	final public void setEngine(final multigear.mginterface.engine.Multigear engine) {
		if (mEngine == null) {
			mEngine = engine;
			final float xdpi = mEngine.getActivity().getResources().getDisplayMetrics().xdpi;
			final float ydpi = mEngine.getActivity().getResources().getDisplayMetrics().ydpi;
			mDPI = xdpi > ydpi ? xdpi : ydpi;
			mDensity = mEngine.getActivity().getResources().getDisplayMetrics().density;
			mWidthPixels = mEngine.getActivity().getResources().getDisplayMetrics().widthPixels;
			mHeightPixels = mEngine.getActivity().getResources().getDisplayMetrics().heightPixels;
			mScaledDensity = getActivity().getResources().getDisplayMetrics().scaledDensity;
		} else {
			multigear.general.utils.KernelUtils.error(mEngine.getActivity(), "Room Class: The method 'setEngine' can only be used by the Engine.", ERROR_PRIVATEMETHOD_CODE);
		}
	}
	
	/**
	 * Return true if has Engine.
	 * 
	 * @return
	 */
	final public boolean hasEngine() {
		return (mEngine != null);
	}
	
	/*
	 * Retorna true caso haja determinada função
	 */
	final public boolean hasFunc(final int func) {
		return ((mFlags & func) == func);
	}
	
	/*
	 * Habilita uma função
	 */
	final public void enable(final int func) {
		if (hasFunc(func))
			return;
		mFlags |= func;
	}
	
	/*
	 * Desabilita uma função
	 */
	final public void disable(final int func) {
		if (!hasFunc(func))
			return;
		mFlags ^= (mFlags & func);
	}
	
	/*
	 * Retorna a engin
	 */
	final public multigear.mginterface.engine.Multigear getEngine() {
		if (mEngine == null) {
			multigear.general.utils.KernelUtils.error(mEngine.getActivity(), "Room Class: A mistake, because this engine null occurred when calling GetEngine ().", ERROR_MISTAKE_CODE);
		}
		return mEngine;
	}
	
	/**
	 * Return Activity
	 * 
	 * @return Activity
	 */
	final public Activity getActivity() {
		if (mEngine == null) {
			multigear.general.utils.KernelUtils.error(mEngine.getActivity(), "Room Class: A mistake, because this engine null occurred when calling GetEngine ().", ERROR_MISTAKE_CODE);
		}
		return mEngine.getActivity();
	}
	
	/*
	 * Retorna configuração
	 */
	final public multigear.mginterface.engine.Configuration getConfiguration() {
		return mEngine.getConfiguration();
	}
	
	/**
	 * Set Opacity
	 * 
	 * @param Int
	 *            Opacity {0-255}
	 */
	final public void setOpacity(final float opacity) {
		mOpacity = Math.max(Math.min(opacity, 1.0f), 0.0f);
	}
	
	/**
	 * Set Scale
	 * 
	 * @param scale
	 *            Float Scale
	 */
	final public void setScale(final Vector2 scale) {
		mScale = scale;
	}
	
	/**
	 * Set Sprite Position
	 * 
	 * @param position
	 *            {@link Vector2} Position
	 */
	final public void setPosition(final Vector2 position) {
		mPosition = position;
	}
	
	/**
	 * Set draw dest texture size. For this use, setRestectTextureSize(false).
	 * 
	 * @param size
	 *            Draw texture dest Size
	 */
	final public void setSize(final Vector2 size) {
		mSize = size;
	}
	
	/**
	 * Set center axis.
	 * 
	 * @param center
	 *            {@link Vector2} Center
	 */
	final public void setCenter(final Vector2 center) {
		mCenter = center;
	}
	
	/**
	 * Set Angle.
	 * 
	 * @param angle
	 *            {@link multigear.general.utils.Vector2} Angle
	 */
	final public void setAngle(final float angle) {
		mAngle = angle;
	}
	
	/**
	 * Set Scroll.
	 * 
	 * @param center
	 *            {@link Vector2} Scroll
	 */
	final public void setScroll(final Vector2 scroll) {
		mScroll = scroll;
	}
	
	/**
	 * Set ID.
	 * 
	 * @param id
	 *            Int ID
	 */
	final public void setID(final int id) {
		mID = id;
	}
	
	/**
	 * Set Touchable.
	 * 
	 * @param touchable
	 *            Boolean Touchable
	 */
	final public void setTouchable(final boolean touchable) {
		if (!mTouchable) {
			while (mMotionEventList.size() > 0)
				mMotionEventList.remove(0);
		}
		mTouchable = touchable;
	}
	
	/**
	 * Get Animation Stack.
	 * 
	 * @return {@link multigear.mginterface.graphics.animations.AnimationStack}
	 */
	final public multigear.mginterface.graphics.animations.AnimationStack getAnimationStack() {
		return mAnimationStack;
	}
	
	/**
	 * Get Opacity
	 */
	final public float getOpacity() {
		return mOpacity;
	}
	
	/**
	 * Get Scale
	 */
	final public Vector2 getScale() {
		return mScale;
	}
	
	/**
	 * Return Position
	 * 
	 * @return {@link Vector2} Position
	 */
	final public Vector2 getPosition() {
		return mPosition;
	}
	
	/**
	 * Return draw dest Texture size.
	 * 
	 * @return {@link Vector2} Size
	 */
	final public Vector2 getSize() {
		return mSize;
	}
	
	/**
	 * Get center axis.
	 * 
	 * @return {@link Vector2} Center
	 */
	final public Vector2 getCenter() {
		return mCenter;
	}
	
	/**
	 * Get Angle.
	 * 
	 * @return {@link multigear.general.utils.Vector2} Angle
	 */
	final public float getAngle() {
		return mAngle;
	}
	
	/**
	 * Get Scroll.
	 * 
	 * @return {@link Vector2} Scroll
	 */
	final public Vector2 getScroll() {
		return mScroll;
	}
	
	/**
	 * Get ID.
	 * 
	 * @return Int ID
	 */
	final public int getID() {
		return mID;
	}
	
	/**
	 * Get Touchable.
	 * 
	 * @return Boolean Touchable
	 */
	final public boolean getTouchable() {
		return mTouchable;
	}
	
	/**
	 * Installs an object of this type Installation Room.
	 * 
	 * @param installation
	 *            Installation
	 */
	final public void install(final multigear.mginterface.scene.Installation installation) {
		mInstallManager.install(installation);
	}
	
	/**
	 * Install with delayed
	 */
	final public void installDelayed(final multigear.mginterface.scene.Installation installation, final long delay) {
		mInstallManager.installDelayed(installation, delay);
	}
	
	/**
	 * Uninstall Object.
	 * 
	 * @param installation
	 *            Installation
	 */
	final public void uninstall(final multigear.mginterface.scene.Installation installation) {
		mInstallManager.uninstall(installation);
	}
	
	/**
	 * Uninstall with delayed
	 */
	final public void uninstallDelayed(final multigear.mginterface.scene.Installation installation, final long delay) {
		mInstallManager.uninstallDelayed(installation, delay);
	}
	
	/**
	 * Calibrate DPI. If you are using ComSupport, this method sends a message
	 * of support for all connections that the same was calibrated. The flag is
	 * used '<i>SupportMessage</i>.<b>PARENT_CALIBREATEDATTRIBUTES'</b>.
	 * 
	 * @param dpc
	 *            Dots per Centimeters
	 */
	final public void calibrateDpiPerCm(final float dpc) {
		final float calibratedDpi = dpc * multigear.general.utils.GeneralUtils.INCH;
		getConfiguration().setAttr(multigear.mginterface.engine.Configuration.ATTR_CALIBRATE_DPI, calibratedDpi);
		getComManager().sendCalibratedAttributes();
	}
	
	/**
	 * Calibrate DPI. If you are using ComSupport, this method sends a message
	 * of support for all connections that the same was calibrated. The flag is
	 * used '<i>SupportMessage</i>.<b>PARENT_CALIBREATEDATTRIBUTES</b>'.
	 * 
	 * @param dpi
	 *            Dots per Inchs
	 */
	final public void calibrateDpiPerInch(final float dpi) {
		getConfiguration().setAttr(multigear.mginterface.engine.Configuration.ATTR_CALIBRATE_DPI, dpi);
		getComManager().sendCalibratedAttributes();
	}
	
	/**
	 * Change base dpi, similar to:
	 * <p>
	 * getConfiguration().setAttr(Configuration.ATTR_BASE_DPI, dpi);
	 * 
	 * @param dpi
	 */
	final public void setBaseDpi(final float dpi) {
		getConfiguration().setAttr(Configuration.ATTR_BASE_DPI, dpi);
	}
	
	/**
	 * Return DPI. 
	 * If has FUNC_CALIBRATED_DPI, this return last calibrated dpi, if not calibrated return original.
	 * @return
	 */
	final public float getDPI() {
		final float calibratedDpi = getConfiguration().getFloatAttr(multigear.mginterface.engine.Configuration.ATTR_CALIBRATE_DPI);
		if (calibratedDpi != -1)
			return calibratedDpi;
		return mDPI;
	}
	
	/**
	 * Get Real Dpi without calibration.
	 * @return
	 */
	final public float getRealDPI() {
		return mDPI;
	}
	
	/*
	 * Retorna a densidade
	 */
	final public float getDensity() {
		return mDensity;
	}
	
	/*
	 * Retorna a densidade utilizada para escalar Fonts
	 */
	final public float getScaledDensity() {
		return mScaledDensity;
	}
	
	/*
	 * Retorna a quantidade de pixels em Y
	 */
	final public int getWidthPixelss() {
		return mWidthPixels;
	}
	
	/*
	 * Retorna a quantidade de pixels em X
	 */
	final public int getHeightPixelss() {
		return mHeightPixels;
	}
	
	/**
	 * Get Physical Screen Size
	 * 
	 * @param measure
	 *            Result Measure
	 * 
	 * @return Measure Value
	 */
	final public Vector2 getPhysicalScreenSize(final multigear.general.utils.Measure measure) {
		final float physicalWidth = Measure.Inch.convertTo(mWidthPixels / mDPI, measure);
		final float physicalHeight = Measure.Inch.convertTo(mHeightPixels / mDPI, measure);
		return new Vector2(physicalWidth, physicalHeight);
	}
	
	/**
	 * Get Physical Screen Size
	 * <p>
	 * Note: This use Measure.Inch, for other Measures use:
	 * {@link ParentAttributes.getPhysicalScreenSize}
	 * 
	 * @param measure
	 *            Result Measure
	 * 
	 * @return Measure Value
	 */
	final public Vector2 getPhysicalScreenSize() {
		final float physicalWidth = Measure.Inch.convertTo(mWidthPixels / mDPI, Measure.Inch);
		final float physicalHeight = Measure.Inch.convertTo(mHeightPixels / mDPI, Measure.Inch);
		return new Vector2(physicalWidth, physicalHeight);
	}
	
	/*
	 * Atualiza o tempo
	 */
	@Override
	final public void time(final long time) {
		mThisTime = time;
		mInstallManager.time(time);
	}
	
	/*
	 * Configuração dos objetos
	 */
	@Override
	final public void setup() {
		mSize = super.getScreenSize();
		mInstallManager.prevSetup();
		onSetup();
		mInstallManager.time(mThisTime);
		mInstallManager.setup();
	}
	
	/*
	 * Arquiva as texturas
	 */
	@Override
	final public void cache() {
		mInstallManager.prevCache();
		onCache(getTextureLoader());
		mInstallManager.time(mThisTime);
		mInstallManager.cache(getTextureLoader());
	}
	
	/*
	 * Redimensiona a tela
	 */
	@Override
	final public void screen() {
		mInstallManager.prevScreen();
		onScreen(getScreenSize());
		mInstallManager.time(mThisTime);
		mInstallManager.screen(getScreenSize());
	}
	
	/*
	 * Atualiza os objetos
	 */
	@Override
	final public void update() {
		mInstallManager.update();
		// Call Handler
		for (final Scene.Handler handle : mHandler)
			handle.call();
		mHandler.clear();
		//
		updateTouch();
		updatePhysics();
		onUpdate();
		mInstallManager.updateManager();
	}
	
	/*
	 * Desenha os objetos
	 */
	@SuppressLint("WrongCall")
	@Override
	final public void draw(final multigear.mginterface.graphics.opengl.drawer.Drawer drawer) {
		// Prepare Animation
		final AnimationSet animationSet = mAnimationStack.prepareAnimation().animate();
		
		// Begin draw room
		beginDrawRoom(drawer, animationSet);
		
		// Get Pre Opacity
		final float preOpacity = animationSet.getOpacity() * mOpacity;
		updateAndDrawDrawables(drawer, preOpacity);
		onDraw(drawer);
		
		// End Draw Room
		endDrawRoom(drawer);
		mInstallManager.draw(drawer);
	}
	
	/*
	 * Prepara a room para desenho
	 */
	final private void beginDrawRoom(Drawer drawer, final AnimationSet animationSet) {
		// Get Infos
		float scaleFactor;
		if(hasFunc(FUNC_VIRTUAL_DPI))
			scaleFactor = getSpaceParser().getScaleFactor();
		else
			scaleFactor = 1;
		
		final Vector2 scale = mScale;
		
		final float ox = mCenter.x * scale.x;
		final float oy = mCenter.y * scale.y;
		final float sx = (float) ((mSize.x * scale.x) / getScreenSize().x);
		final float sy = (float) ((mSize.y * scale.y) / getScreenSize().y);
		
		// Get Matrix Row
		final MatrixRow matrixRow = drawer.getMatrixRow();
		
		// Push Matrix
		matrixRow.push();
		
		// Scale Matrix
		matrixRow.postScalef(sx, sy);
		
		// Create Post Transformations
		Matrix postTransformations = new Matrix();
		
		// Translate and Rotate Matrix
		postTransformations.postTranslate(-ox, -oy);
		postTransformations.postRotate(mAngle + animationSet.getRotation());
		postTransformations.postTranslate(ox, oy);
		
		// Translate Matrix
		final Vector2 translate = animationSet.getPosition();
		final float tX = (mPosition.x - mScroll.x - ox) + translate.x;
		final float tY = (mPosition.y - mScroll.y - oy) + translate.y;
		postTransformations.postTranslate(tX, tY);
		
		// Scale Factor
		postTransformations.postScale(scaleFactor, scaleFactor);
		
		// Set Post Transformations
		matrixRow.setRoomTransformations(postTransformations);
	
	}
	
	/*
	 * Finalizando desenho
	 */
	final private void endDrawRoom(final multigear.mginterface.graphics.opengl.drawer.Drawer drawer) {
		// Get Matrix Row
		final MatrixRow matrixRow = drawer.getMatrixRow();
		// Pop Matrix
		matrixRow.pop();
	}
	
	/*
	 * Return true if uninstalling
	 */
	final private boolean hasUninstalling() {
		final Scene father = getFatherRoom();
		if (father != null) {
			final multigear.mginterface.scene.InstallManager installSupport = father.getInstallManager();
			return installSupport.hasUninstalling(this);
		}
		return false;
	}
	
	/*
	 * Evento de toque
	 */
	final public void touchImpl(final MotionEvent motionEvent) {
		if (!mTouchable)
			return;
		mMotionEventList.add(motionEvent);
	}
	
	/*
	 * Atualiza o touch
	 */
	final private void updateTouch() {
		if (mMotionEventList.size() <= 0)
			return;
		while (mMotionEventList.size() > 0) {
			touch(mMotionEventList.remove(0));
		}
	}
	
	/*
	 * Evento de toque
	 */
	@Override
	final public void touch(final MotionEvent motionEvent) {
		if (hasUninstalling() || isUninstalled())
			return;
		// Not Touch If Uninstalled
		mInstallManager.touch(motionEvent);
		onTouch(motionEvent);
		touchDrawable(motionEvent);
	}
	
	/*
	 * Evento para finalizar a Engine
	 */
	@Override
	final public void finish() {
		mInstallManager.prevFinish();
		onFinish();
		mInstallManager.finish();
	}
	
	/*
	 * Retorna o tempo atual
	 */
	final public long getThisTime() {
		return mThisTime;
	}
	
	/**
	 * Get Screen Size. If the 'FUNC_BASEPLANE_SUPPORT' feature is enabled, this
	 * measure can be based on a factor calculated by climbing base dpi set the
	 * configurations.
	 * 
	 * @return Screen Size.
	 */
	final public Vector2 getScreenSize() {
		return super.getScreenSize().clone();
	}
	
	/**
	 * Add Drawable to Stack. obs(This method is automatically called by
	 * Drawable)
	 * 
	 * @param drawable
	 *            Drawable
	 */
	final public void addDrawable(final multigear.mginterface.graphics.drawable.BaseDrawable drawable) {
		addHandle(Scene.Handler.INSERT_DRAWABLE, drawable);
	}
	
	/**
	 * Remove an Drawable. obs(This method is automatically called by Drawable)
	 * 
	 * @param drawable
	 *            Drawable
	 */
	final public void disposeDrawable(final multigear.mginterface.graphics.drawable.BaseDrawable drawable) {
		addHandle(Scene.Handler.REMOVE_DRAWABLE, drawable);
	}
	
	/**
	 * Add Virtual Sprite Object. obs(This method is automatically called by
	 * VirtualSpriteObject).
	 */
	final public void addVirtualSpriteObject(final multigear.physics.VirtualSpriteObject virtualSpriteObject) {
		mVirtualSpriteObjects.add(virtualSpriteObject);
	}
	
	/**
	 * Return all registered Virtual Sprite Objects.
	 * 
	 * @return Pack of registered Virtual Sprite Objects.
	 */
	final public List<multigear.physics.VirtualSpriteObject> getVirtualSpriteObjects() {
		return mVirtualSpriteObjects;
	}
	
	/*
	 * Atualiza os sprites
	 */
	final private void updateAndDrawDrawables(final multigear.mginterface.graphics.opengl.drawer.Drawer drawer, final float preOpacity) {
		
		Collections.sort(mDrawables, mDrawablesComparatorDraw);
		for (int i = 0; i < mDrawables.size(); i++) {
			final multigear.mginterface.graphics.drawable.BaseDrawable drawable = mDrawables.get(i);
			drawable.updateAndDraw(drawer, preOpacity);
		}
	}
	
	/*
	 * Atualiza os objetos que utilizam fisica
	 */
	final private void updatePhysics() {
		for (final multigear.physics.VirtualSpriteObject virtualSpriteObject : mVirtualSpriteObjects) {
			virtualSpriteObject.update();
		}
	}
	
	/*
	 * Desenha os sprites
	 */
	final private void touchDrawable(final MotionEvent motionEvent) {
		Collections.sort(mDrawables, mDrawablesComparatorTouch);
		for (int i = 0; i < mDrawables.size(); i++) {
			final multigear.mginterface.graphics.drawable.BaseDrawable drawable = mDrawables.get(i);
			drawable.touch(motionEvent);
		}
	}
	
	/**
	 * Run a Runnable on UI Thread.
	 * 
	 * @param runnable
	 *            Runnable
	 */
	final public void runOnUiThread(final Runnable runnable) {
		mEngine.getActivity().runOnUiThread(runnable);
	}
	
	/**
	 * Similar to {@link Toast#makeText(Context, CharSequence, int)}, however
	 * this toast will be displayed in the UI Thread.
	 * 
	 * @param text
	 *            Text to Show
	 * @param duration
	 *            Duration 0 for Short or 1 to Long
	 */
	final public void showToast(final String text, final int duration) {
		mEngine.getActivity().runOnUiThread(new Runnable() {
			
			/*
			 * Runner
			 */
			@Override
			public void run() {
				Toast.makeText(mEngine.getActivity(), text, duration).show();
			}
		});
	}
	
	/*
	 * Retorna o suporte a conexões
	 */
	final public multigear.communication.tcp.support.ComManager getComManager() {
		return mEngine.getComManager();
	}
	
	/*
	 * Retorna o suporte de plano base
	 */
	final public multigear.mginterface.scene.SpaceParser getSpaceParser() {
		return mEngine.getSpaceParser();
	}
	
	/**
	 * Get AudioManager.
	 * 
	 * @return {@link multigear.audio.AudioManager}
	 */
	final public multigear.audio.AudioManager getAudioManager() {
		return mEngine.getAudioManager();
	}
	
	/**
	 * Get Density Parser
	 * 
	 * @return ProportionalParser
	 */
	final public multigear.mginterface.scene.DensityParser getDensityParser() {
		return mEngine.getDensityParser();
	}
	
	/**
	 * Get Font Manager
	 * 
	 * @return {@link FontManager}
	 */
	final public FontManager getFontManager() {
		return mEngine.getFontManager();
	}
	
	/**
	 * Get Cache Manager
	 * 
	 * @return {@link CacheManager}
	 */
	final public CacheManager getCacheManager() {
		return mEngine.getCacheManager();
	}
	
	/**
	 * Get Services Manager
	 * 
	 * @return ConnectionManager
	 */
	final public multigear.services.ServicesManager getServicesManager() {
		return mEngine.getServicesManager();
	}
	
	/**
	 * Create Database
	 * 
	 * @param impl
	 *            Database model
	 */
	final public multigear.database.DataBaseCreator createDatabase(final multigear.database.DataBaseCreator.Interface impl) {
		return new multigear.database.DataBaseCreator(getEngine().getActivity(), impl);
	}
	
	/**
	 * Return Device Name
	 * 
	 * @return Device Name
	 */
	final public String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}
	
	/**
	 * Capitalize
	 * 
	 * @param s
	 * @return
	 */
	final private String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}
	
	/* Evento para configuração dos objetos */
	public void onSetup() {
	};
	
	/* Evento para arquivamento de texturas */
	public void onCache(final multigear.mginterface.graphics.opengl.texture.Loader textureLoader) {
	};
	
	/* Evento para redimensionamento da tela */
	public void onScreen(final Vector2 mScreenSize) {
	};
	
	/* Evento para atualização */
	public void onUpdate() {
	};
	
	/* Desenha os objetos */
	public void onDraw(final multigear.mginterface.graphics.opengl.drawer.Drawer drawer) {
	};
	
	/* Evento de toque */
	public void onTouch(final MotionEvent motionEvent) {
	};
	
	/* Evento para Finalizar a Engine */
	public void onFinish() {
	};
}
