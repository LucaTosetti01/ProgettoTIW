package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Course;
import beans.DegreeCourse;

public class CourseDAO {
	private Connection connection;

	public CourseDAO() {

	}

	public CourseDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Course> findAllCoursesByLecturer(int id) throws SQLException {
		List<Course> courses = new ArrayList<Course>();

		String query = "SELECT * FROM course WHERE ID_Docente = ? ORDER BY nome DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id);
			result = pstatement.executeQuery();
			while (result.next()) {
				Course course = new Course();
				course.setId(result.getInt("id"));
				course.setName(result.getString("name"));
				course.setDescription(result.getString("description"));
				courses.add(course);
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

		return courses;
	}

	public Course findCourseByName(String name) throws SQLException {
		Course course = null;

		String query = "SELECT * FROM corso WHERE Nome = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, name);
			result = pstatement.executeQuery();
			while (result.next()) {
				course = new Course();
				course.setId(result.getInt("ID"));
				course.setName(result.getString("Nome"));
				course.setDescription(result.getString("Descrizione"));
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

		return course;
	}

	public Course findCourseById(int c_id) throws SQLException {
		Course course = new Course();

		String query = "SELECT * FROM corso WHERE ID = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, c_id);
			result = pstatement.executeQuery();
			result.next();
			course.setId(c_id);
			course.setName(result.getString("Nome"));
			course.setDescription(result.getString("Descrizione"));

			LecturerDAO lecDAO = new LecturerDAO(this.connection);
			course.setTaughtBy(lecDAO.findLecturerById(result.getInt("ID_Docente")));

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

		return course;
	}
}
