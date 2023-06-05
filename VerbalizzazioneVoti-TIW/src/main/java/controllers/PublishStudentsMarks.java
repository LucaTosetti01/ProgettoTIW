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
import beans.User;
import exceptions.CallEvaluationDAOException;
import exceptions.GraduationCallDAOException;
import utils.ConnectionHandler;

@WebServlet("/PublishStudentsMarks")
public class PublishStudentsMarks extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public PublishStudentsMarks() {
		super();
	}

	@Override
	public void init() throws ServletException {
		this.connection = ConnectionHandler.getConnection(getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Integer callId = null;

		HttpSession session = request.getSession();
		User lecLogged = (User) session.getAttribute("user");

		try {
			CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);

			callId = Integer.parseInt(request.getParameter("callid"));
			// Checking if the course associated to the call with "callId" as ID is taught
			// by the logged lecturer
			gcDAO.checkIfCourseOfCallIsTaughtByLecturer(callId, lecLogged.getId());
			// Checking if really exists any mark publishable
			ceDAO.checkIfAnyMarkIsPublishable(callId);
			// Proceeding to publish of publishable marks (that are the ones with the state
			// "Inserito")
			ceDAO.publishAllMarksByCallId(callId);
		} catch (NumberFormatException | NullPointerException e) {
			String errorPath = "/GetSubscriptionToCall";
			request.setAttribute("errorMessage", "Incorrect param values");
			request.getRequestDispatcher(errorPath).forward(request, response);
			return;
		} catch (SQLException | GraduationCallDAOException | CallEvaluationDAOException e) {
			String errorPath = "/GetSubscriptionToCall";
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
