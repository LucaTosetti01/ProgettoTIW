package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import DAO.CallEvaluationDAO;
import DAO.GraduationCallDAO;
import DAO.LecturerDAO;
import DAO.VerbalDAO;
import beans.CallEvaluation;
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
			callId = Integer.parseInt(request.getParameter("callid"));
		
			CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
			VerbalDAO vDAO = new VerbalDAO(this.connection);
			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
			
			gcDAO.checkIfCourseOfCallIsTaughtByLecturer(callId, lecLogged.getId());
			ceDAO.checkIfAnyMarkIsVerbalizable(callId);
			
			ceDAO.verbalizeAllMarksByCallId(currentDate, currentTime, callId);
			verbalId = vDAO.findVerbalByCallIdDateTime(currentDate, currentTime, callId).getId();
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
		String path = ctxpath + "/GoToVerbalRecap?verbalid=" + verbalId +"&callid=" + callId;
		response.sendRedirect(path);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	@Override
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
