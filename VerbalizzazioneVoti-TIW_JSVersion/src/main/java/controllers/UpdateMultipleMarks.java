package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import DAO.CallEvaluationDAO;
import DAO.GraduationCallDAO;
import DAO.StudentDAO;
import beans.User;
import exceptions.CallEvaluationDAOException;
import exceptions.GraduationCallDAOException;
import exceptions.StudentDAOException;
import utils.ConnectionHandler;

@WebServlet("/UpdateMultipleMarks")
@MultipartConfig
public class UpdateMultipleMarks extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public UpdateMultipleMarks() {
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
		doPost(request,response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String newMark = null;
		Integer callId = null;
		Map<String, String[]> nameValueParametersMap = null;

		HttpSession session = request.getSession();
		User lecLogged = (User) session.getAttribute("user");

		try {
			nameValueParametersMap = new HashMap<>(request.getParameterMap());
			callId = Integer.parseInt(nameValueParametersMap.remove("callid")[0]);

			CallEvaluationDAO ceDAO = new CallEvaluationDAO(this.connection);
			GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
			StudentDAO sDAO = new StudentDAO(this.connection);

			//Remove via filter all the pair "Studentid" and "mark" in which "mark" is equals to -> ""
			nameValueParametersMap = nameValueParametersMap.entrySet().stream().filter(entry->!entry.getValue()[0].equals("")).collect(Collectors.toMap(entry->entry.getKey(), entry->entry.getValue()));
			List<Integer> studentIds = nameValueParametersMap.entrySet().stream()
					.map(entry -> Integer.parseInt(entry.getKey())).toList();
			List<String> studentMarks = nameValueParametersMap.entrySet().stream().map(entry -> entry.getValue()[0])
					.toList();

			gcDAO.checkIfCourseOfCallIsTaughtByLecturer(callId, lecLogged.getId());

			for (String mark : studentMarks) {
				ceDAO.checkIfMarkFormatIsCorrect(mark);
			}

			for (Integer id : studentIds) {
				sDAO.checkIfStudentIsSubscribedToCall(id, callId);
			}
			
			ceDAO.updateMultipleMarkByStudentAndCallId(studentIds, callId, studentMarks);

		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("Incorrect param values");
			return;
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(e.getMessage());
			return;
		} catch (GraduationCallDAOException | CallEvaluationDAOException | StudentDAOException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(e.getMessage());
			return;
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}
}
