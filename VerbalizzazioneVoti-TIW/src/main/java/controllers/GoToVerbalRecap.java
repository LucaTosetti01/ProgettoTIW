package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import DAO.CourseDAO;
import DAO.GraduationCallDAO;
import DAO.LecturerDAO;
import DAO.StudentDAO;
import DAO.VerbalDAO;
import beans.Course;
import beans.GraduationCall;
import beans.User;
import beans.Verbal;
import utils.ConnectionHandler;

@WebServlet("/GoToVerbalRecap")
public class GoToVerbalRecap extends HttpServlet{
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public GoToVerbalRecap() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer verbalId = null;
		Verbal verbal = null;
		GraduationCall call = null;
		Course course = null;
		User lecturer = null;
		List<User> students = null;
		
		try {
			verbalId = Integer.parseInt(request.getParameter("verbalid"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
		VerbalDAO vDAO = new VerbalDAO(this.connection);
		GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
		CourseDAO cDAO = new CourseDAO(this.connection);
		LecturerDAO lDAO = new LecturerDAO(this.connection);
		StudentDAO sDAO = new StudentDAO(this.connection);
		try {
			verbal = vDAO.getVerbalById(verbalId);
			call = gcDAO.getGraduationCallById(verbal.getCallId());
			course = cDAO.findCourseById(call.getCourseId());
			lecturer = lDAO.findLecturerById(course.getTaughtById());
			students = sDAO.findAllRegistrationsToTheCall(call.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String path = "/WEB-INF/Verbal.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("verbal", verbal);
		ctx.setVariable("call", call);
		ctx.setVariable("course", course);
		ctx.setVariable("lecturer", lecturer);
		ctx.setVariable("students", students);
		templateEngine.process(path, ctx, response.getWriter());
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

	
}
