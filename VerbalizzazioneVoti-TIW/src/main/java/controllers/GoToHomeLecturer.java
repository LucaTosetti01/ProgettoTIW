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
import beans.Course;
import beans.GraduationCall;
import beans.Lecturer;
import utils.ConnectionHandler;

@WebServlet("/GoToHomeLecturer")
public class GoToHomeLecturer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;

	public GoToHomeLecturer() {
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
		List<Course> coursesTaughtByLec = null;

		

		try {
			courseId = Integer.parseInt(request.getParameter("courseid"));

			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
			calls = gcDAO.findAllDegreeCallByCourseId(courseId);

			
		} catch (NumberFormatException | NullPointerException e) {
			calls = new ArrayList<>();
			String requestURL = request.getRequestURI().toString();
			if (requestURL.equals("VerbalizzazioneVoti-TIW/CheckLogin")) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
				return;
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in course's calls extraction");
			return;
		}

		HttpSession session = request.getSession();
		Lecturer lec = (Lecturer) session.getAttribute("user");
		CourseDAO courseDAO = new CourseDAO(connection);

		try {
			coursesTaughtByLec = courseDAO.findAllCoursesByLecturer(lec.getId());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in lecturer's courses extraction");
			return;
		}

		String path = "/WEB-INF/HomeLecturer.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("calls", calls);
		ctx.setVariable("courses", coursesTaughtByLec);
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
