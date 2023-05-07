package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.CallEvaluationDAO;
import utils.ConnectionHandler;

@WebServlet("/UpdateStudentMark")
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

		try {
			studentId = Integer.parseInt(request.getParameter("studentid"));
			callId = Integer.parseInt(request.getParameter("callid"));
			newMark = request.getParameter("newMark");
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
		CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
		
		try {
			ceDAO.updateMarkByStudentAndCallId(studentId, callId, newMark);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToMarkManagement?studentid=" + studentId + "&callid=" + callId;
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
		// TODO Auto-generated method stub
		super.destroy();
	}

}
