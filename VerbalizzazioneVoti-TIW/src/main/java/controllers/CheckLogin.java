package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import DAO.LecturerDAO;
import DAO.StudentDAO;
import beans.Lecturer;
import beans.Student;
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
		String usern = request.getParameter("username");
		String passw = request.getParameter("pwd");

		if (usern == null || usern.isEmpty() || passw == null || passw.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}

		StudentDAO sDAO = new StudentDAO(connection);
		LecturerDAO lDAO = new LecturerDAO(connection);
		Student stud = null;
		Lecturer lect = null;

		try {
			stud = sDAO.checkCredentials(usern, passw);
			lect = lDAO.checkCredentials(usern, passw);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in db credentials checking");
		}

		String redirectionPath = getServletContext().getContextPath();
		if (stud == null && lect == null) {
			redirectionPath = "/index.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMessage", "Username or Password are incorrect, please retry");
			this.templateEngine.process(redirectionPath, ctx, response.getWriter());
		} else {
			String target = (stud != null) ? "/GoToHomeStudent" : "/GoToHomeLecturer";
			request.getSession().setAttribute("user", (stud != null) ? stud : lect);
			request.getSession().setAttribute("role", (stud != null) ? "student" : "lecturer");
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
