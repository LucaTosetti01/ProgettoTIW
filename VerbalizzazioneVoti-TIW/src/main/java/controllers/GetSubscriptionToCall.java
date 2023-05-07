package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
		String orderType = null;
		HttpSession session = request.getSession();
		String prevOrderBy = null;
		String prevOrderType = null;

		prevOrderBy = (String) session.getAttribute("prevOrderBy");
		prevOrderType = (String) session.getAttribute("prevOrderType");
		try {
			callId = Integer.parseInt(request.getParameter("callid"));
			orderBy = (request.getParameter("orderBy")) != null ? request.getParameter("orderBy") : "ID";
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		if (orderBy.equals(prevOrderBy)) {
			if (prevOrderType.equals("ASC")) {
				orderType = "DESC";
			} else {
				orderType = "ASC";
			}
		} else {
			orderType = "ASC";
		}

		GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
		StudentDAO sDAO = new StudentDAO(this.connection);

		try {
			studentsWithEvaluations = sDAO.findAllRegistrationsAndEvaluationToCallOrdered(callId, orderBy, orderType);
			/*
			 * studentsWithEvaluations = (orderBy!=null) ?
			 * sDAO.findAllRegistrationsAndEvaluationToCallOrdered(callId, orderBy,
			 * orderType) : sDAO.findAllRegistrationsAndEvaluationToCallOrdered(callId,
			 * orderBy, orderType);
			 */
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in call's students extraction");
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
		session.setAttribute("prevOrderBy", orderBy);
		session.setAttribute("prevOrderType", orderType);

		String path = "/WEB-INF/Subscribers.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("students", students);
		ctx.setVariable("call", call);
		ctx.setVariable("evaluations", callEvaluations);
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
