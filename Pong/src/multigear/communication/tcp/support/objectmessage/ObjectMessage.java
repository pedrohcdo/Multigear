package multigear.communication.tcp.support.objectmessage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	final private List<Object> mValues;
	
	// Private Variables
	private int mCode;
	
	/*
	 * Construtor
	 */
	private ObjectMessage(final String message) {
		mValues = new ArrayList<Object>();
		translateMessage(message);
	}
	
	/*
	 * Traduz a mensagem
	 */
	final private void translateMessage(final String message) {
		String[] lines = message.split(";");
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
		if (cmd.equals("int"))
			mValues.add(Integer.parseInt(value));
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
		if (cmd.equals("ref2d")) {
			String[] refList = value.split("\\,");
			final multigear.general.utils.Ref2F ref2d = multigear.general.utils.KernelUtils.ref2d(0, 0);
			ref2d.XAxis = Float.parseFloat(refList[0]);
			ref2d.YAxis = Float.parseFloat(refList[1]);
			mValues.add(ref2d);
		}
		if (cmd.equals("vec2d")) {
			String[] vecList = value.split("\\,");
			final multigear.general.utils.Vector2D vec2d = multigear.general.utils.KernelUtils.vec2d(Float.parseFloat(vecList[0]), Float.parseFloat(vecList[1]));
			mValues.add(vec2d);
		}
	}
	
	/*
	 * Retorna o código
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
	 * Create Object Message
	 */
	final static public multigear.communication.tcp.support.objectmessage.ObjectMessageBuilder create(final int code) {
		return new multigear.communication.tcp.support.objectmessage.ObjectMessageBuilder(code);
	}
	
	/**
	 * Read Object Message
	 * 
	 * @param message
	 * @return
	 */
	final static public ObjectMessage read(final String message) {
		return new ObjectMessage(message);
	}
}
