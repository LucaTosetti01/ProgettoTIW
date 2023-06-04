package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
import beans.User;
import exceptions.CallEvaluationDAOException;
import exceptions.GraduationCallDAOException;
import utils.ConnectionHandler;

@WebServlet("/PublishStudentsMarks")
@MultipartConfig
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
		int numberOfPublishableMarks;
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
			// Retrieve from the DB the number of publishable marks (used later to
			// eventually disable the publish button if 0)
			numberOfPublishableMarks = ceDAO.getNumberOfPublishableMarks(callId);
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("Incorrect param value");
			return;
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(e.getMessage());
			return;
		} catch (GraduationCallDAOException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(e.getMessage());
			return;
		}

		// Sending back to the client, in the form of a json object, the number of
		// publishable marks
		String json = new Gson().toJson(numberOfPublishableMarks);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("Incorrect param value");
			return;
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(e.getMessage());
			return;
		} catch (GraduationCallDAOException | CallEvaluationDAOException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(e.getMessage());
			return;
		}
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
