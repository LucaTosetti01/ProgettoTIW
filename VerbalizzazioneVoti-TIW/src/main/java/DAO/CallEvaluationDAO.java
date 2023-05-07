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

	public int updateEvaluationStateByStudentAndCallId(int student_id, int call_id, String evaluationState)
			throws SQLException {
		String query = "UPDATE registrations_calls SET EvaluationStatus = ? WHERE ID_Student = ? AND ID_Call = ?";
		int code = 0;

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, evaluationState);
			pstatement.setInt(2, student_id);
			pstatement.setInt(3, call_id);
			code = pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {

			}
		}
		return code;
	}

	public int publishAllMarksByCallId(int call_id) throws SQLException {
		boolean autoCommitSettedHere = false;
		if(connection.getAutoCommit()) {
			connection.setAutoCommit(false);
			autoCommitSettedHere = true;
		}
		
		String query = "UPDATE registrations_calls SET Mark = 'Rimandato' WHERE EvaluationStatus = 'Rifiutato'";
				
		PreparedStatement pstatement = null;
		
		String query2 = "UPDATE registrations_calls " + "SET EvaluationStatus = 'Verbalizzato' "
				+ "WHERE ID_Call = ? AND (EvaluationStatus = 'Pubblicato' OR EvaluationStatus = 'Rifiutato')";
		
		
		PreparedStatement pstatement2 = null;
		int code = 0;

		
		try {
			pstatement = connection.prepareStatement(query);
			code = pstatement.executeUpdate();
			
			pstatement2 = connection.prepareStatement(query2);
			pstatement2.setInt(1, call_id);
			code = pstatement2.executeUpdate();
			
			if(autoCommitSettedHere) {
				connection.commit();
			}
		} catch (SQLException e) {
			if(autoCommitSettedHere) {
				connection.rollback();
			}
			throw new SQLException(e);
		} finally {
			if(autoCommitSettedHere) {
				connection.setAutoCommit(true);
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {

			}
		}
		return code;
	}
	
	public int updateMarkByStudentAndCallId(int student_id, int call_id, String newMark) throws SQLException {
		String query = "UPDATE registrations_calls SET Mark = ? WHERE ID_Call = ? AND ID_Student = ?";
		int code = 0;

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, newMark);
			pstatement.setInt(2, call_id);
			pstatement.setInt(3, student_id);
			code = pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {

			}
		}
		return code;
	}
}
