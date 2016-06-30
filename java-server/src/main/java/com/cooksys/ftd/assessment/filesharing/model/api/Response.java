package com.cooksys.ftd.assessment.filesharing.model.api;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Response<T> {

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
