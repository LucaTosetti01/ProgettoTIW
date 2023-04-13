package beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Student {
	private int matricola;
	private String name;
	private String surname;
	private String email;
	private String username;
	private Map<GraduationCall, Entry<Integer, Integer>> calls = new HashMap<GraduationCall, Entry<Integer, Integer>>();
	//private Set<Verbal> verbals = new HashSet<Verbal>();

	public int getMatricola() {
		return matricola;
	}

	public void setMatricola(int matricola) {
		this.matricola = matricola;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/*public Set<Verbal> getVerbals() {
		return verbals;
	}

	public void setVerbals(Set<Verbal> verbals) {
		this.verbals = verbals;
	}*/

}
