package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import beans.Course;
import beans.GraduationCall;
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
	
	public int createVerbal(Date creationDate, Time creationTime, int call_id) throws SQLException {
		String query = "INSERT INTO verbals (CreationDate, CreationTime, ID_Call) VALUES (?,?,?)";
		int code=0;
		
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setDate(1, creationDate);
			pstatement.setTime(2, creationTime);
			pstatement.setInt(3, call_id);
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
	
	public Verbal getVerbalById(int verbal_id) throws SQLException {
		String query = "SELECT * FROM verbals WHERE ID = ?";
		Verbal verbal = new Verbal();
		
		PreparedStatement pstatement = null;
		ResultSet result= null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, verbal_id);
			result = pstatement.executeQuery();
			result.next();
			
			verbal.setId(verbal_id);
			verbal.setCreationDate(result.getDate("CreationDate"));
			verbal.setCreationTime(result.getTime("CreationTime"));
			verbal.setCallId(result.getInt("ID_Call"));
			
		}catch (SQLException e) {
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
		return verbal;
	}
	
	public Verbal getVerbalByCallId(int call_id) throws SQLException {
		String query = "SELECT * FROM verbals WHERE ID_Call = ?";
		Verbal verbal = new Verbal();
		
		PreparedStatement pstatement = null;
		ResultSet result= null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, call_id);
			result = pstatement.executeQuery();
			result.next();
			
			verbal.setId(result.getInt("ID"));
			verbal.setCreationDate(result.getDate("CreationDate"));
			verbal.setCreationTime(result.getTime("CreationTime"));
			verbal.setCallId(result.getInt("ID_Call"));
			
		}catch (SQLException e) {
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
		return verbal;
	}

}
