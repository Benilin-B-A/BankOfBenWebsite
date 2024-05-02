package com.bank.interfaces;

import com.bank.exceptions.PersistenceException;
import com.bank.pojo.Customer;

public interface CustomerAgent {

	String getPin(long userId) throws PersistenceException;

	void setPin(String newPin, long userId) throws PersistenceException;

	int getTPinAttempts(long customerId) throws PersistenceException;
	
	void setTPinAttempts(long customerId, int attempt) throws PersistenceException;

	Customer getCustomer(long userId) throws PersistenceException;
	
	boolean isCustomerPresent(long cusId) throws PersistenceException;

	boolean isAlreadyCustomer(String panNum) throws PersistenceException;

}
