package multigear.communication.tcp.support.objectmessage;

import android.util.Base64;
import multigear.general.utils.Vector2;

/**
 * Object Message
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class ObjectMessageBuilder {
	
	// Private Variables
	private String mMessage;
	
	/*
	 * Construtor
	 */
	protected ObjectMessageBuilder(final int code) {
		mMessage = "code:".concat(code + "");
	}
	
	/*
	 * Adiciona para a fila de Mensagens
	 */
	final private void addToStackMessages(final String message) {
		mMessage = mMessage.concat(";");
		mMessage = mMessage.concat(message);
	}
	
	/**
	 * Add Integer Value
	 * @param value Integer Value
	 * @return This ObjectMessage reference
	 */
	final public ObjectMessageBuilder add(final short value) {
		addToStackMessages(new StringBuilder("short:").append(value).toString());
		return this;
	}
	
	/**
	 * Add Integer Value
	 * @param value Integer Value
	 * @return This ObjectMessage reference
	 */
	final public ObjectMessageBuilder add(final int value) {
		addToStackMessages(new StringBuilder("int:").append(value).toString());
		return this;
	}
	
	/**
	 * Add Integer Value
	 * @param value Integer Value
	 * @return This ObjectMessage reference
	 */
	final public ObjectMessageBuilder add(final long value) {
		addToStackMessages(new StringBuilder("long:").append(value).toString());
		return this;
	}
	
	/**
	 * Add Float Value
	 * @param value Float Value
	 * @return This ObjectMessage reference
	 */
	final public ObjectMessageBuilder add(final float value) {
		addToStackMessages(new StringBuilder("float:").append(value).toString());
		return this;
	}
	
	/**
	 * Add Double Value
	 * @param value Double Value
	 * @return This ObjectMessage reference
	 */
	final public ObjectMessageBuilder add(final double value) {
		addToStackMessages(new StringBuilder("double:").append(value).toString());
		return this;
	}
	
	/**
	 * Add Boolean Value
	 * @param value Boolean Value
	 * @return This ObjectMessage reference
	 */
	final public ObjectMessageBuilder add(final boolean value) {
		addToStackMessages(new StringBuilder("bool:").append(value).toString());
		return this;
	}
	
	/**
	 * Add String Value
	 * @param value String Value
	 * @return This ObjectMessage reference
	 */
	final public ObjectMessageBuilder add(final String value) {
		String byteValues = "";
		boolean first = true;
		for(byte b : value.getBytes()) {
			if(!first)
				byteValues = byteValues.concat(".");
			byteValues = byteValues.concat("" + b);
			first = false;
		}
		addToStackMessages("str:".concat(byteValues));
		return this;
	}
	
	/**
	 * Add Ref2D Value
	 * @param value Boolean Value
	 * @return This ObjectMessage reference
	 */
	final public ObjectMessageBuilder add(final Vector2 value) {
		addToStackMessages(new StringBuilder("ref2d:").append(value.x).append(",").append(value.y).toString());
		return this;
	}
	
	/**
	 * Add Object Message
	 * @param message
	 * @return
	 */
	final public ObjectMessageBuilder add(final ObjectMessage message) {
		String byteValues = "";
		boolean first = true;
		for(byte b : message.getMessage().getBytes()) {
			if(!first)
				byteValues = byteValues.concat(".");
			byteValues = byteValues.concat("" + b);
			first = false;
		}
		addToStackMessages("message:".concat(byteValues));
		return this;
	}
	
	/**
	 * Return Build Message
	 */
	final public ObjectMessage build() {
		return new ObjectMessage(mMessage);
	}
}
