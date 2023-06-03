package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import DAO.CallEvaluationDAO;
import DAO.GraduationCallDAO;
import DAO.StudentDAO;
import beans.User;
import exceptions.CallEvaluationDAOException;
import exceptions.GraduationCallDAOException;
import exceptions.StudentDAOException;
import utils.ConnectionHandler;

@WebServlet("/UpdateStudentMark")
@MultipartConfig
public class UpdateStudentMark extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public UpdateStudentMark() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() throws ServletException {
		this.connection = ConnectionHandler.getConnection(getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Integer studentId = null, callId = null;
		String newMark = null;

		HttpSession session = request.getSession();
		User lecLogged = (User) session.getAttribute("user");
		try {
			studentId = Integer.parseInt(request.getParameter("studentid"));
			callId = Integer.parseInt(request.getParameter("callid"));
			newMark = request.getParameter("newMark");
			
			CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
			StudentDAO sDAO = new StudentDAO(this.connection);
			
			gcDAO.checkIfCourseOfCallIsTaughtByLecturer(callId, lecLogged.getId());
			sDAO.checkIfStudentIsSubscribedToCall(studentId, callId);
			ceDAO.checkIfMarkFormatIsCorrect(newMark);
			
			if(!newMark.equals("")) {
				ceDAO.updateMarkByStudentAndCallId(studentId, callId, newMark);
			}
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("Incorrect param values");
			return;
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(e.getMessage());
			return;
		} catch (GraduationCallDAOException | StudentDAOException | CallEvaluationDAOException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(e.getMessage());
			return;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

}
