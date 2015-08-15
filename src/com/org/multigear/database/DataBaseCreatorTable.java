package com.org.multigear.database;

import java.util.ArrayList;
import java.util.List;

/**
 *  Data Base Creator Table.
 *  
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 *
 */

public class DataBaseCreatorTable {
	
	/**
	 *  Table
	 */
	
	public class Table {
		
		List<String> names;
		List<Integer> types;
		
		/**
		 *  --
		 */
		
		public Table() {
			names = new ArrayList<String>();
			types = new ArrayList<Integer>();
		}
		
		/**
		 * Add Value.
		 * @param n
		 * @param t
		 */
		
		public void add(String n, int t) {
			names.add(n);
			types.add(t);
		}
		
		/**
		 * Return Table Size.
		 * @return
		 */
		
		public int size() {
			return names.size();
		}
		
		/**
		 *  Delete item from table.
		 * @param n
		 */
		
		public void delete(String n) {
			for(int i=0; i<this.size(); i++) {
				if(names.get(i).equals(n)) {
					// Delete Objects
					names.remove(i);
					types.remove(i);
					// Retry operation
					this.delete(n);
					// Break
					break;
				}
			}
		}
		
		/**
		 *  Get Name
		 * @param id
		 * @return
		 */
		
		public String getName(int id) {
			return names.get(id);
		}
		
		/**
		 *  Get Type
		 * @param id
		 * @return
		 */
		
		public int getType(int id) {
			return types.get(id);
		}
	}
	
	// Create Table Values
	Table table;
	
	// Values Type
	final public static int VINT = 0;
	final public static int VSTR = 1;
	
	/**
	 *  --
	 */
	
	public DataBaseCreatorTable() {
		// Create Table for Creator
		table = new Table();
	}
	
	/**
	 *  Add item to construtor.
	 * @param name
	 * @param type
	 */
	
	public void addItem(String name, int type) {
		// If exist, delete for update
		this.deleteItem(name);
		// Add Item
		table.add(name, type);
	}
	
	/**
	 * Add Item (Int)
	 * @param name
	 */
	
	public void addIntItem(String name) {
		this.addItem(name, DataBaseCreatorTable.VINT);
	}
	
	/**
	 * Add Item (STR)
	 * @param name
	 */
	
	public void addStringItem(String name) {
		this.addItem(name, DataBaseCreatorTable.VSTR);
	}
	
	/**
	 *  Delete item from construtor.
	 * @param name
	 */
	
	public void deleteItem(String name) {
		table.delete(name);
	}
	
	/**
	 *  Return Table
	 * @return
	 */
	
	public Table getTable() {
		return table;
	}
	
	/**
	 *  Return Columns Size
	 * @return
	 */
	
	public int columnsSize() {
		return table.size();
	}
}
