package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

public class Controller extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static AuthServices auth = new AuthServices();

	public Controller() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = request.getPathInfo();

		switch (path) {

		// home to login
		case "/login":
			request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
			break;

		// logout
		case "/logout":
			request.setAttribute("successMessage", "You've logged out !");
			request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
			request.getSession().invalidate();
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
				request.setAttribute("errorMessage", "Couldn't switch primary account at the moment");
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
					Long accountNum = Long.parseLong(request.getParameter("accountNumber"));
					if (pageNo != null) {
						statements = ((CustomerServices) servObj).getAccountStatement(accountNum, pageNo);
					} else {
						statements = ((CustomerServices) servObj).getAccountStatement(accountNum);
					}
				} else {
					String accNum = request.getParameter("accountNumber");
					if (accNum != null) {
						request.setAttribute("accNum", accNum);
						if (servObj instanceof AdminServices) {
							if (pageNo != null) {
								statements = ((AdminServices) servObj).getAccountStatement(Long.parseLong(accNum),
										pageNo);
							} else {
								statements = ((AdminServices) servObj).getAccountStatement(Long.parseLong(accNum));
							}
						} else {
							if (pageNo != null) {
								statements = ((EmployeeServices) servObj).getAccountStatement(Long.parseLong(accNum),
										pageNo);
							} else {
								statements = ((EmployeeServices) servObj).getAccountStatement(Long.parseLong(accNum));
							}
						}
					}
				}
				request.setAttribute("statements", statements);
			} catch (BankingException exception) {
				exception.printStackTrace();
				request.setAttribute("errorMessage", "Couldn't fetch statements");
			} finally {
				request.getRequestDispatcher("/WEB-INF/jsp/statement.jsp").forward(request, response);
			}
			break;

		case "/customerDetails":
			setCustomerDetails(request);
			request.setAttribute("tab", "viewCustomer");
			request.getRequestDispatcher("/WEB-INF/jsp/customer.jsp").forward(request, response);
			break;

		case "/employeeDetails":
			setEmployeeDetails(request);
			request.setAttribute("tab", "viewEmployee");
			request.getRequestDispatcher("/WEB-INF/jsp/organisation.jsp").forward(request, response);
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
			setCustomerDetails(request);
			request.setAttribute("tab", "updateCustomer");
			request.getRequestDispatcher("/WEB-INF/jsp/customer.jsp").forward(request, response);
			break;

		case "/accountDetails":
			getAccountDetails(request);
			request.setAttribute("tab", "viewAccount");
			request.getRequestDispatcher("/WEB-INF/jsp/accounts.jsp").forward(request, response);
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
			response.sendRedirect(request.getContextPath() + "/app/accountDetails");
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = request.getPathInfo();

		switch (path) {

		// login authentication and assigning user object
		case "/login":
			Long id = Long.parseLong(request.getParameter("userId"));
			String password = request.getParameter("password");
			try {
				auth.login(id, password);
				Object userServices = auth.getServiceObj(id);
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
			break;

		// change password (all Users)
		case "/changePassword":
			String oldPassword = (String) request.getParameter("oldPassword");
			String newPassword = (String) request.getParameter("newPassword");
			try {
				CustomerServices service = (CustomerServices) getUserObject(request);
				service.changePassword(oldPassword, newPassword);
				viewProfile(request);
				request.setAttribute("successMessage", "Password changed successfully");
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", "Couldn't change password at the moment");
			} finally {
				System.out.println(request.getAttribute("profile"));
				request.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(request, response);
			}
			break;

		// change T-PIN for customers
		case "/changePIN":
			String oldPIN = (String) request.getParameter("oldPIN");
			String newPIN = (String) request.getParameter("newPIN");
			try {
				CustomerServices cusService = (CustomerServices) getUserObject(request);
				viewProfile(request);
				cusService.changePin(oldPIN, newPIN);
				System.out.println(request.getAttribute("profile"));
				request.setAttribute("successMessage", "PIN changed successfully");
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
			} catch (InvalidInputException exception) {
				request.setAttribute("errorMessage", "Invalid PIN format");
			} finally {
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
					request.setAttribute("type", "within");
					request.setAttribute("successMessage", "Transaction successful");
				} else {
					sendMoney(request, false);
					request.setAttribute("type", "outside");
					request.setAttribute("successMessage", "Transaction successful");
				}
			} catch (BankingException | InvalidInputException exception) {
				exception.printStackTrace();
				request.setAttribute("errorMessage", "Couldn't complete transaction at the moment");
			} finally {
				CustomerServices cusServ = (CustomerServices) request.getSession().getAttribute("user");

				setAllAccounts(request, cusServ);
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			}
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
			}
			request.setAttribute("customerID", customerId);
			setCustomerDetails(request);
			request.setAttribute("tab", "viewCustomer");
			request.getRequestDispatcher("/WEB-INF/jsp/customer.jsp").forward(request, response);
			break;

		// add new customer for employee
		case "/addCustomer":
			Object obj = request.getSession().getAttribute("user");
			Customer cus = new Customer();
			cus.setName(request.getParameter("name") + " " + request.getParameter("lName"));
			cus.setDOB(request.getParameter("dOB"));
			cus.setPhone(Long.parseLong(request.getParameter("phone")));
			cus.setMail(request.getParameter("eMail"));
			cus.setGender(request.getParameter("gender"));
			cus.setAddress(request.getParameter("addressL1") + "," + request.getParameter("addressL2") + ","
					+ request.getParameter("pincode"));
			cus.setAadharNum(Long.parseLong(request.getParameter("aadharNumber")));
			cus.setPanNum(request.getParameter("panNumber"));
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
			try {
				if (obj instanceof AdminServices) {
					Long branchId = Long.parseLong(request.getParameter("branchId"));
					customerID = ((AdminServices) obj).addCustomer(cus, account, branchId);
				} else {
					customerID = ((EmployeeServices) obj).addCustomer(cus, account);
				}
				request.setAttribute("customerID", customerID);
				request.setAttribute("successMessage", "Customer created successfully");
				setCustomerDetails(request);
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", "Couldn't create Customer at the moment");
			} finally {
				request.setAttribute("tab", "viewCustomer");
				request.getRequestDispatcher("/WEB-INF/jsp/customer.jsp").forward(request, response);
			}
			break;

		case "/createAccount":
			Object object = request.getSession().getAttribute("user");
			Account acc = new Account();
			acc.setUId(Long.parseLong(request.getParameter("customerID")));
			AccountType type1;
			String accType = request.getParameter("accType");
			if (accType.equals("Current")) {
				type1 = AccountType.CURRENT;
			} else {
				type1 = AccountType.SAVINGS;
			}
			acc.setType(type1);
			long accNum = 0;
			try {
				if (object instanceof AdminServices) {
					Long branchID = Long.parseLong(request.getParameter("branchId"));
					accNum = ((AdminServices) object).addAccount(acc, branchID);
				} else {
					accNum = ((EmployeeServices) object).addAccount(acc);
				}
				request.setAttribute("successMessage", "Account created successfully");
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
			}
			request.getSession().setAttribute("accNum", accNum);
			response.sendRedirect(request.getContextPath() + "/app/accountDetails");
			break;

		case "/deposit":
			Object serviceObj = request.getSession().getAttribute("user");
			Long accountNum = Long.parseLong(request.getParameter("accNumber"));
			Long amount = Long.parseLong(request.getParameter("amount"));
			try {
				if (serviceObj instanceof AdminServices) {
					((AdminServices) serviceObj).deposit(accountNum, amount);
				} else {
					((EmployeeServices) serviceObj).deposit(accountNum, amount);
				}
				request.setAttribute("successMessage", "Deposit successful");
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
			} finally {
				request.setAttribute("type", "deposit");
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			}
			break;

		case "/updateCustomer":

			break;

		case "/addEmployee":
			Object objec = request.getSession().getAttribute("user");
			Employee emp = new Employee();
			emp.setName(request.getParameter("name") + " " + request.getParameter("lName"));
			emp.setDOB(request.getParameter("dOB"));
			emp.setPhone(Long.parseLong(request.getParameter("phone")));
			emp.setMail(request.getParameter("eMail"));
			emp.setGender(request.getParameter("gender"));
			emp.setAddress(request.getParameter("addressL1") + "," + request.getParameter("addressL2") + ","
					+ request.getParameter("pincode"));
			emp.setBranchID(Long.parseLong(request.getParameter("branchId")));
			String adminPriv = request.getParameter("adminPrivileges");
			if(adminPriv.equals("true")) {
				emp.setLevel(UserLevel.Admin);
			}else {
				emp.setLevel(UserLevel.Employee);
			}
			long employeeID = 0;
			try {
				employeeID = ((AdminServices) objec).addEmployee(emp);
				request.setAttribute("employeeID", employeeID);
				request.setAttribute("successMessage", "Employee created successfully");
				setEmployeeDetails(request);
				System.out.println(request.getAttribute("employeeDetails"));
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", "Couldn't create Employee at the moment");
			} catch (InvalidInputException exception) {
				request.setAttribute("errorMessage", "Couldn't create Employee at the moment");
			} finally {
				request.setAttribute("tab", "viewEmployee");
				request.getRequestDispatcher("/WEB-INF/jsp/organisation.jsp").forward(request, response);
			}
			break;

		case "/addBranch":

			break;

		case "/withdraw":
			Object servObj = request.getSession().getAttribute("user");
			Long accountNumber = Long.parseLong(request.getParameter("accNumber"));
			Long amt = Long.parseLong(request.getParameter("amount"));
			try {
				if (servObj instanceof AdminServices) {
					((AdminServices) servObj).withdraw(accountNumber, amt);
				} else {
					((EmployeeServices) servObj).withdraw(accountNumber, amt);
				}
				request.setAttribute("successMessage", "Withdrawl successful");
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
			} finally {
				request.setAttribute("type", "withdraw");
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			}
			break;
		}

	}

	private void viewAccounts(HttpServletRequest request) {
		CustomerServices customerService = (CustomerServices) getUserObject(request);
		try {
			request.setAttribute("account", customerService.getAccount());
			if (customerService.getAllAcc().size() > 1) {
				request.setAttribute("otherAcc", customerService.getAccounts());
			}
		} catch (BankingException exception) {
			request.setAttribute("errorMessage", "Couldn't fetch account details at the moment");
		}
	}

	private void setAllAccounts(HttpServletRequest request, CustomerServices serviceObj) {
		try {
			request.setAttribute("accList", ((CustomerServices) serviceObj).getAllAcc());
		} catch (BankingException exception) {
			request.setAttribute("errorMessage", "Couldn't complete request at the moment");
		}
	}

	private void getAccountDetails(HttpServletRequest request) {
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
			if (userObj instanceof AdminServices) {
				String type = request.getParameter("type");
				if (type.equals("customerID")) {
					JSONObject accList;
					try {
						accList = ((AdminServices) userObj).getAccounts(value);
						request.setAttribute("customerID", value);
						request.setAttribute("allAccounts", accList);
					} catch (BankingException exception) {
						request.setAttribute("errorMessage", exception.getMessage());
					}
				} else {
					accountNum = value;
				}
			} else {
				accountNum = value;
			}
		}
		if (accountNum != null) {
			try {
				if (userObj instanceof AdminServices) {
					JSONObject acc = ((AdminServices) userObj).getAccount(accountNum);
					request.setAttribute("account", acc);
				} else {
					JSONObject acc = ((EmployeeServices) userObj).getAccount(accountNum);
					request.setAttribute("account", acc);
				}
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
			}
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

	private void setCustomerDetails(HttpServletRequest request) {
		Long customerID = null;
		Object newCustomerID = request.getAttribute("customerID");
		if (newCustomerID != null) {
			customerID = (Long) newCustomerID;
		}
		String cusID = request.getParameter("customerID");
		if (cusID != null) {
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

	private void sendMoney(HttpServletRequest request, boolean withinBank)
			throws BankingException, InvalidInputException {
		Object services = getUserObject(request);
		Transaction transaction = new Transaction();
		transaction.setTransAccNum(Long.parseLong(request.getParameter("accNumber")));
		transaction.setAmount(Long.parseLong(request.getParameter("amount")));
		transaction.setDescription(request.getParameter("description"));
		String pin = request.getParameter("tpin");
		if (!withinBank) {
			transaction.setiFSC(request.getParameter("iFSC"));
		}
		if (services instanceof CustomerServices) {
			transaction.setAccNumber(Long.parseLong(request.getParameter("ownAccNumber")));
			((CustomerServices) services).transfer(transaction, pin, withinBank);
		} else {
			long accNumber = Long.parseLong(request.getParameter("senderAccNum"));
			if (services instanceof AdminServices) {
				((AdminServices) services).transfer(transaction, accNumber, withinBank);
			} else {
				((EmployeeServices) services).transfer(transaction, accNumber, withinBank);
			}
		}
	}

	private void setEmployeeDetails(HttpServletRequest request) {
		Long employeeID = null;
		Object newEmployeeID = request.getAttribute("employeeID");
		if (newEmployeeID != null) {
			employeeID = (Long) newEmployeeID;
		}
		String empID = request.getParameter("employeeID");
		if (empID != null) {
			employeeID = Long.parseLong(empID);
		}
		if (employeeID != null) {
			Object obj = request.getSession().getAttribute("user");
			JSONObject empDetails;
			try {
				empDetails = ((AdminServices) obj).getEmployeeDetails(employeeID);
				System.out.println(empDetails);
				request.setAttribute("employeeDetails", empDetails);
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
			}
		}
	}

	private Object getUserObject(HttpServletRequest request) {
		return request.getSession(false).getAttribute("user");
	}
}
