package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import beans.CallEvaluation;
import beans.User;

public class StudentDAO {
	private Connection connection;

	public StudentDAO() {

	}

	public StudentDAO(Connection connection) {
		this.connection = connection;
	}

	public List<User> findAllStudentsByDegreeCourse(int degreeCourse_id) throws SQLException {
		List<User> students = new ArrayList<>();

		String query = "SELECT * FROM users WHERE ID_DegreeCourse = ? AND Role = 'Student'";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, degreeCourse_id);
			result = pstatement.executeQuery();
			while (result.next()) {
				User student = new User();

				student.setName(result.getString("ID"));
				student.setSurname(result.getString("Surname"));
				student.setName(result.getString("Name"));
				student.setEmail(result.getString("Email"));
				student.setUsername(result.getString("Username"));
				student.setRole(result.getString("Role"));

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
		String query = "INSERT INTO users (Surname,Name,Email,Username,Password,ID_DegreeCourse,Role) VALUES (?,?,?,?,?,?,?)";
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
			pstatement.setString(7, "Student");
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

	public List<User> findStudentsInVerbal(int verbal_id) throws SQLException {
		List<User> students = new ArrayList<User>();

		String query = "SELECT * FROM students_verbals JOIN users on ID_Student=ID WHERE ID_Verbal = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, verbal_id);
			result = pstatement.executeQuery();
			while (result.next()) {
				User stud = new User();
				stud.setId(result.getInt("ID"));
				stud.setSurname(result.getString("Surname"));
				stud.setName(result.getString("Name"));
				stud.setEmail(result.getString("Email"));
				stud.setUsername(result.getString("Username"));
				stud.setRole(result.getString("Role"));
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

	public List<User> findAllRegistrationsToTheCall(int call_id) throws SQLException {
		List<User> students = new ArrayList<User>();

		String query = "SELECT u.ID,u.Surname,u.Name,u.Email,u.Username,u.ID_DegreeCourse "
				+ "FROM users AS u JOIN registrations_calls AS r ON u.ID=r.ID_Student JOIN degree_courses AS d ON u.ID_DegreeCourse = d.ID "
				+ "WHERE r.ID_Call = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, call_id);
			result = pstatement.executeQuery();
			while (result.next()) {
				User stud = new User();
				stud.setId(result.getInt("ID"));
				stud.setSurname(result.getString("Surname"));
				stud.setName(result.getString("Name"));
				stud.setEmail(result.getString("Email"));
				stud.setUsername(result.getString("Username"));
				DegreeCourseDAO degCourseDAO = new DegreeCourseDAO(this.connection);
				stud.setDegreeCourse(degCourseDAO.findDegreeCourseById(result.getInt("ID_DegreeCourse")));

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

	public List<User> findAllRegistrationsToTheCall(int call_id, String orderBy, String orderType) throws SQLException {
		List<User> students = new ArrayList<User>();

		String query = "SELECT ID,Surname,Name,Email,Username,ID_DegreeCourse,Mark,EvaluationStatus "
				+ "FROM users AS u JOIN registrations_calls AS r ON u.ID=r.ID_Student JOIN degree_courses AS d ON u.ID_DegreeCourse = d.ID "
				+ "WHERE ID_Call = ? " + "ORDER BY ? ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, call_id);
			pstatement.setString(2, orderBy);
			pstatement.setString(3, orderType);
			result = pstatement.executeQuery();
			while (result.next()) {
				User stud = new User();
				stud.setId(result.getInt("ID"));
				stud.setSurname(result.getString("Surname"));
				stud.setName(result.getString("Name"));
				stud.setEmail(result.getString("Email"));
				stud.setUsername(result.getString("Username"));
				DegreeCourseDAO degCourseDAO = new DegreeCourseDAO();
				stud.setDegreeCourse(degCourseDAO.findDegreeCourseById(result.getInt("ID_DegreeCourse")));

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

	public User findStudentById(int student_id) throws SQLException {
		User stud = new User();

		String query = "SELECT * FROM users WHERE ID = ? AND Role = 'Student'";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, student_id);
			result = pstatement.executeQuery();
			result.next();

			stud.setId(student_id);
			stud.setSurname(result.getString("Surname"));
			stud.setName(result.getString("Name"));
			stud.setEmail(result.getString("Email"));
			stud.setUsername(result.getString("Username"));
			stud.setRole(result.getString("Role"));

			DegreeCourseDAO degCourseDAO = new DegreeCourseDAO(this.connection);
			stud.setDegreeCourse(degCourseDAO.findDegreeCourseById(result.getInt("ID_DegreeCourse")));
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

	public Map<User, CallEvaluation> findAllRegistrationsAndEvaluationToCall(int call_id) throws SQLException {
		Map<User, CallEvaluation> studEv = new LinkedHashMap<User, CallEvaluation>();

		String query = "SELECT * " + "FROM users AS u JOIN registrations_calls AS r ON u.ID = r.ID_Student "
				+ "WHERE r.ID_Call = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, call_id);
			result = pstatement.executeQuery();
			while (result.next()) {
				User u = new User();
				u.setId(result.getInt("ID"));
				u.setSurname(result.getString("Surname"));
				u.setName(result.getString("Name"));
				u.setEmail(result.getString("Email"));
				u.setUsername(result.getString("Username"));
				u.setRole(result.getString("Role"));

				DegreeCourseDAO dcDAO = new DegreeCourseDAO(this.connection);
				u.setDegreeCourse(dcDAO.findDegreeCourseById(result.getInt("ID_CorsoDiLaurea")));

				CallEvaluation callEv = new CallEvaluation();
				callEv.setCall_id(call_id);
				callEv.setStudent_id(result.getInt("ID"));
				callEv.setMark(result.getString("Mark"));
				callEv.setState(result.getString("EvaluationStatus"));

				studEv.put(u, callEv);
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
		return studEv;
	}

	public Map<User, CallEvaluation> findAllRegistrationsAndEvaluationToCallOrdered(int call_id, String orderBy,
			String orderType) throws SQLException {
		Map<User, CallEvaluation> studEv = new LinkedHashMap<User, CallEvaluation>();

		String query = "SELECT u.ID,u.Surname,u.Name,u.Email,u.Username,u.Role,u.ID_DegreeCourse,d.Name AS DegreeName,r.Mark,r.EvaluationStatus "
				+ "FROM users AS u JOIN registrations_calls AS r ON u.ID = r.ID_Student JOIN degree_courses AS d ON d.ID = u.ID_DegreeCourse "
				+ "WHERE r.ID_Call = ? AND Role = 'Student' " + "ORDER BY %s %s";
		PreparedStatement pstatement = null;
		ResultSet result = null;

		if (!Arrays.asList("ID", "Surname", "Name", "Email", "Username", "DegreeName", "Mark", "EvaluationStatus")
				.contains(orderBy) || !Arrays.asList("ASC", "DESC").contains(orderType)) {
			throw new SQLException();
		}
		query = String.format(query, orderBy, orderType);

		try {

			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, call_id);
			System.out.println(pstatement.toString());
			result = pstatement.executeQuery();
			while (result.next()) {
				User u = new User();
				u.setId(result.getInt("ID"));
				u.setSurname(result.getString("Surname"));
				u.setName(result.getString("Name"));
				u.setEmail(result.getString("Email"));
				u.setUsername(result.getString("Username"));
				u.setRole(result.getString("Role"));

				DegreeCourseDAO dcDAO = new DegreeCourseDAO(this.connection);
				u.setDegreeCourse(dcDAO.findDegreeCourseById(result.getInt("ID_DegreeCourse")));

				CallEvaluation callEv = new CallEvaluation();
				callEv.setCall_id(call_id);
				callEv.setStudent_id(result.getInt("ID"));
				callEv.setMark(result.getString("Mark"));
				callEv.setState(result.getString("EvaluationStatus"));

				studEv.put(u, callEv);
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
		return studEv;
	}

}
