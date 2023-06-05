package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

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

@WebServlet("/VerbalizeStudentsMarks")
public class VerbalizeStudentsMarks extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public VerbalizeStudentsMarks() {
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
		Integer verbalId = null;

		Date currentDate = Date.valueOf(LocalDate.now());
		Time currentTime = Time.valueOf(LocalTime.now());

		HttpSession session = request.getSession();
		User lecLogged = (User) session.getAttribute("user");
		try {
			CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);

			callId = Integer.parseInt(request.getParameter("callid"));

			// Checking if the course associated to the call with "callId" as ID is taught
			// by the logged lecturer
			gcDAO.checkIfCourseOfCallIsTaughtByLecturer(callId, lecLogged.getId());
			// Checking if really exists any mark verbalizable
			ceDAO.checkIfAnyMarkIsVerbalizable(callId);
			// Proceeding to create a new verbal with the current time and date and
			// associated to the call which has "callId" value as ID, and retrieving the
			// verbalId of the verbal just created
			verbalId = ceDAO.verbalizeAllMarksByCallId(currentDate, currentTime, callId);
		} catch (NumberFormatException | NullPointerException e) {
			String errorPath = "/GetSubscriptionToCall";
			request.setAttribute("errorMessage", "Incorrect param values");
			request.getRequestDispatcher(errorPath).forward(request, response);
			return;
		} catch (SQLException e) {
			String errorPath = "/GetSubscriptionToCall";
			request.setAttribute("errorMessage", e.getMessage());
			request.getRequestDispatcher(errorPath).forward(request, response);
			return;
		} catch (CallEvaluationDAOException e) {
			String errorPath = "/GetSubscriptionToCall";
			request.setAttribute("errorMessage", e.getMessage());
			request.getRequestDispatcher(errorPath).forward(request, response);
			return;
		} catch (GraduationCallDAOException e) {
			String errorPath = "/GetSubscriptionToCall";
			request.setAttribute("errorMessage", e.getMessage());
			request.getRequestDispatcher(errorPath).forward(request, response);
			return;
		}

		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToVerbalRecap?verbalid=" + verbalId;
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
