package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import beans.CallEvaluation;
import beans.User;
import exceptions.CallEvaluationDAOException;
import exceptions.CourseDAOException;
import exceptions.StudentDAOException;

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
			throw new SQLException("Failure in student's evaluation data extraction");
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
			throw new SQLException("Failure in course's evaluations data extraction");
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
			throw new SQLException("Failure in evaluation's data extraction");
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
			throw new SQLException("Failure in updating student's evaluation state");
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

	public int verbalizeAllMarksByCallId(Date verbalDate, Time verbalTime, int call_id) throws SQLException {
		connection.setAutoCommit(false);
		
		
		String query = "UPDATE registrations_calls SET Mark = 'Rimandato' WHERE EvaluationStatus = 'Rifiutato' AND ID_Call = ?";

		PreparedStatement pstatement = null;

		String query2 = "UPDATE registrations_calls " + "SET EvaluationStatus = 'Verbalizzato' "
				+ "WHERE ID_Call = ? AND (EvaluationStatus = 'Pubblicato' OR EvaluationStatus = 'Rifiutato')";

		PreparedStatement pstatement2 = null;
		
		String query3 = "SELECT * "
				+ "FROM registrations_calls "
				+ "WHERE (EvaluationStatus = 'Pubblicato' OR EvaluationStatus = 'Rifiutato') AND ID_Call = ?";
		
		PreparedStatement pstatement3 = null;
		ResultSet result = null;
		List<User> students = new ArrayList<>();

		int verbalKey;

		try {
			pstatement3 = connection.prepareStatement(query3);
			pstatement3.setInt(1, call_id);
			result = pstatement3.executeQuery();
			while(result.next()) {
				User stud = new User();
				stud.setId(result.getInt("ID_Student"));
				
				students.add(stud);
			}
			
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, call_id);
			pstatement.executeUpdate();

			pstatement2 = connection.prepareStatement(query2);
			pstatement2.setInt(1, call_id);
			pstatement2.executeUpdate();

			VerbalDAO vDAO = new VerbalDAO(this.connection);
			verbalKey = vDAO.createVerbal(verbalDate, verbalTime, call_id);
			vDAO.saveStudentsWithinVerbal(call_id, students);

			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw new SQLException("Failure in verbalizing students' marks");
		} finally {
			connection.setAutoCommit(true);
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
		}
		return verbalKey;
	}
	
	public int publishAllMarksByCallId(int call_id) throws SQLException {
		String query = "UPDATE registrations_calls " + "SET EvaluationStatus = 'Pubblicato' "
				+ "WHERE ID_Call = ? AND EvaluationStatus = 'Inserito'";

		PreparedStatement pstatement = null;
		int code = 0;

		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, call_id);
			code = pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException("Failure in publishing students' marks");
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
		}
		return code;
	}

	public int updateMarkByStudentAndCallId(int student_id, int call_id, String newMark) throws SQLException {
		String query = "UPDATE registrations_calls SET Mark = ?, EvaluationStatus = ? WHERE ID_Call = ? AND ID_Student = ?";
		int code = 0;

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, newMark);
			pstatement.setString(2, "Inserito");
			pstatement.setInt(3, call_id);
			pstatement.setInt(4, student_id);
			code = pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException("Failure in updating student's mark");
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
		}
		return code;
	}
	
	public int[] updateMultipleMarkByStudentAndCallId(List<Integer> student_ids, int call_id, List<String> newMarks) throws SQLException {
		String query = "UPDATE registrations_calls SET Mark = ?, EvaluationStatus = 'Inserito' WHERE ID_Call = ? AND ID_Student = ?";
		int[] code = null;

		PreparedStatement pstatement = null;
		pstatement = connection.prepareStatement(query);
		try {
			for(int i=0;i<student_ids.size();i++) {
				pstatement.setString(1, newMarks.get(i));
				pstatement.setInt(2, call_id);
				pstatement.setInt(3, student_ids.get(i));
				pstatement.addBatch();
				
			}
			code = pstatement.executeBatch();
		} catch (SQLException e) {
			throw new SQLException("Failure in updating student's mark");
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
		}
		return code;
	}
	
	
	public void checkIfAnyMarkIsVerbalizable(int call_id) throws SQLException, CallEvaluationDAOException {
		if (this.getNumberOfVerbalizableMarks(call_id) == 0) {
			throw new CallEvaluationDAOException("There aren't verbalizable marks");
		}
	}
	
	public void checkIfAnyMarkIsPublishable(int call_id) throws SQLException, CallEvaluationDAOException {
		if (this.getNumberOfPublishableMarks(call_id) == 0) {
			throw new CallEvaluationDAOException("There aren't publishable marks");
		}
	}
	
	public void checkIfMarkFormatIsCorrect(String mark) throws CallEvaluationDAOException {
		try {
			int markIntConversion = Integer.parseInt(mark);
			if(markIntConversion <18 || markIntConversion > 30) {
				throw new CallEvaluationDAOException("Mark value inserted is not acceptable");
			}
		} catch (NumberFormatException | NullPointerException e) {
			if(!Arrays.asList("","Assente","Rimandato","Riprovato","30L").contains(mark)) {
				throw new CallEvaluationDAOException("Mark value inserted is not acceptable");
			}
		}
	}
	
	public int getNumberOfVerbalizableMarks(int call_id) throws SQLException {
		String query = "SELECT COUNT(*) AS Counter FROM registrations_calls WHERE (EvaluationStatus = 'Pubblicato' OR EvaluationStatus = 'Rifiutato') AND ID_Call = ?";
		
		PreparedStatement pstatement = null;
		ResultSet result = null;
		int numberOfRows;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, call_id);
			result = pstatement.executeQuery();
			result.next();
			numberOfRows = result.getInt("Counter");
		} catch (SQLException e) {
			throw new SQLException("Failure in evaluations' data extraction");
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
		return numberOfRows;
	}
	
	public int getNumberOfPublishableMarks(int call_id) throws SQLException {
		String query = "SELECT COUNT(*) AS Counter FROM registrations_calls WHERE EvaluationStatus = 'Inserito' AND ID_Call = ?";
		
		PreparedStatement pstatement = null;
		ResultSet result = null;
		int numberOfRows;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, call_id);
			result = pstatement.executeQuery();
			result.next();
			numberOfRows = result.getInt("Counter");
		} catch (SQLException e) {
			throw new SQLException("Failure in evaluations' data extraction");
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
		return numberOfRows;
	}
	
	public void checkIfStudentMarkIsUpdatable(int student_id, int call_id) throws SQLException, CallEvaluationDAOException {
		String query = "SELECT EvaluationStatus FROM registrations_calls WHERE ID_Student = ? AND ID_Call = ?";
		
		PreparedStatement pstatement = null;
		ResultSet result = null;
		String status;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, student_id);
			pstatement.setInt(2, call_id);
			result = pstatement.executeQuery();
			result.next();
			status = result.getString("EvaluationStatus");
		} catch (SQLException e) {
			throw new SQLException("Failure in evaluations' data extraction");
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
		if(Arrays.asList("Pubblicato","Verbalizzato","Rifiutato").contains(status)) {
			throw new CallEvaluationDAOException("The chosen student's mark is not modifiable");
		}
	}
	
	public void checkIfStudentMarkIsRefusable(int student_id, int call_id) throws SQLException, CallEvaluationDAOException {
		String query = "SELECT EvaluationStatus FROM registrations_calls WHERE ID_Student = ? AND ID_Call = ?";
		
		PreparedStatement pstatement = null;
		ResultSet result = null;
		String status;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, student_id);
			pstatement.setInt(2, call_id);
			result = pstatement.executeQuery();
			result.next();
			status = result.getString("EvaluationStatus");
		} catch (SQLException e) {
			throw new SQLException("Failure in evaluations' data extraction");
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
		if(!status.equals("Pubblicato")) {
			throw new CallEvaluationDAOException("The chosen student's mark is not refusable");
		}
	}

}
