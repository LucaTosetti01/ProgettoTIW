package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.User;

public class LecturerDAO {
	private Connection connection;

	public LecturerDAO() {

	}

	public LecturerDAO(Connection connection) {
		this.connection = connection;
	}

	public int insertLecturer(String surname, String name, String email, String username, String password)
			throws SQLException {
		String query = "INSERT INTO users (Surname,Name,Email,Username,Password,Role) VALUES (?,?,?,?,?,?)";
		int code = 0;

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, surname);
			pstatement.setString(2, name);
			pstatement.setString(3, email);
			pstatement.setString(4, username);
			pstatement.setString(5, password);
			pstatement.setString(6, "Lecturer");
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

	public User findLecturerById(int lecturer_id) throws SQLException {
		User lecturer = new User();

		String query = "SELECT * FROM users WHERE ID = ? AND Role = 'Lecturer'";
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
			lecturer.setRole(result.getString("Role"));

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
