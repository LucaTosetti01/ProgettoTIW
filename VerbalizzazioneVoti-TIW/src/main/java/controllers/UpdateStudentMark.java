package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
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
public class UpdateStudentMark extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public UpdateStudentMark() {
		super();
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
			CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
			StudentDAO sDAO = new StudentDAO(this.connection);

			studentId = Integer.parseInt(request.getParameter("studentid"));
			callId = Integer.parseInt(request.getParameter("callid"));
			newMark = request.getParameter("newMark");

			// Checking if the course associated to the call with "callId" as ID is taught
			// by the logged lecturer
			gcDAO.checkIfCourseOfCallIsTaughtByLecturer(callId, lecLogged.getId());
			// Checking if the student is subscribed to the call associated to the call
			// with "callId" as ID
			sDAO.checkIfStudentIsSubscribedToCall(studentId, callId);
			// Checking if the format of the mark retrieved from the request is correct
			ceDAO.checkIfMarkFormatIsCorrect(newMark);

			// Proceeding to update student mark with the new value
			ceDAO.updateMarkByStudentAndCallId(studentId, callId, newMark);
		} catch (NumberFormatException | NullPointerException e) {
			String errorPath = "/GoToMarkManagement";
			request.setAttribute("errorMessage", "Incorrect param values");
			request.getRequestDispatcher(errorPath).forward(request, response);
			return;
		} catch (SQLException | GraduationCallDAOException | StudentDAOException | CallEvaluationDAOException e) {
			String errorPath = "/GoToMarkManagement";
			request.setAttribute("errorMessage", e.getMessage());
			request.getRequestDispatcher(errorPath).forward(request, response);
			return;
		}

		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GetSubscriptionToCall?callid=" + callId;
		response.sendRedirect(path);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
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
