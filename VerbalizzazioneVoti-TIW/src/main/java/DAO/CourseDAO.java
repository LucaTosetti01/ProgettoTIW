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

	public List<Course> findAllCoursesByLecturer(int lecturer_id) throws SQLException {
		List<Course> courses = new ArrayList<Course>();

		String query = "SELECT * FROM courses WHERE ID_Lecturer = ? ORDER BY Name DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, lecturer_id);
			result = pstatement.executeQuery();
			while (result.next()) {
				Course course = new Course();
				course.setId(result.getInt("ID"));
				course.setName(result.getString("Name"));
				course.setDescription(result.getString("Description"));
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

		String query = "SELECT * FROM courses WHERE Name = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, name);
			result = pstatement.executeQuery();
			while (result.next()) {
				course = new Course();
				course.setId(result.getInt("ID"));
				course.setName(result.getString("Name"));
				course.setDescription(result.getString("Description"));
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

		String query = "SELECT * FROM courses WHERE ID = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, c_id);
			result = pstatement.executeQuery();
			result.next();
			course.setId(c_id);
			course.setName(result.getString("Name"));
			course.setDescription(result.getString("Description"));
			course.setTaughtById(result.getInt("ID_Lecturer"));

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
