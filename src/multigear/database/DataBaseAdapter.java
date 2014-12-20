package multigear.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 *  Data Base Adapter
 *  
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 *
 */

public class DataBaseAdapter  {
	
	private DataBaseCreator dataBaseCreator;
	private DataBaseHelper helper;
	private SQLiteDatabase database;

	/** 
	 * Initiate.
	 * @param context
	 */
	
	public DataBaseAdapter(Context context, DataBaseCreator dbc) {
		dataBaseCreator = dbc;
		dataBaseCreator.onCreate();
		helper = new DataBaseHelper(context, dataBaseCreator.getDataBaseName());
		database = helper.getWritableDatabase();
		helper.updateAdapter(this);
	}
	
	/**
	 *  Get Database
	 * @return
	 */
	
	public SQLiteDatabase getDataBase() {
		return database;
	}
	
	/**
	 *  Close Data Base.
	 */
	
	public void closeDataBase() {
		// Close Any open DataBase
		database.close();
	}
	
	/**
	 *  On Create DB
	 */
	
	public void onCreate() {
		// Get cmd and execute
		String cmd = dataBaseCreator.getCreateTableCMD();
		database.execSQL(cmd);
	}
	
	/**
	 *  On Upgrade DB
	 */
	
	public void onUpgrade() {
		dataBaseCreator.onUpgrade();
	}
}
