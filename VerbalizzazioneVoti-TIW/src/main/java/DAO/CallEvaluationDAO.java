package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.CallEvaluation;

public class CallEvaluationDAO {
	private Connection connection;

	public CallEvaluationDAO() {

	}

	public CallEvaluationDAO(Connection connection) {
		this.connection = connection;
	}

	public List<CallEvaluation> findAllEvaluationByStudentId(int student_id) throws SQLException {
		String query = "SELECT * FROM registrations_calls WHERE ID_Student = ?";
		List<CallEvaluation> evaluations = new ArrayList<>();

		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, student_id);
			result = pstatement.executeQuery();
			while (result.next()) {
				CallEvaluation ce = new CallEvaluation();
				ce.setCall_id(result.getInt("ID_Call"));
				ce.setState(result.getString("EvaluationStatus"));
				ce.setMark(result.getString("Mark"));
				ce.setStudent_id(student_id);

				evaluations.add(ce);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (result != null) {
					result.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		return evaluations;
	}

	public List<CallEvaluation> findAllEvaluationByCallId(int call_id) throws SQLException {
		String query = "SELECT * FROM registrations_calls WHERE ID_Call = ?";
		List<CallEvaluation> evaluations = new ArrayList<>();

		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, call_id);
			result = pstatement.executeQuery();
			while (result.next()) {
				CallEvaluation ce = new CallEvaluation();
				ce.setCall_id(call_id);
				ce.setState(result.getString("EvaluationStatus"));
				ce.setMark(result.getString("Mark"));
				ce.setStudent_id(result.getInt("ID_Student"));

				evaluations.add(ce);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (result != null) {
					result.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		return evaluations;
	}

	public CallEvaluation findEvaluationByCallAndStudentId(int call_id, int student_id) throws SQLException {
		String query = "SELECT * FROM registrations_calls WHERE ID_Call = ? AND ID_Student = ?";
		CallEvaluation callEv = null;

		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, call_id);
			pstatement.setInt(2, student_id);
			result = pstatement.executeQuery();
			result.next();
			callEv = new CallEvaluation();
			callEv.setCall_id(call_id);
			callEv.setState(result.getString("EvaluationStatus"));
			callEv.setMark(result.getString("Mark"));
			callEv.setStudent_id(student_id);

		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (result != null) {
					result.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		return callEv;
	}
}
