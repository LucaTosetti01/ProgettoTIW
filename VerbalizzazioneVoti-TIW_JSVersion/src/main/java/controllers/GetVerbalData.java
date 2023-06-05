package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import DAO.CourseDAO;
import DAO.GraduationCallDAO;
import DAO.LecturerDAO;
import DAO.StudentDAO;
import DAO.VerbalDAO;
import beans.Course;
import beans.GraduationCall;
import beans.User;
import beans.Verbal;
import exceptions.GraduationCallDAOException;
import utils.ConnectionHandler;

@WebServlet("/GetVerbalData")
public class GetVerbalData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public GetVerbalData() {
		super();
	}

	@Override
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Integer verbalId = null, callId = null;
		Verbal verbal = null;
		GraduationCall call = null;
		Course course = null;
		User lecturer = null;
		List<User> students = null;

		HttpSession session = request.getSession();
		User lecLogged = (User) session.getAttribute("user");

		try {
			VerbalDAO vDAO = new VerbalDAO(this.connection);
			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
			CourseDAO cDAO = new CourseDAO(this.connection);
			LecturerDAO lDAO = new LecturerDAO(this.connection);
			StudentDAO sDAO = new StudentDAO(this.connection);

			verbalId = Integer.parseInt(request.getParameter("verbalid"));
			// Retrieving the verbal with "verbalId" as ID
			verbal = vDAO.findVerbalById(verbalId);

			callId = verbal.getCallId();

			// Checking if the call associated to the requested verbal is taught by the
			// logged lecturer
			gcDAO.checkIfCourseOfCallIsTaughtByLecturer(callId, lecLogged.getId());

			// Retrieving from the DB all the data associated to the requested verbal
			call = gcDAO.findGraduationCallById(callId);
			course = cDAO.findCourseById(call.getCourseId());
			lecturer = lDAO.findLecturerById(course.getTaughtById());
			students = sDAO.findAllStudentsInVerbalById(verbal.getId());
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

		// Sending back to the client, in the form of a json object, a <String, Object>
		// map containing pairs of <"VariableName", variableObject>, in this case of
		// "student" ,"verbal", "call", "course" and finally "lecturer" objects
		Map<String, Object> mapStringData = new HashMap<>();
		mapStringData.put("students", students);
		mapStringData.put("verbal", verbal);
		mapStringData.put("call", call);
		mapStringData.put("course", course);
		mapStringData.put("lecturer", lecturer);

		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
		String json = gson.toJson(mapStringData);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
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
