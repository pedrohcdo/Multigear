package multigear.mginterface.graphics.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView.Renderer;

/**
 * 
 * Renderisador base para o GL Surface
 * 
 * @author PedroH, RaphaelB
 *
 * Property SpringBall.
 */
public abstract class BaseRenderer implements Renderer {
	
	// Final Private Variables
    final private multigear.mginterface.engine.Multigear mEngine;
    
    // Private Variables
    private boolean mSurfaceCreated;
    private Vector2 mScreenSize;
	private Drawer mDrawer;
    
    /*
     * Construtor
     */
    public BaseRenderer(final multigear.mginterface.engine.Multigear engine) {
    	mEngine = engine;
        mSurfaceCreated = false;
        mScreenSize = new Vector2(-1, -1);
    }
    
    /*
     * Retorna a Engine
     */
    final protected multigear.mginterface.engine.Multigear getEngine() {
    	return mEngine;
    }
 
    /*
     * Criação da surface
     * 
     * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mSurfaceCreated = true;
    }
    
    /*
     * Modificação da surface
     * 
     * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
     */
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    	// Discard if Screen Changed
        if (!mSurfaceCreated)
            return;
        // Set surface created
        mSurfaceCreated = false;
        // Get Display Size
        final Vector2 defaultScreenSize = new Vector2(width, height);
        mScreenSize  = multigear.general.utils.GeneralUtils.getRealScreenSize(mEngine.getActivity().getWindowManager().getDefaultDisplay(), defaultScreenSize);
        // Send Message to extended Object
        onCreate(mScreenSize); 
        // Instantiate Texture Loader
        final multigear.mginterface.graphics.opengl.texture.Loader textureLoader = new multigear.mginterface.graphics.opengl.texture.Loader(mEngine, mScreenSize);
        mDrawer = new Drawer(mEngine.getMainRoom(), (multigear.mginterface.graphics.opengl.Renderer)this);
        // Synchronize and Setup
        mEngine.sync()
        	.time()
        	.prepareScreen(mScreenSize)
        	.prepareCache(textureLoader)
        	.setup()
        	.screen()
        	.cache()
        .unsync();
    }
 
    /*
     * Desenhando frame atual
     * 
     * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
     */
    @SuppressLint("WrongCall") @Override
    public void onDrawFrame(GL10 unused) {
    	// Send message for extended object
        onDraw();
    	// Send Messages for Engine
    	mEngine.sync().time().update().draw(mDrawer).unsync();
    }
    
    /*
     * Retorna o tamanho
     */
    protected Vector2 getScreenSize() {
    	return mScreenSize;
    }
    
    /* Criação da surface abstrata */
    public abstract void onCreate(final Vector2 screenSize);
    
    /* Desenhando frame atual abstrata */
    public abstract void onDraw();
}
