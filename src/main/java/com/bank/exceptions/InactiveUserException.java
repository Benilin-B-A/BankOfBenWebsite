package com.bank.exceptions;

public class InactiveUserException extends BankingException{

	private static final long serialVersionUID = -7784190969726042365L;

	public InactiveUserException(String message) {
		super(message);
	}

}
