package com.org.sample;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MotionEvent;

import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.engine.Configuration;
import com.org.multigear.mginterface.engine.Multigear;

/**
 * Multigear Base Activity
 * 
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class MainActivity extends  FragmentActivity {
	
	com.org.multigear.mginterface.engine.Multigear mMultigear;
	
	
	/**
	 * onCreate
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Create Multigear Confguration
		Configuration config = new Configuration();
		
		// Set default base Dpi
		config.setAttr(Configuration.ATTR_BASE_DPI, Configuration.DEFAULT_VALUE);
		
		// Set texture function 
		config.enable(Configuration.FUNC_TEXTURE_PROPORTION);
		config.setAttr(Configuration.ATTR_BASE_DENSITY, Configuration.DENSITY_XXHDPI);
		config.setAttr(Configuration.ATTR_BASE_SCREEN, new Vector2(1080, 1920));
		config.setAttr(Configuration.ATTR_PROPORTION_FROM, Configuration.PROPORTION_FROM_GENERAL);
		config.setAttr(Configuration.ATTR_PROPORTION_MODE, Configuration.PROPORTION_MODE_BIGGER);
		
		// Set Restorer Function
		config.enable(Configuration.FUNC_RESTORER_SERVICE);
		config.setAttr(Configuration.ATTR_RESTORER_NOTIFICATION, createRestorerNotification());
		config.getObjectAttr(Configuration.ATTR_RESTORER_NOTIFICATION);
		
		
		// Set Background Color
		config.setAttr(Configuration.ATTR_BACKGROUND_COLOR, 0xFF000000);
		
		// Set Main Room
		config.setMainRoom(MainRoom.class);
		
		// Create Multigear Engine
		mMultigear = new Multigear(this, config);
		mMultigear.sync().setupActivity().fillActivityContentView().unsync();
		
	}
	
	/**
	 * Get Multigear Engine.
	 */
	final protected com.org.multigear.mginterface.engine.Multigear getMultigearEngine() {
		return mMultigear;
	}
	
	/**
	 * Create custom Restore Notification
	 * @return
	 */
	Notification createRestorerNotification() {
		Intent intent = new Intent(this, MainActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		final Notification notification = new NotificationCompat
				.Builder(this)
				.setOngoing(true)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Pong Duo")
				.setContentText("After this game closed this service restore all connections and close.")
				.setContentIntent(pendingIntent)
				.build();
		return notification;
	}
	
	/**
	 * Send Touch Event to Multigear. Obs(Return true to super for handle touch
	 * events).
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean result = super.onTouchEvent(event);
		if (mMultigear != null) {
			mMultigear.safe().touch(event).unsafe();
			return true;
		}
		return false | result;
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
	 * Stop
	 */
	@Override
	protected void onStop() {
		Log.d("LogTest", "Stop called");
		super.onStop();
	}
	
	/**
	 * Finish Multigear.
	 */
	@Override
	protected void onDestroy() {
		Log.d("LogTest", "Destroy called");
		if (mMultigear != null)
			mMultigear.onDestroy();
		mMultigear = null;
		super.onDestroy();
	}
    
	/**
	 * Close this Game
	 */
	public void closeGame() {
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
}
