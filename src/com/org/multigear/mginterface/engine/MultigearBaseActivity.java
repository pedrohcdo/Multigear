package com.org.multigear.mginterface.engine;

import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;

/**
 * Multigear Base Activity
 * 
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class MultigearBaseActivity extends ActionBarActivity {
	
	
	
	// Multigear
	com.org.multigear.mginterface.engine.Multigear mMultigear;
	
	/**
	 * Create Multigear Engine.
	 * 
	 * @param configuration
	 *            {@link com.org.multigear.mginterface.engine.Configuration}
	 */
	final protected void createMultigearEngine(final com.org.multigear.mginterface.engine.Configuration configuration) {
		mMultigear = new Multigear(this, configuration);
		mMultigear.sync().setupActivity().fillActivityContentView().unsync();
	}
	
	/**
	 * Close Multigear Engine
	 */
	final public void closeMultigearEngine() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (mMultigear != null) {
					mMultigear.onPause();
					mMultigear.onDestroy();
				}
				mMultigear = null;
				finish();
			}
		});
	}
	
	/**
	 * Get Multigear Engine.
	 */
	final protected com.org.multigear.mginterface.engine.Multigear getMultigearEngine() {
		return mMultigear;
	}
	
	/**
	 * Send Touch Event to Multigear. Obs(Return true to super for handle touch
	 * events).
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mMultigear != null) {
			mMultigear.safe().touch(event).unsafe();
			return true;
		}
		return false;
	}
	
	/**
	 * Setup Activity after focus changed.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (mMultigear != null)
			mMultigear.setupActivity();
	}
	
	/**
	 * Unhandle Multigear.
	 */
	@Override
	protected void onPause() {
		if (mMultigear != null)
			mMultigear.onPause();
		super.onPause();
	}
	
	/**
	 * Handle Multigear.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (mMultigear != null)
			mMultigear.onResume();
	}
	
	/**
	 * Back pressed
	 */
	@Override
	public void onBackPressed() {
		if(mMultigear != null) {
			mMultigear.sync().backPress().unsync();
			return;
		}
		super.onBackPressed();
	}
	
	/**
	 * Finish Multigear.
	 */
	@Override
	protected void onDestroy() {
		if (mMultigear != null)
			mMultigear.onDestroy();
		mMultigear = null;
		super.onDestroy();
	}
}
