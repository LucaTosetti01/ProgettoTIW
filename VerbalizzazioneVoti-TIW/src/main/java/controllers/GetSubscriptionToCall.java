package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import beans.GraduationCall;
import beans.User;
import exceptions.GraduationCallDAOException;
import utils.ConnectionHandler;

@WebServlet("/GetSubscriptionToCall")
public class GetSubscriptionToCall extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

	public GetSubscriptionToCall() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");

		connection = ConnectionHandler.getConnection(getServletContext());
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer callId = null;
		Map<User, CallEvaluation> studentsWithEvaluations = null;
		GraduationCall call = null;
		List<User> students = new ArrayList<>();
		List<CallEvaluation> callEvaluations = new ArrayList<>();
		String orderBy = null;
		boolean orderType;

		int numberOfVerbalizableMarks, numberOfPublishableMarks;

		HttpSession session = request.getSession();
		User lecLogged = (User) session.getAttribute("user");

		String error = (String) request.getAttribute("errorMessage");
		try {
			CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
			StudentDAO sDAO = new StudentDAO(this.connection);

			callId = Integer.parseInt(request.getParameter("callid"));

			// Getting the number of verbalizable and publishable marks in order
			// to send them to the template and decide if the verbalize and publish
			// button have to be disabled or not
			numberOfVerbalizableMarks = ceDAO.getNumberOfVerbalizableMarks(callId);
			numberOfPublishableMarks = ceDAO.getNumberOfPublishableMarks(callId);

			// Retrieving from the request the parameter "orderBy" and checking if
			// it has a valid value, in that case it is passed to the template
			String orderByToCheck = request.getParameter("orderBy");
			orderBy = orderByToCheck != null && Arrays
					.asList("ID", "Surname", "Name", "Email", "Username", "DegreeName", "Mark", "EvaluationStatus")
					.contains(orderByToCheck) ? orderByToCheck : "ID";

			// Retrieving from the request the parameter "orderType" and checking if
			// it has a valid value, in that case it is passed to the template
			orderType = (request.getParameter("orderType") != null)
					? Boolean.parseBoolean(request.getParameter("orderType"))
					: true;

			// Checking if the course associated to the call with "callId" as ID is taught
			// by the logged lecturer
			gcDAO.checkIfCourseOfCallIsTaughtByLecturer(callId, lecLogged.getId());

			// Retrieving a map of students and their respective evaluations ordered using
			// as ORDER BY parameter the value of the orderBy variable previously checked,
			// and as order type "ASC" if orderType variable is true, otherwise "DESC"
			// In this way i get the map of students e their evaluations already ordered as
			// requested by the user at client side
			studentsWithEvaluations = sDAO.findAllRegistrationsAndEvaluationToCallOrdered(callId, orderBy,
					orderType ? "ASC" : "DESC");
			// Retrieving the call's data chosen by the user or saved in the subscriptions
			// page in order to implement the table sorting
			call = gcDAO.findGraduationCallById(callId);

			// Splitting the map into 2 different lists but still maintaining the order
			// (This operation is not necessary but i decided to split data into two list
			// but for my convenience in order to simplify their use on the server side)
			students = studentsWithEvaluations.keySet().stream().toList();
			callEvaluations = studentsWithEvaluations.values().stream().toList();
		} catch (NumberFormatException | NullPointerException e) {
			String errorPath = "/GoToHomeLecturer";
			request.setAttribute("errorMessage", "Incorrect param values");
			request.getRequestDispatcher(errorPath).forward(request, response);
			return;
		} catch (SQLException e) {
			String errorPath = "/GoToHomeLecturer";
			request.setAttribute("errorMessage", e.getMessage());
			request.getRequestDispatcher(errorPath).forward(request, response);
			return;
		} catch (GraduationCallDAOException e) {
			String errorPath = "/GoToHomeLecturer";
			request.setAttribute("errorMessage", e.getMessage());
			request.getRequestDispatcher(errorPath).forward(request, response);
			return;
		}

		// Sending to the template page, in order to use them within the page itself,
		// students, call, callEvaluation, orderBy, orderType, errorMessage, numberOfVerbalizableMarks
		// and finally numberOfPublishableMarks objects
		String path = "/WEB-INF/Subscribers.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("students", students);
		ctx.setVariable("call", call);
		ctx.setVariable("evaluations", callEvaluations);
		ctx.setVariable("orderBy", orderBy);
		ctx.setVariable("orderType", orderType);
		ctx.setVariable("errorMessage", error);
		ctx.setVariable("numberOfVerbalizableMarks", numberOfVerbalizableMarks);
		ctx.setVariable("numberOfPublishableMarks", numberOfPublishableMarks);
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
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
