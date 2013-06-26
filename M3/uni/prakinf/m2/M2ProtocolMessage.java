package uni.prakinf.m2;

import java.io.Serializable;

public class M2ProtocolMessage implements Serializable {
	private static final long serialVersionUID = 7896917326203081150L;
	
	private MessageType messageType;
	private String paramA;
	private String paramB;
	
	public M2ProtocolMessage(MessageType messageType, String paramA,
			String paramB) {
		super();
		this.messageType = messageType;
		this.paramA = paramA;
		this.paramB = paramB;
	}



	public MessageType getMessageType() {
		return messageType;
	}



	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}



	public String getParamA() {
		return paramA;
	}



	public void setParamA(String paramA) {
		this.paramA = paramA;
	}



	public String getParamB() {
		return paramB;
	}



	public void setParamB(String paramB) {
		this.paramB = paramB;
	}



	public enum MessageType {
		// Anmeldung
		LOGIN_REQUEST,
		LOGIN_RESPONSE_OK,
		LOGIN_RESPONSE_ERROR,
		
		// Statusmeldungen
		CHANNEL_LIST,
		
		// Nachrichten
		MESSAGE_REQUEST,
		MESSAGE_BROADCAST
	}
}
