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
import DAO.CourseDAO;
import DAO.GraduationCallDAO;
import DAO.LecturerDAO;
import DAO.StudentDAO;
import beans.CallEvaluation;
import beans.Course;
import beans.GraduationCall;
import beans.User;
import exceptions.CallEvaluationDAOException;
import exceptions.StudentDAOException;
import utils.ConnectionHandler;

@WebServlet("/GetOutcome")
public class GetOutcome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public GetOutcome() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer callId = null;
		User student = null;
		CallEvaluation evaluation = null;
		Course studentCourse = null;
		GraduationCall courseCall = null;
		User courseLecturer = null;

		HttpSession session = request.getSession();
		User studLogged = (User) session.getAttribute("user");
		try {
			StudentDAO sDAO = new StudentDAO(this.connection);
			CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
			CourseDAO cDAO = new CourseDAO(this.connection);
			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
			LecturerDAO lDAO = new LecturerDAO(this.connection);

			callId = Integer.parseInt(request.getParameter("callid"));

			// Checking if the student is subscribed to the call of which he has requested
			// the outcome
			sDAO.checkIfStudentIsSubscribedToCall(studLogged.getId(), callId);

			// Checking if the student could effectively request the outcome of the call
			// (this can happen
			// only if the evaluationState of the mark is "Verbalizzato" || "Rifiutato" ||
			// "Pubblicato",
			// which are the same condition to check if the student's mark is updatable by a
			// lecturer)
			ceDAO.checkIfOutcomeCanBeRequested(studLogged.getId(), callId);

			// Retrieving all the data necessary to show the student's outcome of the call
			student = sDAO.findStudentById(studLogged.getId());
			evaluation = ceDAO.findEvaluationByCallAndStudentId(callId, studLogged.getId());
			courseCall = gcDAO.findGraduationCallById(evaluation.getCall_id());
			studentCourse = cDAO.findCourseById(courseCall.getCourseId());
			courseLecturer = lDAO.findLecturerById(studentCourse.getTaughtById());
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("Incorrect param value");
			return;
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(e.getMessage());
			return;
		} catch (CallEvaluationDAOException | StudentDAOException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(e.getMessage());
			return;
		}

		// Sending back to the client, in the form of a json object, a <String, Object>
		// map containing pairs of <"VariableName", variableObject>, in this case of
		// "student", "evaluation", "call", "course" and finally "lecturer" object
		Map<String, Object> mapStringData = new HashMap<>();
		mapStringData.put("student", student);
		mapStringData.put("evaluation", evaluation);
		mapStringData.put("call", courseCall);
		mapStringData.put("course", studentCourse);
		mapStringData.put("lecturer", courseLecturer);

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
