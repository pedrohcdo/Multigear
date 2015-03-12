package multigear.mginterface.engine;

import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.widget.Toast;

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
	multigear.mginterface.engine.Multigear mMultigear;
	
	/**
	 * Create Multigear Engine.
	 * 
	 * @param configuration
	 *            {@link multigear.mginterface.engine.Configuration}
	 */
	final protected void createMultigearEngine(final multigear.mginterface.engine.Configuration configuration) {
		mMultigear = new Multigear(this, configuration);
		mMultigear.sync().setupActivity().fillActivityContentView().unsync();
	}
	
	/**
	 * Get Multigear Engine.
	 */
	final protected multigear.mginterface.engine.Multigear getMultigearEngine() {
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
			mMultigear.sync().setupActivity().unsync();
	}
	
	/**
	 * Unhandle Multigear.
	 */
	@Override
	protected void onPause() {
		
		super.onPause();
	}
	
	protected void onStop() {
		if (mMultigear != null)
			mMultigear.sync().pause().unsync();
		super.onStop();
	}
	
	/**
	 * Handle Multigear.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (mMultigear != null)
			mMultigear.sync().resume().unsync();
	}
	
	/**
	 * Finish Multigear.
	 */
	@Override
	protected void onDestroy() {
		if (mMultigear != null)
			mMultigear.sync().destroy().unsync();
		mMultigear = null;
		super.onDestroy();
	}
}
