package com.cooksys.ftd.assessment.filesharing.model.api;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//*
@XmlRootElement(name = "ClientMessage")
public class ClientMessage {
	@XmlElement(name = "message")
	private String message;
	@XmlElement(name = "command")
	private String command;
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
}
//*/
/*/
public class ClientMessage<T> {
	
	private String message;
	private T data;
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public T getData() {
		return data;
	}
	
	public void setData(T data) {
		this.data = data;
	}
	
}
//*/
