package multigear.physics;

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
		final multigear.general.utils.Vector2D mFinalForce;
		final multigear.general.utils.Vector2D mApplyForce;
		
		/*
		 * Constrtor
		 */
		private Response(final multigear.general.utils.Vector2D finalForce, final multigear.general.utils.Vector2D applyForce) {
			mFinalForce = finalForce;
			mApplyForce = applyForce;
		}
		
		/*
		 * Retorna a força final
		 */
		final public multigear.general.utils.Vector2D getFinalForce() {
			return mFinalForce;
		}
		
		/*
		 * Retorna a força necessaria para aplicar no objeto
		 */
		final public multigear.general.utils.Vector2D getApplyForce() {
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
	final static public Response emulateLineMoviment(final multigear.physics.Shape body, final multigear.general.utils.Vector2D force, final multigear.physics.Shape[] shapes) {
		final multigear.general.utils.Ref2F[] vertices = body.getVertices();
		
		
		for(final multigear.general.utils.Ref2F vertice : vertices) {
			final multigear.general.utils.Ref2F endVertice = vertice.clone();
			endVertice.applyForce(force);
			
			final float dx = endVertice.XAxis - vertice.XAxis;
			final float dy = endVertice.YAxis - vertice.YAxis;
			final float d = (float)Math.sqrt(dx*dx + dy*dy);
			multigear.general.utils.Ref2F lastFinalVertices = null;
			
			for(int i=0; i<d; i+=100) {
				
				final float x = vertice.XAxis + (i * dx) / d;
				final float y = vertice.YAxis + (i * dy) / d;
				
				final multigear.general.utils.Ref2F testeVertice = multigear.general.utils.KernelUtils.ref2d(x, y);
				
				for(final multigear.physics.Shape shape : shapes) {
					final multigear.general.utils.Vector2D normal = multigear.physics.ColideMath.getOverShapeNormal(testeVertice, shape);
					
					if(normal == null)
						continue;
					
					// Reflect Force
					final multigear.general.utils.Vector2D finalForce = force.clone();
					finalForce.rotate180Deg();
					
					// Get apply force
					if(lastFinalVertices == null)
						lastFinalVertices = testeVertice;
					
					final multigear.general.utils.Vector2D applyForce = new multigear.general.utils.Vector2D(lastFinalVertices, vertice);
					
					return new Response(finalForce, applyForce);
				}
				
				lastFinalVertices = testeVertice;
				
			}
		}
		
		return null;
	}
}
