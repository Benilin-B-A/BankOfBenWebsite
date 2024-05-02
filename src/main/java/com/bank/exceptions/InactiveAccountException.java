package com.bank.exceptions;

public class InactiveAccountException extends BankingException{

	private static final long serialVersionUID = -7923333379053392830L;

	public InactiveAccountException(String message) {
		super(message);
	}

}
