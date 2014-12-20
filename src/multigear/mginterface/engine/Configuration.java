package multigear.mginterface.engine;

import java.util.ArrayList;
import java.util.List;

import multigear.general.utils.Vector2;
import android.app.Activity;

/**
 * 
 * Configuração utilisada pela biblioteca.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class Configuration {
	
	// Constants Flags
	final static public int FUNC_TEXTURE_PROPORTION = 0x1;
	final static public int FUNC_RESTORER_SERVICE = 0x2;
	
	// Attributes
	final static public String ATTR_BASE_DPI = "_B_DPI";
	final static public String ATTR_BASE_DENSITY = "_B_DENSITY";
	final static public String ATTR_BASE_SCREEN = "_B_SCREENS";
	final static public String ATTR_PROPORTION_FROM = "_PT_FROM";
	final static public String ATTR_PROPORTION_MODE = "_PT_FUNC";
	final static public String ATTR_BACKGROUND_COLOR = "_BG_COLOR";
	final static public String ATTR_CALIBRATE_DPI = "_CL_DPI";
	final static public String ATTR_RESTORER_NOTIFICATION = "_RESTORER_NOTIFICATIOn";
	
	// Proportion Modes
	final static public int PROPORTION_FROM_GENERAL = 0;
	final static public int PROPORTION_FROM_INDIVIDUAL = 1;
	
	// Proportion Funcs
	final static public int PROPORTION_MODE_BIGGER = 0;
	final static public int PROPORTION_MODE_SMALLER = 1;
	final static public int PROPORTION_MODE_DIAGONAL = 2;
	final static public int PROPORTION_MODE_UNSPECT = 3;
	
	// Densities
	final static public float DENSITY_LDPI = 120;
	final static public float DENSITY_MDPI = 1f;
	final static public float DENSITY_HDPI = 1.5f;
	final static public float DENSITY_XHDPI = 2.0f;
	final static public float DENSITY_XXHDPI = 3.0f;
	
	// Default Values
	final static public int DEFAULT_VALUE = -1;
	final static public Vector2 DEFAULT_REF2D = null;
	
	// Default Constants
	final static private float DEFAULT_BASE_DPI = DEFAULT_VALUE;
	final static private float DEFAULT_BASE_DENSITY = DEFAULT_VALUE;
	final static private Vector2 DEFAULT_BASE_SCREEN = DEFAULT_REF2D;
	final static private int DEFAULT_PROPORTION_FROM = PROPORTION_FROM_GENERAL;
	final static private int DEFAULT_PROPORTION_MODES = PROPORTION_MODE_UNSPECT;
	final static private int DEFAULT_BACKGROUND_COLOR = 0;
	final static private int DEFAULT_CALIBRATE_DPI = DEFAULT_VALUE;
	
	// Constants (Do not change)
	final static private String ERROR_MESSAGE_ILLEGAL_MOD_MAINROOM = "An error occurred while changing the Main Room. It is not possible to perform this modification after engine startup, please change before.";
	final static private int ERROR_CODE_ILLEGAL_MOD_MAINROOM = 0x3;
	
	/**
	 * 
	 * Utilisado para atributos
	 * 
	 * @author PedroH, RaphaelB
	 *
	 * Property Createlier.
	 */
	final private class Attr {
		
		// Public Variables
		volatile public String Name;
		volatile public float Value;
		volatile public Object Object;
		volatile public Vector2 Vector;
		
		/*
		 * Construtor
		 */
		public Attr(final String name, final float value) {
			Name = name;
			Value = value;
			Vector = new Vector2(0, 0);
		}
		
		/*
		 * Construtor Secundario
		 */
		public Attr(final String name, final Vector2 value) {
			Name = name;
			Vector = value;
		}
		
		/*
		 * Construtor Secundario
		 */
		public Attr(final String name, final Object object) {
			Name = name;
			Object = object;
		}
		
		/*
		 * Construtor Conjugado
		 */
		public Attr(final String name, final float valueF, final Vector2 valueR) {
			Name = name;
			Value = valueF;
			Vector = valueR;
		}
		
		/*
		 * Construtor Conjugado
		 */
		public Attr(final String name, final float valueF, final Vector2 valueR, final Object object) {
			Name = name;
			Value = valueF;
			Vector = valueR;
			Object = object;
		}
		
		/*
		 * Retorna uma copia do objeto
		 */
		final public Attr clone() {
			if(Vector == null)
				return new Attr(Name, Value, null);
			else if(Object == null)
				return new Attr(Name, Value, Vector.clone());
			else
				return new Attr(Name, Value, Vector.clone(), Object);
		}
	}
	
	/*
	 * Cria uma chave otimizada
	 */
	final public class OptimizedKey {
		
		// Private Variables
		private long mKey;
		final private int mMode;
		final private String mName;
		Attr mSaveAttr;
		
		/*
		 * Construtor
		 */
		public OptimizedKey() {
			mKey = mOptmizedKey - 1;
			mMode = 0;
			mName = "";
			mSaveAttr = null;
		}
		
		/*
		 * Construtor secundario
		 */
		public OptimizedKey(final String name) {
			mKey = mOptmizedKey - 1;
			mMode = 1;
			mName = name;
			mSaveAttr = null;
		}
		
		/*
		 * Verifica modificação e atualiza
		 */
		final public boolean wasReconfigured() {
			boolean reconfigured = false;
			if(mKey != mOptmizedKey) {
				reconfigured = true;
				mKey = mOptmizedKey;
			}
			if(mMode == 1 && reconfigured) {
				final multigear.mginterface.engine.Configuration.Attr attr = getAttr(mName);
				if(mSaveAttr == null) {
					mSaveAttr = attr.clone();
					return true;
				}
				if(mSaveAttr.Value != attr.Value) {
					mSaveAttr = attr.clone();
					return true;
				}
				final int an = mSaveAttr.Vector == null ? 1 : 0;
				final int bn = attr.Vector == null ? 1 : 0;
				if(an != bn || !(mSaveAttr.Vector.equals(attr.Vector))) {
					mSaveAttr = attr.clone();
					return true;
				}
				return false;
			}
			return reconfigured;
		}
		
		/*
		 * Retorna o valore longo referente a chave
		 */
		final public float getFloatAttr() {
			if(mName.length() == 0)
				return -1;
			if(mSaveAttr == null)
				return getAttr(mName).Value;
			return mSaveAttr.Value;
		}
		
		/*
		 * Retorna o valore longo referente a chave
		 */
		final public Vector2 getRef2DAttr() {
			if(mName.length() == 0)
				return null;
			if(mSaveAttr == null)
				return getAttr(mName).Vector;
			return mSaveAttr.Vector;
		}
	}
	
	// Final Private Variables
	final private List<Attr> mAttributes;
	
	// Private Variables
	private Class<? extends multigear.mginterface.scene.Scene> mMainRoom;
	private int mFlags;
	private boolean mEngineCreated;
	private Activity mActivity;
	private long mOptmizedKey;
	
	/*
	 * Construtor
	 */
	public Configuration() {
		mMainRoom = multigear.mginterface.scene.Scene.class;
		mFlags = 0;
		mAttributes = new ArrayList<Attr>();
		mEngineCreated = false;
		mOptmizedKey = 0;
		setupAttributes();
	}
	
	/*
	 * Inicia os atributos omitidos
	 */
	final private void setupAttributes() {
		mAttributes.add(new multigear.mginterface.engine.Configuration.Attr(ATTR_BASE_DPI, DEFAULT_BASE_DPI));
		mAttributes.add(new multigear.mginterface.engine.Configuration.Attr(ATTR_BASE_DENSITY, DEFAULT_BASE_DENSITY));
		mAttributes.add(new multigear.mginterface.engine.Configuration.Attr(ATTR_BASE_SCREEN, DEFAULT_BASE_SCREEN));
		mAttributes.add(new multigear.mginterface.engine.Configuration.Attr(ATTR_PROPORTION_FROM, DEFAULT_PROPORTION_FROM));
		mAttributes.add(new multigear.mginterface.engine.Configuration.Attr(ATTR_PROPORTION_MODE, DEFAULT_PROPORTION_MODES));
		mAttributes.add(new multigear.mginterface.engine.Configuration.Attr(ATTR_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR));
		mAttributes.add(new multigear.mginterface.engine.Configuration.Attr(ATTR_CALIBRATE_DPI, DEFAULT_CALIBRATE_DPI));
		mAttributes.add(new multigear.mginterface.engine.Configuration.Attr(ATTR_RESTORER_NOTIFICATION, new Object()));
	}
	
	/*
	 * Engine criada
	 */
	final protected void onEngineCreated(final Activity activity) {
		mEngineCreated = true;
		mActivity = activity;
	}
	
	/*
	 * Altera o espaço inicial
	 */
	final public void setMainRoom(final Class<? extends multigear.mginterface.scene.Scene> mainRoom) {
		if(mEngineCreated)
			multigear.general.utils.KernelUtils.error(mActivity, ERROR_MESSAGE_ILLEGAL_MOD_MAINROOM, ERROR_CODE_ILLEGAL_MOD_MAINROOM);
		mMainRoom = mainRoom;
	}
	
	/*
	 * Retorna a Room inicial
	 */
	final public Class<? extends multigear.mginterface.scene.Scene> getMainRoom() {
		return mMainRoom;
	}
	
	/*
	 * Habilita funções
	 */
	final public void enable(final int func) {
		mFlags |= func;
	}
	
	/*
	 * Desabilita funções
	 */
	final public void disable(final int func) {
		mFlags ^= (mFlags & func);
	}
	
	/*
	 * Retorna true caso houver uma função
	 */
	final public boolean hasFunc(final int func) {
		return ((mFlags & func) == func);
	}
	
	/*
	 * Adiciona um atributo
	 */
	final public void setAttr(final String name, final float value) {
		mOptmizedKey++;
		for(final Attr attr : mAttributes) {
			if(attr.Name.equals(name))
				attr.Value = value;
		}
	}
	
	/*
	 * Adiciona um atributo
	 */
	final public void setAttr(final String name, final Object value) {
		mOptmizedKey++;
		for(final Attr attr : mAttributes) {
			if(attr.Name.equals(name))
				attr.Object = value;
		}
	}
	
	/*
	 * Adiciona um atributo
	 */
	final public void setAttr(final String name, final Vector2 value) {
		mOptmizedKey++;
		for(final Attr attr : mAttributes) {
			if(attr.Name.equals(name)) {
				if(value == null)
					attr.Vector = null;
				else
					attr.Vector = value.clone();
			}
		}
	}
	
	/*
	 * Retorna o objeto referente ao atributo
	 */
	final private multigear.mginterface.engine.Configuration.Attr getAttr(final String name) {
		for(final Attr attr : mAttributes) {
			if(attr.Name.equals(name))
				return attr;
		}
		return null;
	}
	
	/*
	 * Retorna um atributo
	 */
	final public float getFloatAttr(final String name) {
		for(final Attr attr : mAttributes) {
			if(attr.Name.equals(name))
				return attr.Value;
		}
		return 0;
	}
	
	/*
	 * Retorna um atributo
	 */
	final public Object getObjectAttr(final String name) {
		for(final Attr attr : mAttributes) {
			if(attr.Name.equals(name))
				return attr.Object;
		}
		return null;
	}
	
	/*
	 * Retorna um atributo
	 */
	final public Vector2 getRef2DAttr(final String name) {
		for(final Attr attr : mAttributes) {
			if(attr.Name.equals(name))
				return attr.Vector;
		}
		return new Vector2(0, 0);
	}
	
	/*
	 * Cria uma nova chave otimizada
	 */
	final public OptimizedKey createOptimizedKey() {
		return new OptimizedKey();
	}
	
	/*
	 * Cria uma nova chave rotulada otimizada
	 */
	final public OptimizedKey createOptimizedKey(final String name) {
		return new OptimizedKey(name);
	}
}
