package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.bank.enums.AccountType;
import com.bank.enums.Status;
import com.bank.exceptions.BankingException;
import com.bank.exceptions.InvalidInputException;
import com.bank.pojo.Account;
import com.bank.pojo.Customer;
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

		// nav - customer personal accounts view
		case "/accounts":
			CustomerServices customerService = (CustomerServices) getUserObject(request);
			try {
				setMessage(request);
				request.setAttribute("account", customerService.getAccount());
				if (customerService.getAllAcc().size() > 1) {
					request.setAttribute("otherAcc", customerService.getAccounts());
				}
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", "Couldn't fetch account details");
			} finally {
				request.getRequestDispatcher("/WEB-INF/jsp/customerAccounts.jsp").forward(request, response);
			}
			break;

		// make another account primary for customer
		case "/accounts/makePrimary":
			CustomerServices customerServ = (CustomerServices) getUserObject(request);
			String message = null;
			int status = 0;
			try {
				customerServ.switchAccount(Long.parseLong(request.getParameter("newAcc")));
				message = "Primary account switched";
				status = 1;
			} catch (BankingException exception) {
				message = "Couldn't switch primary account";
			} finally {
				response.sendRedirect(request.getContextPath() + "/app/accounts?msg=" + message + "&status=" + status);
			}
			break;

		// nav - profile (for all users)
		case "/profile":
			Object userServiceObject = getUserObject(request);
			setMessage(request);
			try {
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
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", "Couldn't fetch profile at the moment");
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
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			} else if (tab.equals("withdraw")) {
				request.setAttribute("type", "withdraw");
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			} else if (tab.equals("deposit")) {
				request.setAttribute("type", "deposit");
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			} else {
				request.setAttribute("type", "outside");
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			}

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
					if (pageNo != null) {
						statements = ((CustomerServices) servObj).getAccountStatement(pageNo);
					} else {
						statements = ((CustomerServices) servObj).getAccountStatement();
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
				request.setAttribute("errorMessage", "Couldn't fetch statements");
			} finally {
				request.getRequestDispatcher("/WEB-INF/jsp/statement.jsp").forward(request, response);
			}
			break;

		case "/logout":
			request.getSession().invalidate();
			response.sendRedirect(request.getContextPath() + "/app/login");
			break;

		case "/customerDetails":
			Long customerID = null;
			Object newCustomerID = request.getSession().getAttribute("customerID");
			if (newCustomerID != null) {
				customerID = (Long) newCustomerID;
				request.getSession().removeAttribute("customerID");
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
					// handle-----------------------------------------------------------
				}
			}
			request.setAttribute("tab", "viewCustomer");
			request.getRequestDispatcher("/WEB-INF/jsp/customer.jsp").forward(request, response);
			break;

		case "/addCustomer":
			request.setAttribute("tab", "addCustomer");
			request.getRequestDispatcher("/WEB-INF/jsp/customer.jsp").forward(request, response);
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
			Object obj = request.getSession().getAttribute("user");
			try {
				((AdminServices) obj).setStatus(customerId, state);
			} catch (BankingException exception) {
				// hanlde--------------------------------------------------
			}
			request.getSession().setAttribute("customerID", customerId);
			response.sendRedirect(request.getContextPath() + "/app/customerDetails");
			break;

		case "/accountDetails":
