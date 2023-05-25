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
import beans.Course;
import beans.GraduationCall;
import beans.User;
import exceptions.CourseDAOException;
import exceptions.StudentDAOException;
import utils.ConnectionHandler;

@WebServlet("/GetCoursesCalls")
public class GetCoursesCalls extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//private TemplateEngine templateEngine;
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
			//Retrieving courses taught by the lecturer
			CourseDAO courseDAO = new CourseDAO(connection);
			StudentDAO sDAO = new StudentDAO(this.connection);
			//Retrieving query string parameter "courseid"
			courseId = Integer.parseInt(request.getParameter("courseid"));
			//Checking if the query string parameter is correct
			if(user.getRole().equals("Lecturer")) {
				courseDAO.checkIfCourseIsTaughtByLecturer(courseId, user.getId());
			} else {
				sDAO.checkIfStudentIsSubscribedToCourse(user.getId(),courseId);
			}
			
			
			//Retrieving calls associated to the course that the lecturer chose
			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
			calls = gcDAO.findAllDegreeCallByCourseId(courseId);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(e.getMessage());
			return;
		} catch (CourseDAOException | StudentDAOException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(e.getMessage());
			return;
		}

		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
