package multigear.mginterface.scene.utils;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.scene.Installation;

/**
 * Updater
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public abstract class Updater extends Installation {

	/** Unused */
	@Override
	protected void time(long thisTime) {}
			
	/** Unused */
	@Override
	public void setup() {}
	
	/** Unused */
	@Override
	public void cache() {}
	
	/** Unused */
	@Override
	public void screen() {}
	
	/**
	 * Update
	 */
	@Override
	public void update() {
		onUpdate();
	}
	
	/** Unused */
	@Override
	@SuppressLint("WrongCall")
	public void draw(Drawer drawer) {}
	
	/** Unused */
	@Override
	public void touch(MotionEvent motionEvent) {}
	
	/** Unused */
	@Override
	public void finish() {}
	
	/**
	 * Updater
	 */
	public abstract void onUpdate();
}
