package com.org.multigear.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Data Base Helper. Create and Update Data Base.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 * 
 */

public class DataBaseHelper extends SQLiteOpenHelper {
	
	/**
	 * Listener to send Msg.
	 * 
	 * @author user
	 * 
	 */
	
	static public interface Listener {
		public void onCreate();
		
		public void onUpgrade();
	}
	
	// --
	private static final int DATABASE_VERSION = 1;
	
	// --
	Listener listener;
	public boolean onCreateB = false;
	public boolean onUpgradeB = false;
	
	/**
	 * --
	 * 
	 * @param context
	 */
	
	public DataBaseHelper(Context context, String name) {
		super(context, name, null, DATABASE_VERSION);
	}
	
	/**
	 * On Create Data Base.
	 */
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		onCreateB = true;
	}
	
	/**
	 * On Update Data Base.
	 */
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgradeB = true;
	}
	
	/**
	 * Update DataBase Adapter.
	 */
	
	public void updateAdapter(DataBaseAdapter adapter) {
		if (onCreateB)
			adapter.onCreate();
		if (onUpgradeB)
			adapter.onUpgrade();
		onCreateB = false;
		onUpgradeB = false;
	}
}
