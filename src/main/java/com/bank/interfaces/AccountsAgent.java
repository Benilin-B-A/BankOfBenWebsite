package com.bank.interfaces;

import java.util.List;
import java.util.Map;

import com.bank.enums.Status;
import com.bank.exceptions.PersistenceException;
import com.bank.pojo.Account;

public interface AccountsAgent {

	long getBalance(long userId) throws PersistenceException;

	long getPrimaryAcc(long userId) throws PersistenceException;

	boolean isAccountPresent(long accNum) throws PersistenceException;

	long getCustomerId(long accNum) throws PersistenceException;

	void switchPrimary(long userId, long currentAccNum, long newAccNum) throws PersistenceException;

	Account getAccount(long accNum) throws PersistenceException;

	Map<Long, Account> getAccounts(long customerId) throws PersistenceException;

	public int getAccStatus(long accNum) throws PersistenceException;

	public void setAccStatus(long accNum, Status status) throws PersistenceException;

	void closeAccount(long accNum) throws PersistenceException;

	long getBranchId(long accNum) throws PersistenceException;

	List<Long> getAccountList(long key) throws PersistenceException;
}
