package com.org.multigear.physics;

/**
 * Emulate Physics Moviment.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class Emulator {
	
	/**
	 * Emulate Response.
	 * 
	 * @author PedroH, RaphaelB
	 *
	 * Property Createlier.
	 */
	final static public class Response {
		
		// Private Variables
		final com.org.multigear.general.utils.Vector2 mFinalForce;
		final com.org.multigear.general.utils.Vector2 mApplyForce;
		
		/*
		 * Constrtor
		 */
		private Response(final com.org.multigear.general.utils.Vector2 finalForce, final com.org.multigear.general.utils.Vector2 applyForce) {
			mFinalForce = finalForce;
			mApplyForce = applyForce;
		}
		
		/*
		 * Retorna a força final
		 */
		final public com.org.multigear.general.utils.Vector2 getFinalForce() {
			return mFinalForce;
		}
		
		/*
		 * Retorna a força necessaria para aplicar no objeto
		 */
		final public com.org.multigear.general.utils.Vector2 getApplyForce() {
			return mApplyForce;
		}
	}
	
	/**
	 * Emulate line movimente in all shapes.
	 * 
	 * @param body Shape body.
	 * @param force Force to apply in the shape.
	 * @param shapes Shapes for check is over.
	 */
	final static public Response emulateLineMoviment(final com.org.multigear.physics.Shape body, final com.org.multigear.general.utils.Vector2 force, final com.org.multigear.physics.Shape[] shapes) {
		
		return null;
	}
}
