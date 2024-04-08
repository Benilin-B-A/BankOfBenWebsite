package com.bank.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bank.enums.Status;
import com.bank.exceptions.BankingException;
import com.bank.exceptions.PersistenceException;
import com.bank.interfaces.AccountsAgent;
import com.bank.interfaces.EmployeeAgent;
import com.bank.interfaces.TransactionAgent;
import com.bank.persistence.util.PersistenceObj;
import com.bank.pojo.Account;
import com.bank.pojo.Customer;
import com.bank.pojo.Transaction;
import com.bank.util.LogHandler;

public class EmployeeServices {

	private long userId;

	private long branchId;

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}

	private static AccountsAgent accAgent = PersistenceObj.getAccountsAgent();
	private static EmployeeAgent empAgent = PersistenceObj.getEmployeeAgent();
	private static TransactionAgent tranAgent = PersistenceObj.getTransactionAgent();

	private static Logger logger = LogHandler.getLogger(CustomerServices.class.getName(), "EmployeeServices.txt");

	private boolean validateEmpAccess(long accNum) throws BankingException {
		try {
			AuthServices.validateAccountPresence(accNum);
			long brId = accAgent.getBranchId(accNum);
			if (brId == branchId) {
				return true;
			}
			throw new BankingException("Employee doesn't have access to this account");
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in authorizing employee access");
			throw new BankingException("Couldn't validate credentials");
		}
	}

	public long getBalance(long accNum) throws BankingException {
		validateEmpAccess(accNum);
		return UserServices.getBalance(accNum);
	}

	public void withdraw(long accNum, long amount) throws BankingException {
		validateEmpAccess(accNum);
		UserServices.withdraw(accNum, amount);
	}

	public void deposit(long accNum, long amount) throws BankingException {
		validateEmpAccess(accNum);
		UserServices.deposit(accNum, amount);
	}

	public void transfer(Transaction trans, long accNum, boolean withinBank) throws BankingException {
		validateEmpAccess(accNum);
		trans.setAccNumber(accNum);
		UserServices.transferMoney(trans, withinBank);
	}

	public void changePassword(String oldPass, String newPass) throws BankingException {
		UserServices.changePassword(userId, oldPass, newPass);
	}

	public JSONObject getAccountStatement(long accNum) throws BankingException {
		return getAccountStatement(accNum, 1);
	}

	public JSONObject getAccountStatement(long accNum, int page) throws BankingException {
		validateEmpAccess(accNum);
		return UserServices.getAccountStatement(accNum, page);
	}

	public JSONObject getTransStatement(long transId) throws BankingException {
		try {
			long branchId = tranAgent.getTransactionBranch(transId);
			if (branchId == empAgent.getBranchId(userId)) {
				return UserServices.getTransStatement(transId);
			}
			throw new BankingException("Employee doesn't have access to this transaction");
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Cannot get transaction branch");
			throw new BankingException("Couldn't fetch transaction statement");
		}
	}

	public JSONObject getAccount(long accNum) throws BankingException {
		validateEmpAccess(accNum);
		return UserServices.getAccountDetails(accNum);
	}

	public long addCustomer(Customer cus, Account acc) throws BankingException {
		acc.setBranchId(branchId);
		return UserServices.addCustomer(cus, acc);
	}

	public long addAccount(Account acc) throws BankingException {
		acc.setBranchId(branchId);
		return UserServices.addAccount(acc);
	}

	public void setAccountStatus(long accNum, Status status) throws BankingException {
		validateEmpAccess(accNum);
		AuthServices.setAccountStatus(accNum, status);
	}

	public void closeAcc(long accNumber) throws BankingException {
		validateEmpAccess(accNumber);
		UserServices.closeAcc(accNumber);
	}

	public JSONObject getCustomerDetails(long cusId) throws BankingException {
		return UserServices.getCustomerDetails(cusId);
	}

	public JSONObject getEmployeeDetails() throws BankingException {
		return getEmployeeDetails(userId);
	}
	
	public JSONObject getEmployeeDetails(long empId) throws BankingException {
		return UserServices.getEmployeeDetails(empId);
	}
}
