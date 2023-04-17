package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

	public List<Verbal> findAllVerbalsByCall(int id_call) throws SQLException {
		List<Verbal> verbals = new ArrayList<>();

		String query = "SELECT * FROM verbale WHERE ID_Appello = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id_call);
			result = pstatement.executeQuery();
			while (result.next()) {
				Verbal verb = new Verbal();

				verb.setId(result.getInt("ID"));
				verb.setCreationDate(result.getDate("DataCreazione"));
				verb.setCreationTime(result.getTime("OraCreazione"));

				GraduationCallDAO gcDAO = new GraduationCallDAO(this.connection);
				verb.setCall(
						gcDAO.getGraduationCallByIdAndCourse(result.getInt("ID_Appello"), result.getInt("ID_Corso")));

				verbals.add(verb);
			}
			for (Verbal v : verbals) {
				findStudentsInVerbal(v);
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
		return verbals;
	}

	public void findStudentsInVerbal(Verbal v) throws SQLException {
		Student stud = null;

		String query = "SELECT * FROM student_verbal JOIN student on ID_Studente=Matricola WHERE ID_Verbale = ?";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, v.getId());
			result = pstatement.executeQuery();
			while (result.next()) {
				stud = new Student();
				stud.setMatricola(result.getInt("Matricola"));
				stud.setSurname(result.getString("Cognome"));
				stud.setName(result.getString("Nome"));
				stud.setEmail(result.getString("Email"));
				stud.setUsername(result.getString("Username"));

				v.addStudent(stud);
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
