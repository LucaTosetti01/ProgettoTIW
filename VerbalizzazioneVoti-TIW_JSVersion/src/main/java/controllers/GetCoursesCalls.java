package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import DAO.CourseDAO;
import DAO.GraduationCallDAO;
import DAO.StudentDAO;
import beans.GraduationCall;
import beans.User;
import exceptions.CourseDAOException;
import exceptions.StudentDAOException;
import utils.ConnectionHandler;

@WebServlet("/GetCoursesCalls")
public class GetCoursesCalls extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// private TemplateEngine templateEngine;
	private Connection connection;

	public GetCoursesCalls() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer courseId = null;
		List<GraduationCall> calls = null;

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		calls = new ArrayList<>();
		try {
			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
			CourseDAO courseDAO = new CourseDAO(connection);
			StudentDAO sDAO = new StudentDAO(this.connection);

			courseId = Integer.parseInt(request.getParameter("courseid"));
			// If user role is "Lecturer", I check if the course, whose id was retrieved as
			// request parameter, is taught by the lecturer
			// If user role is "Student", I check if student is subscribed to the course,
			// whose id was retrieved as request parameter
			// If user role is different from "Lecturer" or "Student", then something went
			// wrong and I disconnect the current user redirecting the request to /Logout
			// servlet
			if (user.getRole().equals("Lecturer")) {
				courseDAO.checkIfCourseIsTaughtByLecturer(courseId, user.getId());
				// Retrieving calls associated to the course whose id was sent as request
				// parameter
				calls = gcDAO.findAllDegreeCallByCourseId(courseId);
			} else if (user.getRole().equals("Student")) {
				sDAO.checkIfStudentIsSubscribedToCourse(user.getId(), courseId);
				// Retrieving calls associated to the course whose id was sent as request
				// parameter and to which the logged student is subscribed
				calls = gcDAO.findAllDegreeCallWhichStudentSubscribedToByCourseId(user.getId(),courseId);
			} else {
				String loginpath = request.getServletContext().getContextPath() + "/Logout";
				response.sendRedirect(loginpath);
				return;
			}

			
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("Incorrect param value");
			return;
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(e.getMessage());
			return;
		} catch (CourseDAOException | StudentDAOException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(e.getMessage());
			return;
		}

		// Sending back to the client, in the form of a json object, the calls
		// associated to the course with |courseId| as ID
		String json = new Gson().toJson(calls);
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
