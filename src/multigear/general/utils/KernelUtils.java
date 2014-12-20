package multigear.general.utils;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

/**
 * 
 * Utilidades rapidas.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class KernelUtils {
	
	// Public Constants
	final static public String LOG_TAG = "MultigearLog";
	final static public boolean DEBUG = false;
	
	// Private Constants
	final static private String ERROR_TEXT = "Ok";
	
	/* Privando COnstrutor */
	private KernelUtils() {
	};

	/*
	 * Log de aviso
	 */
	final static public void logW(final String message) {
		if (DEBUG)
			Log.w(LOG_TAG, message);
	}
	
	/*
	 * Log de erro
	 */
	final static public void logE(final String message) {
		if (DEBUG)
			Log.e(LOG_TAG, message);
	}
	
	/**
	 * Call Simple Engine Func
	 * @return
	 */
	final static public Object callEngineFunc(Object instance, String methodName) {
		Class<?> klass = instance.getClass();
		try {
			Method method = klass.getDeclaredMethod(methodName);
			method.setAccessible(true);
			return method.invoke(instance);
		} catch (Exception e) {
		}
		return null;
	}
	
	/*
	 * Mensagem de erro
	 */
	final static public void error(final Activity activity, final String errorMessage, final int errorCode) {
		logE(errorMessage);
		/* Run Code on Ui Thread */
		activity.runOnUiThread(new Runnable() {
			/* Run */
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.
				setMessage(String.format(errorMessage)).
				setCancelable(false).
				setNeutralButton(ERROR_TEXT, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						activity.finish();
					}
				});
				AlertDialog error = builder.create();
				error.show();
			}
		});
	}
}
