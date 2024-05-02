package filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bank.exceptions.BankingException;
import com.bank.services.AdminServices;
import com.bank.services.AuthServices;
import com.bank.services.CustomerServices;
import com.bank.services.EmployeeServices;

public class ApplicationFilter implements Filter {

//	private static Logger logger = LogHandler.getLogger(ApplicationFilter.class.getName(), "FilterLogs.txt");

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

		setNoCache(res);

		String path = req.getPathInfo();

//		System.out.println(path);

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
			if (validateSession(req, res)) {
				chain.doFilter(request, response);
			}
		}

	}

	private void setNoCache(HttpServletResponse httpResponse) {
		httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		httpResponse.setHeader("Pragma", "no-cache");
		httpResponse.setDateHeader("Expires", 0);
	}

	private boolean validateSession(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute("user");
		boolean validUser = false;
		if (obj == null) {
			request.setAttribute("errorMessage", "Invalid session : Login to continue");
			request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
		}
		try {
			validUser = AuthServices.isValidUser(getUserId(obj));
		} catch (BankingException e) {
		}
		if (!validUser) {
			request.setAttribute("errorMessage", "User Blocked : Contact BOB authorities to regain access");
			request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
		}
		return true;
	}

	private long getUserId(Object obj) {
		if (obj instanceof AdminServices) {
			return ((AdminServices) obj).getUserId();
		} else if (obj instanceof EmployeeServices) {
			return ((EmployeeServices) obj).getUserId();
		} else {
			return ((CustomerServices) obj).getUserId();
		}
	}

}