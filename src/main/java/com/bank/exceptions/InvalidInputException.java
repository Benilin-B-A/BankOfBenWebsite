package com.bank.exceptions;

public class InvalidInputException extends Exception{

	private static final long serialVersionUID = 1096280708628189017L;
	
	public InvalidInputException(String message) {
		super(message);
	}
	
}
