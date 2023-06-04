package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import DAO.CourseDAO;
import DAO.StudentDAO;
import beans.Course;
import beans.User;
import exceptions.StudentDAOException;
import utils.ConnectionHandler;

@WebServlet("/GetStudentCourses")
public class GetStudentCourses extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public GetStudentCourses() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Course> coursesStudentSubscribedTo = null;

		HttpSession session = request.getSession();
		User student = (User) session.getAttribute("user");

		try {
			CourseDAO courseDAO = new CourseDAO(connection);
			StudentDAO sDAO = new StudentDAO(this.connection);

			// Retrieve the courses to which the logged student is subscribed
			coursesStudentSubscribedTo = courseDAO.findAllCoursesByStudentId(student.getId());
			// Check for further security that the logged student is subscribed to each
			// course found (not really necessary, but for more security)
			for (Course c : coursesStudentSubscribedTo) {
				sDAO.checkIfStudentIsSubscribedToCourse(student.getId(), c.getId());
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(e.getMessage());
			return;
		} catch (StudentDAOException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(e.getMessage());
			return;
		}

		// Sending back to the client, in the form of a json object, the courses to
		// which the logged student is subscribed
		String json = new Gson().toJson(coursesStudentSubscribedTo);
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
