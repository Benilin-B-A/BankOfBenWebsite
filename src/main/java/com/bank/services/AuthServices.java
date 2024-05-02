package com.bank.services;

import java.nio.channels.SelectableChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindrot.jbcrypt.BCrypt;

import com.bank.cache.AccountCache;
import com.bank.cache.ProfileCache;
import com.bank.enums.Status;
import com.bank.exceptions.BankingException;
import com.bank.exceptions.InactiveAccountException;
import com.bank.exceptions.InactiveUserException;
import com.bank.exceptions.InvalidAccountException;
import com.bank.exceptions.InvalidUserException;
import com.bank.exceptions.PersistenceException;
import com.bank.interfaces.AccountsAgent;
import com.bank.interfaces.CustomerAgent;
import com.bank.interfaces.EmployeeAgent;
import com.bank.interfaces.UserAgent;
import com.bank.persistence.util.PersistenceObj;
import com.bank.pojo.Event;
import com.bank.util.LogHandler;
import com.bank.util.TimeUtil;

public class AuthServices {

	private static Logger logger = LogHandler.getLogger(AuthServices.class.getName(), "AuthenticationLog.txt");

	private static UserAgent userAgent = PersistenceObj.getUserAgent();
	private static EmployeeAgent empAgent = PersistenceObj.getEmployeeAgent();
	private static CustomerAgent cusAgent = PersistenceObj.getCustmomerAgent();
	private static AccountsAgent accAgent = PersistenceObj.getAccountsAgent();
	static AccountCache accCache = AccountCache.getInstance();
	static ProfileCache proCache = ProfileCache.getInstance();

	/**
	 * 
	 * @param userId
	 * @param password
	 * @return Returns true if the login is successful
	 * @throws BankingException
	 */
	public boolean login(long userId, String password) throws BankingException {
		try {
			isUserPresent(userId);
			int attempt = userAgent.getAttempt(userId);
			if (isValidPassword(userId, password)) {
				isValidUser(userId);
				if (attempt < 3) {
					if (attempt > 1) {
						setLoginAttempts(userId, 1);
					}
					log("Login", userId, "Successful login");
					return true;
				}
				setUserStatus(userId, Status.BLOCKED);
				logger.warning("User id : " + userId + "blocked exceeding login attempt limit");
				throw new BankingException("User blocked for exceeding login attempts : Contact BOB authority");
			}
			log("Login", userId, "Failed login attempt : " + (3 - attempt) + " attempts left");
			setLoginAttempts(userId, attempt + 1);
			logger.warning("Incorrect login attempt on user id : " + userId);
			throw new BankingException("Invalid Login ID or Password");

		} catch (InvalidUserException exception) {
			throw new BankingException("Invalid ID or Password");
		} catch (InactiveUserException exception) {
			throw new BankingException("User Blocked/Inactive : Contact BOB authorities to regain access");
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Login error", exception);
			throw new BankingException("Something went wrong... Try Again Later");
		}
	}

	/**
	 * @param userId
	 * @param password
	 * @return Returns true if the entered password matches the user password
	 * @throws BankingException
	 */
	static boolean isValidPassword(long userId, String password) throws BankingException, PersistenceException {
		boolean valid = BCrypt.checkpw(password, userAgent.getPassword(userId));
		if (valid) {
			return true;
		}
		return false;
	}

	/**
	 * @param userId
	 * @param pin
	 * @return Returns true if the entered TPin matches the customer's TPin
	 * @throws BankingException
	 */
	static boolean isValidPin(long userId, String pin) throws BankingException {
		try {
			int attempts = cusAgent.getTPinAttempts(userId);
			if (attempts < 3) {
				boolean validPIN = BCrypt.checkpw(pin, getTPin(userId));
				if (validPIN) {
					if (attempts > 1) {
						setTPinAttempt(userId, 1);
					}
					return true;
				}
				setTPinAttempt(userId, attempts + 1);
				throw new BankingException("Incorrect T-Pin");
			}
			setUserStatus(userId, Status.BLOCKED);
			throw new BankingException(
					"User blocked for exceeding TPin attempts " + ": Contact nearby BOB authority to regain access");
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in validating TPin", exception);
			throw new BankingException("Something went wrong... Try Again Later");
		}
	}

	/**
	 * @param customerId
	 * @return Returns the TPin of the given customer ID
	 * @throws BankingException
	 */
	private static String getTPin(long customerId) throws PersistenceException {
		return cusAgent.getPin(customerId);
	}

	/**
	 * @param password
	 * @return Returns the hashed string using blowfish algorithm
	 */
	static String hashPassword(String password) {
		String salt = BCrypt.gensalt();
		return BCrypt.hashpw(password, salt);
	}

	/**
	 * @param userId
	 * @param attempt
	 * @throws BankingException
	 * 
	 */
	private static void setLoginAttempts(long userId, int attempt) {
		try {
			userAgent.setAttempt(userId, attempt);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in updating login attempts", exception);
		}
	}

