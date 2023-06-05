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
import DAO.StudentDAO;
import beans.User;
import exceptions.CallEvaluationDAOException;
import exceptions.StudentDAOException;
import utils.ConnectionHandler;

@WebServlet("/RefuseMark")
public class RefuseMark extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public RefuseMark() {
		super();
	}

	public void init() throws ServletException {
		this.connection = ConnectionHandler.getConnection(getServletContext());
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer callId = null;

		HttpSession session = request.getSession();
		User studLogged = (User) session.getAttribute("user");
		try {
			CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
			StudentDAO sDAO = new StudentDAO(this.connection);

			callId = Integer.parseInt(request.getParameter("callid"));

			// Checking if the student is subscribed to the course associated to the call
			// with "callId" as ID
			sDAO.checkIfStudentIsSubscribedToCall(studLogged.getId(), callId);

			// Checking if the mark of the call, is "PUBBLICATO", if it's not student can't
			// refuse the mark because it's not published yet
			ceDAO.checkIfStudentMarkIsRefusable(studLogged.getId(), callId);
			
			// Proceeding to refuse the student's mark associated to the chosen call
			ceDAO.updateEvaluationStateByStudentAndCallId(studLogged.getId(), callId, "Rifiutato");
		} catch (NumberFormatException | NullPointerException e) {
			String errorPath = "/GoToOutcome";
			request.setAttribute("errorMessage", "Incorrect param values");
			request.getRequestDispatcher(errorPath).forward(request, response);
			return;
		} catch (SQLException | StudentDAOException | CallEvaluationDAOException e) {
			String errorPath = "/GoToOutcome";
			request.setAttribute("errorMessage", e.getMessage());
			request.getRequestDispatcher(errorPath).forward(request, response);
			return;
		}

		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToOutcome?callid=" + callId;
		response.sendRedirect(path);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
