package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Student;

public class StudentDAO {
	private Connection connection;

	public StudentDAO() {

	}

	public StudentDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Student> findAllStudentsByDegreeCourse(int id_degreeCourse) throws SQLException {
		List<Student> students = new ArrayList<>();

		String query = "SELECT * FROM studente WHERE ID_CorsoDiLaurea = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id_degreeCourse);
			result = pstatement.executeQuery();
			while (result.next()) {
				Student student = new Student();

				student.setMatricola(result.getInt("Matricola"));
				student.setSurname(result.getString("Cognome"));
				student.setName(result.getString("Nome"));
				student.setEmail(result.getString("Email"));
				student.setUsername(result.getString("Username"));

				students.add(student);
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

		return students;
	}

	public int registerStudent(String surname, String name, String email, String username, String password,
			int id_degreeCourse) throws SQLException {
		String query = "INSERT into studente (Cognome,Nome,Email,Username,Password,ID_CorsoDiLaurea) VALUES (?,?,?,?,?,?)";
		int code = 0;

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, surname);
			pstatement.setString(2, name);
			pstatement.setString(3, email);
			pstatement.setString(4, username);
			pstatement.setString(5, password);
			pstatement.setInt(6, id_degreeCourse);
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
