package com.bank.interfaces;

import java.util.List;

import org.json.JSONObject;

import com.bank.exceptions.BankingException;
import com.bank.exceptions.PersistenceException;
import com.bank.pojo.Transaction;

public interface TransactionAgent {

	void doTransaction(Transaction trans) throws PersistenceException;

	long getTransactionBranch(long transId) throws PersistenceException;
	
	JSONObject getAccountStatement(long accNum, int limit, int offset) throws PersistenceException, BankingException;

	JSONObject getTransactionStatement(long transId) throws PersistenceException, BankingException;
	
}
