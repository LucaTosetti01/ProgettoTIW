package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
import beans.User;
import exceptions.CallEvaluationDAOException;
import exceptions.GraduationCallDAOException;
import exceptions.StudentDAOException;
import utils.ConnectionHandler;

@WebServlet("/GetMarkManagement")
public class GetMarkManagement extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public GetMarkManagement() {
		super();
	}

	public void init() throws ServletException {
		this.connection = ConnectionHandler.getConnection(getServletContext());
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer studentId = null, callId = null;
		User student = null;
		CallEvaluation evaluation = null;

		HttpSession session = request.getSession();
		User lecLogged = (User) session.getAttribute("user");

		try {
			StudentDAO sDAO = new StudentDAO(this.connection);
			CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);

			studentId = Integer.parseInt(request.getParameter("studentid"));
			callId = Integer.parseInt(request.getParameter("callid"));

			// Checking if the course associated to the call with "callId" as ID is taught
			// by the logged lecturer
			// Checking if the logged student is subscribed to the call with "callId" as ID
			// and is subscribed to the course associated to the "callId" call
			// Checking if the student's mark is updatable (if its state is not
			// "Verbalizzato" || "Rifiutato" || "Pubblicato"
			gcDAO.checkIfCourseOfCallIsTaughtByLecturer(callId, lecLogged.getId());
			sDAO.checkIfStudentIsSubscribedToCall(studentId, callId);
			ceDAO.checkIfStudentMarkIsUpdatable(studentId, callId);

			// Retrieving from the DB the student that has as ID the "studentId" variable's
			// value
			student = sDAO.findStudentById(studentId);
			// Retrieving from the DB the evaluation of the chosen student associated to the
			// chosen call
			evaluation = ceDAO.findEvaluationByCallAndStudentId(callId, studentId);
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("Incorrect param values");
			return;
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(e.getMessage());
			return;
		} catch (StudentDAOException | GraduationCallDAOException | CallEvaluationDAOException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(e.getMessage());
			return;
		}

		// Sending back to the client, in the form of a json object, a <String, Object>
		// map containing pairs of <"VariableName", variableObject>, in this case of
		// "student" and "evaluation" objects
		Map<String, Object> mapStringData = new HashMap<>();
		mapStringData.put("student", student);
		mapStringData.put("evaluation", evaluation);

		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
		String json = gson.toJson(mapStringData);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
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
