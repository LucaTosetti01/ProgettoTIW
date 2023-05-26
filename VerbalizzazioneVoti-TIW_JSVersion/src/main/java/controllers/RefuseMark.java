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

import com.google.gson.Gson;

import DAO.CallEvaluationDAO;
import DAO.StudentDAO;
import beans.User;
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
		this.connection=ConnectionHandler.getConnection(getServletContext());
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		Integer callId = null;
		
		HttpSession session = request.getSession();
		User studLogged = (User) session.getAttribute("user");
		try {
			callId = Integer.parseInt(request.getParameter("callid"));
			
			CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
			StudentDAO sDAO = new StudentDAO(this.connection);
			
			sDAO.checkIfStudentIsSubscribedToCall(studLogged.getId(), callId);;
			
			ceDAO.updateEvaluationStateByStudentAndCallId(studLogged.getId(), callId, "Rifiutato");
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("Incorrect param value");
			return;
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(e.getMessage());
			return;
		} catch(StudentDAOException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(e.getMessage());
			return;
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		doGet(request,response);
	}
	
}