//			Enumeration<String> attributes = request.getSession().getAttributeNames();
//			while (attributes.hasMoreElements()) {
//			    String attribute = (String) attributes.nextElement();
//			    System.out.println(attribute+" : "+request.getSession().getAttribute(attribute));
//			}
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
						} catch (BankingException e) {
							// handle----------------------------------------------------------------
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
					// handle----------------------------------------------
				}
			}
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
					// handle-----------------------------------------
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
					response.sendRedirect(
							request.getContextPath() + "/app/customerDetails?msg=Login Successful&status=1");
				} else {
					response.sendRedirect(request.getContextPath() + "/app/accounts?msg=Login Successful&status=1");
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
				CustomerServices service = (CustomerServices) request.getSession().getAttribute("user");
				service.changePassword(oldPassword, newPassword);
				response.sendRedirect(
						request.getContextPath() + "/app/profile?msg=Password Changed Successfully&status=1");
			} catch (BankingException exception) {
				response.sendRedirect(request.getContextPath() + "/app/profile?msg=Couldn't change password&status=0");
			}
			break;

		// change T-PIN for customers
		case "/changePIN":
			String oldPIN = (String) request.getParameter("oldPIN");
			String newPIN = (String) request.getParameter("newPIN");
			CustomerServices cusService = (CustomerServices) getUserObject(request);
			try {
				cusService.changePin(oldPIN, newPIN);
				response.sendRedirect(request.getContextPath() + "/app/profile?msg=Pin Changed Successfully&status=1");
			} catch (BankingException | InvalidInputException exception) {
				response.sendRedirect(request.getContextPath() + "/app/profile?msg=Couldn't change pin&status=0");
			}
			break;

		// set T-PIN during first transaction (customer)
		case "/setPin":
			String tPIN = request.getParameter("newPin");
			CustomerServices cServices = (CustomerServices) getUserObject(request);
			try {
				cServices.setPin(tPIN);
				request.setAttribute("successMessage", "T-Pin set successfully");
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			} catch (BankingException | InvalidInputException exception) {
				request.setAttribute("error", exception.getMessage());
				response.sendRedirect(request.getContextPath() + "/app/accounts?msg=Couldn't set T-Pin&status=0");
			}
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
				request.setAttribute("errorMessage", exception.getMessage());
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			} finally {
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			}
			break;

		// add new customer for employee
		case "/addCustomer":
			Object obj = request.getSession().getAttribute("user");
			Customer cus = new Customer();
			cus.setName(request.getParameter("name"));
			cus.setDOB(request.getParameter("dOB"));
			cus.setPhone(Long.parseLong(request.getParameter("phone")));
			cus.setMail(request.getParameter("eMail"));
			cus.setGender(request.getParameter("gender"));
			cus.setAddress(request.getParameter("address"));
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
					Long branchId = Long.parseLong(request.getParameter("branchID"));
					customerID = ((AdminServices) obj).addCustomer(cus, account, branchId);
				} else {
					customerID = ((EmployeeServices) obj).addCustomer(cus, account);
				}
			} catch (BankingException exception) {
				// handle-----------------------------------------------------------
			}
			request.getSession().setAttribute("customerID", customerID);
			response.sendRedirect(request.getContextPath() + "/app/customerDetails");
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
			} catch (BankingException exception) {
				// ---------------------------------------------------------------------------
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
			} catch (BankingException exception) {
				// handle
				// ---------------------------------------------------------------------------
			}
			request.setAttribute("type", "deposit");
			request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
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
			} catch (BankingException exception) {
				// handle
				// ---------------------------------------------------------------------------
			}
			request.setAttribute("type", "withdraw");
			request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			break;
		}

	}

	private void sendMoney(HttpServletRequest request, boolean withinBank)
			throws BankingException, InvalidInputException {
		Object services = request.getSession().getAttribute("user");
		Transaction transaction = new Transaction();
		transaction.setTransAccNum(Long.parseLong(request.getParameter("accNumber")));
		transaction.setAmount(Long.parseLong(request.getParameter("amount")));
		transaction.setDescription(request.getParameter("description"));
		String pin = request.getParameter("tpin");
		if (!withinBank) {
			transaction.setiFSC(request.getParameter("iFSC"));
		}
		if (services instanceof CustomerServices) {
			try {
				((CustomerServices) services).transfer(transaction, pin, withinBank);
			} catch (BankingException e) {

			}
		} else {
			long accNumber = Long.parseLong(request.getParameter("senderAccNum"));
			if (services instanceof AdminServices) {
				((AdminServices) services).transfer(transaction, accNumber, withinBank);
			} else {
				((EmployeeServices) services).transfer(transaction, accNumber, withinBank);
			}
		}

	}

	private void setMessage(HttpServletRequest request) {
		String message = request.getParameter("msg");
		System.out.println(message);
		if (message != null) {
			String status = request.getParameter("status");
			System.out.println(status);
			if (status.equals("1")) {
				request.setAttribute("successMessage", message);
			} else if (status.equals("0")) {
				request.setAttribute("errorMessage", message);
			}
		}
	}

	private Object getUserObject(HttpServletRequest request) {
		return request.getSession(false).getAttribute("user");
	}
}
