package com.bank.services;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.bank.adapter.JSONAdapter;
import com.bank.cache.AccountCache;
import com.bank.cache.CustomerAccountsCache;
import com.bank.cache.ProfileCache;
import com.bank.enums.UserLevel;
import com.bank.exceptions.BankingException;
import com.bank.exceptions.InvalidInputException;
import com.bank.exceptions.PersistenceException;
import com.bank.interfaces.AccountsAgent;
import com.bank.interfaces.EmployeeAgent;
import com.bank.interfaces.TransacAgent;
import com.bank.interfaces.TransactionAgent;
import com.bank.interfaces.UserAgent;
import com.bank.persistence.util.PersistenceObj;
import com.bank.pojo.Account;
import com.bank.pojo.Customer;
import com.bank.pojo.Employee;
import com.bank.pojo.Transaction;
import com.bank.util.LogHandler;
import com.bank.util.Validator;

public abstract class UserServices {

	private static AccountsAgent accAgent = PersistenceObj.getAccountsAgent();
	private static TransactionAgent tranAgent = PersistenceObj.getTransactionAgent();
	private static UserAgent userAgent = PersistenceObj.getUserAgent();
//	private static CustomerAgent cusAgent = PersistenceObj.getCustmomerAgent();
	private static EmployeeAgent empAgent = PersistenceObj.getEmployeeAgent();
	private static TransacAgent trAgnet = PersistenceObj.getTransacAgent();

	private static Logger logger = LogHandler.getLogger(UserServices.class.getName(), "UserServices.txt");
	
	static AccountCache accCache = AccountCache.getInstance();
	static ProfileCache proCache = ProfileCache.getInstance();
	static CustomerAccountsCache accsCache = CustomerAccountsCache.getInstance();

	static long getBalance(long aNum) throws BankingException {
		try {
			return accAgent.getBalance(aNum);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in fetching balance", exception);
			throw new BankingException("Error in fetching balance", exception);
		}
	}

	static long getCustomerId(long accNum) throws BankingException {
		try {
			return accAgent.getCustomerId(accNum);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in fetching customerId", exception);
			throw new BankingException("Cannot complete withdrawl. Try again");
		}
	}

	static void withdraw(long accNum, long amount) throws BankingException {
		AuthServices.validateAccount(accNum);
		Transaction trans = new Transaction();
		trans.setAccNumber(accNum);
		trans.setAmount(amount);
		trans.setDescription("Withdrawl");
		trans.setTransactionId(System.currentTimeMillis());
		withdraw(trans);
	}

	private static void withdraw(Transaction trans) throws BankingException {
		long bal = getBalance(trans.getAccNumber());
		long amount = trans.getAmount();
		validateBal(bal, amount);
		trans.setCustomerId(getCustomerId(trans.getAccNumber()));
		trans.setType("Debit");
		trans.setOpeningBal(bal);
		trans.setClosingBal(bal - amount);
		doTransaction(trans);
	}

	static void deposit(long accNum, long amount) throws BankingException {
		AuthServices.validateAccount(accNum);
		Transaction trans = new Transaction();
		trans.setAccNumber(accNum);
		trans.setCustomerId(getCustomerId(trans.getAccNumber()));
		trans.setDescription("Deposit");
		trans.setTransactionId(System.currentTimeMillis());
		trans.setAmount(amount);
		deposit(trans);
	}

	private static void deposit(Transaction trans) throws BankingException {
		long bal = getBalance(trans.getAccNumber());
		long amount = trans.getAmount();
		trans.setType("Credit");
		trans.setOpeningBal(bal);
		trans.setClosingBal(bal + amount);
		doTransaction(trans);
	}
	
	private static void doTransaction(Transaction trans) throws BankingException {
		try {
			tranAgent.doTransaction(trans);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in deposit", exception);
			throw new BankingException("cannot complete deposit", exception);
		}
	}

	static void transferMoney(Transaction trans, boolean withinBank) throws BankingException {
		try {
			Validator.checkNull(trans);
		} catch (InvalidInputException exception) {
			throw new BankingException("Transaction is null");
		}
		long senderAccNum = trans.getAccNumber();
		long tId = System.currentTimeMillis();
		trans.setTransactionId(tId);
		Transaction recepient = null;
		if (withinBank) {
			long accNum = trans.getTransAccNum();
			AuthServices.validateAccount(accNum);
			recepient = new Transaction();
			recepient.setCustomerId(getCustomerId(accNum));
			recepient.setAccNumber(accNum);
			recepient.setTransAccNum(senderAccNum);
			recepient.setDescription(trans.getDescription());
			recepient.setAmount(trans.getAmount());
			recepient.setTransactionId(tId);
		}
		withdraw(trans);
		if (recepient != null) {
			deposit(recepient);
		}
	}

