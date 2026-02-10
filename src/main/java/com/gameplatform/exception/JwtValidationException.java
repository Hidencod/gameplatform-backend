package com.gameplatform.exception;

public class JwtValidationException extends RuntimeException{
	public JwtValidationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
