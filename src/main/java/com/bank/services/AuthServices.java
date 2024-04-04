package com.bank.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindrot.jbcrypt.BCrypt;

import com.bank.cache.AccountCache;
import com.bank.enums.Status;
import com.bank.exceptions.BankingException;
import com.bank.exceptions.PersistenceException;
import com.bank.interfaces.AccountsAgent;
import com.bank.interfaces.CustomerAgent;
import com.bank.interfaces.EmployeeAgent;
import com.bank.interfaces.UserAgent;
import com.bank.persistence.util.PersistenceObj;
import com.bank.util.LogHandler;

public class AuthServices {

	private static Logger logger = LogHandler.getLogger(AuthServices.class.getName(), "AuthServicesLogs.txt");

	private static UserAgent userAgent = PersistenceObj.getUserAgent();
	private static EmployeeAgent empAgent = PersistenceObj.getEmployeeAgent();
	private static CustomerAgent cusAgent = PersistenceObj.getCustmomerAgent();
	private static AccountsAgent accAgent = PersistenceObj.getAccountsAgent();
	static AccountCache accCache = AccountCache.getInstance();

	public boolean login(long uId, String password) throws BankingException {
		try {
			validateUser(uId);
			int attempt = userAgent.getAttempt(uId);
			if (attempt < 3) {
				if (authUser(uId, password)) {
					setAttempt(uId, 1);
					return true;
				}
				setAttempt(uId, attempt + 1);
				logger.warning("Incorrect login attempt on user id : " + uId);
				throw new BankingException("Invalid LoginID or Password");
			}
			setStatus(uId, Status.BLOCKED);
			logger.warning("User id : " + uId + "blocked exceeding login attempt limit");
			throw new BankingException("User blocked for exceeding login attempts : Contact BOB authority");
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Login error", exception);
			throw new BankingException("Login Error : Try Again Later");
		}
	}

	static boolean authUser(long userId, String password) throws BankingException {
		try {
			boolean isValid = BCrypt.checkpw(password, userAgent.getPassword(userId));
			if (isValid) {
				return true;
			}
			return false;
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in authenticating user", exception);
			throw new BankingException("Authentication Error", exception);
		}
	}

	static boolean authPin(long userId, String pin) throws BankingException {
		try {
			int attempts = cusAgent.getTpinAttempts(userId);
			if (attempts < 3) {
				if (BCrypt.checkpw(pin, getPin(userId))) {
					return true;
				}
				setTpinAttempt(userId, attempts + 1);
				logger.warning("Incorrect tpin attempt on user id : " + userId);
				throw new BankingException("Incorrect pin : " + (3 - attempts) + " attempt(s) left");
			}
			setStatus(userId, Status.BLOCKED);
			logger.warning("User id : " + userId + "blocked exceeding tpin attempt limit");
			throw new BankingException("User Blocked : Contact nearby BOB authority to regain access");
		} catch (PersistenceException | BankingException exception) {
			logger.log(Level.SEVERE, "Error in authenticating user", exception);
			throw new BankingException("Authentication Error", exception);
		}
	}

	private static String getPin(long customerId) throws BankingException {
		try {
			return cusAgent.getPin(customerId);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Couldn't fetch pin");
			throw new BankingException("Couldn't fetch pin");
		}
	}

	static String hashPassword(String plainPassword) {
		String salt = BCrypt.gensalt();
		return BCrypt.hashpw(plainPassword, salt);
	}

	private static void setAttempt(long userId, int attempt) throws BankingException {
		try {
			userAgent.setAttempt(userId, attempt);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in updating failed attempts", exception);
			throw new BankingException("Login Attempt update error", exception);
		}
	}

	private static void setTpinAttempt(long userId, int attempt) throws BankingException {
		try {
			cusAgent.setTpinAttempts(userId, attempt);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in updating failed tpin attempts", exception);
			throw new BankingException("Tpin Attempt update error", exception);
		}
	}

	public static void setStatus(long uId, Status status) throws BankingException {
		try {
			validateUserPresence(uId);
			int presentState = userAgent.getStatus(uId);
			int state = status.getState();
			if (presentState == state) {
				throw new BankingException("User already " + state);
			}
			userAgent.setStatus(uId, status);
			if (presentState == 3 && state == 1) {
				setAttempt(uId, 1);
				setTpinAttempt(uId, 1);
			}
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in setting user status", exception);
			exception.printStackTrace();
			throw new BankingException("Status Update Error", exception);
		}
	}

	static void setAccountStatus(long accNum, Status status) throws BankingException {
		try {
			validateAccountPresence(accNum);
			int state = status.getState();
			if (accAgent.getAccStatus(accNum) == state) {
				throw new BankingException("User already " + state);
			}
			accAgent.setAccStatus(accNum, status);
			accCache.remove(accNum);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in setting account status", exception);
			throw new BankingException("Status Update Error", exception);
		}
	}

	static void validateUserPresence(long userId) throws BankingException {
		try {
			boolean isValidUser = userAgent.isUserPresent(userId);
			if (!isValidUser) {
				throw new BankingException("Invalid user Id");
			}
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in validating user", exception);
			exception.printStackTrace();
			throw new BankingException("Couldn't validate user");
		}
	}

	static void validateUser(long userId) throws BankingException {
		try {
			validateUserPresence(userId);
			int status = userAgent.getStatus(userId);
			if (!(status == 1)) {
				throw new BankingException("User Blocked/Inactive");
			}
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Couldn't validate user", exception);
			throw new BankingException("Couldn't validate user");
		}
	}

	static void validateAccountPresence(long accNum) throws BankingException {
		try {
			boolean isValid = accAgent.isAccountPresent(accNum);
			if (!isValid) {
				throw new BankingException("Invalid account number");
			}
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in validating account", exception);
			throw new BankingException("Couldn't validate account");
		}
	}

	static void validateAccount(long accNum) throws BankingException {
		try {
			validateAccountPresence(accNum);
			int status = accAgent.getAccStatus(accNum);
			if (!(status == 1)) {
				throw new BankingException("Account Blocked/Inactive");
			}
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Couldn't validate account", exception);
			throw new BankingException("Couldn't validate account", exception);
		}
	}

	public Object getServiceObj(long userId) throws BankingException {
		try {
			int type = userAgent.getUserLevel(userId);
			if (type == 1) {
				CustomerServices cus = new CustomerServices();
				cus.setUserId(userId);
				String pin = getPin(userId);
				if (!(pin == null)) {
					cus.setPinSet(true);
				}
				long accNumber = accAgent.getPrimaryAcc(userId);
				cus.setCurrentAccount(accCache.get(accNumber));
				return cus;
			} else if (type == 2) {
				EmployeeServices emp = new EmployeeServices();
				emp.setUserId(userId);
				emp.setBranchId(empAgent.getBranchId(userId));
				return emp;
			} else if (type == 3) {
				AdminServices admin = new AdminServices();
				admin.setUserId(userId);
				return admin;
			} else {
				logger.log(Level.SEVERE, "Couldn't determine authorization level");
				throw new BankingException("Couldn't authorize user");
			}
		}
		 catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in determining user type", exception);
			throw new BankingException("Cannot fetch available services at the moment... Try again");
		}
	}

	static boolean validateCustomer(long cusId) throws BankingException {
		try {
			boolean isValid = cusAgent.isCustomerPresent(cusId);
			if (!isValid) {
				throw new BankingException("Invalid Customer Id");
			}
			return true;
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Couldn't validate customer", exception);
			throw new BankingException("Cannot validate customer");
		}
	}
}