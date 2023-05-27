package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
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
}
