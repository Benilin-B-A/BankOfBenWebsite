package servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.bank.util.LogHandler;

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
			CustomerServices customer = (CustomerServices) request.getSession().getAttribute("user");
			try {
				JSONObject cus = customer.getCustomerDetails();
				request.setAttribute("customerDetails", cus);
				request.setAttribute("tab", "profile");
				request.getRequestDispatcher("/WEB-INF/jsp/customer.jsp").forward(request, response);
			} catch (BankingException exception) {
				//// handle-----------
			}
			break;

		// customer nav - accounts
		case "/accounts":
			CustomerServices user = (CustomerServices) request.getSession().getAttribute("user");
			try {
				request.setAttribute("account", user.getAccount());
				if (user.getAllAcc().size() > 1) {
					request.setAttribute("otherAcc", user.getAccounts());
				}
				request.setAttribute("tab", "accounts");
				request.getRequestDispatcher("/WEB-INF/jsp/customer.jsp").forward(request, response);
			} catch (BankingException exception) {
				//handle--------------------------------
			}
			break;

		case "/transaction/withinBank":
			CustomerServices cusServices = (CustomerServices) request.getSession().getAttribute("user");
			boolean pinSet = cusServices.isPinSet();
			if (pinSet) {
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			} else {
				request.getRequestDispatcher("/WEB-INF/jsp/setPin.jsp").forward(request, response);
			}
			break;

		case "/transaction/outsideBank":
			request.setAttribute("type", "outside");
			request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			break;

		// customer nav - statement
		case "/statement":
			CustomerServices cuServices = (CustomerServices) request.getSession().getAttribute("user");
			try {
				JSONArray transactions = cuServices.getAccountStatement();
				System.out.println(transactions);
				request.setAttribute("transactions", transactions);
				request.getRequestDispatcher("/WEB-INF/jsp/statement.jsp").forward(request, response);
			} catch (BankingException e) {

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
				HttpSession session = request.getSession();
				session.setAttribute("user", userServices);
				response.sendRedirect(request.getContextPath() + "/app/accounts");
			}
			break;

		case "/transaction/sendMoney":
			try {
				if (request.getParameter("type").equals("within")) {
					sendMoney(request, false);
					request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
				}
				sendMoney(request, true);
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);
			} catch (BankingException | InvalidInputException exception) {
				request.setAttribute("error", exception.getMessage());
				request.getRequestDispatcher("/WEB-INF/jsp/transaction.jsp").forward(request, response);

			}
			break;

		case "/transaction/setTPIN":
			String tPIN = request.getParameter("tPIN");
			if (tPIN.equals(request.getParameter("confTPIN"))) {
				CustomerServices cServices = (CustomerServices) request.getSession().getAttribute("user");
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
		Transaction transaction = new Transaction();
		transaction.setAccNumber(Long.parseLong(request.getParameter("accNumber")));
		transaction.setAmount(Long.parseLong(request.getParameter("amount")));
		transaction.setDescription(request.getParameter("description"));
		String pin = request.getParameter("tpin");
		if (withinBank) {
			transaction.setiFSC(request.getParameter("iFSC"));
		}
		CustomerServices cusServices = (CustomerServices) request.getSession().getAttribute("user");
		cusServices.transfer(transaction, pin, withinBank);
	}

}
