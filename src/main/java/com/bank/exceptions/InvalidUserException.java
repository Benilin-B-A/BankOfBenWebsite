package com.bank.exceptions;

public class InvalidUserException extends BankingException{

	private static final long serialVersionUID = 6630726383616541162L;

	public InvalidUserException(String message) {
		super(message);
	}

}
