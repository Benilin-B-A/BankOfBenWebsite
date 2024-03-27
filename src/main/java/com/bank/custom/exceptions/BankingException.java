package com.bank.custom.exceptions;

public class BankingException extends Exception{

	private static final long serialVersionUID = 9023559937374907148L;

	public BankingException(String message) {
		super(message);
	}

	public BankingException(String message,Throwable cause) {
		super(message,cause);
	}
}
