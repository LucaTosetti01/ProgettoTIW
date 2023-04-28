package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.Lecturer;

public class LecturerDAO {
	private Connection connection;

	public LecturerDAO() {

	}

	public LecturerDAO(Connection connection) {
		this.connection = connection;
	}

	public int registerLecturer(String surname, String name, String email, String username, String password)
			throws SQLException {
		String query = "INSERT INTO lecturers (Surname,Name,Email,Username,Password) VALUES (?,?,?,?,?)";
		int code = 0;

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, surname);
			pstatement.setString(2, name);
			pstatement.setString(3, email);
			pstatement.setString(4, username);
			pstatement.setString(5, password);
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

	public Lecturer findLecturerById(int lecturer_id) throws SQLException {
		Lecturer lecturer = new Lecturer();

		String query = "SELECT * FROM lecturers WHERE ID = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, lecturer_id);
			result = pstatement.executeQuery();
			result.next();

			lecturer.setId(lecturer_id);
			lecturer.setSurname(result.getString("Surname"));
			lecturer.setName(result.getString("Name"));
			lecturer.setEmail(result.getString("Email"));
			lecturer.setUsername(result.getString("Username"));

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
		return lecturer;
	}

	public int publishTheVotes(int call_id) throws SQLException {
		String query = "UPDATE registrations_calls SET EvaluationStatus = ? WHERE ID_Call = ?";
		int code = 0;

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, "Pubblicato");
			pstatement.setInt(2, call_id);
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

	public Lecturer checkCredentials(String username, String password) throws SQLException {
		String query = "SELECT ID,Surname,Name,Email,Username FROM lecturers WHERE Username = ? AND Password = ?";

		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			result = pstatement.executeQuery();
			if (!result.isBeforeFirst()) {
				return null;
			} else {
				result.next();
				Lecturer lect = new Lecturer();
				lect.setId(result.getInt("ID"));
				lect.setSurname(result.getString("Surname"));
				lect.setName(result.getString("Name"));
				lect.setEmail(result.getString("Email"));
				lect.setUsername(result.getString("Username"));
				return lect;
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
	}
}
