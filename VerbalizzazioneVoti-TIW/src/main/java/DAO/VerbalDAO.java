package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import beans.User;
import beans.Verbal;

public class VerbalDAO {
	private Connection connection;

	public VerbalDAO() {

	}

	public VerbalDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Verbal> findAllVerbalsByCall(int call_id) throws SQLException {
		List<Verbal> res = new ArrayList<>();

		String query = "SELECT * FROM verbals WHERE ID_Call = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, call_id);
			result = pstatement.executeQuery();
			while (result.next()) {
				Verbal verb = new Verbal();

				verb.setId(result.getInt("ID"));
				verb.setCreationDate(result.getDate("CreationDate"));
				verb.setCreationTime(result.getTime("CreationTime"));
				verb.setCallId(call_id);

				res.add(verb);
			}

		} catch (SQLException e) {
			throw new SQLException("Failure in verbals' data extraction");
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
		return res;
	}

	public int createVerbal(Date creationDate, Time creationTime, int call_id) throws SQLException {
		String query = "INSERT INTO verbals (CreationDate, CreationTime, ID_Call) VALUES (?,?,?)";

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			pstatement.setDate(1, creationDate);
			pstatement.setTime(2, creationTime);
			pstatement.setInt(3, call_id);
			pstatement.executeUpdate();
			
			ResultSet generatedKeys = pstatement.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getInt(1);
			} else {
				throw new SQLException("Creating user failed, no ID obtained.");
			}
		} catch (SQLException e) {
			throw new SQLException("Failure in creating a new verbal");
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {

			}
		}
	}

	public int saveStudentsWithinVerbal(int verbal_id, List<User> students) throws SQLException {
		//this.connection.setAutoCommit(false);
		String query = "INSERT INTO students_verbals (ID_Student, ID_Verbal) VALUES (?,?)";

		int code = 0;

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			for (User student : students) {
				pstatement.setInt(1, student.getId());
				pstatement.setInt(2, verbal_id);
				code = pstatement.executeUpdate();
			}
			//connection.commit();
		} catch (SQLException e) {
			//connection.rollback();
			throw new SQLException("Failure while saving students within a new verbal");
		} finally {
			//connection.setAutoCommit(true);
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {

			}
		}
		return code;
	}

	public Verbal findVerbalById(int verbal_id) throws SQLException {
		String query = "SELECT * FROM verbals WHERE ID = ?";
		Verbal verbal = new Verbal();

		PreparedStatement pstatement = null;
		ResultSet result = null;

		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, verbal_id);
			result = pstatement.executeQuery();
			result.next();

			verbal.setId(verbal_id);
			verbal.setCreationDate(result.getDate("CreationDate"));
			verbal.setCreationTime(result.getTime("CreationTime"));
			verbal.setCallId(result.getInt("ID_Call"));

		} catch (SQLException e) {
			throw new SQLException("Failure in verbal's data extraction");
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
		return verbal;
	}

	public Verbal findVerbalByCallId(int call_id) throws SQLException {
		String query = "SELECT * FROM verbals WHERE ID_Call = ?";
		Verbal verbal = new Verbal();

		PreparedStatement pstatement = null;
		ResultSet result = null;

		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, call_id);
			result = pstatement.executeQuery();
			result.next();

			verbal.setId(result.getInt("ID"));
			verbal.setCreationDate(result.getDate("CreationDate"));
			verbal.setCreationTime(result.getTime("CreationTime"));
			verbal.setCallId(result.getInt("ID_Call"));

		} catch (SQLException e) {
			throw new SQLException("Failure in verbal's data extraction");
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
		return verbal;
	}

	public Verbal findVerbalByCallIdDateTime(Date date, Time time, int call_id) throws SQLException {
		String query = "SELECT * FROM verbals WHERE ID_Call = ? AND CreationDate = ? AND CreationTime = ?";
		Verbal verbal = new Verbal();

		PreparedStatement pstatement = null;
		ResultSet result = null;

		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, call_id);
			pstatement.setDate(2, date);
			pstatement.setTime(3, time);
			result = pstatement.executeQuery();
			result.next();

			verbal.setId(result.getInt("ID"));
			verbal.setCreationDate(result.getDate("CreationDate"));
			verbal.setCreationTime(result.getTime("CreationTime"));
			verbal.setCallId(result.getInt("ID_Call"));

		} catch (SQLException e) {
			throw new SQLException("Failure in verbal's data extraction");
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
		return verbal;
	}
}
