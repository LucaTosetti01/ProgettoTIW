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
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

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
@MultipartConfig
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
		int numberOfVerbalizableMarks;
		
		CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
		try {
			numberOfVerbalizableMarks = ceDAO.getNumberOfVerbalizableMarks();
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(e.getMessage());
			return;
		}
		
		String json = new Gson().toJson(numberOfVerbalizableMarks);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
			ceDAO.checkIfAnyMarkIsVerbalizable();
			
			verbalId = ceDAO.verbalizeAllMarksByCallId(currentDate, currentTime, callId);
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("Incorrect param value");
			return;
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(e.getMessage());
			return;
		} catch (CallEvaluationDAOException | GraduationCallDAOException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(e.getMessage());
			return;
		}
		
		String json = new Gson().toJson(verbalId);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
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
