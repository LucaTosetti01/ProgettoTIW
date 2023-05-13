package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

import DAO.CourseDAO;
import DAO.GraduationCallDAO;
import DAO.StudentDAO;
import beans.Course;
import beans.GraduationCall;
import beans.User;
import exceptions.StudentDAOException;
import utils.ConnectionHandler;

@WebServlet("/GoToHomeStudent")
public class GoToHomeStudent extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;

	public GoToHomeStudent() {
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
		Integer courseId = null;
		List<GraduationCall> calls = null;
		List<Course> coursesStudentSubscribedTo = null;

		HttpSession session = request.getSession();
		User student = (User) session.getAttribute("user");
		String error = (String) request.getAttribute("errorMessage");
		boolean firstTime = false;

		calls = new ArrayList<>();
		try {
			// Retrieving courses to which student is subscribed
			CourseDAO courseDAO = new CourseDAO(connection);
			coursesStudentSubscribedTo = courseDAO.findAllCoursesByStudent(student.getId());

			// Retrieving query string parameter "courseid"
			courseId = Integer.parseInt(request.getParameter("courseid"));
			//Checking if the query string parameter is correct
			StudentDAO sDAO = new StudentDAO(this.connection);
			sDAO.checkIfStudentIsSubscribedToCourse(student.getId(), courseId);

			//Retrieving calls associated to the course that has been chosen by the student
			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
			calls = gcDAO.findAllDegreeCallWhichStudentSubscribedToByCourseId(student.getId(), courseId);
		} catch (NumberFormatException | NullPointerException e) {
			if (request.getParameter("courseid") != null) {
				error = "Incorrect param values";
			} else {
				firstTime = true;
			}
		} catch (SQLException e) {
			error = e.getMessage();
		} catch (StudentDAOException e) {
			error = e.getMessage();
		}

		String path = "/WEB-INF/HomeStudent.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("calls", calls);
		ctx.setVariable("courses", coursesStudentSubscribedTo);
		ctx.setVariable("errorMessage", error);
		ctx.setVariable("firstTime", firstTime);
		templateEngine.process(path, ctx, response.getWriter());
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
