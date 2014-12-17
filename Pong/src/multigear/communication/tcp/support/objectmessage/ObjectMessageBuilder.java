package multigear.communication.tcp.support.objectmessage;

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
		mMessage = "code:" + code;
	}
	
	/*
	 * Adiciona para a fila de Mensagens
	 */
	final private void addToStackMessages(final String message) {
		mMessage += ";" + message;
	}
	
	/**
	 * Add Integer Value
	 * @param value Integer Value
	 * @return This ObjectMessage reference
	 */
	final public ObjectMessageBuilder add(final int value) {
		addToStackMessages("int:" + value);
		return this;
	}
	
	/**
	 * Add Float Value
	 * @param value Float Value
	 * @return This ObjectMessage reference
	 */
	final public ObjectMessageBuilder add(final float value) {
		addToStackMessages("float:" + value);
		return this;
	}
	
	/**
	 * Add Double Value
	 * @param value Double Value
	 * @return This ObjectMessage reference
	 */
	final public ObjectMessageBuilder add(final double value) {
		addToStackMessages("double:" + value);
		return this;
	}
	
	/**
	 * Add Boolean Value
	 * @param value Boolean Value
	 * @return This ObjectMessage reference
	 */
	final public ObjectMessageBuilder add(final boolean value) {
		addToStackMessages("bool:" + value);
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
				byteValues += ".";
			byteValues += "" + b;
			first = false;
		}
		addToStackMessages("str:" + byteValues);
		return this;
	}
	
	/**
	 * Add Ref2D Value
	 * @param value Boolean Value
	 * @return This ObjectMessage reference
	 */
	final public ObjectMessageBuilder add(final multigear.general.utils.Ref2F value) {
		addToStackMessages("ref2d:" + value.XAxis + "," + value.YAxis);
		return this;
	}
	
	/**
	 * Add Vector2D Value
	 * @param value Boolean Value
	 * @return This ObjectMessage reference
	 */
	final public ObjectMessageBuilder add(final multigear.general.utils.Vector2D value) {
		addToStackMessages("vec2d:" + value.getX() + "," + value.getY());
		return this;
	}
	
	/**
	 * Return Build Message
	 */
	final public multigear.communication.tcp.support.objectmessage.ObjectMessageBuilt build() {
		return new multigear.communication.tcp.support.objectmessage.ObjectMessageBuilt(mMessage);
	}
}
