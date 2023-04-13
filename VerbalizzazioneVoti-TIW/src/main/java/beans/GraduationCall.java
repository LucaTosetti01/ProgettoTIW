package beans;

import java.sql.Date;
import java.sql.Time;

public class GraduationCall {
	private int id;
	private Date date;
	private Time time;
	private Course id_course;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public Course getId_course() {
		return id_course;
	}

	public void setId_course(Course id_course) {
		this.id_course = id_course;
	}

}
