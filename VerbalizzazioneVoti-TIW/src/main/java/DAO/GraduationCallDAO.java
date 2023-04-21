package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import beans.Course;
import beans.GraduationCall;

public class GraduationCallDAO {
	private Connection connection;

	public GraduationCallDAO() {

	}

	public GraduationCallDAO(Connection connection) {
		this.connection = connection;
	}

	public List<GraduationCall> findAllDegreeCallByCourseId(int course_id) throws SQLException {
		List<GraduationCall> calls = new ArrayList<GraduationCall>();

		String query = "SELECT * FROM calls WHERE ID_Course = ? ORDER BY Date DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, course_id);
			result = pstatement.executeQuery();
			while (result.next()) {
				GraduationCall call = new GraduationCall();

				call.setId(result.getInt("ID"));
				call.setDate(result.getDate("Date"));
				call.setTime(result.getTime("Time"));
				call.setCourseId(result.getInt("ID_Course"));
				calls.add(call);
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

		return calls;
	}

	public List<GraduationCall> findAllDegreeCallByDate(Date date) throws SQLException {
		List<GraduationCall> calls = new ArrayList<GraduationCall>();

		String query = "SELECT * FROM calls WHERE Date = ? ORDER BY Date DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setDate(1, date);
			result = pstatement.executeQuery();
			while (result.next()) {
				GraduationCall call = new GraduationCall();

				call.setId(result.getInt("ID"));
				call.setDate(date);
				call.setTime(result.getTime("Time"));
				call.setCourseId(result.getInt("ID_Course"));
		
				calls.add(call);
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

		return calls;
	}

	public int createGraduationCall(Date date, Time time, int id_course) throws SQLException {
		String query = "INSERT INTO calls (Date, Time, ID_Course) VALUES (?, ?, ?)";
		int code = 0;

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setDate(1, date);
			pstatement.setTime(2, time);
			pstatement.setInt(3, code);
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

	public GraduationCall getGraduationCallByIdAndCourse(int gc_id, int c_id) throws SQLException {
		GraduationCall gc = new GraduationCall();

		String query = "SELECT * FROM calls WHERE ID = ? AND ID_Course = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, gc_id);
			pstatement.setInt(2, c_id);
			result = pstatement.executeQuery();
			result.next();
			gc.setId(gc_id);
			gc.setDate(result.getDate("Date"));
			gc.setTime(result.getTime("Time"));
			gc.setCourseId(c_id);
			
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
		return gc;
	}
}
