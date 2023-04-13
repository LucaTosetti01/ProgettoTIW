package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

				call.setId(result.getInt("id"));
				call.setDate(result.getDate("data"));
				call.setTime(result.getTime("ora"));
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
}
