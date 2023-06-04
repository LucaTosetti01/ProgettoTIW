package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

	public GetSubscriptionToCall() {
		super();
	}

	public void init() throws ServletException {

		connection = ConnectionHandler.getConnection(getServletContext());
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Local variables initializations
		Integer callId = null;
		Map<User, CallEvaluation> studentsWithEvaluations = null;

		HttpSession session = request.getSession();
		User lecLogged = (User) session.getAttribute("user");

		try {
			// Retrieving query string parameters "callId", "orderBy" and "orderType"
			callId = Integer.parseInt(request.getParameter("callid"));

			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
			StudentDAO sDAO = new StudentDAO(this.connection);

			// Checking if the query string parameter ("callid") is correct
			gcDAO.checkIfCourseOfCallIsTaughtByLecturer(callId, lecLogged.getId());

			// Retrieving a map of students and their respective evaluations ordered using
			// "orderBy" and "orderType"
			studentsWithEvaluations = sDAO.findAllRegistrationsAndEvaluationToCallOrdered(callId, "ID", "ASC");


		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("Incorrect param value");
			return;
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(e.getMessage());
			return;
		} catch (GraduationCallDAOException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(e.getMessage());
			return;
		}

		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
		
		String json = gson.toJson(studentsWithEvaluations);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
