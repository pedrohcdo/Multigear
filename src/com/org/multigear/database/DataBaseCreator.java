package com.org.multigear.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 *  Data Base Creator
 *  
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 *
 */

public class DataBaseCreator {
	
	/**
	 *  Exception.
	 * @author user
	 *
	 */
	
	public class CreatorException extends Exception {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String exceptionMsg;
		
		/**
		 *  Initiate Exception
		 * @param m
		 */
		
		public CreatorException(String m) {
			exceptionMsg = m;
		}
		
		/**
		 *  GetException Msg
		 * @return
		 */
		
		public String getExceptionMsg() {
			return exceptionMsg;
		}
		
	}
	
	/**
	 *  Used for parameters.
	 * @author user
	 *
	 */
	
	public class Item<PT> {
		
		PT value;
		
		/**
		 *  Initiate
		 * @param i
		 */
		
		public Item(PT i) {
			value = i;
		}
		
		/**
		 *  Return Param Value
		 * @return
		 */
		
		public PT getValue() {
			return value;
		}
	}
	
	/**
	 *  Interface for Creator.
	 * @author user
	 *
	 */
	
	static public interface Interface {
		public void onCreate(DataBaseCreatorTable table);
		public String tableName();
	}
	
	/**
	 *  Query Result
	 * @author user
	 *
	 */
	
	public class QueryResult {
		
		List<Item<?>> itemsStack;
		int itemsSize;
		int pointer;
		
		/**
		 *  Result Stack.
		 * @param ls
		 */
		
		public QueryResult(List<Item<?>> s, int is) {
			itemsStack = s;
			itemsSize = is;
			pointer = 0;
		}
		
		/**
		 *  Move Pointer to First
		 * @return True Boolean if pointer in stack
		 */
		
		public boolean moveToFirst() {
			pointer = 0;
			return !pointerOut();
		}
		
		/**
		 * Move To next
		 * @return True Boolean if pointer in stack
		 */
		
		public boolean moveToNext() {
			pointer += itemsSize;
			return !pointerOut();
		}
		
		/**
		 * Move to back
		 * @return True Boolean if pointer in stack
		 */
		
		public boolean moveToBack() {
			pointer -= itemsSize;
			if(pointer < 0)
				pointer = 0;
			return !pointerOut();
		}
		
		/**
		 * @return True Boolean if pointer in stack
		 */
		
		public boolean pointerOut() {
			return (pointer >= itemsStack.size());
		}
		
		/**
		 *  Get Item
		 * @param index
		 * @return
		 */
		
		public Item<?> getItem(int index) {
			if(pointerOut())
				return null;
			return itemsStack.get(index + pointer);
		}
		
		/**
		 *  return Columns Size
		 * @return
		 */
		
		public int columnsSize() {
			return itemsStack.size() / itemsSize;
		}
	}
	
	// Adapter
	DataBaseAdapter adapter;
	DataBaseCreatorTable table;
	Interface creatorClass;
	String dataBaseName;
	
	/**
	 * @param Data Base Adapter
	 */
	
	public DataBaseCreator(Context context, Interface clss) {
		creatorClass = clss;
		dataBaseName = clss.tableName();
		table = new DataBaseCreatorTable();
		adapter = new DataBaseAdapter(context, this);
	}
	
	/**
	 *  Return Creator table.
	 * @return
	 */
	
	public DataBaseCreatorTable getCreatorTable() {
		return table;
	}
	
	/**
	 * Get Data Base Name.
	 * @return
	 */
	
	public String getDataBaseName() {
		return dataBaseName + ".db";
	}
	
	/**
	 *  Close DataBase Construtor.
	 */
	
	public void close() {
		adapter.closeDataBase();
	}
	
	/**
	 *  On Create Data Base
	 */
	
	public void onCreate() {
		creatorClass.onCreate(table);
	}
	
	/**
	 *  On Upgrade Data Base
	 */
	
