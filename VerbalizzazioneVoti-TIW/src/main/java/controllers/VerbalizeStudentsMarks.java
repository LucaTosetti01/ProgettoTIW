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

import DAO.CallEvaluationDAO;
import DAO.VerbalDAO;
import beans.Verbal;
import utils.ConnectionHandler;

@WebServlet("/VerbalizeStudentsMarks")
public class VerbalizeStudentsMarks extends HttpServlet{
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer callId = null;
		Integer verbalId = null;
		
		try {
			callId = Integer.parseInt(request.getParameter("callid"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
		
		CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
		VerbalDAO vDAO = new VerbalDAO(this.connection);
		try {
			this.connection.setAutoCommit(false);
			ceDAO.publishAllMarksByCallId(callId);
			vDAO.createVerbal(Date.valueOf(LocalDate.now()), Time.valueOf(LocalTime.now()), callId);

			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		try {
			verbalId = vDAO.getVerbalByCallId(callId).getId();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToVerbalRecap?verbalid=" + verbalId;
		response.sendRedirect(path);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request,response);
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
