package multigear.mginterface.engine;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
		mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 0, 0, 8);
		
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
		if(mFillMode == -1) {
			mFillMode = 1;
			mFillLinearLayout = layout;
		}
		layout.addView(mGLSurfaceView);
	}
	
	/*
	 * Completa o conteudo da atividade com o GLSurface
	 */
	final public void fillActivityContentView() {
		if(mFillMode == -1) {
			mFillMode = 2;
			mEngine.getActivity().setContentView(mGLSurfaceView);
		}
	}
	
	/*

	final public void onPause() {
		// Pause GL
		mGLSurfaceView.onPause();
		// Pause a Renderer
		mRenderer.onPause();
		// Pause Engine
		GameEngine.SyncEngine syncEngine = mGameEngine.getSyncEngine();
		syncEngine.onPause();
		syncEngine.release();
	}
	

	final public void onResume() {
		// Resume OpenGl
		mGLSurfaceView.onResume();
		// Resume Engine
		if (mSurfaceCreated) {
			GameEngine.SyncEngine syncEngine = mGameEngine.getSyncEngine();
			syncEngine.onResume();
			syncEngine.release();
		}
	}

	final public boolean onTouchEvent(MotionEvent event) {
		GameEngine.SyncEngine synchronizedGameEngine = mGameEngine.getSyncEngine();
		boolean handled = synchronizedGameEngine.touch(event);
		synchronizedGameEngine.release();
		return handled;
	}
	
	
	final public void onDestroy() {
		GameEngine.SyncEngine syncEngine = mGameEngine.getSyncEngine();
		syncEngine.onDestroy();
		syncEngine.release();
	}
	

	public boolean onBackPressed() {
		// Send Message
		GameEngine.SyncEngine syncEngine = mGameEngine.getSyncEngine();
		boolean handled = syncEngine.onBackPressed();
		syncEngine.release();
		return handled;
	}
	*/
	
	/*
	 * Finaliza a surface
	 */
	final protected void destroy() {
		mGLSurfaceView.destroyDrawingCache();
		switch(mFillMode) {
			case 1:
				mFillLinearLayout.removeView(mGLSurfaceView);
				break;
			case 2:
				ViewGroup clearViewGroup = new ViewGroup(mEngine.getActivity()) {
					
					@Override
					protected void onLayout(boolean changed, int l, int t, int r, int b) {
					}
				};
				mEngine.getActivity().setContentView(clearViewGroup);
		}
	}
}
