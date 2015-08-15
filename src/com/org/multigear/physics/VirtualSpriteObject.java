package com.org.multigear.physics;


import java.util.List;

import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.graphics.drawable.sprite.Sprite;
import com.org.multigear.mginterface.scene.Scene;



/**
 * Objeto Virtual, este tipo de objeto virtual se utiliza de um Sprite.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class VirtualSpriteObject {
	
	// Constants
	final static public int FLAG_DISABLE_LEFT_LIMITSCREEN = 1;
	final static public int FLAG_DISABLE_TOP_LIMITSCREEN = 2;
	final static public int FLAG_DISABLE_RIGHT_LIMITSCREEN = 4;
	final static public int FLAG_DISABLE_BOTTOM_LIMITSCREEN = 8;
	
	// Final Private Variables
	final private com.org.multigear.mginterface.graphics.drawable.sprite.Sprite mSprite;
	final private com.org.multigear.mginterface.scene.Scene mRoom;
	
	// Private Variables
	private boolean mStarted;
	private com.org.multigear.general.utils.Vector2 mForce;
	private Vector2[] mVertices;
	//private int mWait;
	private com.org.multigear.physics.Shape mShapeSquare;
	private int mFlags;
	
	// Public Variables
	/** Screen limit object */
	public boolean ScreenLimit = false;
	/** Apply physics with world objects */
	public boolean WorldObjects = false;
	/** Friction used for decelerate force */
	public float Friction = 1.01f;
	/** Screen Size */
	public Vector2 ScreenSize;
	
	/*
	 * Constutor
	 */
	public VirtualSpriteObject(final Scene scene, final Sprite sprite) {
		mSprite = sprite;
		mRoom = scene;
		mStarted = false;
		mForce = new com.org.multigear.general.utils.Vector2(0, 0);
		ScreenSize = mRoom.getScreenSize();
		
		//mWait = 0;
		mShapeSquare = new Shape(4);
		mFlags = 0;
	}
	
	/*
	 * Adiciona um flag
	 */
	final public void addFlag(final int flag) {
		mFlags |= flag;
	}
	
	/*
	 * Remove um flag
	 */
	final public void removeFlag(final int flag) {
		mFlags ^= (mFlags & flag);
	}
	
	/*
	 * Verifica se há um flag
	 */
	final protected boolean hasFlag(final int flag) {
		return ((mFlags & flag) == flag);
	}
	
	/**
	 * Update Objects.
	 * obs(This method is automatically called by Room).
	 */
	final public void update() {
		if(mStarted) {
			mSprite.getPosition().sum(mForce);
			mForce.div(Friction);
			//mVertices = mSprite.getDesiredVerticesPosition();
			if(mVertices == null)
				return;
			mShapeSquare.copy(mVertices);
			if(mVertices != null) {
				if(ScreenLimit)
					updateScreenLimit();
				if(WorldObjects)
					updateWorldObjects();
			}
		}
	}
	
	/*
	 * Retorna o Shape referente ao quadrado do objeto.
	 */
	final protected com.org.multigear.physics.Shape getObjectSquareShape() {
		return mShapeSquare;
	}
	
	/*
	 * Atualiza a posição na tela
	 */
	final private void updateScreenLimit() {
		float scaleFactor = 1;
		if(mRoom.hasFunc(com.org.multigear.mginterface.scene.Scene.FUNC_VIRTUAL_DPI))
			scaleFactor = mRoom.getSpaceParser().getScaleFactor();
		
		float xForce = 0;
		float yForce = 0;
		boolean over = false;
		for(final Vector2 vertice : mVertices) {
			if(vertice.x < 0 && !hasFlag(FLAG_DISABLE_LEFT_LIMITSCREEN)) {
				xForce += vertice.x * -1;
				over = true;
			}
			if(vertice.y < 0 && !hasFlag(FLAG_DISABLE_TOP_LIMITSCREEN)) {
				yForce += vertice.y * -1;
				over = true;
			}
			if(vertice.x >= ScreenSize.x && !hasFlag(FLAG_DISABLE_RIGHT_LIMITSCREEN)) {
				final double diff = (vertice.x - ScreenSize.x);
				xForce -= diff;
				over = true;
			}
			if(vertice.y >= ScreenSize.y && !hasFlag(FLAG_DISABLE_BOTTOM_LIMITSCREEN)) {
				final double diff = (vertice.y - ScreenSize.y);
				yForce -= diff;
				over = true;
			}
		}
		
		if(over) {
			xForce /= scaleFactor;
			yForce /= scaleFactor;
		
			final com.org.multigear.general.utils.Vector2 force = new com.org.multigear.general.utils.Vector2(xForce, yForce);
			
		
			mForce.sum(force);
			mSprite.getPosition().sum(force);
			mForce.div(1.1f);
		}
		
	
	}
	
	/*
	 * Atualiza fisica entre os objetos
	 */
	final private void updateWorldObjects() {

		/**
		final List<VirtualSpriteObject> virtualSpriteObjects = mRoom.getVirtualSpriteObjects();
		final multigear.physics.Shape[] shapes = new multigear.physics.Shape[virtualSpriteObjects.size()];
		int i = 0;
		for(final VirtualSpriteObject virtualSpriteObject : virtualSpriteObjects) {
			shapes[i++] = virtualSpriteObject.getObjectSquareShape();
		}
		multigear.physics.Emulator.Response response = multigear.physics.Emulator.emulateLineMoviment(mShapeSquare, mForce, shapes);
		if(response != null) {
			mSprite.getPosition().sum(response.getApplyForce());
			mForce = response.getFinalForce();
			mForce.div(1.4f);
			//mWait = 5;
		}
		*/
	}
	
	/**
	 * Return forces.
	 */
	final public com.org.multigear.general.utils.Vector2 getForces() {
		return mForce;
	}
	
	/**
	 * Set forces.
	 */
	final public void setForces(final com.org.multigear.general.utils.Vector2 forces) {
		mForce = forces;
	}
	
	/**
	 * Start virtual object physics.
	 */
	final public void start() {
		mStarted = true;
	}
	
	/**
	 * Stop virtual object physics.
	 */
	final public void stop() {
		mStarted = false;
		reset();
	}
	
	/**
	 * Aply force to Object.
	 * 
	 * @param force Force to Apply
	 */
	final public void applyForce(final com.org.multigear.general.utils.Vector2 force) {
		mForce.sum(force);
	}
	
	/**
	 * Reset Physics.
	 */
	final public void reset() {
		mForce = new com.org.multigear.general.utils.Vector2(0, 0);
	}
}
