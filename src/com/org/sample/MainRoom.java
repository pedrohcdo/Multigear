package com.org.sample;

import com.org.multigear.general.utils.Color;
import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.graphics.drawable.gui.Canvas;
import com.org.multigear.mginterface.graphics.opengl.drawer.Drawer;
import com.org.multigear.mginterface.scene.Scene;

public class MainRoom extends Scene {

	@Override
	public void onDraw(Drawer drawer) {
		Canvas canvas = new Canvas(drawer);
		canvas.drawRect(Color.RED, new Vector2(0, 0), new Vector2(100, 100));

	}
	
	@Override
	public boolean onBackPressed() {
		closeEngine();
		return true;
	}
}
