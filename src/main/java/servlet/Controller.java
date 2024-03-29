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
				//handle-------------------------
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

			} else if (userServices instanceof EmployeeServices) {

			} else if (userServices instanceof CustomerServices) {
				HttpSession session = request.getSession(true);
				session.setAttribute("user", userServices);
				response.sendRedirect(request.getContextPath() + "/app/accounts");
			}
			break;

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
