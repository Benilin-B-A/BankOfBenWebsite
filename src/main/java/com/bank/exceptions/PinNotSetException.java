package com.bank.exceptions;

public class PinNotSetException extends BankingException{

	private static final long serialVersionUID = 3407850170856753849L;

	public PinNotSetException(String message) {
		super(message);
	}

}
