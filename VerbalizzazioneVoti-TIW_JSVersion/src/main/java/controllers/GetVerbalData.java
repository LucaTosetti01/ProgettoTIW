package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
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
public class GetVerbalData extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public GetVerbalData() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());

	}
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
			verbal = vDAO.findVerbalById(verbalId);
			
			callId = verbal.getCallId();
			gcDAO.checkIfCourseOfCallIsTaughtByLecturer(callId, lecLogged.getId());
			
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
