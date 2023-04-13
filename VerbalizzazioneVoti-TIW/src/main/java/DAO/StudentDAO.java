package DAO;

import java.sql.Connection;

public class StudentDAO {
	private Connection connection;
	
	public StudentDAO() {
		
	}

	public StudentDAO(Connection connection) {
		this.connection = connection;
	}
	
	
}
