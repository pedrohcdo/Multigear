package com.org.multigear.mginterface.tools.mgmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.org.multigear.communication.tcp.support.objectmessage.ObjectMessageBuilder;
import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.tools.mgmap.MultigearGame.Player;
import com.org.multigear.mginterface.tools.mgmap.MultigearGame.RegisterMode;

/**
 * MultigearGame
 * 
 * @author user
 *
 */
final public class GameVariables {
	
	/**
	 * Game Message
	 * 
	 * @author user
	 *
	 */
	final private class GameMessage {
		
		int code;
		Object object1, object2, object3;
	}
	
	/**
	 * Variable Type
	 * 
	 * @author user
	 *
	 */
	public enum VariableType {
		
		/* Consts */
		BOOLEAN,
		BYTE,
		CHAR,
		SHORT,
		INTEGER,
		LONG,
		FLOAT,
		DOUBLE,
		VECTOR2;
	}
	
	/**
	 * Variable
	 * 
	 * @author user
	 *
	 */
	final private class MonitorVariable {
		
		int group;
		int id;
		VariableType type;
		RegisterMode mode;
		Object value;
		Player adm;
		
		/**
		 * Set Default Value
		 * @param type
		 */
		final private void setDefault(final VariableType type) {
			switch(type) {
			case BOOLEAN:
				value = false;
				break;
			case BYTE:
				value = (byte)0;
				break;
			case CHAR:
				value = (char)0;
				break;
			case DOUBLE:
				value = (double)0;
				break;
			case FLOAT:
				value = (float)0;
				break;
			case INTEGER:
				value = (int)0;
				break;
			case LONG:
				value = (long)0;
				break;
			case SHORT:
				value = (short)0;
				break;
			case VECTOR2:
				value = new Vector2();
				break;
			default:
				break;
			}
		}
	}
	
	// Conts
	final private static int VARIABLE_SET = 1;
	
	// Final Private Variables
	final private List<MonitorVariable> mMonitorVariables = new ArrayList<GameVariables.MonitorVariable>();
	final private List<GameMessage> mGameMessages = new ArrayList<GameMessage>();
	final private MultigearGame mMultigearGame;
	
	// Private Variables
	private boolean mAutoSync = false;
	
	/**
	 * Private Constructor
	 */
	protected GameVariables(final MultigearGame duoMap) {
		mMultigearGame = duoMap;
	}
	
	/**
	 * Register Variable
	 * 
	 * @param adm
	 * @param type
	 * @param id
	 */
	final public void register(final Player adm, final VariableType type, final RegisterMode mode, final int group, final int id) {
		if(getMonitorVariable(group, id) != null)
			throw new RuntimeException("This id already in use");
		final MonitorVariable variable = new MonitorVariable();
		variable.setDefault(type);
		variable.group = group;
		variable.id = id;
		variable.type = type;
		variable.adm = adm;
		variable.mode = mode;
		mMonitorVariables.add(variable);
	}
	
	/**
	 * Register Variable in group 0
	 * 
	 * @param adm
	 * @param type
	 * @param id
	 */
	final public void register(final Player adm, final VariableType type, final RegisterMode mode, final int id) {
		register(adm, type, mode, 0, id);
	}
	
	/**
	 * Register Variable and set value
	 * 
	 * @param adm
	 * @param type
	 * @param id
	 */
	final public void register(final Player adm, final VariableType type, final RegisterMode mode, final int group, final int id, final Object value) {
		register(adm, type, mode, group, id);
		set(group, id, value);
	}
	
	/**
	 * Register Variable and set value in group 0
	 * 
	 * @param adm
	 * @param type
	 * @param id
	 */
	final public void register(final Player adm, final VariableType type, final RegisterMode mode, final int id, final Object value) {
		register(adm, type, mode, 0, id, value);
	}
	
