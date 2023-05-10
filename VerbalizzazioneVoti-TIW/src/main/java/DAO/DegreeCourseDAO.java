package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.DegreeCourse;

public class DegreeCourseDAO {
	private Connection connection;

	public DegreeCourseDAO() {

	}

	public DegreeCourseDAO(Connection connection) {
		this.connection = connection;
	}

	public List<DegreeCourse> findAllDegreeCourses() throws SQLException {
		List<DegreeCourse> degreeCourses = new ArrayList<>();

		String query = "SELECT * FROM degree_courses";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			result = pstatement.executeQuery();
			while (result.next()) {
				DegreeCourse dCourse = new DegreeCourse();
				dCourse.setId(result.getInt("ID"));
				dCourse.setName(result.getString("Name"));
				dCourse.setDescription(result.getString("Description"));
				degreeCourses.add(dCourse);
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

		return degreeCourses;
	}

	public DegreeCourse findDegreeCourseById(int degreeCourse_id) throws SQLException {
		DegreeCourse dcourse = null;

		String query = "SELECT * FROM degree_courses WHERE ID = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, degreeCourse_id);
			result = pstatement.executeQuery();
			while (result.next()) {
				dcourse = new DegreeCourse();
				dcourse.setId(result.getInt("ID"));
				dcourse.setName(result.getString("Name"));
				dcourse.setDescription(result.getString("Description"));
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

		return dcourse;
	}
}
