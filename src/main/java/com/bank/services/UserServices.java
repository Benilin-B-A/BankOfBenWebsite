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
import com.bank.interfaces.CustomerAgent;
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
import com.bank.util.TimeUtil;
import com.bank.util.Validator;

public abstract class UserServices {

	private static AccountsAgent accAgent = PersistenceObj.getAccountsAgent();
	private static TransactionAgent tranAgent = PersistenceObj.getTransactionAgent();
	private static UserAgent userAgent = PersistenceObj.getUserAgent();
	private static CustomerAgent cusAgent = PersistenceObj.getCustmomerAgent();
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

	static void withdraw(long accNum, long amount, long empID) throws BankingException {
		AuthServices.isValidAccount(accNum);
		Transaction trans = new Transaction();
		trans.setAccNumber(accNum);
		trans.setCreatedBy(empID);
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

	static void deposit(long accNum, long amount, long empId) throws BankingException {
		AuthServices.isValidAccount(accNum);
		Transaction trans = new Transaction();
		trans.setAccNumber(accNum);
		trans.setCreatedBy(empId);
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
			AuthServices.isValidAccount(trans.getAccountNumber());
			trans.setCreatedOn(TimeUtil.getTime());
			tranAgent.doTransaction(trans);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in deposit", exception);
			throw new BankingException("Cannot complete deposit", exception);
		}
	}

	static void transferMoney(Transaction trans, boolean withinBank) throws BankingException {
		try {
			Validator.checkNull(trans);
		} catch (InvalidInputException exception) {
			throw new BankingException("Transaction is null");
		}
		long senderAccNum = trans.getAccNumber();
		long tId = TimeUtil.getTime();
		trans.setTransactionId(tId);
		Transaction recepient = null;
		if (withinBank) {
			long accNum = trans.getTransAccNum();
			if (accNum == senderAccNum) {
				throw new BankingException("Cannot transfer money to the same account");
			}
			AuthServices.isValidAccount(accNum);
			recepient = new Transaction();
			recepient.setCustomerId(getCustomerId(accNum));
			recepient.setAccNumber(accNum);
			recepient.setTransAccNum(senderAccNum);
			recepient.setDescription(trans.getDescription());
			recepient.setAmount(trans.getAmount());
			recepient.setTransactionId(tId);
			recepient.setCreatedBy(trans.getCreatedBy());
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
		try {
			boolean isValid = AuthServices.isValidPassword(userId, oldPassword);
			if (isValid) {
				Long lastChange = userAgent.getLastPasswordChange(userId);
				if (lastChange != null) {
					long timeBetween = TimeUtil.getTime() - lastChange;
					if (timeBetween < 86400000) {
						throw new BankingException(
								"Password changes are limited to once every 24 hours... Try again later");
					}
				}
				if (oldPassword.equals(newPassword)) {
					throw new BankingException("New password and existing password cannot be the same");
				}
				Validator.validatePassword(newPassword);
				userAgent.changePassword(userId, AuthServices.hashPassword(newPassword));
				userAgent.updatePasswordChange(userId, TimeUtil.getTime());
				return true;
			}
			throw new BankingException("Incorrect password entered");
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in changing password", exception);
			throw new BankingException("Something went wrong... Try again later");
		} catch (InvalidInputException exception) {
			throw new BankingException("Invalid password format... Try again");
		}
	}

	static JSONObject getAccountDetails(long accNum) throws BankingException {
		AuthServices.isAccountPresent(accNum);
//		Account acc = accCache.get(accNum);
		Account acc;
		try {
			acc = accAgent.getAccount(accNum);
		} catch (PersistenceException exception) {
			throw new BankingException("Something went wrong... Try again later");
		}
		return JSONAdapter.objToJSONObject(acc);
	}

	static JSONObject getAccountStatement(long accNum, int page) throws BankingException {
		try {
			if (AuthServices.isAccountPresent(accNum)) {
				AuthServices.isValidAccount(accNum);
			}
			int limit = 10;
			int offset = limit * (page - 1);
			return tranAgent.getAccountStatement(accNum, limit, offset);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Couldn't fetch account statement", exception);
			throw new BankingException("Something went wrong... try again later", exception);
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
		try {
			if (AuthServices.isCustomer(userId)) {
				Customer customer = cusAgent.getCustomer(userId);
				return JSONAdapter.objToJSONObject(customer);
			} else {
				throw new BankingException("No matches found for given Customer ID");
			}
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Couldn't fetch customer details", exception);
			throw new BankingException("Something went wrong... Try again later");
		}

	}

	static JSONObject getEmployeeDetails(long userId) throws BankingException {
		try {
			if (empAgent.isEmployeePresent(userId)) {
				Employee employee = empAgent.getEmployee(userId);
				return JSONAdapter.objToJSONObject(employee);
			}
			throw new BankingException("No matches found for given Employee ID");
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
		AuthServices.isAlreadyUser(cus.getAadharNum());
		AuthServices.isAlreadyCustomer(cus.getPanNum());
		try {
			cus.setCreatedOn(TimeUtil.getTime());
			account.setCreatedOn(TimeUtil.getTime());
			account.setPrimary(true);
			String password = AuthServices.hashPassword(cus.getDOB());
			cus.setLevel(UserLevel.Customer);
			return trAgnet.addCustomer(cus, account, password);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in adding Customer", exception);
			throw new BankingException("Couldn't add Customer", exception);
		}
	}

	public static JSONObject getAccounts(long customerId) throws BankingException {
		try {
			if (AuthServices.isCustomer(customerId)) {
				Map<Long, Account> map = accAgent.getAccounts(customerId);
				return JSONAdapter.mapToJSON(map);
			}
			throw new BankingException("Invalid Customer ID");
		} catch (PersistenceException exception) {
			logger.log(Level.INFO, "Couldn't fetch accounts", exception);
			throw new BankingException("Couldn't fetch accounts for customer ID : " + customerId, exception);
		}
	}

	static long addAccount(Account account) throws BankingException {
		try {
			Validator.checkNull(account);
			account.setCreatedOn(TimeUtil.getTime());
		} catch (InvalidInputException exception) {
			throw new BankingException("Account is null");
		}
		try {
			return trAgnet.addAccount(account);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in adding account", exception);
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
