package multigear.mginterface.scene;

import multigear.general.utils.Vector2;
import android.annotation.SuppressLint;
import android.view.MotionEvent;

/**
 * Installation
 * 
 * s
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
abstract public class Installation {
	
	// Private Variables
	private int mZ;
	private multigear.mginterface.scene.Scene mFatherRoom;
	private Vector2 mScreenSize;
	private multigear.mginterface.graphics.opengl.texture.Loader mTextureLoader;
	private boolean mUninstalled;
	
	/*
	 * Constutor
	 */
	public Installation() {
		mFatherRoom = null;
		mUninstalled = false;
		mZ = 0;
	}
	
	/**
	 * Set inatallation depth
	 * @param z
	 */
	final public void setZ(final int z) {
		mZ = z;
	}
	
	/**
	 * Get inatallation depth
	 * @param z
	 */
	final public int getZ() {
		return mZ;
	}
	
	/**
	 * Set Father
	 */
	final void setFather(final multigear.mginterface.scene.Scene fatherRoom) {
		mFatherRoom = fatherRoom;
	}

	/**
	 * Set Uninstalled
	 */
	final void setUninstalled() {
		mUninstalled = true;
	}
	
	/**
	 * Return True if Uninstalled.
	 * 
	 * @return True if Uninstalled
	 */
	final public boolean isUninstalled() {
		return mUninstalled;
	}
	
	/**
	 * Uninstall Request
	 */
	final public void requestUninstall() {
		mFatherRoom.getInstallManager().uninstall(this);
	}
	
	/**
	 * Uninstall Request with Delay
	 * 
	 * @param timeInMilles Time in Millis
	 */
	final public void requestUninstallDelayed(final long timeInMillis) {
		mFatherRoom.getInstallManager().uninstallDelayed(this, timeInMillis);
	}
	
	/*
	 * Retorna o Room Pai
	 */
	final public multigear.mginterface.scene.Scene getFatherRoom() {
		return mFatherRoom;
	}
	
	/*
	 * Atualiza o relógio de mesa
	 */
	abstract protected void time(long thisTime);
	
	/*
	 * Redimensiona a tela
	 */
	final public void prepareScreen(final Vector2 screenSize) {
		mScreenSize = screenSize;
	}
	
	/*
	 * Redimensiona a tela
	 */
	final public void prepareCache(final multigear.mginterface.graphics.opengl.texture.Loader textureLoader) {
		mTextureLoader = textureLoader;
	}
	
	/**
	 * Get Screen Size. If the 'FUNC_BASEPLANE_SUPPORT' feature is enabled, 
	 * this measure can be based on a factor calculated by climbing base dpi set the configurations.
	 * 
	 * @return Screen Size.
	 */
	public Vector2 getScreenSize() {
		return mScreenSize.clone();
	}
	
	/**
	 * Return TextureLoader.
	 * 
	 * @return TextureLoader
	 */
	public multigear.mginterface.graphics.opengl.texture.Loader getTextureLoader() {
		return mTextureLoader;
	}
	
	/*
	 * Configuração dos objetos
	 */
	abstract public void setup();
	
	/*
	 * Arquiva as texturas
	 */
	abstract public void cache();
	
	/*
	 * Redimensiona a tela
	 */
	abstract public void screen();
	
	/*
	 * Atualiza os objetos
	 */
	abstract public void update();
	
	/*
	 * Desenha os objetos
	 */
	@SuppressLint("WrongCall") 
	abstract public void draw(final multigear.mginterface.graphics.opengl.drawer.Drawer drawer);
	
	/*
	 * Evento de toque
	 */
	abstract public boolean touch(final MotionEvent motionEvent);

	/*
	 * Evento de toque
	 */
	abstract public boolean backPressed();
	
	/* Evento de intalação completa */
	public void onInstalled() {};
	
	/* Evento de desinstalação complenta */
	public void onUninstalled() {};
}