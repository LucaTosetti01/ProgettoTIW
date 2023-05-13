package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Course;
import exceptions.CourseDAOException;

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
			throw new SQLException("Failure in courses' data extraction taught by lecturer");
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

	public List<Course> findAllCoursesByStudent(int student_id) throws SQLException {
		List<Course> courses = new ArrayList<Course>();

		String query = "SELECT r.ID_Course,c.ID,r.ID_Student,u.ID,u.Role,c.Name,c.Description "
				+ "FROM registrations_courses AS r JOIN courses AS c ON r.ID_Course = c.ID JOIN users AS u ON u.ID = r.ID_Student "
				+ "WHERE r.ID_Student = ? AND u.Role = 'Student' " + "ORDER BY c.Name DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, student_id);
			result = pstatement.executeQuery();
			while (result.next()) {
				Course course = new Course();
				course.setId(result.getInt("ID"));
				course.setName(result.getString("Name"));
				course.setDescription(result.getString("Description"));
				courses.add(course);
			}
		} catch (SQLException e) {
			throw new SQLException("Failure in courses' data extraction which student is subscribed to");
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
			throw new SQLException("Failure in course's data extraction");
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
	
	public void checkIfCourseIsTaughtByLecturer(int course_id, int lecturer_id) throws SQLException, CourseDAOException {
		String query = "SELECT COUNT(*) AS Counter FROM courses WHERE ID = ? AND ID_Lecturer = ?";
		
		PreparedStatement pstatement = null;
		ResultSet result = null;
		int numberOfRows;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, course_id);
			pstatement.setInt(2, lecturer_id);
			result = pstatement.executeQuery();
			result.next();
			numberOfRows = result.getInt("Counter");
		} catch (SQLException e) {
			throw new SQLException("Failure in course's data extraction");
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
		if(numberOfRows!=1) {
			throw new CourseDAOException("The chosen course is not taught by the lecturer logged");
		}
		
	}

}
