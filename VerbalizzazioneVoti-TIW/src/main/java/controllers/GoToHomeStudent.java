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

		// Retrieving from the request coming from other lecturer's pages
		// the error happened in that pages
		String error = (String) request.getAttribute("errorMessage");

		// I use firstTime variable in order to keep track if it is the first time
		// that this servlet was called, so that, if it is, I can set its value
		// to "true" so that the HomeLecturer page won't show "No calls found",
		// which would have no sense to show the first time the user access the page
		// since he hasn't still chose a course
		boolean firstTime = false;

		calls = new ArrayList<>();
		try {
			CourseDAO courseDAO = new CourseDAO(connection);
			StudentDAO sDAO = new StudentDAO(this.connection);
			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
			// Retrieve the courses to which the logged student is subscribed
			coursesStudentSubscribedTo = courseDAO.findAllCoursesByStudentId(student.getId());

			// The courseid parameter will be NULL, and so throw an exception only if
			// it's the first time that the servlet has been called. In that case the
			// exception is ignored and the variable "firstTime" is set to "true".
			// Otherwise is created an error message that will be shown after to the
			// user on the client
			courseId = Integer.parseInt(request.getParameter("courseid"));

			// Checking that the logged student is subscribed to the chosen course
			sDAO.checkIfStudentIsSubscribedToCourse(student.getId(), courseId);

			// Retrieving calls associated to the course that has been chosen by the student
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

		// Sending to the template page, in order to use them within the page itself,
		// calls, courses, errorMessage, firstTime objects
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
			System.out.println("Can't close db connection");
		}
	}
}