	private static void setTPinAttempt(long userId, int attempt) {
		try {
			cusAgent.setTPinAttempts(userId, attempt);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in updating incorrect TPin attempts", exception);
		}
	}

	public static void setUserStatus(long userId, Status status) throws BankingException {
		try {
			isUserPresent(userId);
			int presentState = userAgent.getStatus(userId);
			int state = status.getState();
			if (presentState == state) {
				throw new BankingException("User already " + state);
			}
			userAgent.setStatus(userId, status);
			if (presentState == 3 && state == 1) {
				setLoginAttempts(userId, 1);
				setTPinAttempt(userId, 1);
			}
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in setting user status", exception);
			exception.printStackTrace();
			throw new BankingException("Status Update Error", exception);
		}
	}

	static void setAccountStatus(long accNum, Status status) throws BankingException {
		try {
			isAccountPresent(accNum);
			int state = status.getState();
			if (accAgent.getAccStatus(accNum) == state) {
				throw new BankingException("User already " + state);
			}
			accAgent.setAccStatus(accNum, status);
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in setting account status", exception);
			throw new BankingException("Status Update Error", exception);
		}
	}

	public static boolean isValidUser(long userId) throws BankingException {
		try {
			int status = userAgent.getStatus(userId);
			if (status == 1) {
				return true;
			}
			throw new InactiveUserException("User state : " + Status.getStatusByState(status));
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in validating user status", exception);
			throw new BankingException("Something went wrong... Try again later");
		}
	}

	static boolean isUserPresent(long userId) throws BankingException {
		try {
			boolean validUser = userAgent.isUserPresent(userId);
			if (validUser) {
				return true;
			}
			throw new InvalidUserException("User doesn't exist");
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in validating user persence", exception);
			throw new BankingException("Something went wrong... Try again later");
		}
	}

	static boolean isAccountPresent(long accountNum) throws BankingException {
		try {
			boolean validAccount = accAgent.isAccountPresent(accountNum);
			if (validAccount) {
				return true;
			}
			throw new InvalidAccountException("Account doesn't exist");
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in validating account presence", exception);
			throw new BankingException("Something went wrong... Try again later");
		}
	}

	public static boolean isValidAccount(long accountNum) throws BankingException {
		try {
			int status = accAgent.getAccStatus(accountNum);
			if (status == 1) {
				return true;
			}
			throw new InactiveAccountException(
					"Account ( " + accountNum + ") state : " + Status.getStatusByState(status));
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in validating account presence", exception);
			throw new BankingException("Something went wrong... Try again later", exception);
		}
	}

	public Object getServiceObject(long userId) throws BankingException {
		try {
			int level = userAgent.getUserLevel(userId);
			if (level == 1) {
				CustomerServices cus = new CustomerServices();
				cus.setUserId(userId);
				String pin = getTPin(userId);
				if (!(pin == null)) {
					cus.setPinSet(true);
				}
				cus.setPrimaryAcc(accAgent.getPrimaryAcc(userId));
//				System.out.println(accAgent.getPrimaryAcc(userId));
				return cus;
			} else if (level == 2) {
				EmployeeServices emp = new EmployeeServices();
				emp.setUserId(userId);
				emp.setBranchId(empAgent.getBranchId(userId));
				return emp;
			} else if (level == 3) {
				AdminServices admin = new AdminServices();
				admin.setUserId(userId);
				return admin;
			} else {
				logger.log(Level.SEVERE, "Couldn't determine user level");
				throw new BankingException("Something went wrong... Try again later");
			}
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in determining user type", exception);
			throw new BankingException("Something went wrong... Try again later");
		}
	}

	public static boolean isCustomer(long customerId) throws BankingException {
		try {
			boolean isValid = cusAgent.isCustomerPresent(customerId);
			if (!isValid) {
				return false;
			}
			return true;
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error while validating customer", exception);
			throw new BankingException("Something went wrong... Try again later");
		}
	}
	
	public static boolean isAlreadyUser(long aadharNumber) throws BankingException {
		try {
			boolean isUser = userAgent.isAlreadyUser(aadharNumber);
			if(isUser) {
				throw new BankingException("User with aadhar number " + aadharNumber + " already exists");
			}
			return false;
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in determining user type", exception);
			throw new BankingException("Something went wrong... Try again later");
		}
	}

	private void log(String eventName, Long userId, String description) {
		Event event = new Event();
		event.setUserId(userId);
		event.setEvent(eventName);
		event.setTargetUserId(userId);
		event.setDescription(description);
		event.setTime(TimeUtil.getTime());
		EventLogger.log(event);
	}

	public static boolean isAlreadyCustomer(String panNum) throws BankingException {
		try {
			boolean isUser = cusAgent.isAlreadyCustomer(panNum);
			if(isUser) {
				throw new BankingException("Customer with PAN number " + panNum + " already exists");
			}
			return false;
		} catch (PersistenceException exception) {
			logger.log(Level.SEVERE, "Error in determining user type", exception);
			throw new BankingException("Something went wrong... Try again later");
		}
	}
}