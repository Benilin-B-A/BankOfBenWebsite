package com.bank.services;

import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.bank.pojo.Event;
import com.bank.pojo.Transaction;
import com.bank.util.LogHandler;
import com.bank.util.TimeUtil;

public class EmployeeServices {

	private long userId;

	private long branchId;

	public long getUserId() {
		return userId;
	}

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
			AuthServices.isAccountPresent(accNum);
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
		UserServices.withdraw(accNum, amount, this.userId);
		log("Withdraw", UserServices.getCustomerId(accNum), "Money withdrawn from bank");
	}

	public void deposit(long accNum, long amount) throws BankingException {
		validateEmpAccess(accNum);
		UserServices.deposit(accNum, amount, this.userId);
		log("Deposit", UserServices.getCustomerId(accNum), "Money deposited in bank");
	}

	public void transfer(Transaction trans, long accNum, boolean withinBank) throws BankingException {
		validateEmpAccess(accNum);
		trans.setAccNumber(accNum);
		trans.setCreatedBy(this.userId);
		UserServices.transferMoney(trans, withinBank);
		String str = null;
		if(withinBank) {
			str = "Money transfered within bank to account number :";
		}else {
			str = "Money transfered  outside bank to account number :";
		}
		log("Money transfer", UserServices.getCustomerId(accNum),str + trans.getTransactionAccountNumber());
	}

	public void changePassword(String oldPass, String newPass) throws BankingException {
		UserServices.changePassword(userId, oldPass, newPass);
		log("Password changed", this.userId,
				"Password changed");
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
		cus.setModifiedBy(this.userId);
		acc.setModifiedBy(this.userId);
		long customerId = UserServices.addCustomer(cus, acc);
		log("New Customer added", customerId, "New Customer ID : " + customerId);
		return customerId;
	}

	public long addAccount(Account acc) throws BankingException {
		acc.setBranchId(branchId);
		acc.setModifiedBy(this.userId);
		long accountNumber = UserServices.addAccount(acc);
		log("New Account added", accountNumber, "New Account ID : " + accountNumber);
		return accountNumber;
	}

	public void setAccountStatus(long accNum, Status status) throws BankingException {
		validateEmpAccess(accNum);
		AuthServices.setAccountStatus(accNum, status);
		log("Account status update", UserServices.getCustomerId(accNum), "Account status set to " + status);
	}

	public void closeAcc(long accNum) throws BankingException {
		validateEmpAccess(accNum);
		UserServices.closeAcc(accNum);
		log("Account closed", UserServices.getCustomerId(accNum), "Account number " + accNum);
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
	
	private void log(String eventName, Long targetId, String description) {
		Event event = new Event();
		event.setUserId(this.userId);
		event.setEvent(eventName);
		event.setTargetUserId(targetId);
		event.setDescription(description);
		event.setTime(TimeUtil.getTime());
		EventLogger.log(event);
	}
}
