package com.org.multigear.communication.tcp.support.objectmessage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.org.multigear.general.utils.Vector2;

/**
 * Object Message
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class ObjectMessage {
	
	// Final Private Variables
	final private Pattern mCommandPattern = Pattern.compile("(\\w+)\\:(.*)");
	final private List<Object> mValues = new ArrayList<Object>();
	final private String mMessage;
	
	// Private Variables
	private int mCode;
	
	/*
	 * Construtor
	 */
	protected ObjectMessage(final String message) {
		mMessage = message;
	}
	
	/*
	 * Traduz a mensagem
	 */
	final public void translate() {
		mValues.clear();
		String[] lines = mMessage.split(";");
		for (String line : lines) {
			final Matcher commandMatcher = mCommandPattern.matcher(line);
			if (commandMatcher.matches()) {
				addCommand(commandMatcher.group(1), commandMatcher.group(2));
			}
		}
	}
	
	/*
	 * Adciona um comando
	 */
	final private void addCommand(final String cmd, final String value) {
		if (cmd.equals("code"))
			mCode = Integer.parseInt(value);
		if (cmd.equals("short"))
			mValues.add(Short.parseShort(value));
		if (cmd.equals("int"))
			mValues.add(Integer.parseInt(value));
		if (cmd.equals("long"))
			mValues.add(Long.parseLong(value));
		if (cmd.equals("float"))
			mValues.add(Float.parseFloat(value));
		if (cmd.equals("double"))
			mValues.add(Double.parseDouble(value));
		if (cmd.equals("bool"))
			mValues.add(Boolean.parseBoolean(value));
		if (cmd.equals("str")) {
			String[] bytesString = value.split("\\.");
			byte[] bytes = new byte[bytesString.length];
			int index = 0;
			for (String byteString : bytesString)
				bytes[index++] = Byte.parseByte(byteString);
			mValues.add(new String(bytes));
		}
		if(cmd.equals("message")) {
			String[] bytesString = value.split("\\.");
			byte[] bytes = new byte[bytesString.length];
			int index = 0;
			for (String byteString : bytesString)
				bytes[index++] = Byte.parseByte(byteString);
			String message = new String(bytes);
			final ObjectMessage om = new ObjectMessage(message);
			om.translate();
			mValues.add(om);
		}
		if (cmd.equals("ref2d")) {
			String[] refList = value.split("\\,");
			final Vector2 ref2d = new Vector2(0, 0);
			ref2d.x = Float.parseFloat(refList[0]);
			ref2d.y = Float.parseFloat(refList[1]);
			mValues.add(ref2d);
		}
		if (cmd.equals("vec2d")) {
			String[] vecList = value.split("\\,");
			final com.org.multigear.general.utils.Vector2 vec2d = new Vector2(Float.parseFloat(vecList[0]), Float.parseFloat(vecList[1]));
			mValues.add(vec2d);
		}
	}
	
	/*
	 * Retorna o c�digo
	 */
	final public int getCode() {
		return mCode;
	}
	
	/*
	 * Retorna o valor desejado
	 */
	final public Object getValue(final int id) {
		return mValues.get(id);
	}
	
	/**
	 * Get Values Size
	 * @return
	 */
	final public int size() {
		return mValues.size();
	}
	
	/**
	 * Create Object Message
	 */
	final static public ObjectMessageBuilder create(final int code) {
		return new ObjectMessageBuilder(code);
	}
	
	/**
	 * Read Object Message and Translate automatically
	 * 
	 * @param message
	 * @return
	 */
	final static public ObjectMessage read(final String message) {
		final ObjectMessage om = new ObjectMessage(message);
		om.translate();
		return om;
	}
	
	/**
	 * Get Message
	 */
	final public String getMessage() {
		return mMessage;
	}
}
