package com.bank.exceptions;

public class InvalidAccountException extends BankingException{

	private static final long serialVersionUID = -5915078941932299426L;

	public InvalidAccountException(String message) {
		super(message);
	}

}
