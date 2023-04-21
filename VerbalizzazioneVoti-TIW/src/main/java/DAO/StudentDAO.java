package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.DegreeCourse;
import beans.Student;
import beans.Verbal;

public class StudentDAO {
	private Connection connection;

	public StudentDAO() {

	}

	public StudentDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Student> findAllStudentsByDegreeCourse(int degreeCourse_id) throws SQLException {
		List<Student> students = new ArrayList<>();

		String query = "SELECT * FROM students WHERE ID_DegreeCourse = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, degreeCourse_id);
			result = pstatement.executeQuery();
			while (result.next()) {
				Student student = new Student();

				student.setMatricola(result.getInt("ID"));
				student.setSurname(result.getString("Surname"));
				student.setName(result.getString("Name"));
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
		String query = "INSERT INTO students (Surname,Name,Email,Username,Password,ID_DegreeCourse) VALUES (?,?,?,?,?,?)";
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

	public List<Student> findStudentsInVerbal(int verbal_id) throws SQLException {
		List<Student> students = new ArrayList<Student>();

		String query = "SELECT * FROM students_verbals JOIN students on ID_Student=ID WHERE ID_Verbal = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, verbal_id);
			result = pstatement.executeQuery();
			while (result.next()) {
				Student stud = new Student();
				stud.setMatricola(result.getInt("ID"));
				stud.setSurname(result.getString("Surname"));
				stud.setName(result.getString("Name"));
				stud.setEmail(result.getString("Email"));
				stud.setUsername(result.getString("Username"));
				DegreeCourseDAO degCourseDAO = new DegreeCourseDAO();
				stud.setDegreeCourse(degCourseDAO.findDegreeCourseById(verbal_id));

				students.add(stud);
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

	public List<Student> findAllRegistrationsToTheCall(int call_id) throws SQLException {
		List<Student> students = new ArrayList<Student>();

		String query = "SELECT ID,Surname,Name,Email,Username,ID_DegreeCourse,Mark,EvaluationStatus "
				+ "FROM students AS s JOIN registrations_calls AS r ON s.ID=r.ID_Student JOIN degree_courses AS d ON s.ID_DegreeCourse = d.ID "
				+ "WHERE ID_Call = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, call_id);
			result = pstatement.executeQuery();
			while (result.next()) {
				Student stud = new Student();
				stud.setMatricola(result.getInt("ID"));
				stud.setSurname(result.getString("Surname"));
				stud.setName(result.getString("Name"));
				stud.setEmail(result.getString("Email"));
				stud.setUsername(result.getString("Username"));
				DegreeCourseDAO degCourseDAO = new DegreeCourseDAO();
				stud.setDegreeCourse(degCourseDAO.findDegreeCourseById(result.getInt("ID_CorsoDiLaurea")));

				students.add(stud);
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

	public Student findStudentById(int student_id) throws SQLException {
		Student stud = new Student();

		String query = "SELECT * FROM students WHERE ID = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, student_id);
			result = pstatement.executeQuery();
			result.next();

			stud.setMatricola(student_id);
			stud.setSurname(result.getString("Surname"));
			stud.setName(result.getString("Name"));
			stud.setEmail(result.getString("Email"));
			stud.setUsername(result.getString("Username"));

			DegreeCourseDAO degCourseDAO = new DegreeCourseDAO();
			stud.setDegreeCourse(degCourseDAO.findDegreeCourseById(result.getInt("ID_CorsoDiLaurea")));
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
		return stud;
	}

	public int modifyStudentMark(int student_id, int call_id, String mark) throws SQLException {
		String query = "UPDATE registrations_calls SET Mark = ? AND EvaluationStatus = ? WHERE ID_Student = ? AND ID_Call = ?";
		int code = 0;

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, mark);
			pstatement.setString(2, "Inserito");
			pstatement.setInt(3, student_id);
			pstatement.setInt(4, call_id);
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
