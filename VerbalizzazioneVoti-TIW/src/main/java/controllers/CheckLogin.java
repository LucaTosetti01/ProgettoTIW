package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import DAO.UserDAO;
import beans.User;
import utils.ConnectionHandler;

@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public CheckLogin() {
		super();
	}

	public CheckLogin(Connection connection) {
		this.connection = connection;
	}

	public void init() throws ServletException {
		this.connection = ConnectionHandler.getConnection(getServletContext());

		// Necessary for processing the errorMessage in case login fail.
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		User user = null;
		
		UserDAO uDAO = new UserDAO(connection);
		
		String usern = request.getParameter("username");
		String passw = request.getParameter("pwd");
		String redirectionPath;
		if (usern == null || usern.isEmpty() || passw == null || passw.isEmpty()) {
			redirectionPath = "/index.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMessage", "Missing parameters");
			this.templateEngine.process(redirectionPath, ctx, response.getWriter());
			return;
		}

		try {
			user = uDAO.checkCredentials(usern, passw);
		} catch (SQLException e) {
			if (request.getSession(false) != null) {
				request.getSession().invalidate();
			}
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in db credentials checking");
		}

		redirectionPath = getServletContext().getContextPath();
		if (user == null) {
			redirectionPath = "/index.html";

			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMessage", "Username or Password are incorrect, please retry");
			this.templateEngine.process(redirectionPath, ctx, response.getWriter());
		} else {
			String target = (user.getRole().equals("Student")) ? "/GoToHomeStudent" : "/GoToHomeLecturer";
			request.getSession().setAttribute("user", user);
			redirectionPath += target;

			response.sendRedirect(redirectionPath);
		}

	}

	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println("Can't close db connection");
		}
	}
}
