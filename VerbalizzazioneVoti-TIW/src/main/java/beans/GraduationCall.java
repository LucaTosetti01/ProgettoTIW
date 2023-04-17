package beans;

import java.sql.Date;
import java.sql.Time;

public class GraduationCall {
	private int id;
	private Date date;
	private Time time;
	private Course course;

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

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course id_course) {
		this.course = id_course;
	}

}
