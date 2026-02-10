package com.gameplatform.dto;

import java.time.LocalDate;

public class ErrorResponse {
	private String message;
	private LocalDate timestamp;
	public ErrorResponse(String message)
	{
		this.message = message;
		setTimestamp(LocalDate.now());
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public LocalDate getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDate timestamp) {
		this.timestamp = timestamp;
	}
	
}
