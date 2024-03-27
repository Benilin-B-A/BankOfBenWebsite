package com.bank.custom.exceptions;

public class PersistenceException extends Exception{

	private static final long serialVersionUID = 2424780489082663721L;
	
	public PersistenceException(String message,Throwable cause) {
		super(message,cause);
	}
}
