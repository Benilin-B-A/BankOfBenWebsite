package com.bank.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.bank.adapter.JSONAdapter;
import com.bank.cache.AccountCache;
import com.bank.cache.CustomerAccountsCache;
import com.bank.exceptions.BankingException;
import com.bank.exceptions.InvalidInputException;
import com.bank.exceptions.PersistenceException;
import com.bank.interfaces.AccountsAgent;
import com.bank.interfaces.CustomerAgent;
import com.bank.persistence.util.PersistenceObj;
import com.bank.pojo.Event;
import com.bank.pojo.Transaction;
import com.bank.util.LogHandler;
import com.bank.util.TimeUtil;
import com.bank.util.Validator;

public class CustomerServices {

	private String name;
	private long userId;
	public long getUserId() {
		return userId;
	}

	private boolean isPinSet;
	
	private long primaryAcc;
	
//	private Account currentAccount;
	
	public long getPrimaryAcc() {
		return primaryAcc;
	}

	public void setPrimaryAcc(long primaryAcc) {
		this.primaryAcc = primaryAcc;
	}

	public long getAccNum() {
		return this.primaryAcc;
	}

	static AccountCache accCache = AccountCache.getInstance();
	static CustomerAccountsCache accsCache = CustomerAccountsCache.getInstance();

	public void setPinSet(boolean isPinSet) {
		this.isPinSet = isPinSet;
	}

	public boolean isPinSet() {
		return isPinSet;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	private static AccountsAgent accAgent = PersistenceObj.getAccountsAgent();
	private static CustomerAgent cusAgent = PersistenceObj.getCustmomerAgent();

	private static Logger logger = LogHandler.getLogger(CustomerServices.class.getName(), "CustomerServices.txt");

	public boolean isPrimary(long accNum) {
		if(accNum == primaryAcc) {
			return true;
		}
		return false;
	}
	
//	public void withdraw(long amount, String pin) throws BankingException, InvalidInputException {
//		AuthServices.isValidPin(userId, pin);
//		UserServices.withdraw(primaryAcc, amount, this.userId);
//		log("Withdraw", this.userId, "Money withdrawn from bank");
//	}

//	public void deposit(long amount) throws BankingException {
//		UserServices.deposit(primaryAcc, amount, this.userId);
//		log("Deposit", this.userId, "Money deposited in bank");
//	}

	public void transfer(Transaction transaction, String pin, boolean withinBank)
			throws BankingException{
		AuthServices.isValidPin(userId, pin);
		transaction.setCreatedBy(this.userId);
		UserServices.transferMoney(transaction, withinBank);
		String str = null;
		if(withinBank) {
			str = "Money transfered within bank to account number :";
		}else {
			str = "Money transfered  outside bank to account number :";
		}
		log("Money transfer", this.userId,str + "Amount transferd from account number : " + transaction.getAccNumber());
	}

	public void changePassword(String oldPass, String newPass) throws BankingException {
		UserServices.changePassword(userId, oldPass, newPass);
		log("Password changed", this.userId,
				"Password changed");
	}

	public void changePin(String oldPin, String newPin)
			throws BankingException, InvalidInputException {
		AuthServices.isValidPin(userId, oldPin);
		setPin(newPin);
		log("T-PIN changed", this.userId,
				"T-PIN changed");
	}

	public void setPin(String newPin) throws BankingException, InvalidInputException {
		try {
			Validator.validatePin(newPin);
			cusAgent.setPin(AuthServices.hashPassword(newPin), userId);
			log("T-PIN set", this.userId,
					"T-PIN set");
			isPinSet = true;
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Couldn't set pin", exception);
			throw new BankingException("Couldn't set pin", exception);
		}
	}

	public JSONObject getAccountStatement(long accountNum) throws BankingException {
		return getAccountStatement(accountNum, 1);
	}

	public JSONObject getAccountStatement(long accountNum, int page) throws BankingException {
		return UserServices.getAccountStatement(accountNum, page);
	}

	public void switchAccount(long accoNum) throws BankingException {
		validateSwitch(accoNum);
		try {
			accAgent.switchPrimary(userId, primaryAcc, accoNum);
			primaryAcc = accoNum;
			log("Primary account switched", this.userId,
					"Primary account switched");
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in switching account", exception);
			throw new BankingException("Couldn't switch account");
		}
	}

	private boolean validateSwitch(long accNum) throws BankingException {
		if (!(accNum == primaryAcc)) {
			if (UserServices.getCustomerId(accNum) == userId) {
				return true;
			}
			throw new BankingException("No such account");
		}
		throw new BankingException("Account number " + accNum + " is already primary");
	}

	public JSONObject getAccount() throws BankingException {
		return UserServices.getAccountDetails(primaryAcc);
	}

	public JSONObject getCustomerDetails() throws BankingException {
		return UserServices.getCustomerDetails(userId);
	}

	public JSONObject getAccounts() throws BankingException {
		JSONObject accs = UserServices.getAccounts(userId);
		accs.remove(String.valueOf(primaryAcc));
		return accs;
	}

	public List<Long> getAllAcc() throws BankingException {
		try {
			return accAgent.getAccountList(this.userId);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in fetching accounts", exception);
			throw new BankingException("Something went wrong...Try again");
		}
		//		return accsCache.get(userId);
	}
	
	public List<Long> getActiveAccounts() throws BankingException{
		JSONObject accs = UserServices.getAccounts(userId);
		List<Long> activeAccList = new ArrayList<>();
		Set<String> keySet = accs.keySet();
		for(String acc : keySet) {
			int status = accs.getJSONObject(acc).getJSONObject("status").getInt("state");
			if(status == 1) {
				activeAccList.add(accs.getJSONObject(acc).getLong("accNum"));
			}
		}
		return activeAccList;
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


