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
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import DAO.CallEvaluationDAO;
import DAO.GraduationCallDAO;
import DAO.StudentDAO;
import beans.CallEvaluation;
import beans.User;
import exceptions.CallEvaluationDAOException;
import exceptions.GraduationCallDAOException;
import exceptions.StudentDAOException;
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

		HttpSession session = request.getSession();
		User lecLogged = (User) session.getAttribute("user");
		
		String error = (String) request.getAttribute("errorMessage");
		try {
			studentId = Integer.parseInt(request.getParameter("studentid"));
			callId = Integer.parseInt(request.getParameter("callid"));

			StudentDAO sDAO = new StudentDAO(this.connection);
			CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
			// Checking if the student with "studentid" is subscribed to the call with
			// "callid", if the student is subscribed to the course associated to the call
			// and finally if the course is taught by the lecturer logged
			gcDAO.checkIfCourseOfCallIsTaughtByLecturer(callId, lecLogged.getId());
			sDAO.checkIfStudentIsSubscribedToCall(studentId, callId);
			ceDAO.checkIfStudentMarkIsUpdatable(studentId, callId);

			student = sDAO.findStudentById(studentId);

			evaluation = ceDAO.findEvaluationByCallAndStudentId(callId, studentId);
		} catch (NumberFormatException | NullPointerException e) {
			String errorPath = "/GetSubscriptionToCall";
			request.setAttribute("errorMessage", "Incorrect param values");
			request.getRequestDispatcher(errorPath).forward(request, response);
			return;
		} catch (SQLException | StudentDAOException | GraduationCallDAOException | CallEvaluationDAOException e) {
			String errorPath = "/GetSubscriptionToCall";
			request.setAttribute("errorMessage", e.getMessage());
			request.getRequestDispatcher(errorPath).forward(request, response);
			return;
		}

		String path = "WEB-INF/MarkManagement.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("student", student);
		ctx.setVariable("evaluation", evaluation);
		ctx.setVariable("errorMessage", error);
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
