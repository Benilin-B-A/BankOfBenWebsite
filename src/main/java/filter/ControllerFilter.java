package filter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bank.services.AdminServices;
import com.bank.services.CustomerServices;
import com.bank.services.EmployeeServices;
import com.bank.util.LogHandler;

public class ControllerFilter implements Filter {

	private static Logger logger = LogHandler.getLogger(ControllerFilter.class.getName(), "FilterLogs.txt");

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		System.out.println(req.getRequestURL());
		
		setNoCache(res);

		String path = req.getPathInfo();

		switch (path) {

		case "/login":
			chain.doFilter(request, response);
			break;

		case "/changePassword":
			if (validateSession(req, res)) {
				if ("POST".equals(req.getMethod())) {
					String newPass = req.getParameter("newPassword");
					String reNewPass = req.getParameter("reNewPassword");
					String oldPass = req.getParameter("oldPassword");
					if (newPass.equals(reNewPass)) {
						if (!newPass.equals(oldPass)) {
							chain.doFilter(req, res);
						} else {
							req.setAttribute("credentialType", "password");
							req.setAttribute("errorMessage", "New password cannot be the same as old password");
							req.getRequestDispatcher("/WEB-INF/jsp/changeCredential.jsp").forward(req, res);
						}
					} else {
						req.setAttribute("credentialType", "password");
						req.setAttribute("errorMessage", "Entered passwords don't match");
						req.getRequestDispatcher("/WEB-INF/jsp/changeCredential.jsp").forward(req, res);
					}
				} else {
					chain.doFilter(req, res);
				}
			}
			break;

		case "/changePIN":
			if (validateSession(req, res)) {
				if ("POST".equals(req.getMethod())) {
					String newPin = req.getParameter("newPin");
					String reNewPin = req.getParameter("reNewPin");
					String oldPin = req.getParameter("oldPin");
					if (newPin.equals(reNewPin)) {
						if (!newPin.equals(oldPin)) {
							chain.doFilter(req, res);
						} else {
							req.setAttribute("credentialType", "pin");
							req.setAttribute("errorMessage", "New pin cannot be the same as old pin");
							req.getRequestDispatcher("/WEB-INF/jsp/changeCredential.jsp").forward(req, res);
						}
					} else {
						req.setAttribute("credentialType", "pin");
						req.setAttribute("errorMessage", "Entered pin doesn't match");
						req.getRequestDispatcher("/WEB-INF/jsp/changeCredential.jsp").forward(req, res);
					}
				} else {
					chain.doFilter(req, res);
				}
			}
			break;

		case "/setPin":
			if (validateSession(req, res)) {
				String newPin = request.getParameter("newPin");
				String reNewPin = request.getParameter("reNewPin");
				if (newPin.equals(reNewPin)) {
					chain.doFilter(request, response);
				} else {
					req.setAttribute("errorMessage", "Entered pin doesn't match");
					req.getRequestDispatcher("/WEB-INF/jsp/setPin.jsp").forward(req, res);
				}
			}
			break;

		default:
			String auth = req.getHeader("authenticationKey");
			System.out.println(auth);
			if (auth != null) {
				if (auth.equals("1234567890")) {
					setObject(req);
					req.setAttribute("type", "api");
					chain.doFilter(request, response);
				}
			} else if (validateSession(req, res)) {
				chain.doFilter(request, response);
			}
		}

	}

	private void setNoCache(HttpServletResponse httpResponse) {
		httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		httpResponse.setHeader("Pragma", "no-cache");
		httpResponse.setDateHeader("Expires", 0);
	}

	private boolean validateSession(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute("user");
		if (obj == null) {
			request.setAttribute("errorMessage", "Invalid session : Login to continue");
			try {
				request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
			} catch (ServletException | IOException exception) {
				logger.log(Level.SEVERE, "Error in validating session", exception);
			}
			return false;
		}
		return true;
	}

	private void setObject(HttpServletRequest request) {
		String level = request.getHeader("authorizationLevel");
		Long userId = Long.parseLong(request.getHeader("userId"));
		if (level != null && userId != null) {
			if (level.equals("1")) {
				CustomerServices user = new CustomerServices();
				user.setUserId(userId);
				request.getSession().setAttribute("user",user);
			} else if (level.equals("2")) {
				EmployeeServices user = new EmployeeServices();
				user.setUserId(userId);
				request.getSession().setAttribute("user",user);
			} else {
				AdminServices user = new AdminServices();
				user.setUserId(userId);
				request.getSession().setAttribute("user",user);
				System.out.println("here---------------------------");
			}
		}
	}

}