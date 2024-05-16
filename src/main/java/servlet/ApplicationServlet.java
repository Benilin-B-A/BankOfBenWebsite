package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.bank.enums.AccountType;
import com.bank.enums.Status;
import com.bank.enums.UserLevel;
import com.bank.exceptions.BankingException;
import com.bank.exceptions.InvalidInputException;
import com.bank.pojo.Account;
import com.bank.pojo.Customer;
import com.bank.pojo.Employee;
import com.bank.pojo.Transaction;
import com.bank.services.AdminServices;
import com.bank.services.AuthServices;
import com.bank.services.CustomerServices;
import com.bank.services.EmployeeServices;
import com.bank.util.Validator;

public class ApplicationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static AuthServices auth = new AuthServices();

	public ApplicationServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = request.getPathInfo();
//		System.out.println(path);
//		System.out.println(request.getRequestURL());

		switch (path) {

		// home to login
		case "/login":
//			System.out.println(request.getRemoteAddr());
			request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
			break;

		// logout
		case "/logout":
			request.getSession().invalidate();
			request.setAttribute("successMessage", "You've logged out !");
			request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
			break;

		// nav - customer personal accounts view
		case "/accounts":
			viewAccounts(request);
			request.getRequestDispatcher("/WEB-INF/jsp/customerAccounts.jsp").forward(request, response);
			break;

		// make another account primary for customer
		case "/accounts/makePrimary":
			CustomerServices customerServ = (CustomerServices) getUserObject(request);
			try {
				customerServ.switchAccount(Long.parseLong(request.getParameter("newAcc")));
				viewAccounts(request);
				request.setAttribute("successMessage", "Primary account switched");
			} catch (BankingException exception) {
				viewAccounts(request);
				request.setAttribute("errorMessage", exception.getMessage());
			} finally {
				request.getRequestDispatcher("/WEB-INF/jsp/customerAccounts.jsp").forward(request, response);
			}
			break;

		// nav - profile (for all users)
		case "/profile":
			try {
				viewProfile(request);
			} catch (BankingException exception) {
				exception.printStackTrace();
				request.setAttribute("errorMessage", exception.getMessage());
			} finally {
				request.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(request, response);
			}
			break;

		// change password for all users
		case "/changePassword":
			request.setAttribute("credentialType", "password");
			request.getRequestDispatcher("/WEB-INF/jsp/changeCredential.jsp").forward(request, response);
			break;

		// change T-Pin for customer
		case "/changePin":
			request.setAttribute("credentialType", "pin");
			request.getRequestDispatcher("/WEB-INF/jsp/changeCredential.jsp").forward(request, response);
			break;

		// nav - for all user transactions
		// (Customer - only transfer)
		// (Employee transfer + withdraw & deposit)
		case "/transaction":
			Object serviceObj = getUserObject(request);
			if (serviceObj instanceof CustomerServices) {
				boolean pinSet = ((CustomerServices) serviceObj).isPinSet();
				if (!pinSet) {
					request.getRequestDispatcher("/WEB-INF/jsp/setPin.jsp").forward(request, response);
				}
			}
			checkMessage(request);
			String tab = request.getParameter("transactionType");
			if (tab == null || tab.equals("withinBank")) {
				if (serviceObj instanceof CustomerServices) {
					setAllAccounts(request, ((CustomerServices) serviceObj));
				}
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			} else if (tab.equals("withdraw")) {
				request.setAttribute("type", "withdraw");
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			} else if (tab.equals("deposit")) {
				request.setAttribute("type", "deposit");
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			} else {
				request.setAttribute("type", "outside");
				if (serviceObj instanceof CustomerServices) {
					setAllAccounts(request, ((CustomerServices) serviceObj));
				}
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			}

			break;

		case "/organisation":
			request.setAttribute("tab", "viewEmployee");
			request.getRequestDispatcher("/WEB-INF/jsp/organisation.jsp").forward(request, response);
			break;

		// statements for all users
		case "/statement":
			viewStatement(request, response);
			break;

		case "/customerDetails":
			try {
				setCustomerDetails(request);
			} catch (InvalidInputException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
			}
			request.setAttribute("tab", "viewCustomer");
			request.getRequestDispatcher("/WEB-INF/jsp/customer.jsp").forward(request, response);
			break;

		case "/employeeDetails":
			try {
				setEmployeeDetails(request);
			} catch (InvalidInputException | BankingException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
			} finally {
				request.setAttribute("tab", "viewEmployee");
				request.getRequestDispatcher("/WEB-INF/jsp/organisation.jsp").forward(request, response);
			}
			break;

		case "/addEmployee":
			Object userServiceObject = request.getSession().getAttribute("user");
			request.setAttribute("tab", "addEmployee");
			try {
				request.setAttribute("branches", ((AdminServices) userServiceObject).getBranches());
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", "Something went wrong... try again");
			}
			request.getRequestDispatcher("/WEB-INF/jsp/organisation.jsp").forward(request, response);
			break;

		case "/addCustomer":
			Object userSObject = request.getSession().getAttribute("user");
			request.setAttribute("tab", "addCustomer");
			if (userSObject instanceof AdminServices) {
				try {
					request.setAttribute("branches", ((AdminServices) userSObject).getBranches());
				} catch (BankingException exception) {
					request.setAttribute("errorMessage", "Something went wrong... try again");
				}
			}
			request.getRequestDispatcher("/WEB-INF/jsp/customer.jsp").forward(request, response);
			break;

		case "/updateCustomer":
			try {
				setCustomerDetails(request);
			} catch (InvalidInputException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
			}
			request.setAttribute("tab", "updateCustomer");
			request.getRequestDispatcher("/WEB-INF/jsp/customer.jsp").forward(request, response);
			break;

		case "/accountDetails":
			getAccountDetails(request);
//			EmployeeServices user = (EmployeeServices) request.getSession().getAttribute("user");
//			System.out.println( String.valueOf(((EmployeeServices) user).getBranchId()));
//			System.out.println(request.getAttribute("allAccounts"));
			request.setAttribute("tab", "viewAccount");
			request.getRequestDispatcher("/WEB-INF/jsp/accounts.jsp").forward(request, response);
			break;

		case "/createAccount":
			request.setAttribute("tab", "createAccount");
			Object userObject = request.getSession().getAttribute("user");
			if (userObject instanceof AdminServices) {
				try {
					request.setAttribute("branches", ((AdminServices) userObject).getBranches());
				} catch (BankingException exception) {
					request.setAttribute("errorMessage", "Couldn't create an account at the moment");
				}
			}
			request.getRequestDispatcher("/WEB-INF/jsp/accounts.jsp").forward(request, response);
			break;
		}

	}

	private void checkMessage(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String successMsg = (String) session.getAttribute("successMessage");
		String errorMsg = (String) session.getAttribute("errorMessage");
		String firstRound = (String) session.getAttribute("firstRound");
		if (firstRound != null) {
			if (successMsg != null) {
				session.removeAttribute("successMessage");
			} else {
				session.removeAttribute("errorMessage");
			}
			session.removeAttribute("firstRound");
			return;
		} else if (successMsg != null || errorMsg != null) {
			session.setAttribute("firstRound", "firstRound");
			return;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = request.getPathInfo();

		switch (path) {

		// login authentication and assigning user object
		case "/login":
			login(request, response);
			break;

		// change password (all Users)
		case "/changePassword":
			changePassword(request, response);
			break;

		// change T-PIN for customers
		case "/changePIN":
			String oldPIN = (String) request.getParameter("oldPin");
			String newPIN = (String) request.getParameter("newPin");
			try {
				CustomerServices cusService = (CustomerServices) getUserObject(request);
				cusService.changePin(oldPIN, newPIN);
				request.setAttribute("successMessage", "PIN changed successfully");
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
			} catch (InvalidInputException exception) {
				request.setAttribute("errorMessage", "Invalid PIN format");
			} finally {
				try {
					viewProfile(request);
				} catch (BankingException exception) {
				}
				request.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(request, response);
			}
			break;

		// set T-PIN during first transaction (customer)
		case "/setPin":
			String tPIN = request.getParameter("newPin");
			CustomerServices cServices = (CustomerServices) getUserObject(request);
			try {
				cServices.setPin(tPIN);
				request.setAttribute("successMessage", "T-Pin set successfully");
				setAllAccounts(request, cServices);
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			} catch (BankingException | InvalidInputException exception) {
				request.setAttribute("errorMessage", "Couldn't set PIN at the moment. Try again later...");
				viewAccounts(request);
				request.getRequestDispatcher("/WEB-INF/jsp/customerAccounts.jsp").forward(request, response);
			}
			break;

		case "/accountDetails":
			getAccountDetails(request);
			request.setAttribute("tab", "viewAccount");
			request.getRequestDispatcher("/WEB-INF/jsp/accounts.jsp").forward(request, response);
			break;

		// money transfer
		case "/sendMoney":
			try {
				if (request.getParameter("type").equals("within")) {
					sendMoney(request, true);
				} else {
					sendMoney(request, false);
				}
				request.getSession().setAttribute("successMessage", "Transaction successful");
			} catch (BankingException | InvalidInputException exception) {
				request.getSession().setAttribute("errorMessage", exception.getMessage());
			} finally {
				Object serv = getUserObject(request);
				if (serv instanceof CustomerServices) {
					setAllAccounts(request, ((CustomerServices) serv));
				}
				if (request.getParameter("type").equals("within")) {
					request.getSession().setAttribute("redirect", "yes");
					response.sendRedirect(request.getContextPath() + "/app/transaction?transactionType=withinBank");
				} else {
					request.getSession().setAttribute("redirect", "yes");
					response.sendRedirect(request.getContextPath() + "/app/transaction?transactionType=toOtherBank");
				}
			}
			break;

		case "/setAccStatus":
			Long accNum = Long.parseLong(request.getParameter("iD"));
			Status accState;
			String act = request.getParameter("action");
			if (act.equals("activate")) {
				accState = Status.ACTIVE;
			} else {
				accState = Status.INACTIVE;
			}
			Object object = request.getSession().getAttribute("user");
			try {
				if (object instanceof AdminServices) {
					((AdminServices) object).setAccountStatus(accNum, accState);
				} else {
					((EmployeeServices) object).setAccountStatus(accNum, accState);
				}
			} catch (BankingException exception) {
				// hanlde--------------------------------------------------
			}
			request.getSession().setAttribute("accNum", accNum);
			request.getSession().setAttribute("redirect", "yes");
			response.sendRedirect(request.getContextPath() + "/app/accountDetails");
			break;

		case "/setStatus":
			Long customerId = Long.parseLong(request.getParameter("iD"));
			Status state;
			String action = request.getParameter("action");
			if (action.equals("activate")) {
				state = Status.ACTIVE;
			} else {
				state = Status.INACTIVE;
			}
			Object sObject = request.getSession().getAttribute("user");
			try {
				((AdminServices) sObject).setStatus(customerId, state);
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
				request.getRequestDispatcher("/WEB-INF/jsp/customer.jsp").forward(request, response);
			}
			try {
				if (AuthServices.isCustomer(customerId)) {
					request.setAttribute("customerID", customerId);
					setCustomerDetails(request);
					request.setAttribute("tab", "viewCustomer");
					request.getRequestDispatcher("/WEB-INF/jsp/customer.jsp").forward(request, response);
				} else {
					request.setAttribute("employeeID", customerId);
					setEmployeeDetails(request);
					request.setAttribute("tab", "viewEmployee");
					request.getRequestDispatcher("/WEB-INF/jsp/organisation.jsp").forward(request, response);
				}
			} catch (BankingException | InvalidInputException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
				request.getRequestDispatcher("/WEB-INF/jsp/organisation.jsp").forward(request, response);
			}

			break;

		// add new customer for employee
		case "/addCustomer":
			try {
				addCustomer(request, response);
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
			} catch (InvalidInputException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
			} finally {
				request.setAttribute("tab", "viewCustomer");
				request.getRequestDispatcher("/WEB-INF/jsp/customer.jsp").forward(request, response);
			}
			break;

		case "/createAccount":
			try {
				Object obj = request.getSession().getAttribute("user");
				Account acc = new Account();
				String customerID = request.getParameter("customerID");
				Validator.validateUserID(customerID);
				acc.setUId(Long.parseLong(customerID));
				AccountType type1;
				String accType = request.getParameter("accType");
				Validator.checkNull(accType, "Account type cannot be null");
				if (accType.equals("Current")) {
					type1 = AccountType.CURRENT;
				} else if (accType.equals("Savings")) {
					type1 = AccountType.SAVINGS;
				} else {
					throw new BankingException("Invalid Account type");
				}
				acc.setType(type1);
				long accoNum = 0;

				if (obj instanceof AdminServices) {
					String brID = request.getParameter("branchId");
					Validator.validateBranchID(brID);
					Long branchID = Long.parseLong(brID);
					accoNum = ((AdminServices) obj).addAccount(acc, branchID);
				} else {
					accoNum = ((EmployeeServices) obj).addAccount(acc);
				}
				request.setAttribute("successMessage", "Account created successfully");
				request.getSession().setAttribute("accNum", accoNum);
				request.getSession().setAttribute("redirect", "yes");
				request.setAttribute("tab", "viewAccount");
				response.sendRedirect(request.getContextPath() + "/app/accountDetails");
			} catch (BankingException | InvalidInputException exception) {
				request.setAttribute("tab", "viewAccount");
				request.setAttribute("errorMessage", exception.getMessage());
				request.getRequestDispatcher("/WEB-INF/jsp/accounts.jsp");
			}
			break;

		case "/deposit":
			try {
				Object serviceObj = request.getSession().getAttribute("user");
				String accNumber = request.getParameter("accNumber");
				Validator.validateAccount(accNumber);
				Long accountNumber = Long.parseLong(accNumber);
				String amt = request.getParameter("amount");
				Validator.validateAmount(amt);
				Long amount = Long.parseLong(amt);
				if (serviceObj instanceof AdminServices) {
					((AdminServices) serviceObj).deposit(accountNumber, amount);
				} else {
					((EmployeeServices) serviceObj).deposit(accountNumber, amount);
				}
				request.getSession().setAttribute("successMessage", "Deposit successful");
			} catch (BankingException | InvalidInputException exception) {
				request.getSession().setAttribute("errorMessage", exception.getMessage());
			} finally {
				request.getSession().setAttribute("redirect", "yes");
				response.sendRedirect(request.getContextPath() + "/app/transaction?transactionType=deposit");
//				request.setAttribute("type", "deposit");
//				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			}
			break;

//		case "/updateCustomer":
//
//			break;

		case "/addEmployee":
			try {
				addEmployee(request, response);
				request.setAttribute("successMessage", "Employee created !");
			} catch (ServletException | IOException | InvalidInputException | BankingException exception) {
				exception.printStackTrace();
				request.setAttribute("errorMessage", exception.getMessage());
			} finally {
				request.setAttribute("tab", "viewEmployee");
				request.getRequestDispatcher("/WEB-INF/jsp/organisation.jsp").forward(request, response);
			}
			break;

//		case "/addBranch":
//
//			break;

		case "/withdraw":
			try {
				Object servObj = request.getSession().getAttribute("user");
				String acc = request.getParameter("accNumber");
				Validator.validateAccount(acc);
				Long accountNum = Long.parseLong(acc);
				String withdrawAmount = request.getParameter("amount");
				Validator.validateAmount(withdrawAmount);
				Long wAmount = Long.parseLong(withdrawAmount);

				if (servObj instanceof AdminServices) {
					((AdminServices) servObj).withdraw(accountNum, wAmount);
				} else {
					((EmployeeServices) servObj).withdraw(accountNum, wAmount);
				}
				request.getSession().setAttribute("successMessage", "Withdrawl successful");
			} catch (BankingException | InvalidInputException exception) {
				request.getSession().setAttribute("errorMessage", exception.getMessage());
			} finally {
//				request.setAttribute("type", "withdraw");
//				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
				request.getSession().setAttribute("redirect", "yes");
				response.sendRedirect(request.getContextPath() + "/app/transaction?transactionType=withdraw");
			}
			break;
		}

	}

	private void addEmployee(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, InvalidInputException, BankingException {
		Object objec = request.getSession().getAttribute("user");
		Employee emp = new Employee();
		String name = request.getParameter("name") + " " + request.getParameter("lName");
		Validator.validateName(name);
		emp.setName(sanitizeInput(name));
		String dOB = request.getParameter("dOB");
		Validator.validateEmployeeDOB(dOB);
		emp.setDOB(dOB);
		String phone = request.getParameter("phone");
		Validator.validateMobile(phone);
		emp.setPhone(Long.parseLong(phone));
		String mail = request.getParameter("eMail");
		Validator.validateMail(mail);
		emp.setMail(mail);
		String genderString = request.getParameter("gender");
		Validator.validateGender(genderString);
		emp.setGender(genderString);
		String address = request.getParameter("addressL1") + "," + request.getParameter("addressL2") + ",";
		String pinCode = request.getParameter("pincode");
		Validator.validateAddress(address);
		Validator.validatePinCode(pinCode);
		emp.setAddress(address + pinCode);
		String aadharNumber = request.getParameter("aadharNumber");
		Validator.validateAadharNumber(aadharNumber);
		emp.setAadharNum(Long.parseLong(aadharNumber));
		emp.setBranchID(Long.parseLong(request.getParameter("branchId")));
		String adminPriv = request.getParameter("adminPrivileges");
		if (adminPriv.equals("true")) {
			emp.setLevel(UserLevel.Admin);
		} else {
			emp.setLevel(UserLevel.Employee);
		}
		long employeeID = 0;
		employeeID = ((AdminServices) objec).addEmployee(emp);
		request.setAttribute("employeeID", employeeID);
		setEmployeeDetails(request);

	}

	private void viewStatement(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Object servObj = getUserObject(request);
		JSONObject statements = null;
		String page = request.getParameter("pageNo");
		Integer pageNo = null;
		if (page != null) {
			pageNo = Integer.valueOf(page);
		}
		try {
			if (servObj instanceof CustomerServices) {
				request.setAttribute("accNum", ((CustomerServices) servObj).getAccNum());
				setAllAccounts(request, ((CustomerServices) servObj));
				String acc = request.getParameter("accountNumber");
				String account = request.getParameter("accountNum");
				Long accountNum = null;
				if (account != null) {
					Validator.validateAccount(account);
					accountNum = Long.parseLong(account);
					request.setAttribute("accNum", accountNum);
				} else if (acc != null) {
					Validator.validateAccount(acc);
					accountNum = Long.parseLong(acc);
					request.setAttribute("accNum", accountNum);
				} else {
					accountNum = ((CustomerServices) servObj).getAccNum();
				}
				if (pageNo != null) {
					statements = ((CustomerServices) servObj).getAccountStatement(accountNum, pageNo);
				} else {
					statements = ((CustomerServices) servObj).getAccountStatement(accountNum);
				}
			} else {
				String accNum = request.getParameter("accountNumber");
				if (accNum != null) {
					Validator.validateAccount(accNum);
					Long accountNumber = Long.parseLong(accNum);
					request.setAttribute("accNum", accountNumber);
					if (servObj instanceof AdminServices) {
						if (pageNo != null) {
							statements = ((AdminServices) servObj).getAccountStatement(accountNumber, pageNo);
						} else {
							statements = ((AdminServices) servObj).getAccountStatement(accountNumber);
						}
					} else {
						if (pageNo != null) {
							statements = ((EmployeeServices) servObj).getAccountStatement(accountNumber, pageNo);
						} else {
							statements = ((EmployeeServices) servObj).getAccountStatement(accountNumber);
						}
					}
				}
			}
			request.setAttribute("statements", statements);
		} catch (BankingException | InvalidInputException exception) {
			request.setAttribute("errorMessage", exception.getMessage());
		} finally {
			request.getRequestDispatcher("/WEB-INF/jsp/statement.jsp").forward(request, response);
		}
	}

	private void changePassword(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String oldPassword = (String) request.getParameter("oldPassword");
		String newPassword = (String) request.getParameter("newPassword");
		Object service = getUserObject(request);
		try {
			if (service instanceof CustomerServices) {
				((CustomerServices) service).changePassword(oldPassword, newPassword);
			} else if (service instanceof EmployeeServices) {
				((EmployeeServices) service).changePassword(oldPassword, newPassword);
			} else {
				((AdminServices) service).changePassword(oldPassword, newPassword);
			}
			viewProfile(request);
			request.setAttribute("successMessage", "Password changed successfully");
		} catch (BankingException exception) {
			try {
				viewProfile(request);
			} catch (BankingException e) {
			}
			request.setAttribute("errorMessage", exception.getMessage());
		} finally {
			request.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(request, response);
		}
	}

	private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("userId");
		try {
			Validator.validateUserID(id);
		} catch (InvalidInputException exception) {
			request.setAttribute("errorMessage", exception.getMessage());
			request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
		}
		Long userId = Long.parseLong(id);
		String password = request.getParameter("password");
		try {
			auth.login(userId, password);
			Object userServices = auth.getServiceObject(userId);
			request.getSession().setAttribute("user", userServices);
			if (userServices instanceof AdminServices || userServices instanceof EmployeeServices) {
				request.setAttribute("successMessage", "Login Successful!");
				request.setAttribute("tab", "viewCustomer");
				request.getRequestDispatcher("/WEB-INF/jsp/customer.jsp").forward(request, response);
			} else {
				request.setAttribute("successMessage", "Login Successful!");
				viewAccounts(request);
				request.getRequestDispatcher("/WEB-INF/jsp/customerAccounts.jsp").forward(request, response);
			}
		} catch (BankingException exception) {
			request.setAttribute("errorMessage", exception.getMessage());
			request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
		}
	}

	private void viewAccounts(HttpServletRequest request) {
		CustomerServices customerService = (CustomerServices) getUserObject(request);
		try {
			request.setAttribute("account", customerService.getAccount());
			System.out.println(customerService.getAccount());
			if (customerService.getAllAcc().size() > 1) {
				JSONObject accounts = customerService.getAccounts();
				request.setAttribute("otherAcc", accounts);
//				System.out.println(accounts);
			}
		} catch (BankingException exception) {
			request.setAttribute("errorMessage", "Couldn't fetch account details at the moment..Try again later");
		}
	}

	private void setAllAccounts(HttpServletRequest request, CustomerServices serviceObj) {
		try {
			request.setAttribute("accList", ((CustomerServices) serviceObj).getActiveAccounts());
		} catch (BankingException exception) {
			request.setAttribute("errorMessage", "Couldn't complete request at the moment");
		}
	}

	private void getAccountDetails(HttpServletRequest request) {
		try {
			Long accountNum = null;
			Object newAccNum = request.getSession().getAttribute("accNum");
			if (newAccNum != null) {
				accountNum = (Long) newAccNum;
				request.getSession().removeAttribute("accNum");
			}
			String typeValue = request.getParameter("value");
			Object userObj = request.getSession().getAttribute("user");
			if (typeValue != null) {
				Long value = Long.parseLong(typeValue);
				String type = request.getParameter("type");
				Validator.checkNull(type, "Search category cannot be null");
				if (type.equals("customerID")) {
					JSONObject accList;
					try {
						if (userObj instanceof AdminServices) {
							accList = ((AdminServices) userObj).getAccounts(value);
						} else {
							accList = ((EmployeeServices) userObj).getAccounts(value);
						}
						request.setAttribute("customerID", value);
						request.setAttribute("allAccounts", accList);
					} catch (BankingException exception) {
						request.setAttribute("errorMessage", exception.getMessage());
					}
				} else if (type.equals("accountNumber")) {
					accountNum = value;
				} else {
					throw new BankingException("Invalid search category");
				}
			}
			if (accountNum != null) {

				if (userObj instanceof AdminServices) {
					JSONObject acc = ((AdminServices) userObj).getAccount(accountNum);
					request.setAttribute("account", acc);
				} else {
					JSONObject acc = ((EmployeeServices) userObj).getAccount(accountNum);
					request.setAttribute("account", acc);
				}

			}
		} catch (BankingException | InvalidInputException exception) {
			request.setAttribute("errorMessage", exception.getMessage());
		}
	}

	private void viewProfile(HttpServletRequest request) throws BankingException {
		Object userServiceObject = getUserObject(request);
		if (userServiceObject instanceof CustomerServices) {
			JSONObject cus = ((CustomerServices) userServiceObject).getCustomerDetails();
			request.setAttribute("profile", cus);
		} else if (userServiceObject instanceof AdminServices) {
			JSONObject admin = ((AdminServices) userServiceObject).getEmployeeDetails();
			request.setAttribute("profile", admin);
		} else {
			JSONObject emp = ((EmployeeServices) userServiceObject).getEmployeeDetails();
			request.setAttribute("profile", emp);
		}
	}

	private void setCustomerDetails(HttpServletRequest request) throws InvalidInputException {
		Long customerID = null;
		Object newCustomerID = request.getAttribute("customerID");
		if (newCustomerID != null) {
			customerID = (Long) newCustomerID;
		}
		String cusID = request.getParameter("customerID");
		if (cusID != null) {
			Validator.validateUserID(cusID);
			customerID = Long.parseLong(cusID);
		}
		if (customerID != null) {
			Object obj = request.getSession().getAttribute("user");
			JSONObject cusDetails;
			try {
				if (obj instanceof AdminServices) {
					cusDetails = ((AdminServices) obj).getCustomerDetails(customerID);
				} else {
					cusDetails = ((EmployeeServices) obj).getCustomerDetails(customerID);
				}
				request.setAttribute("customerDetails", cusDetails);
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
			}
		}
	}

	private void addCustomer(HttpServletRequest request, HttpServletResponse response)
			throws InvalidInputException, BankingException {
		Object obj = request.getSession().getAttribute("user");
		Customer cus = new Customer();
		String name = request.getParameter("name") + " " + request.getParameter("lName");
		Validator.validateName(name);
		cus.setName(sanitizeInput(name));
		String dOB = request.getParameter("dOB");
		Validator.validateCustomerDOB(dOB);
		cus.setDOB(dOB);
		String phoneString = request.getParameter("phone");
		Validator.validateMobile(phoneString);
		cus.setPhone(Long.parseLong(phoneString));
		String mail = request.getParameter("eMail");
		Validator.validateMail(mail);
		cus.setMail(sanitizeInput(mail));
		String genderString = request.getParameter("gender");
		Validator.validateGender(genderString);
		cus.setGender(genderString);
		String address = request.getParameter("addressL1") + "," + request.getParameter("addressL2") + ",";
		String pinCode = request.getParameter("pincode");
		Validator.validateAddress(address);
		Validator.validatePinCode(pinCode);
		cus.setAddress(sanitizeInput(address + pinCode));
		String aadharNumber = request.getParameter("aadharNumber");
		Validator.validateAadharNumber(aadharNumber);
		cus.setAadharNum(Long.parseLong(aadharNumber));
		String panNumber = request.getParameter("panNumber");
		cus.setPanNum(panNumber);
		Account account = new Account();
		String type = request.getParameter("accountType");
		AccountType accountType;
		if (type.equals("Savings")) {
			accountType = AccountType.SAVINGS;
		} else {
			accountType = AccountType.CURRENT;
		}
		account.setType(accountType);
		long customerID = 0;
		if (obj instanceof AdminServices) {
			Long branchId = Long.parseLong(request.getParameter("branchId"));
			customerID = ((AdminServices) obj).addCustomer(cus, account, branchId);
		} else {
			customerID = ((EmployeeServices) obj).addCustomer(cus, account);
		}
		request.setAttribute("customerID", customerID);
		request.setAttribute("successMessage", "Customer created successfully");
		setCustomerDetails(request);
	}

	private void sendMoney(HttpServletRequest request, boolean withinBank)
			throws BankingException, InvalidInputException {
		Object services = getUserObject(request);
		Transaction transaction = new Transaction();
		String accNumber = request.getParameter("accNumber");
		Validator.validateAccount(accNumber);
		transaction.setTransAccNum(Long.parseLong(accNumber));
		String amount = request.getParameter("amount");
		Validator.validateAmount(amount);
		transaction.setAmount(Long.parseLong(amount));
		String description = request.getParameter("description");
		Validator.validateDescription(description);
		transaction.setDescription(sanitizeInput(description));
		if (!withinBank) {
			String iFSC = request.getParameter("iFSC");
			Validator.validateIFSC(iFSC);
			transaction.setiFSC(iFSC);
		}
		if (services instanceof CustomerServices) {
			String pin = request.getParameter("tpin");
			Validator.validatePin(pin);
			String accountNumber = request.getParameter("ownAccNumber");
			Validator.validateAccount(accountNumber);
			transaction.setAccountNumber(Long.parseLong(accountNumber));
			((CustomerServices) services).transfer(transaction, pin, withinBank);
		} else {
			String senderAccountNumber = request.getParameter("senderAccNum");
			Validator.validateAccount(senderAccountNumber);
			long accoNumber = Long.parseLong(senderAccountNumber);
			if (services instanceof AdminServices) {
				((AdminServices) services).transfer(transaction, accoNumber, withinBank);
			} else {
				((EmployeeServices) services).transfer(transaction, accoNumber, withinBank);
			}
		}
	}

	private void setEmployeeDetails(HttpServletRequest request) throws InvalidInputException, BankingException {
		Long employeeID = null;
		Object newEmployeeID = request.getAttribute("employeeID");
		if (newEmployeeID != null) {
			employeeID = (Long) newEmployeeID;
		}
		String empID = request.getParameter("employeeID");
		if (empID != null) {
			Validator.validateUserID(empID);
			employeeID = Long.parseLong(empID);
		}
		if (employeeID != null) {
			Object obj = request.getSession().getAttribute("user");
			JSONObject empDetails;
			empDetails = ((AdminServices) obj).getEmployeeDetails(employeeID);
			request.setAttribute("employeeDetails", empDetails);
//			System.out.println(empDetails);
		}
	}

	private Object getUserObject(HttpServletRequest request) {
		return request.getSession(false).getAttribute("user");
	}

	private String sanitizeInput(String input) {
		return input.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;").replaceAll("'", "&#39;")
				.replaceAll("&", "&amp;");
	}

}
