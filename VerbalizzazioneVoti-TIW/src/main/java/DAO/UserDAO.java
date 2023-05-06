package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.User;

public class UserDAO {
	private Connection connection;

	public UserDAO() {

	}

	public UserDAO(Connection connection) {
		this.connection = connection;
	}

	public User checkCredentials(String username, String password) throws SQLException {
		String query = "SELECT ID,Surname,Name,Email,Username,Role FROM users WHERE Username = ? AND Password = ?";

		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			result = pstatement.executeQuery();
			if (!result.isBeforeFirst()) {
				return null;
			} else {
				result.next();
				User user = new User();
				user.setId(result.getInt("ID"));
				user.setSurname(result.getString("Surname"));
				user.setName(result.getString("Name"));
				user.setEmail(result.getString("Email"));
				user.setUsername(result.getString("Username"));
				user.setRole(result.getString("Role"));
				return user;
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
	}
}
