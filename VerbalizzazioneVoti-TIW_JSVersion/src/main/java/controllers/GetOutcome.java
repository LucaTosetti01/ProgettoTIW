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

import DAO.CallEvaluationDAO;
import DAO.CourseDAO;
import DAO.GraduationCallDAO;
import DAO.LecturerDAO;
import DAO.StudentDAO;
import beans.CallEvaluation;
import beans.Course;
import beans.GraduationCall;
import beans.User;
import exceptions.StudentDAOException;
import utils.ConnectionHandler;

@WebServlet("/GetOutcome")
public class GetOutcome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public GetOutcome() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());

		
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer callId = null;
		User student = null;
		CallEvaluation evaluation = null;
		Course studentCourse = null;
		GraduationCall courseCall = null;
		User courseLecturer = null;
		boolean actualNumber = false;

		HttpSession session = request.getSession();
		User studLogged = (User) session.getAttribute("user");
		try {
			callId = Integer.parseInt(request.getParameter("callid"));
		
			StudentDAO sDAO = new StudentDAO(this.connection);
			CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
			CourseDAO cDAO = new CourseDAO(this.connection);
			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
			LecturerDAO lDAO = new LecturerDAO(this.connection);
			
			sDAO.checkIfStudentIsSubscribedToCall(studLogged.getId(), callId);
			
			student = sDAO.findStudentById(studLogged.getId());
			evaluation = ceDAO.findEvaluationByCallAndStudentId(callId, studLogged.getId());
			courseCall = gcDAO.findGraduationCallById(evaluation.getCall_id());
			studentCourse = cDAO.findCourseById(courseCall.getCourseId());
			courseLecturer = lDAO.findLecturerById(studentCourse.getTaughtById());
			
		} catch (NumberFormatException | NullPointerException e) {
			String errorPath = "/GoToHomeStudent";
			request.setAttribute("errorMessage", "Incorrect param values");
			request.getRequestDispatcher(errorPath).forward(request, response);
			return;
		} catch (SQLException | StudentDAOException e) {
			String errorPath = "/GoToHomeStudent";
			request.setAttribute("errorMessage", e.getMessage());
			request.getRequestDispatcher(errorPath).forward(request, response);
			return;
		}

		Map<String, Object> mapStringToData = new HashMap<>();
		mapStringToData.put("student", student);
		mapStringToData.put("evaluation", evaluation);
		mapStringToData.put("call", courseCall);
		mapStringToData.put("course", studentCourse);
		mapStringToData.put("lecturer", courseLecturer);
		
		String json = new Gson().toJson(mapStringToData);
		
		actualNumber = checkIfActualNumber(evaluation.getMark());

		String path = "WEB-INF/Outcome.html";
		/*ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("student", student);
		ctx.setVariable("evaluation", evaluation);
		ctx.setVariable("call", courseCall);
		ctx.setVariable("course", studentCourse);
		ctx.setVariable("lecturer", courseLecturer);
		ctx.setVariable("markActualNumber", actualNumber);
		templateEngine.process(path, ctx, response.getWriter());*/
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	private boolean checkIfActualNumber(String mark) {
		try {
			Integer.parseInt(mark);
		} catch (NumberFormatException e) {
			if(mark.equals("30L")) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
}
