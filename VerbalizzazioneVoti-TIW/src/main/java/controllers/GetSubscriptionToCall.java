package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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

import DAO.GraduationCallDAO;
import DAO.StudentDAO;
import beans.CallEvaluation;
import beans.GraduationCall;
import beans.User;
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

		String orderBy = null;
		boolean orderType;
		String prevOrderBy = null;
		boolean prevOrderType;

		try {
			callId = Integer.parseInt(request.getParameter("callid"));
			orderBy = (request.getParameter("orderBy")) != null ? request.getParameter("orderBy") : "ID";
			orderType = (request.getParameter("orderType")) != null ? Boolean.parseBoolean(request.getParameter("orderType")) : true;
			prevOrderType = request.getParameter("prevOrderType") != null ? Boolean.parseBoolean(request.getParameter("prevOrderType")) : true;
			prevOrderBy = request.getParameter("prevOrderBy");
		} catch (NumberFormatException | NullPointerException e) {
			//request.setAttribute("errore", "ERRORE CO3KIOEFK");
			//request.getRequestDispatcher("/WEB-INF/HomeLecturer.html").forward(request, response);
			//return;
			
			//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			//return;
			String errorPath="/GoToHomeLecturer";
			request.setAttribute("error", "Incorrect param values");
			request.getRequestDispatcher(errorPath).forward(request, response);
			/*ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMessage", "Incorrect param values");
			templateEngine.process(errorPath, ctx, response.getWriter());*/
			return;
		}

		if (orderBy.equals(prevOrderBy)) {
			orderType = !prevOrderType;
		} else {
			orderType = true;		//"ASC"
		}
		
		GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
		StudentDAO sDAO = new StudentDAO(this.connection);

		try {
			studentsWithEvaluations = sDAO.findAllRegistrationsAndEvaluationToCallOrdered(callId, orderBy, orderType ? "ASC" : "DESC");
			/*
			 * studentsWithEvaluations = (orderBy!=null) ?
			 * sDAO.findAllRegistrationsAndEvaluationToCallOrdered(callId, orderBy,
			 * orderType) : sDAO.findAllRegistrationsAndEvaluationToCallOrdered(callId,
			 * orderBy, orderType);
			 */
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in students and their evaluations data extraction");
			return;
		}
		try {
			call = gcDAO.getGraduationCallById(callId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in call's data extraction");
			return;
		}

		List<User> students = studentsWithEvaluations.keySet().stream().toList();
		List<CallEvaluation> callEvaluations = studentsWithEvaluations.values().stream().toList();


		String path = "/WEB-INF/Subscribers.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("students", students);
		ctx.setVariable("call", call);
		ctx.setVariable("evaluations", callEvaluations);
		ctx.setVariable("orderType", orderType);
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
