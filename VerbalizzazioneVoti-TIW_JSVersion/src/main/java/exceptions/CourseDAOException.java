package exceptions;

public class CourseDAOException extends Exception{
	public CourseDAOException() {
		super();
	}
	
	public CourseDAOException(String message) {
		super(message);
	}
}