	/**
	 * Set Variable Value
	 * 
	 * @param id Variable Id
	 * @param value Variable Value
	 */
	final public void set(final int group, final int id, final Object value) {
		final MonitorVariable variable = getMonitorVariable(group, id);
		if(variable == null)
			throw new RuntimeException("This variable id not exist");
		if(freeToModify(variable)) {
			// Message
			final ObjectMessageBuilder builder = mMultigearGame.prepareVariablesMessage(VARIABLE_SET);
			// Put Identify
			builder.add(variable.group);
			builder.add(id);
			// Message
			switch(variable.type) {
			default:
			case BOOLEAN:
				if(!(value instanceof Boolean))
					throw new IllegalArgumentException("Wrong value type, this variable type is Boolean.");
				builder.add((Boolean)value);
				break;
			case BYTE:
				break;
			case CHAR:
				break;
			case DOUBLE:
				if(!(value instanceof Double))
					throw new IllegalArgumentException("Wrong value type, this variable type is Double.");
				builder.add((Double)value);
				break;
			case FLOAT:
				if(!(value instanceof Float))
					throw new IllegalArgumentException("Wrong value type, this variable type is Float.");
				builder.add((Float)value);
				break;
			case INTEGER:
				if(!(value instanceof Integer))
					throw new IllegalArgumentException("Wrong value type, this variable type is Integer.");
				builder.add((Integer)value);
				break;
			case LONG:
				if(!(value instanceof Long))
					throw new IllegalArgumentException("Wrong value type, this variable type is Long.");
				builder.add((Long)value);
				break;
			case SHORT:
				if(!(value instanceof Short))
					throw new IllegalArgumentException("Wrong value type, this variable type is Short.");
				builder.add((Short)value);
				break;
			case VECTOR2:
				if(!(value instanceof Vector2))
					throw new IllegalArgumentException("Wrong value type, this variable type is Vector2.");
				builder.add((Vector2)value);
			}
			// Set Variable
			variable.value = value;
			// Finish and send
			mMultigearGame.sendMessage(builder.build());
		}
	}
	
	/**
	 * Set Variable Value in group 0
	 * 
	 * @param id Variable Id
	 * @param value Variable Value
	 */
	final public void set(final int id, final Object value) {
		set(0, id, value);
	}
	
	/**
	 * Return true if free to modify
	 * 
	 * @param variable
	 * @return
	 */
	final private boolean freeToModify(final MonitorVariable variable) {
		if(variable.adm == mMultigearGame.getState().getPlayer() || variable.mode == RegisterMode.FREE)
			return true;
		return false;
	}
	
	/**
	 * Get Variable value in group by id<br>
	 * <b>Note:</b> The value is Object instanced of variable type
	 * @param id Variable Id
	 * @return Variable value
	 */
	final public Object get(final int group, final int id) {
		final MonitorVariable variable = getMonitorVariable(group, id);
		if(variable == null)
			throw new RuntimeException("This variable id not exist");
		return variable.value;
	}
	
	/**
	 * Get Variable value in group 0 by id<br>
	 * <b>Note:</b> The value is Object instanced of variable type
	 * @param id Variable Id
	 * @return Variable value
	 */
	final public Object get(final int id) {
		return get(0, id);
	}
	
	/**
	 * Get Monitor Variable
	 * @param id
	 * @return
	 */
	final private MonitorVariable getMonitorVariable(final int group, final int id) {
		for(final MonitorVariable variable : mMonitorVariables) {
			if(variable.group == group && variable.id == id)
				return variable;
		}
		return null;
	}
	
	/**
	 * On Message
	 * @param values
	 */
	final protected void message(int code, final List<Object> values) {
		final GameMessage message = new GameMessage();
		message.code = code;
		switch(code) {
		case VARIABLE_SET:
			message.object1 = values.get(0);
			message.object2 = values.get(1);
			message.object3 = values.get(2);
			break;
		}
		mGameMessages.add(message);
	}
	
	/**
	 * On Update
	 */
	final protected void update() {
		final Iterator<GameMessage> itr = mGameMessages.iterator();
		while(itr.hasNext()) {
			final GameMessage message = itr.next();
			// Variables
			int group, id, control;
			Object object;
			MonitorVariable variable;
			// Message
			switch(message.code) {
			case VARIABLE_SET:
				group = (Integer)message.object1;
				id = (Integer)message.object2;
				object = message.object3;
				variable = getMonitorVariable(group, id);
				// If object exist, consume message
				if(variable != null) {
					// Set variable value
					switch(variable.type) {
					default:
					case BOOLEAN:
						variable.value = (Boolean) object;
						break;
					case BYTE:
						break;
					case CHAR:
						break;
					case DOUBLE:
						variable.value = (Double) object;
						break;
					case FLOAT:
						variable.value = (Float) object;
						break;
					case INTEGER:
						variable.value = (Integer) object;
						break;
					case LONG:
						variable.value = (Long) object;
						break;
					case SHORT:
						variable.value = (Short) object;
						break;
					case VECTOR2:
						variable.value = (Vector2) object;
						break;
					}
					// Consume
					itr.remove();
				}
				break;
			}
		}
	}
}
