package filters;

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

import beans.Student;

public class StudentChecker implements Filter{
	public StudentChecker() {

	}

	public void destroy() {

	}

	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.print("Student filter executing ..\n");
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String loginpath = req.getServletContext().getContextPath() + "/index.html";

		HttpSession s = req.getSession();
		Student u = null;
		String role = null;
		u = (Student) s.getAttribute("user");
		role = (String) s.getAttribute("role");
		//If the session's attribute "role" is different from "student" then go back to the login page
		if (!role.equals("student")) {
			res.sendRedirect(loginpath);
			return;
		}
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}
	
}
