package com.bank.interfaces;

import com.bank.exceptions.BankingException;

public interface CacheAgent {
	
	public <T> T get(long key) throws BankingException;
	
}
