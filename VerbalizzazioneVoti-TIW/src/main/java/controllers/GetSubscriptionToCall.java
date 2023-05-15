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
		// Local variables initializations
		Integer callId = null;
		Map<User, CallEvaluation> studentsWithEvaluations = null;
		GraduationCall call = null;
		List<User> students = new ArrayList<>();
		List<CallEvaluation> callEvaluations = new ArrayList<>();
		String orderBy = null;
		boolean orderType;
		//I initialize this var at 1, so that if an error occur
		int numberOfVerbalizableMarks;

		HttpSession session = request.getSession();
		User lecLogged = (User) session.getAttribute("user");

		String error = (String) request.getAttribute("errorMessage");
		try {
			CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
			numberOfVerbalizableMarks = ceDAO.getNumberOfVerbalizableMarks();
			//Retrieving query string parameters "callId", "orderBy" and "orderType"
			callId = Integer.parseInt(request.getParameter("callid"));
			String tempOrderBy = request.getParameter("orderBy");
			orderBy = tempOrderBy != null && Arrays
					.asList("ID", "Surname", "Name", "Email", "Username", "DegreeName", "Mark", "EvaluationStatus")
					.contains(tempOrderBy) ? tempOrderBy : "ID";
			orderType = (request.getParameter("orderType") != null) ? Boolean.parseBoolean(request.getParameter("orderType")) : true;

			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
			StudentDAO sDAO = new StudentDAO(this.connection);
			

			//Checking if the query string parameter ("callid") is correct
			gcDAO.checkIfCourseOfCallIsTaughtByLecturer(callId, lecLogged.getId());

			//Retrieving a map of students and their respective evaluations ordered using "orderBy" and "orderType"
			studentsWithEvaluations = sDAO.findAllRegistrationsAndEvaluationToCallOrdered(callId, orderBy,
					orderType ? "ASC" : "DESC");
			call = gcDAO.getGraduationCallById(callId);

			//Splitting the map into 2 different lists but still maintaining the order;
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
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