	private static void validateBal(long balance, long amount) throws BankingException {
		if (balance < amount) {
			throw new BankingException("Insufficient Funds");
		}
	}

	static boolean changePassword(long userId, String oldPassword, String newPassword) throws BankingException {
		boolean isValid = AuthServices.authUser(userId, oldPassword);
		try {
			if(isValid) {
				if (oldPassword.equals(newPassword)) {
					throw new BankingException("New password cannot be your previous password");
				}
				Validator.validatePassword(newPassword);
				userAgent.changePassword(userId, AuthServices.hashPassword(newPassword));
				return true;
			}
			throw new BankingException("Invalid Password");
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in changing password", exception);
			throw new BankingException("Couldn't change password");
		} catch (InvalidInputException exception) {
			logger.log(Level.WARNING, "Invalid Password", exception);
			throw new BankingException("Password must contain minimum of 8 characters including," + "\n*One Uppercase"
					+ "\n*One special character" + "\n*One number");
		}
	}
	
	static JSONObject getAccountDetails(long accNum) throws BankingException {
		AuthServices.validateAccountPresence(accNum);
		Account acc = accCache.get(accNum);
		return JSONAdapter.objToJSONObject(acc);
	}

	static JSONObject getAccountStatement(long accNum, int page) throws BankingException {
		try {
			AuthServices.validateAccount(accNum);
			int limit = 10;
			int offset = limit * (page - 1);
			return tranAgent.getAccountStatement(accNum, limit, offset);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Couldn't fetch account statement", exception);
			throw new BankingException("Couldn't fetch account statement", exception);
		}
	}

	public static JSONObject getTransStatement(long transId) throws BankingException {
		try {
			return tranAgent.getTransactionStatement(transId);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Couldn't fetch transaction statement", exception);
			throw new BankingException("Couldn't fetch transaction statement", exception);
		}
	}

	static JSONObject getCustomerDetails(long userId) throws BankingException {
		AuthServices.validateCustomer(userId);
		Customer customer = proCache.get(userId);
		return JSONAdapter.objToJSONObject(customer);
	}

	static JSONObject getEmployeeDetails(long userId) throws BankingException {
		try {
			if (empAgent.isEmployeePresent(userId)) {
				Employee employee = empAgent.getEmployee(userId);
				return JSONAdapter.objToJSONObject(employee);
			}
			throw new BankingException("Invalid Employee Id");
		} catch (PersistenceException exception) {
			logger.log(Level.INFO, "Couldn't fetch employee", exception);
			throw new BankingException("Couldn't fetch employee for userId : " + userId, exception);
		}
	}

	static long addCustomer(Customer cus, Account account) throws BankingException {
		try {
			Validator.checkNull(cus);
			Validator.checkNull(account);
		} catch (InvalidInputException exception) {
			throw new BankingException("Customer/Account is null");
		}
		try {
			account.setPrimary(true);
			String password = AuthServices.hashPassword(cus.getDOB());
			cus.setLevel(UserLevel.Customer);
			return trAgnet.addCustomer(cus, account, password);
		} catch (PersistenceException exception) {
			exception.printStackTrace();
			logger.log(Level.SEVERE, "Error in adding Customer", exception);
			throw new BankingException("Couldn't add Customer", exception);
		}
	}
	
	public static JSONObject getAccounts(long customerId) throws BankingException {
		try {
			AuthServices.validateCustomer(customerId);
			Map<Long, Account> map = accAgent.getAccounts(customerId);
			return JSONAdapter.mapToJSON(map);
		} catch (PersistenceException exception) {
			logger.log(Level.INFO, "Couldn't fetch accounts", exception);
			throw new BankingException("Couldn't fetch accounts for customer ID : " + customerId, exception);
		}
	}

	static long addAccount(Account account) throws BankingException {
		try {
			Validator.checkNull(account);
		} catch (InvalidInputException exception) {
			throw new BankingException("Account is null");
		}
		try {
			return trAgnet.addAccount(account);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in adding account", exception);
			exception.printStackTrace();
			throw new BankingException("Couldn't add account");
		}

	}

	static void closeAcc(long accNum) throws BankingException {
		try {
			accAgent.closeAccount(accNum);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in closing account", exception);
			throw new BankingException("Account cannot be closed", exception);
		}
	}
}