	public void onUpgrade() {
		// Create New table
		DataBaseCreatorTable newt = new DataBaseCreatorTable();
		// Update Table Items
		creatorClass.onCreate(newt);
		// Update Data Base
		//...
		table = newt;
	}
	
	/**
	 *  Make Table.
	 * @return
	 */
	
	public String getCreateTableCMD() {
		
		String cmd = "create table " + dataBaseName + " ( ";
		DataBaseCreatorTable.Table tableV = table.getTable();
		//
		for(int i=0; i<tableV.size(); i++) {
			String name = tableV.getName(i);
			int type = tableV.getType(i);
			// Add Item Name
			cmd += name + " ";
			// Add Item Type
			switch(type) {
				case DataBaseCreatorTable.VINT:
					cmd += "integer ";
					break;
				case DataBaseCreatorTable.VSTR:
					cmd += "text ";
					break;
			}
			// Enable not null 
			cmd += "not null";
			// If not end each
			if(i < tableV.size() - 1)
				cmd += ", ";
		}
		// End command
		cmd += " ); ";
		return cmd;
	}
	
	/**
	 *  Return Param
	 * @param value
	 * @return
	 */
	
	public <PT> Item<PT> item(PT value) {
		return new Item<PT>(value);
	}
	
	/**
	 *  Add Items
	 * @param a
	 */
	
	public void insert(Item<?>... a) throws CreatorException {
		// Get Database and Table
		SQLiteDatabase database = adapter.getDataBase();
		DataBaseCreatorTable.Table tableV = table.getTable();
		// Values
		ContentValues cv = new ContentValues(); 
		// Add values to content
		for(int i=0; i<a.length; i++) {
			Item<?> param = a[i];
			String name = tableV.getName(i);
			int type = tableV.getType(i);
			boolean testType = true;
			// Check Argument Type and add to Content Values
			switch(type) {
				// If type Integer
				case DataBaseCreatorTable.VINT:
					if(param.getValue().getClass() != Integer.class)
						testType = false;
					else {
						// Convert value to put CV
						int value = (Integer) param.getValue();
						cv.put(name, value);
					}
					break;
				// If type String
				case DataBaseCreatorTable.VSTR:
					if(param.getValue().getClass() != String.class)
						testType = false;
					else {
						String value = (String) param.getValue();
						// Convert value to put CV
						cv.put(name, value);
					}
					break;
			}
			// If incompatible argument
			if(!testType)
				throw new CreatorException("Error on insert item to DataBase '" + getDataBaseName() + "'.\n" +
						"Incompatible argument insert to '" + name + "'.");
		}
		// Insert Content Values to DB
		database.insert(dataBaseName, null, cv);
	}
	
	/**
	 *  Query All Items
	 */
	
	public QueryResult queryAll() {
		// Get Database and Table
		SQLiteDatabase database = adapter.getDataBase();
		DataBaseCreatorTable.Table tableV = table.getTable();
		// Get Columns
		String[] columns = new String[tableV.size()];
		for(int i=0; i<tableV.size(); i++) {
			columns[i] = tableV.getName(i);
		}
		// Get Query Cursor
		Cursor cursor = database.query(dataBaseName, columns, null, null, null, null, null); 
		// List of Query Parameters
		List<Item<?>> stack = new ArrayList<Item<?>>();
		// if find any item
		if(cursor.moveToFirst()) {
			do {
				// --
				for(int i=0; i<tableV.size(); i++) {
					// Get Type Item
					int type = tableV.getType(i);
					Item<?> param = null;
					// Get Item Parameter
					switch(type) {
						// This item is Integer
						case DataBaseCreatorTable.VINT:
							param = this.item(cursor.getInt(i));
							break;
							// This item is String
						case DataBaseCreatorTable.VSTR:
							param = this.item(cursor.getString(i));
							break;
						
					}
					// Add to Stack
					stack.add(param);
				}
			// While Next Column
			} while (cursor.moveToNext());
		}
		// return Result
		return new QueryResult(stack, table.columnsSize());
	}
}
