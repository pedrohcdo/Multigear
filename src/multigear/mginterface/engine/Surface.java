package multigear.mginterface.engine;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 
 * Gerencia os graficos.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class Surface {

	// Private Variables
	private multigear.mginterface.engine.Multigear mEngine;
	private GLSurfaceView mGLSurfaceView;
	private multigear.mginterface.graphics.opengl.Renderer mRenderer;
	private int mFillMode;
	private LinearLayout mFillLinearLayout;

	
	/**
	 * Custom Config Chooser
	 * @author user
	 *
	 */
	private class CustomConfigChooser implements EGLConfigChooser {
		
		/** Variables */
		private int[][] mConfigSpecs;
		private int[] mValue = new int[1];
		private int[][] mProfiles;
        
		/**
		 * Constructor
		 * 
		 * @param redSize Red Component Size
		 * @param greenSize green Component Size
		 * @param blueSize Blue Component Size
		 * @param alphaSize Alpha Component Size
		 * @param depthSize Depth Component Size
		 * @param stencilSize Stencil Component Size
		 */
		public CustomConfigChooser(int[][] profiles) {
			mConfigSpecs = new int[profiles.length][];
			for(int i=0; i<profiles.length; i++) {
				int[] profile = profiles[i];
				mConfigSpecs[i] = filterConfigSpec(new int[] {
	                    EGL10.EGL_RED_SIZE, profile[0],
	                    EGL10.EGL_GREEN_SIZE, profile[1],
	                    EGL10.EGL_BLUE_SIZE, profile[2],
	                    EGL10.EGL_ALPHA_SIZE, profile[3],
	                    EGL10.EGL_DEPTH_SIZE, 0,
	                    EGL10.EGL_STENCIL_SIZE, profile[4],
	                    EGL10.EGL_NONE});
			}
			
			mProfiles = profiles;
		}
		
		/**
		 * Choose COnfig
		 * 
		 * @param egl
		 * @param display
		 * @return
		 */
		public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
			
			// Choose best profile
			for(int profile=0; profile<mProfiles.length; profile++) {
				
				// Configs count or continue if invalid
				int[] num_config = new int[1];
			
				// Get Config for specs
				if (!egl.eglChooseConfig(display, mConfigSpecs[profile], null, 0, num_config))
					continue;
			
				// COntinue if invalid config
				int numConfigs = num_config[0];
				if (numConfigs <= 0)
					continue;
			
				// Get All configs profile or continue if error
				EGLConfig[] configs = new EGLConfig[numConfigs];
				if (!egl.eglChooseConfig(display, mConfigSpecs[profile], configs, numConfigs, num_config))
					continue;
			
				// Get best config or continue if not choosen
				EGLConfig config = chooseBestConfig(profile, egl, display, configs);
				if (config == null)
					continue;
			
				// Return config
				return config;
			}
			// Unsupported Device
			Toast.makeText(mEngine.getActivity(), "Unsupported device.", Toast.LENGTH_LONG).show();
            throw new IllegalArgumentException("Unsupported device.");
		}
		
		/**
		 * Choose Best config
		 * 
		 * @param egl
		 * @param display
		 * @param configs
		 * @return
		 */
		private EGLConfig chooseBestConfig(int profile, EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
			//
			EGLConfig best = null;
			int stencilTest = Integer.MIN_VALUE;
			/**
			 * Choose Config
			 */
	        for (EGLConfig select : configs) {
	        	// Find config attrib
	        	int r = findConfigAttrib(egl, display, select, EGL10.EGL_RED_SIZE, 0);
	            int g = findConfigAttrib(egl, display, select, EGL10.EGL_GREEN_SIZE, 0);
	            int b = findConfigAttrib(egl, display, select, EGL10.EGL_BLUE_SIZE, 0);
	            int a = findConfigAttrib(egl, display, select, EGL10.EGL_ALPHA_SIZE, 0);
	            int s = findConfigAttrib(egl, display, select, EGL10.EGL_STENCIL_SIZE, 0);
	            
	            Log.d("LogTest", " -> Red: " + r + " Green: " + g + " Blue: " + b + " Alpha: " + a + " Stencil: " + s);
	            
	            // Check if valid component colors
	          	if(r == mProfiles[profile][0] && g == mProfiles[profile][1] && b == mProfiles[profile][2] && a == mProfiles[profile][3]) {
	          		// If found best profile
	          		if(s == mProfiles[profile][4]) {
	          			best = select;
	          			Log.d("LogTest", " 		-> Best Profile.");
	          			break;
	          		}
	          		// If found permissible profile
	          		if(s > stencilTest) {
	          			Log.d("LogTest", " 		-> Permissible Profile.");
	          			best = select;
	          			stencilTest = s;
	          		}
	          	}
	         }
	        return best;
		}
		
		/**
		 * Fin Attribute size
		 * @param egl
		 * @param display
		 * @param config
		 * @param attribute
		 * @param defaultValue
		 * @return
		 */
	    private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
	    	if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
	           return mValue[0];
	         }
	        return defaultValue;
	   }
	    
	    /**
	     * Filter for client 2.0
	     * @param configSpec
	     * @return
	     */
        private int[] filterConfigSpec(int[] configSpec) {
            int len = configSpec.length;
            int[] newConfigSpec = new int[len + 2];
            System.arraycopy(configSpec, 0, newConfigSpec, 0, len-1);
            newConfigSpec[len-1] = EGL10.EGL_RENDERABLE_TYPE;
            newConfigSpec[len] = 4; /* EGL_OPENGL_ES2_BIT */
            newConfigSpec[len+1] = EGL10.EGL_NONE;
            return newConfigSpec;
        }
	}

	/*
	 * Construtor
	 */
	@SuppressLint("NewApi")
	public Surface(final Multigear engine) {
		// Get Engine
		mEngine = engine;
		// Create GL Surface
		mGLSurfaceView = new GLSurfaceView(engine.getActivity());
		mGLSurfaceView.setEGLContextClientVersion(2);
		
	
		mGLSurfaceView.setEGLConfigChooser(new CustomConfigChooser(new int[][] {{8, 8, 8, 0, 8}, {5, 6, 5, 0, 8}}));
		

		// Preserve Context
		if (Build.VERSION.SDK_INT >= 11)
			mGLSurfaceView.setPreserveEGLContextOnPause(false);
		// Create Renderer
		mRenderer = new multigear.mginterface.graphics.opengl.Renderer(engine);

		// Set GL Renderer
		mGLSurfaceView.setRenderer(mRenderer);
		// --
		mFillMode = -1;
		mFillLinearLayout = null;
	}

	/*
	 * Retorna a surface
	 */
	public GLSurfaceView getGLSurfaceView() {
		return mGLSurfaceView;
	}

	/*
	 * Completa o conteudo da atividade com o GLSurface
	 */
	final public void addToLayout(LinearLayout layout) {
		if (mFillMode == -1) {
			mFillMode = 1;
			mFillLinearLayout = layout;
		}
		layout.addView(mGLSurfaceView);
	}

	/*
	 * Completa o conteudo da atividade com o GLSurface
	 */
	final public void fillActivityContentView() {
		if (mFillMode == -1) {
			mFillMode = 2;
			mEngine.getActivity().setContentView(mGLSurfaceView);
		}
	}

	/*
	 * 
	 * final public void onPause() { // Pause GL mGLSurfaceView.onPause(); //
	 * Pause a Renderer mRenderer.onPause(); // Pause Engine
	 * GameEngine.SyncEngine syncEngine = mGameEngine.getSyncEngine();
	 * syncEngine.onPause(); syncEngine.release(); }
	 * 
	 * 
	 * final public void onResume() { // Resume OpenGl
	 * mGLSurfaceView.onResume(); // Resume Engine if (mSurfaceCreated) {
	 * GameEngine.SyncEngine syncEngine = mGameEngine.getSyncEngine();
	 * syncEngine.onResume(); syncEngine.release(); } }
	 * 
	 * final public boolean onTouchEvent(MotionEvent event) {
	 * GameEngine.SyncEngine synchronizedGameEngine =
	 * mGameEngine.getSyncEngine(); boolean handled =
	 * synchronizedGameEngine.touch(event); synchronizedGameEngine.release();
	 * return handled; }
	 * 
	 * 
	 * final public void onDestroy() { GameEngine.SyncEngine syncEngine =
	 * mGameEngine.getSyncEngine(); syncEngine.onDestroy();
	 * syncEngine.release(); }
	 * 
	 * 
	 * public boolean onBackPressed() { // Send Message GameEngine.SyncEngine
	 * syncEngine = mGameEngine.getSyncEngine(); boolean handled =
	 * syncEngine.onBackPressed(); syncEngine.release(); return handled; }
	 */

	/*
	 * Finaliza a surface
	 */
	final protected void destroy() {
		mGLSurfaceView.destroyDrawingCache();
		switch (mFillMode) {
		case 1:
			mFillLinearLayout.removeView(mGLSurfaceView);
			break;
		case 2:
			ViewGroup clearViewGroup = new ViewGroup(mEngine.getActivity()) {

				@Override
				protected void onLayout(boolean changed, int l, int t, int r,
						int b) {
				}
			};
			mEngine.getActivity().setContentView(clearViewGroup);
		}
	}
}
