package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import beans.Course;
import beans.GraduationCall;
import beans.Student;
import beans.Verbal;

public class VerbalDAO {
	private Connection connection;

	public VerbalDAO() {

	}

	public VerbalDAO(Connection connection) {
		this.connection = connection;
	}

	public Optional<Verbal> findAllVerbalsByCall(int call_id) throws SQLException {
		Optional<Verbal> res = Optional.empty();

		String query = "SELECT * FROM verbals WHERE ID_Call = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, call_id);
			result = pstatement.executeQuery();
			while (result.next()) {
				Verbal verb = new Verbal();

				verb.setId(result.getInt("ID"));
				verb.setCreationDate(result.getDate("CreationDate"));
				verb.setCreationTime(result.getTime("CreationTime"));
				verb.setCallId(call_id);
				
				res = Optional.of(verb);
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
		return res;
	}

}
