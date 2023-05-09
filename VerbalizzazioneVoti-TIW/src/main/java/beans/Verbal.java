package beans;

import java.sql.Date;
import java.sql.Time;

public class Verbal {
	private int id;
	private Date creationDate;
	private Time creationTime;
	private int callId;
	//La relazione N:N non la implemento all'interno dei Beans, ma userò i dao per realizzarla 
	//nelle servlets
	//private Set<Student> students = new HashSet<Student>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Time getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Time creationTime) {
		this.creationTime = creationTime;
	}
/*
	public Set<Student> getStudents() {
		return students;
	}

	public void setStudents(Set<Student> students) {
		this.students = students;
	}
	
	public void addStudent(Student student) {
		this.students.add(student);
	}

	public void removeStudent(Student student) {
		this.students.remove(student);
	}
*/
	public int getCallId() {
		return callId;
	}

	public void setCallId(int callId) {
		this.callId = callId;
	}

}
