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

public class CredentialsChecker implements Filter {

	public CredentialsChecker() {

	}

	public void destroy() {

	}

	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.print("Login checker filter executing ...\n");

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String loginpath = req.getServletContext().getContextPath() + "/index.html";

		HttpSession s = req.getSession();
		// If the session is just created or his attribute "user" is null (for any
		// reason) go back to the login page
		if (s.isNew() || s.getAttribute("user") == null) {
			res.setStatus(403);
			res.setHeader("Location", loginpath);
			System.out.println("Login checker FAILED...");
			return;
		}
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

}
