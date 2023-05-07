package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;

import DAO.CallEvaluationDAO;
import utils.ConnectionHandler;

@WebServlet("/RefuseMark")
public class RefuseMark extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public RefuseMark() {
		super();
	}
	
	public void init() throws ServletException {
		this.connection=ConnectionHandler.getConnection(getServletContext());
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		Integer studentId = null, callId = null;
		String updateType = null;
		
		try {
			studentId= Integer.parseInt(request.getParameter("studentid"));
			callId = Integer.parseInt(request.getParameter("callid"));
			updateType = request.getParameter("updatetype");
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
		CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
		
		try {
			ceDAO.updateEvaluationStateByStudentAndCallId(studentId, callId, updateType);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToOutcome?studentid=" + studentId + "&callid=" + callId;
		response.sendRedirect(path);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		doGet(request,response);
	}
	
}
