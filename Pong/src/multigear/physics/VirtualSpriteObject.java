package multigear.physics;


import java.util.List;

import multigear.general.utils.Ref2F;



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
	final private multigear.mginterface.graphics.drawable.sprite.Sprite mSprite;
	final private multigear.mginterface.scene.Scene mRoom;
	
	// Private Variables
	private boolean mStarted;
	private multigear.general.utils.Vector2D mForce;
	private multigear.general.utils.Ref2F[] mVertices;
	//private int mWait;
	private multigear.physics.Shape mShapeSquare;
	private int mFlags;
	
	// Public Variables
	/** Screen limit object */
	public boolean ScreenLimit = false;
	/** Apply physics with world objects */
	public boolean WorldObjects = false;
	/** Friction used for decelerate force */
	public float Friction = 1.01f;
	/** Screen Size */
	public Ref2F ScreenSize;
	
	/*
	 * Constutor
	 */
	public VirtualSpriteObject(final multigear.mginterface.graphics.drawable.sprite.Sprite sprite) {
		mSprite = sprite;
		mRoom = mSprite.getAttachedRoom();
		mRoom.addVirtualSpriteObject(this);
		mStarted = false;
		mForce = new multigear.general.utils.Vector2D(0, 0);
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
			mSprite.getPosition().applyForce(mForce);
			mForce.friction(Friction);
			mVertices = mSprite.getDesignedVerticesPosition();
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
	final protected multigear.physics.Shape getObjectSquareShape() {
		return mShapeSquare;
	}
	
	/*
	 * Atualiza a posição na tela
	 */
	final private void updateScreenLimit() {
		float scaleFactor = 1;
		if(mRoom.hasFunc(multigear.mginterface.scene.Scene.FUNC_VIRTUAL_DPI))
			scaleFactor = mRoom.getSpaceParser().getScaleFactor();
		
		float xForce = 0;
		float yForce = 0;
		boolean over = false;
		for(final multigear.general.utils.Ref2F vertice : mVertices) {
			if(vertice.XAxis < 0 && !hasFlag(FLAG_DISABLE_LEFT_LIMITSCREEN)) {
				xForce += vertice.XAxis * -1;
				over = true;
			}
			if(vertice.YAxis < 0 && !hasFlag(FLAG_DISABLE_TOP_LIMITSCREEN)) {
				yForce += vertice.YAxis * -1;
				over = true;
			}
			if(vertice.XAxis >= ScreenSize.XAxis && !hasFlag(FLAG_DISABLE_RIGHT_LIMITSCREEN)) {
				final double diff = (vertice.XAxis - ScreenSize.XAxis);
				xForce -= diff;
				over = true;
			}
			if(vertice.YAxis >= ScreenSize.YAxis && !hasFlag(FLAG_DISABLE_BOTTOM_LIMITSCREEN)) {
				final double diff = (vertice.YAxis - ScreenSize.YAxis);
				yForce -= diff;
				over = true;
			}
		}
		
		if(over) {
			xForce /= scaleFactor;
			yForce /= scaleFactor;
		
			final multigear.general.utils.Vector2D force = new multigear.general.utils.Vector2D(xForce, yForce);
			
		
			mForce.applyForce(force);
			mSprite.getPosition().applyForce(force);
			mForce.friction(1.1f);
		}
		
	
	}
	
	/*
	 * Atualiza fisica entre os objetos
	 */
	final private void updateWorldObjects() {

		final List<VirtualSpriteObject> virtualSpriteObjects = mRoom.getVirtualSpriteObjects();
		final multigear.physics.Shape[] shapes = new multigear.physics.Shape[virtualSpriteObjects.size()];
		int i = 0;
		for(final VirtualSpriteObject virtualSpriteObject : virtualSpriteObjects) {
			shapes[i++] = virtualSpriteObject.getObjectSquareShape();
		}
		multigear.physics.Emulator.Response response = multigear.physics.Emulator.emulateLineMoviment(mShapeSquare, mForce, shapes);
		if(response != null) {
			mSprite.getPosition().applyForce(response.getApplyForce());
			mForce = response.getFinalForce();
			mForce.friction(1.4f);
			//mWait = 5;
		}
	}
	
	/**
	 * Return forces.
	 */
	final public multigear.general.utils.Vector2D getForces() {
		return mForce;
	}
	
	/**
	 * Set forces.
	 */
	final public void setForces(final multigear.general.utils.Vector2D forces) {
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
	final public void applyForce(final multigear.general.utils.Vector2D force) {
		mForce.applyForce(force);
	}
	
	/**
	 * Reset Physics.
	 */
	final public void reset() {
		mForce = new multigear.general.utils.Vector2D(0, 0);
	}
}
