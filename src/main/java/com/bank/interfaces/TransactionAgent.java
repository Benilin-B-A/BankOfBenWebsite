package com.bank.interfaces;

import java.util.List;

import com.bank.custom.exceptions.PersistenceException;
import com.bank.pojo.Transaction;

public interface TransactionAgent {

	void doTransaction(Transaction trans) throws PersistenceException;

	long getTransactionBranch(long transId) throws PersistenceException;
	
	List<Transaction> getAccountStatement(long accNum, int limit, int offset) throws PersistenceException;

	List<Transaction> getTransactionStatement(long transId) throws PersistenceException;
	
}
