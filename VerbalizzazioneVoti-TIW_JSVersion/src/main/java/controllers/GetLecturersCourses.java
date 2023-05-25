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
import beans.Course;
import beans.GraduationCall;
import beans.User;
import exceptions.CourseDAOException;
import utils.ConnectionHandler;

@WebServlet("/GetLecturersCourses")
public class GetLecturersCourses extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public GetLecturersCourses() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Course> coursesTaughtByLec = null;
		
		HttpSession session = request.getSession();
		User lec = (User) session.getAttribute("user");

		coursesTaughtByLec = new ArrayList<>();
		try {
			//Retrieving courses taught by the lecturer
			CourseDAO courseDAO = new CourseDAO(connection);
			coursesTaughtByLec = courseDAO.findAllCoursesByLecturer(lec.getId());

			for(Course c : coursesTaughtByLec) {
				courseDAO.checkIfCourseIsTaughtByLecturer(c.getId(), lec.getId());
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(e.getMessage());
			return;
		} catch (CourseDAOException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(e.getMessage());
			return;
		}

		String json = new Gson().toJson(coursesTaughtByLec);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
