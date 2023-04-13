package beans;

public class Course {
	private int id;
	private String name;
	private String description;
	private Lecturer taughtBy;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Lecturer getTaughtBy() {
		return taughtBy;
	}

	public void setTaughtBy(Lecturer taughtBy) {
		this.taughtBy = taughtBy;
	}

}
