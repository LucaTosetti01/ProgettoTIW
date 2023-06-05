package controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public Logout() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Getting the current active session (not creating a new one if it's not existing)
		HttpSession session = request.getSession(false);
		if (session != null) {
			//Invalidate the current session if it's not already null
			session.invalidate();
		}
		//Redirect to the login page
		String loginPath = getServletContext().getContextPath() + "/index.html";
		response.sendRedirect(loginPath);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
