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

	public List<GraduationCall> findAllDegreeCallByCourseID(int id) throws SQLException {
		List<GraduationCall> calls = new ArrayList<GraduationCall>();

		String query = "SELECT * FROM appello WHERE ID_Corso = ? ORDER BY data DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id);
			result = pstatement.executeQuery();
			while (result.next()) {
				GraduationCall call = new GraduationCall();

				call.setId(result.getInt("ID"));
				call.setDate(result.getDate("Data"));
				call.setTime(result.getTime("Ora"));
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

		String query = "SELECT * FROM appello WHERE Data = ? ORDER BY data DESC";
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
				call.setTime(result.getTime("Ora"));

				int id_course = result.getInt("ID_Corso");
				String convenientQuery = "SELECT * FROM corso WHERE ID = ?";
				PreparedStatement cstatement = connection.prepareStatement(convenientQuery);
				cstatement.setInt(1, id_course);
				ResultSet convenientResult = cstatement.executeQuery();
				convenientResult.next();
				Course course = new Course();

				course.setId(id_course);
				course.setName(result.getString("Nome"));
				course.setDescription(result.getString("Descrizione"));

				call.setCourse(course);
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
		String query = "INSERT into appello (Data, Ora, ID_Corso) VALUES (?, ?, ?)";
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

		String query = "SELECT * FROM appello WHERE ID = ? AND ID_Corso = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, gc_id);
			pstatement.setInt(2, c_id);
			result = pstatement.executeQuery();
			result.next();
			gc.setId(gc_id);
			gc.setDate(result.getDate("Data"));
			gc.setTime(result.getTime("Ora"));

			CourseDAO cDAO = new CourseDAO(this.connection);
			gc.setCourse(cDAO.findCourseById(result.getInt("ID_Corso")));

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
