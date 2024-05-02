package filter;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.bank.services.AdminServices;
import com.bank.services.CustomerServices;
import com.bank.services.EmployeeServices;

public class APIFilter implements Filter{
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	
	private void setObject(HttpServletRequest request) {
		String level = request.getHeader("authorizationLevel");
		Long userId = Long.parseLong(request.getHeader("userId"));
		if (level != null && userId != null) {
			if (level.equals("1")) {
				CustomerServices user = new CustomerServices();
				user.setUserId(userId);
				request.getSession().setAttribute("user", user);
			} else if (level.equals("2")) {
				EmployeeServices user = new EmployeeServices();
				user.setUserId(userId);
				request.getSession().setAttribute("user", user);
			} else {
				AdminServices user = new AdminServices();
				user.setUserId(userId);
				request.getSession().setAttribute("user", user);
				System.out.println("here---------------------------");
			}
		}
	}

	

}
