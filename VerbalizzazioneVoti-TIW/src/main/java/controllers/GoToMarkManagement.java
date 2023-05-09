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

import DAO.CallEvaluationDAO;
import DAO.StudentDAO;
import beans.CallEvaluation;
import beans.User;
import utils.ConnectionHandler;

@WebServlet("/GoToMarkManagement")
public class GoToMarkManagement extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

	public GoToMarkManagement() {
		super();
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

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer studentId = null, callId = null;
		User student = null;
		CallEvaluation evaluation = null;

		try {
			studentId = Integer.parseInt(request.getParameter("studentid"));
			callId = Integer.parseInt(request.getParameter("callid"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		StudentDAO sDAO = new StudentDAO(this.connection);
		CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
		try {
			student = sDAO.findStudentById(studentId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in student's data extraction");
			return;
		}
		try {
			evaluation = ceDAO.findEvaluationByCallAndStudentId(callId, studentId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in student's evaluation data extraction");
			return;
		}

		String path = "WEB-INF/MarkManagement.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("student", student);
		ctx.setVariable("evaluation", evaluation);
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
