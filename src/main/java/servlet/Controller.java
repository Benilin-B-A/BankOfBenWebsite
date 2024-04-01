package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bank.custom.exceptions.BankingException;
import com.bank.custom.exceptions.InvalidInputException;
import com.bank.enums.AccountType;
import com.bank.enums.Status;
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

		// customer nav - profile
		case "/profile":
			CustomerServices customer = (CustomerServices) request.getSession(false).getAttribute("user");
			try {
				JSONObject cus = customer.getCustomerDetails();
				request.setAttribute("customerDetails", cus);
				request.getRequestDispatcher("/WEB-INF/jsp/customerProfile.jsp").forward(request, response);
			} catch (BankingException exception) {
				//// handle-----------
			}
			break;

		// customer nav - accounts
		case "/accounts":
			CustomerServices customer1 = (CustomerServices) request.getSession(false).getAttribute("user");
			try {
				request.setAttribute("account", customer1.getAccount());
				if (customer1.getAllAcc().size() > 1) {
					request.setAttribute("otherAcc", customer1.getAccounts());
				}
				request.getRequestDispatcher("/WEB-INF/jsp/customerAccounts.jsp").forward(request, response);
			} catch (BankingException exception) {
				// handle--------------
			}
			break;

		// customer nav - transaction
		case "/transaction":
			CustomerServices cusServices = (CustomerServices) request.getSession(false).getAttribute("user");
			boolean pinSet = cusServices.isPinSet();
			if (pinSet) {
				String tab = request.getParameter("transactionType");
				if (tab == null || tab.equals("withinBank")) {
					request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
				} else {
					request.setAttribute("type", "outside");
					request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
				}
			} else {
				request.getRequestDispatcher("/WEB-INF/jsp/setPin.jsp").forward(request, response);
			}
			break;

		// customer nav - statement
		case "/statement":
			CustomerServices cuServices = (CustomerServices) request.getSession(false).getAttribute("user");
			try {
				JSONArray statements = cuServices.getAccountStatement();
				request.setAttribute("statements", statements);
				request.getRequestDispatcher("/WEB-INF/jsp/statement.jsp").forward(request, response);
			} catch (BankingException e) {
				// handle-------------------------
			}
			break;

		// make an account primary
		case "/accounts/makePrimary":
			CustomerServices cuServ = (CustomerServices) request.getSession(false).getAttribute("user");
			try {
				cuServ.switchAccount(Long.parseLong(request.getParameter("newAcc")));
				response.sendRedirect(request.getContextPath() + "/app/accounts");
			} catch (BankingException exception) {
				// handle--------------------------------
			}
			break;

		// change password for all users (nav to change page)
		case "/changePassword":
			request.setAttribute("credentialType", "password");
			request.getRequestDispatcher("/WEB-INF/jsp/changeCredential.jsp").forward(request, response);
			break;

		// change pin for customer (nav from profile)
		case "/changePin":
			request.setAttribute("credentialType", "pin");
			request.getRequestDispatcher("/WEB-INF/jsp/changeCredential.jsp").forward(request, response);
			break;

		case "/logout":
			request.getSession().invalidate();
			response.sendRedirect(request.getContextPath() + "/app/login");
			break;

		case "/customerDetails":
			Long customerID = null;
			Object newCustomerID = request.getSession().getAttribute("customerID");
			if(newCustomerID!=null) {
				customerID = (Long) newCustomerID ;
				request.getSession().removeAttribute("customerID");
			}
			String cusID = request.getParameter("customerID");
			if (cusID != null) {
				customerID = Long.parseLong(cusID) ;
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
			Status status;
			String action = request.getParameter("action");
			if(action.equals("activate")) {
				status = Status.ACTIVE;
			}
			else{
				status = Status.INACTIVE;
			}
			Object obj = request.getSession().getAttribute("user");
			try {
				((AdminServices) obj).setStatus(customerId, status);
			}catch(BankingException exception) {
				//hanlde--------------------------------------------------
			}
			request.getSession().setAttribute("customerID", customerId);
			response.sendRedirect(request.getContextPath() + "/app/customerDetails");
			break;

		case "/accountDetails":
			Long accountNum = null;
			Object newAccNum = request.getSession().getAttribute("accNum");
			if(newAccNum!=null) {
				accountNum = (Long) newAccNum ;
				request.getSession().removeAttribute("accNum");
			}
			String accNum = request.getParameter("accountNumber");
			if (accNum != null) {
				accountNum = Long.parseLong(accNum) ;
			}
			if (accountNum != null) {
				Object userObj = request.getSession().getAttribute("user");
				JSONObject accDetails;
				try {
					if (userObj instanceof AdminServices) {
						accDetails = ((AdminServices) userObj).getAccount(accountNum);
					} else {
						accDetails = ((EmployeeServices) userObj).getAccount(accountNum);
					}
					request.setAttribute("accDetails", accDetails);
				} catch (BankingException exception) {
					// handle-----------------------------------------------------------
				}
			}
			request.setAttribute("tab", "viewAccount");
			request.getRequestDispatcher("/WEB-INF/jsp/accounts.jsp").forward(request, response);
			break;
		}
		

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = request.getPathInfo();

		switch (path) {

		// login authentication
		case "/login":

			String id = request.getParameter("userId");
			String password = request.getParameter("password");
			if (id == null || password == null) {
				request.setAttribute("errorMessage", "Enter valid credentials");
				request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
			}
			try {
				auth.login(Long.parseLong(id), password);
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
				request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
			}

			Object userServices = null;
			try {
				userServices = AuthServices.getServiceObj(Long.parseLong(id));
			} catch (BankingException exception) {
				request.setAttribute("errorMessage", exception.getMessage());
				request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
			}
			if (userServices instanceof AdminServices) {
				HttpSession session = request.getSession(true);
				session.setAttribute("user", userServices);
				response.sendRedirect(request.getContextPath() + "/app/customerDetails");
			} else if (userServices instanceof EmployeeServices) {
				HttpSession session = request.getSession(true);
				session.setAttribute("user", userServices);
				response.sendRedirect(request.getContextPath() + "/app/customerDetails");
			} else if (userServices instanceof CustomerServices) {
				HttpSession session = request.getSession(true);
				session.setAttribute("user", userServices);
				response.sendRedirect(request.getContextPath() + "/app/accounts");
			}
			break;

		// money transfer
		case "/transaction/sendMoney":
			try {
				if (request.getParameter("type").equals("within")) {
					sendMoney(request, true);
					request.setAttribute("successMessage", "Transaction Successful");
					request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
				} else {
					sendMoney(request, false);
					request.setAttribute("type", "outside");
					request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
				}
			} catch (BankingException | InvalidInputException exception) {
				request.setAttribute("error", exception.getMessage());
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);

			}
			break;

		// set T-PIN during first transaction (customer)
		case "/transaction/setTPIN":
			String tPIN = request.getParameter("tPIN");
			if (tPIN.equals(request.getParameter("confTPIN"))) {
				CustomerServices cServices = (CustomerServices) request.getSession(false).getAttribute("user");
				try {
					cServices.setPin(tPIN);
					request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
				} catch (BankingException | InvalidInputException exception) {
					request.setAttribute("error", exception.getMessage());
					request.getRequestDispatcher("/WEB-INF/jsp/setPin.jsp").forward(request, response);
				}
			}
			request.setAttribute("error", "PIN doesn't match");
			request.getRequestDispatcher("/WEB-INF/jsp/setPin.jsp").forward(request, response);

			break;

		// change password (all Users)
		case "/changePassword":
			String oldPassword = (String) request.getParameter("oldPassword");
			String newPassword = (String) request.getParameter("newPassword");
			CustomerServices service = (CustomerServices) request.getSession().getAttribute("user");
			try {
				service.changePassword(oldPassword, newPassword);
			} catch (BankingException e) {
				// handle---------------------------------------------------
			}
			request.getSession().invalidate();
			response.sendRedirect(request.getContextPath() + "/app/login");
			break;

		// change T-PIN for customers
		case "/changePIN":
			String oldPIN = (String) request.getParameter("oldPIN");
			String newPIN = (String) request.getParameter("newPIN");
			CustomerServices cusService = (CustomerServices) request.getSession().getAttribute("user");
			try {
				cusService.changePin(oldPIN, newPIN);
			} catch (BankingException | InvalidInputException e) {
				// handle---------------------------------------------------
			}
			response.sendRedirect(request.getContextPath() + "/app/profile");
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

		}

	}

	private void sendMoney(HttpServletRequest request, boolean withinBank)
			throws BankingException, InvalidInputException {
		CustomerServices cusServices = (CustomerServices) request.getSession().getAttribute("user");
		Transaction transaction = new Transaction();
		transaction.setTransAccNum(Long.parseLong(request.getParameter("accNumber")));
		transaction.setAmount(Long.parseLong(request.getParameter("amount")));
		transaction.setDescription(request.getParameter("description"));
		String pin = request.getParameter("tpin");
		if (!withinBank) {
			transaction.setiFSC(request.getParameter("iFSC"));
		}
		cusServices.transfer(transaction, pin, withinBank);
	}

}
