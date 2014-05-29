package com.roxtr.iot.notif.common;

import java.io.Serializable;

public class DesktopNSMessage implements Serializable {

	private static final long serialVersionUID = 1L;


	public DesktopNSMessage(String clientName, String password, String title, String msgString) {
		this.clientName = clientName;
		this.password = password;
		this.msgString = msgString;
		this.title = title;
	}
	
	private String type, clientName, password, title, msgString;
	

	public String getType() {
		return type;
	}

	public String getPassword() {
		return password;
	}

	public String getMsgString() {
		return msgString;
	}

	public String getTitle() {
		return title;
	}

	public String getClientName() {
		return clientName;
	}

}
