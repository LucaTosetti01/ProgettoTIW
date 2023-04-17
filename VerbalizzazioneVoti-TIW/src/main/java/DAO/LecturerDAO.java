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
		String query = "INSERT into docente (Cognome,Nome,Email,Username,Password) VALUES (?,?,?,?,?)";
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

	public Lecturer findLecturerById(int id_lecturer) throws SQLException {
		Lecturer lecturer = new Lecturer();

		String query = "SELECT * FROM docente WHERE ID = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id_lecturer);
			result = pstatement.executeQuery();
			result.next();

			lecturer.setId(id_lecturer);
			lecturer.setSurname(result.getString("Cognome"));
			lecturer.setName(result.getString("Nome"));
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
}
