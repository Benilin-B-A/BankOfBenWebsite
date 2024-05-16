package com.bank.services;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.bank.enums.Status;
import com.bank.enums.UserLevel;
import com.bank.exceptions.BankingException;
import com.bank.exceptions.InvalidInputException;
import com.bank.exceptions.PersistenceException;
import com.bank.interfaces.BranchAgent;
import com.bank.interfaces.TransacAgent;
import com.bank.persistence.util.PersistenceObj;
import com.bank.pojo.Account;
import com.bank.pojo.Branch;
import com.bank.pojo.Customer;
import com.bank.pojo.Employee;
import com.bank.pojo.Event;
import com.bank.pojo.Transaction;
import com.bank.util.LogHandler;
import com.bank.util.TimeUtil;
import com.bank.util.Validator;



public class AdminServices {

	protected long userId;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	private static Logger logger = LogHandler.getLogger(AuthServices.class.getName(), "AdminServicesLogs.txt");

//	private static AccountsAgent accAgent = PersistenceObj.getAccountsAgent();
	private static BranchAgent branchAgent = PersistenceObj.getBranchAgent();
	private static TransacAgent trAgnet = PersistenceObj.getTransacAgent();

	public long getBalance(long accNum) throws BankingException {
		AuthServices.isAccountPresent(accNum);
		return UserServices.getBalance(accNum);
	}

	public void withdraw(long accNum, long amount) throws BankingException {
		UserServices.withdraw(accNum, amount, this.userId);
		log("Withdrawl", UserServices.getCustomerId(accNum), "Money withdrawn from bank in account number : " + accNum);
	}

	public void deposit(long accNum, long amount) throws BankingException {
		UserServices.deposit(accNum, amount, this.userId);
		log("Deposit", UserServices.getCustomerId(accNum), "Money deposited in bank account number : " + accNum);
	}

	public void transfer(Transaction trans, long accNum, boolean withinBank) throws BankingException {
		AuthServices.isValidAccount(accNum);
		trans.setAccountNumber(accNum);
		trans.setCreatedBy(this.userId);
		UserServices.transferMoney(trans, withinBank);
		String str = null;
		if(withinBank) {
			str = "Money transfered within bank to account number :";
		}else {
			str = "Money transfered outside bank to account number :";
		}
		log("Money transfer", UserServices.getCustomerId(accNum),str + trans.getTransactionAccountNumber());
	}

	public void changePassword(String oldPass, String newPass) throws BankingException {
		UserServices.changePassword(userId, oldPass, newPass);
		log("Password changed", this.userId,"Password changed");
	}

	public JSONObject getAccountStatement(long accNum) throws BankingException {
		return getAccountStatement(accNum, 1);
	}
	
	public JSONObject getAccountStatement(long accNum, int page) throws BankingException {
		return UserServices.getAccountStatement(accNum, page);
	}

	public JSONObject getTransStatement(long transId) throws BankingException {
		return UserServices.getTransStatement(transId);
	}

	public JSONObject getAccount(long accNum) throws BankingException{
		AuthServices.isAccountPresent(accNum);
		return UserServices.getAccountDetails(accNum);
	}

	public JSONObject getAccounts(long customerId) throws BankingException {
		return UserServices.getAccounts(customerId);
	}
	
	public long addEmployee(Employee emp) throws BankingException, InvalidInputException {
		try {
			Validator.checkNull(emp);
			String password = AuthServices.hashPassword(emp.getDOB());
			emp.setLevel(UserLevel.Employee);
			isBranchPresent(emp.getBranchID());
			emp.setModifiedBy(this.userId);
			emp.setCreatedOn(TimeUtil.getTime());
			AuthServices.isAlreadyUser(emp.getAadharNum());
			long empId = trAgnet.addEmployee(emp, password);
			log("New Employee added", empId, "New Employee ID : " + empId);
			return empId;
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in adding Employee", exception);
			throw new BankingException("Something went wrong... Try again later");
		}
	}
	
	private boolean isBranchPresent(long branchID) throws BankingException {
		try {
			if(branchAgent.isBranchIdPresent(branchID)) {
				return true;
			}
			throw new BankingException("Invalid BranchId");
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error validating BranchId", exception);
			throw new BankingException("Couldn't validate branch ID");
		}
	}

	public long addAccount(Account account, long branchId) throws BankingException {
		isBranchPresent(branchId);
		account.setBranchId(branchId);
		account.setModifiedBy(this.userId);
		long accNumber = UserServices.addAccount(account);
		log("New Account added", UserServices.getCustomerId(accNumber), "New Account ID : " + accNumber);
		return accNumber;
	}

	public long addCustomer(Customer cus, Account acc, long branchId) throws BankingException {
		isBranchPresent(branchId);
		acc.setBranchId(branchId);
		cus.setModifiedBy(this.userId);
		acc.setModifiedBy(this.userId);
		long customerId = UserServices.addCustomer(cus, acc);
		log("New Customer added", customerId, "New Customer ID : " + customerId);
		return customerId;
		
	}
	
	public void addBranch(Branch branch) throws BankingException, InvalidInputException {
		Validator.checkNull(branch);
		try {
			String iFSC = branch.getIFSC();
			Validator.validateAlphaNum(iFSC);
			if(branchAgent.isIFSCPresent(iFSC)) {
				throw new BankingException("IFSC code already exists");
			}		
			branch.setCreatedOn(TimeUtil.getTime());
			branch.setModifiedBy(this.userId);
			long branchId = branchAgent.addBranch(branch);
			log("New Branch added", branchId, "New Branch ID : " + branchId);
		} catch(PersistenceException exception){
			logger.log(Level.SEVERE,"Error in adding Branch",exception);
			throw new BankingException("Couldn't add branch");
		} catch (InvalidInputException excep) {
			logger.log(Level.WARNING,"Invalid IFSC",excep);
			throw new BankingException("Enter valid IFSC code");
		}
	}

	public void setStatus(long customerId, Status state) throws BankingException {
		AuthServices.setUserStatus( customerId, state);
		log("Customer status update", customerId, "Customer status set to " + state);
	}

	public void setAccountStatus(long accNum, Status status) throws BankingException {
		AuthServices.isAccountPresent(accNum);
		AuthServices.setAccountStatus(accNum, status);
		log("Account status update", UserServices.getCustomerId(accNum), "Account status set to " + status);
	}
	
	public List<Branch> getBranches() throws BankingException{
		try {
			return branchAgent.getBranches();
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE,"Error in fetching branch details", exception);
			throw new BankingException("Couldn't fetch branch details");
		}
	}

	public void closeAcc(long accNum) throws BankingException {
		AuthServices.isAccountPresent(accNum);
		UserServices.closeAcc(accNum);
		log("Account closed", UserServices.getCustomerId(accNum), "Account number " + accNum);
	}

	public JSONObject getCustomerDetails(long cusId) throws BankingException {
		return UserServices.getCustomerDetails(cusId);
	}

	public JSONObject getEmployeeDetails() throws BankingException{
		return getEmployeeDetails(userId);
	}
	
	public JSONObject getEmployeeDetails(long empId) throws BankingException{
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
